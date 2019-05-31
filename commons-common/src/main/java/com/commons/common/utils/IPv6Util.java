package com.commons.common.utils;

import com.commons.common.utils.ipv6.*;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * 对操作的目标IP进行合法性校验，以及对目标IP进行在用户所属的IP范围内校验
 */
public class IPv6Util {
    /**
     * 校验两个IP地址段是否一致
     *
     * @param sourceIPRange IP地址段，多段之间用逗号分隔，每段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段的四种格式
     * @param targetIPRange IP地址段，多段之间用逗号分隔，每段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段的四种格式
     * @return 一致则返回true，否则为false
     */
    public static boolean validateIPEqual(String sourceIPRange, String targetIPRange) {
        if (sourceIPRange.contains(",") || targetIPRange.contains(",")) {
            /* IP地址段字符串中有多个用逗号分隔的IP段，则采用以下比较方法：
            * targetIPRange是sourceIPRange的子集，同时sourceIPRange也是targetIPRange的子集，则两者完全相等
            */
            String[] sourceIPRanges = sourceIPRange.split(",");
            String[] targetIPRanges = targetIPRange.split(",");
            return checkIPRangeIncluded(sourceIPRanges, targetIPRanges)
                    && checkIPRangeIncluded(targetIPRanges, sourceIPRanges);
        } else {
            //进行比较的都是单一的IP地址段，直接比较两者的起始和截止地址
            return getBeginAddress(sourceIPRange) == getBeginAddress(targetIPRange)
                    && getEndAddress(sourceIPRange) == getEndAddress(targetIPRange);
        }
    }

    /**
     * 校验一组IP段之间是否有交集
     *
     * @param ipRanges IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return true：有交集，false：无交集
     */
    public static boolean checkIPRangeIntersected(String... ipRanges) {
        //求出IP地址段的起始和截止IP
        double[][] longIPRanges = ipRangesToLongRanges(ipRanges).toArray(new double[ipRanges.length][]);
        //比较是否有交集
        for (int i = 0; i < ipRanges.length; i++) {
            for (int j = i + 1; j < ipRanges.length; j++) {
                if (longIPRanges[i][0] <= longIPRanges[j][1]
                        && longIPRanges[i][1] >= longIPRanges[j][0]) {
                    //A的起始地址小于等于B的截止地址并且A的截止地址大于等于B的起始地址，则有交集
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 校验一组IP段之间是否有交集
     *
     * @param ipRanges IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return true：有交集，false：无交集
     */
    public static boolean checkIPRangeIntersected(Collection<String> ipRanges) {
        return checkIPRangeIntersected(ipRanges.toArray(new String[ipRanges.size()]));
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges 全集IP地址段
     * @param toCheckRange 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(Collection<String> sourceRanges, String toCheckRange) {
        String[] empty = new String[0];
        return checkIPRangeIncluded(sourceRanges.toArray(empty), toCheckRange);
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges 全集IP地址段
     * @param toCheckRange 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(String[] sourceRanges, String toCheckRange) {
        return checkIPRangeIncluded(sourceRanges, new String[]{toCheckRange});
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges  全集IP地址段
     * @param toCheckRanges 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(Collection<String> sourceRanges, Collection<String> toCheckRanges) {
        String[] empty = new String[0];
        return checkIPRangeIncluded(sourceRanges.toArray(empty), toCheckRanges.toArray(empty));
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges  源IP地址段，IP地址段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 待校验IP地址段，IP地址段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 当toCheckRanges为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(String[] sourceRanges, String[] toCheckRanges) {

        for (String toCheckRange : toCheckRanges) {
            IPv6AddressRange targetNetwork = null;
            if (toCheckRange.contains("-")) {
                targetNetwork = IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(toCheckRange.split("-")[0]), IPv6Address.fromString(toCheckRange.split("-")[1]));
            } else {
                targetNetwork =   IPv6Network.fromString(toCheckRange);
            }
            boolean contains = false;
            for (String sourceRange : sourceRanges) {
                IPv6AddressRange sourceNetwork = null;
                if (sourceRange.contains("-")) {
                    sourceNetwork = IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(sourceRange.split("-")[0]), IPv6Address.fromString(sourceRange.split("-")[1]));
                } else {
                    sourceNetwork = IPv6Network.fromString(sourceRange);
                }
                contains = sourceNetwork.contains(targetNetwork);
                if (contains) {
                    break;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    /**
     * 取两个IP段之间的交集
     *
     * @param ranges1 第一个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param ranges2 第二个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 交集IP地址段的字符串表示
     */
    public static List<String> getIPRangeIntersection(Collection<String> ranges1, Collection<String> ranges2) {
        return getIPRangeIntersection(
                ranges1.toArray(new String[ranges1.size()]),
                ranges2.toArray(new String[ranges2.size()])
        );
    }

    /**
     * 取两个IP段之间的交集
     *
     * @param sourceRanges  第一个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 第二个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 交集IP地址段的字符串表示
     */
    public static List<String> getIPRangeIntersection(String[] sourceRanges, String[] toCheckRanges) {
        List<String> source = getIpList(sourceRanges);
        List<String> target = getIpList(toCheckRanges);
        return new ArrayList<>(CollectionUtils.intersection(source, target));
    }

    /**
     * 求出要校验的IP地址段中不在源IP地址段的部分
     *
     * @param sourceRanges  源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 要校验的IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 要校验的IP地址段不在源IP地址段中的部分
     */
    public static List<String> getIPRangeDisjoint(Collection<String> sourceRanges, Collection<String> toCheckRanges) {
        return getIPRangeDisjoint(
                sourceRanges.toArray(new String[sourceRanges.size()]),
                toCheckRanges.toArray(new String[toCheckRanges.size()])
        );
    }

    /**
     * 求出要校验的IP地址段中不在源IP地址段的部分
     *
     * @param sourceRanges  源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 要校验的IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 要校验的IP地址段不在源IP地址段中的部分
     */
    public static List<String> getIPRangeDisjoint(String[] sourceRanges, String[] toCheckRanges) {
        List<String> source = getIpList(sourceRanges);
        List<String> target = getIpList(toCheckRanges);
//        List c  = new ArrayList(source);
//        c.retainAll(target); // 得到  a, b 的交集。
//        List d = new ArrayList(source);
//        d.addAll(target); // 合并 a, b 值到 d 中。
//        d.removeAll(c); // 去掉交集 c 中的所有条目。留下只出现在a 或 b 中的条目。
        return new ArrayList<>(CollectionUtils.disjunction(source, target));
    }


    /**
     * 将离散的、无序的IP地址段拼接成从小到大排序的连续的IP地址段
     *
     * @param ipRanges 源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 拼接后的有序连续的IP地址段
     */
    public static List<String> concatIPRanges(List<String> ipRanges) {
        return concatIPRanges(ipRanges.toArray(new String[ipRanges.size()]));
    }

    /**
     * 将离散的、无序的IP地址段拼接成从小到大排序的连续的IP地址段
     *
     * @param ipRanges 源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 拼接后的有序连续的IP地址段
     */
    public static List<String> concatIPRanges(String... ipRanges) {
        return getIpList(ipRanges);
    }

    /**
     * 将离散的、无序的IP地址段拼接成从小到大排序的连续的IP地址段
     *
     * @param ipRanges 源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 非重复IP记数
     */
    public static long getRangeIPCount(String... ipRanges) {
        long ipCount = 0L;
        for (String ipRange : ipRanges) {
            if (ipRange.contains("-")) {
                ipCount += IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(ipRange.split("-")[0]), IPv6Address.fromString(ipRange.split("-")[1])).size().longValue();
                continue;
            }
            ipCount += IPv6Network.fromString(ipRange).size().longValue();
        }
        return ipCount;
    }

    /**
     * 将一组IP地址段拼接，并按起始IP从小到大排序，再转换成点分十进制IPv4/掩码位数或IPv4 点分十进制掩码的表示方式
     *
     * @param showMaskSize 为true时返回的格式为：点分十进制IPv4/掩码位数，否则为IPv4 点分十进制掩码
     * @param ipRanges     待转换的IP地址范围
     * @return 转换后的IP地址段
     */
    public static List<String> changeIPRangesToSubNets(boolean showMaskSize, String... ipRanges) {
        return changeIPRangesToSubNets(1, showMaskSize, ipRanges);
    }

    /**
     * 将一组IP地址段拼接，并按起始IP从小到大排序，再转换成点分十进制IPv4/掩码位数或IPv4 点分十进制掩码的表示方式
     *
     * @param minMaskSize  允许的最小的掩码位数
     * @param showMaskSize 为true时返回的格式为：点分十进制IPv4/掩码位数，否则为IPv4 点分十进制掩码
     * @param ipRanges     待转换的IP地址范围
     * @return 转换后的IP地址段
     */
    public static List<String> changeIPRangesToSubNets(int minMaskSize, boolean showMaskSize, String... ipRanges) {
        //先将字符串型IP地址段转换成数值型起止IP数组
        List<String> resultList = new ArrayList<>();
        for (String ipRange : ipRanges) {
            Iterator<IPv6Network> networks = null;
            if (ipRange.contains("-")) {
                networks = IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(ipRange.split("-")[0]), IPv6Address.fromString(ipRange.split("-")[1])).toSubnets();
            } else {
                networks = IPv6Network.fromString(ipRange).toSubnets();
            }
            while (networks.hasNext()) {
                resultList.add(networks.next().toString());
            }
        }
        return resultList;
    }


    /**
     * 校验IP地址范围是否有交集（保留旧有的方法名）
     *
     * @param sourceIP IP范围，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param targetIP 待校验的IP范围，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 有交集返回true，否则返回false
     */
    public static boolean validateIPRange(String sourceIP, String targetIP) {
        return checkIPRangeIntersected(sourceIP, targetIP);
    }


    /**
     * 计算网络地址
     *
     * @param ip 接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 三种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 网络地址（即子网起始IP）
     */
    public static String getNetworkAddress(String ip) {
        //接受IP、IP 掩码、IP/掩码位数三种格式
        return numberToIP(getBeginAddress(ip));
    }

    /**
     * 计算网络地址
     *
     * @param ip   点分十进制表示的IPv4地址
     * @param mask 点分十进制表示的子网掩码或十进制数值表示的子网掩码位数
     * @return 网络地址（即子网起始IP）
     */
    public static String getNetworkAddress(String ip, String mask) {
        return numberToIP(getBeginAddress(ip + "/" + mask));
    }

    /**
     * 计算第一个ip地址，并将其转换为long型数字
     *
     * @param ipRange IP地址段，接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 或者 IPv4-IPv4片段 四种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 起始IP的long型数值
     */
    public static BigInteger getBeginAddress(String ipRange) {

        String[] rangeParts = null;
        if (ipRange.contains("-")) {
            // '-'隔开的地址段
            rangeParts = ipRange.split("-");
            BigInteger begin = IPv6Address.fromString(rangeParts[0]).toBigInteger();
            BigInteger end = IPv6Address.fromString(rangeParts[1]).toBigInteger();
            return end.compareTo(begin) > 0 ? begin : end;
        }
        return IPv6Network.fromString(ipRange).getFirst().toBigInteger();
    }

    /**
     * 计算最后一位IP地址，将其转换为long型数字
     *
     * @param ipRange IP地址段，接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 或者 IPv4-IPv4片段 四种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 最后一位IP的long型数值
     */
    public static BigInteger getEndAddress(String ipRange) {
        String[] rangeParts = null;
        if (ipRange.contains("-")) {
            rangeParts = ipRange.split("-");
            BigInteger begin = IPv6Address.fromString(rangeParts[0]).toBigInteger();
            BigInteger end = IPv6Address.fromString(rangeParts[1]).toBigInteger();
            return end.compareTo(begin) > 0 ? end : begin;
        }
        return IPv6Network.fromString(ipRange).getLast().toBigInteger();
    }

    /**
     * 根据IP地址(点分十进制)、子网掩码获得子网起始地址
     *
     * @param num       掩码位数
     * @param ipAddress IP地址(点分十进制)
     * @return String 点分十进制子网起始地址
     */
    public static String getBeginSubnetAddress(int num, String ipAddress) {
        return IPv6Network.fromAddressAndMask(IPv6Address.fromString(ipAddress), IPv6NetworkMask.fromPrefixLength(num)).getFirst().toLongString();
    }


    /**
     * 根据子网地址(十进制)、子网掩码位数计算网段最后的IP地址
     *
     * @param num 掩码位数
     * @param ip  点分十进制IP
     * @return 点分十进制子网最后一位IP
     */
    public static String getEndSubnetAddress(int num, String ip) {
        return IPv6Network.fromAddressAndMask(IPv6Address.fromString(ip), IPv6NetworkMask.fromPrefixLength(num)).getLast().toLongString();
    }

    /**
     * 将16进制整数形式转换成ipv6形式的IP地址
     *
     * @param ip IP地址的数值表示
     * @return 点分十进制表示的IP地址
     */
    public static String numberToIP(BigInteger ip) {
        return IPv6AddressHelpers.numberToIp(ip);
    }

    /**
     * 将127.0.0.1 形式的IP地址转换成10进制整数
     *
     * @param strIP 点分十进制表示的IP地址
     * @return IP地址的数值表示
     */
    public static BigInteger ipToNumber(String strIP) {
        return IPv6AddressHelpers.ipToNumber(strIP);
    }


    /**
     * 取出IP地址段中的所有IP
     *
     * @param ipRange IP地址段
     * @return IP列表
     */
    public static List<String> getIpList(String ipRange) {
        Iterable<IPv6Address> addresses = null;

        if (ipRange.contains("-")) {
            addresses = IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString(ipRange.split("-")[0]), IPv6Address.fromString(ipRange.split("-")[1]));
        } else {
            addresses = IPv6Network.fromString(ipRange);
        }
        Set<String> ipList = new LinkedHashSet<>();
        for (IPv6Address ip : addresses) {
            ipList.add(ip.toLongString());
        }
        return new ArrayList<>(ipList);
    }

    public static List<String> getIpList(String... ipRange) {
        Set<String> ipList = new LinkedHashSet<>();
        for (String ip : ipRange) {
            ipList.addAll(getIpList(ip));
        }
        return new ArrayList<>(ipList);
    }

    public static List<String> getIpList(Collection<String> ipRange) {
        Set<String> ipList = new LinkedHashSet<>();
        for (String ip : ipRange) {
            ipList.addAll(getIpList(ip));
        }
        return new ArrayList<>(ipList);
    }

    /**
     * 根据Ip地址和子网掩码位数  查询所有该网段的地址
     *
     * @param ip      ip地址（通常是网络地址）
     * @param maskNum 子网掩码位数 （如 24,28,30）
     * @return 网络ip集合
     */
    public static List<String> getIpListByIpMask(String ip, Integer maskNum) {
        return getIpList(ip + "/" + maskNum);
    }


    /**
     * 根据IP计算子网掩码位数
     *
     * @param ip 目标IP，接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 三种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 子网掩码位数
     */
    private static int getSubnetMaskSizeFromIP(String ip) {
        if (ip.contains("-")) {
            return 0;
        }
        if (ip.contains("/")) {
            return getSubnetMaskSizeFromMask(ip.split("/")[1]);
        } else if (ip.contains(" ")) {
            return getSubnetMaskSizeFromMask(ip.split(" ")[1]);
        }
        return 32;
    }

    /**
     * 根据掩码返回掩码位数
     *
     * @param mask 点分十进制表示的子网掩码或者字符串表示的掩码位数
     * @return 子网掩码位数
     */
    private static int getSubnetMaskSizeFromMask(String mask) {
        return IPv6NetworkMask.fromPrefixLength(Integer.valueOf(mask)).asPrefixLength();
    }

    /**
     * 拆分出IP和子网掩码
     *
     * @param ipRange 以点分十进制表示的IPv4 或 IPv4 掩码 或 IPv4/掩码位数 表达的IP地址段
     * @return 字符串数组，第一个元素是IP，第二个元素是掩码（或掩码位数）
     */
    private static String[] splitIPAndMask(String ipRange) {
        if (ipRange.contains("/")) {
            return ipRange.split("/");
        } else if (ipRange.contains(" ")) {
            return ipRange.split(" ");
        } else {
            return new String[]{ipRange, "128"};
        }
    }


    /**
     * 验证long型IP地址段起始IP必需小于等于endIP，如不满足则抛出运行时异常
     *
     * @param startIP 起始IP
     * @param endIP   截止IP
     */
    private static void checkLongIPRange(long startIP, long endIP) {
        if (startIP > endIP) {
            throw new RuntimeException("Invalid IPv4 address range");
        }
    }


    /**
     * 将一组IP地址段转换成数值型起止IP数组表示的IP地址段
     *
     * @param ipRanges IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段的四种格式
     * @return 以数值型起止IP数组表示的IP地址段
     */
    private static List<double[]> ipRangesToLongRanges(String... ipRanges) {
        List<double[]> longList = new ArrayList<>(ipRanges.length);
        for (String range : ipRanges) {
            longList.add(
                    new double[]{
                            getBeginAddress(range).doubleValue(),
                            getEndAddress(range).doubleValue()
                    }
            );
        }
        return longList;
    }
}