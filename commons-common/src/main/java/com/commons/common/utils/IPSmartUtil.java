package com.commons.common.utils;

import java.math.BigInteger;
import java.util.*;

/**
 * 对操作的目标IP进行合法性校验，以及对目标IP进行在用户所属的IP范围内校验
 */
public class IPSmartUtil {

    public static boolean isIpV6(String ip) {
        return ip.indexOf(":") != -1;
    }

    /**
     * 校验两个IP地址段是否一致
     *
     * @param sourceIPRange IP地址段，多段之间用逗号分隔，每段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段的四种格式
     * @param targetIPRange IP地址段，多段之间用逗号分隔，每段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段的四种格式
     * @return 一致则返回true，否则为false
     */
    public static boolean validateIPEqual(String sourceIPRange, String targetIPRange) {
        if (isIpV6(sourceIPRange)) {
            return IPv6Util.validateIPEqual(sourceIPRange, targetIPRange);
        }
        return IPUtil.validateIPEqual(sourceIPRange, targetIPRange);
    }

    /**
     * 校验一组IP段之间是否有交集
     *
     * @param ipRanges IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return true：有交集，false：无交集
     */
    public static boolean checkIPRangeIntersected(String... ipRanges) {
        if (isIpV6(ipRanges[0])) {
            return IPv6Util.checkIPRangeIntersected(ipRanges);
        }
        return IPUtil.checkIPRangeIntersected(ipRanges);
    }

    /**
     * 校验一组IP段之间是否有交集
     *
     * @param ipRanges IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return true：有交集，false：无交集
     */
    public static boolean checkIPRangeIntersected(Collection<String> ipRanges) {
        if (isIpV6(ipRanges.iterator().next())) {
            return IPv6Util.checkIPRangeIntersected(ipRanges);
        }
        return IPv6Util.checkIPRangeIntersected(ipRanges);
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges 全集IP地址段
     * @param toCheckRange 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(Collection<String> sourceRanges, String toCheckRange) {
        if (isIpV6(toCheckRange)) {
            return IPv6Util.checkIPRangeIncluded(sourceRanges, toCheckRange);
        }
        return IPUtil.checkIPRangeIncluded(sourceRanges, toCheckRange);
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges 全集IP地址段
     * @param toCheckRange 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(String[] sourceRanges, String toCheckRange) {
        if (isIpV6(toCheckRange)) {
            return IPv6Util.checkIPRangeIncluded(sourceRanges, toCheckRange);
        }
        return IPUtil.checkIPRangeIncluded(sourceRanges, toCheckRange);
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges  全集IP地址段
     * @param toCheckRanges 待校验IP地址段
     * @return 当toCheckRange为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(Collection<String> sourceRanges, Collection<String> toCheckRanges) {
        if (isIpV6(sourceRanges.iterator().next())) {
            return IPv6Util.checkIPRangeIncluded(sourceRanges, toCheckRanges);
        }
        return IPUtil.checkIPRangeIncluded(sourceRanges, toCheckRanges);
    }

    /**
     * 判断一组IP段是否完全包含了另一IP段
     *
     * @param sourceRanges  源IP地址段，IP地址段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 待校验IP地址段，IP地址段接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 当toCheckRanges为sourceRanges的子集时为true，否则为false
     */
    public static boolean checkIPRangeIncluded(String[] sourceRanges, String[] toCheckRanges) {
        if (isIpV6(sourceRanges[0])) {
            return IPv6Util.checkIPRangeIncluded(sourceRanges, toCheckRanges);
        }
        return IPUtil.checkIPRangeIncluded(sourceRanges, toCheckRanges);
    }

    /**
     * 取两个IP段之间的交集
     *
     * @param ranges1 第一个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param ranges2 第二个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 交集IP地址段的字符串表示
     */
    public static List<String> getIPRangeIntersection(Collection<String> ranges1, Collection<String> ranges2) {
        if (isIpV6(ranges1.iterator().next())) {
            return IPv6Util.getIPRangeIntersection(ranges1, ranges2);
        }
        return IPUtil.getIPRangeIntersection(ranges1, ranges2);
    }

    /**
     * 取两个IP段之间的交集
     *
     * @param ranges1 第一个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param ranges2 第二个IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 交集IP地址段的字符串表示
     */
    public static List<String> getIPRangeIntersection(String[] ranges1, String[] ranges2) {
        if (isIpV6(ranges1[0])) {
            return IPv6Util.getIPRangeIntersection(ranges1, ranges2);
        }
        return IPUtil.getIPRangeIntersection(ranges1, ranges2);
    }

    /**
     * 求出要校验的IP地址段中不在源IP地址段的部分
     *
     * @param sourceRanges  源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 要校验的IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 要校验的IP地址段不在源IP地址段中的部分
     */
    public static List<String> getIPRangeDisjoint(Collection<String> sourceRanges, Collection<String> toCheckRanges) {
        if (isIpV6(sourceRanges.iterator().next())) {
            return IPv6Util.getIPRangeDisjoint(sourceRanges, toCheckRanges);
        }
        return IPUtil.getIPRangeDisjoint(sourceRanges, toCheckRanges);
    }

    /**
     * 求出要校验的IP地址段中不在源IP地址段的部分
     *
     * @param sourceRanges  源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param toCheckRanges 要校验的IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 要校验的IP地址段不在源IP地址段中的部分
     */
    public static List<String> getIPRangeDisjoint(String[] sourceRanges, String[] toCheckRanges) {
        if (isIpV6(sourceRanges[0])) {
            return IPv6Util.getIPRangeDisjoint(sourceRanges, toCheckRanges);
        }
        return IPUtil.getIPRangeDisjoint(sourceRanges, toCheckRanges);
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
        if (isIpV6(ipRanges[0])) {
            return IPv6Util.concatIPRanges(ipRanges);
        }
        return IPUtil.concatIPRanges(ipRanges);
    }

    /**
     * 将离散的、无序的IP地址段拼接成从小到大排序的连续的IP地址段
     *
     * @param ipRanges 源IP地址段，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 非重复IP记数
     */
    public static long getRangeIPCount(String... ipRanges) {
        if (isIpV6(ipRanges[0])) {
            return IPv6Util.getRangeIPCount(ipRanges);
        }
        return IPUtil.getRangeIPCount(ipRanges);
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
        if (isIpV6(ipRanges[0])) {
            return IPv6Util.changeIPRangesToSubNets(minMaskSize, showMaskSize, ipRanges);
        }
        return IPUtil.changeIPRangesToSubNets(minMaskSize, showMaskSize, ipRanges);
    }

    /**
     * 将一组IP地址段拼接，并按起始IP从小到大排序，再转换成点分十进制IPv4 点分十进制反掩码的表示方式
     *
     * @param ipRanges 待转换的IP地址范围
     * @return 转换后的结果
     */
    public static List<String> changeIPRangesWithAntiMask(String... ipRanges) {
        return changeIPRangesWithAntiMask(1, ipRanges);
    }

    /**
     * 将一组IP地址段拼接，并按起始IP从小到大排序，再转换成点分十进制IPv4 点分十进制反掩码的表示方式
     *
     * @param minMaskSize 允许的最小掩码位数
     * @param ipRanges    待转换的IP地址范围
     * @return 转换后的结果
     */
    public static List<String> changeIPRangesWithAntiMask(int minMaskSize, String... ipRanges) {
        if (isIpV6(ipRanges[0])) {
            return null;
        }
        return IPUtil.changeIPRangesWithAntiMask(minMaskSize, ipRanges);
    }

    /**
     * 校验IP地址范围是否有交集（保留旧有的方法名）
     *
     * @param sourceIP IP范围，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @param targetIP 待校验的IP范围，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数、IPv4-IPv4片段四种格式
     * @return 有交集返回true，否则返回false
     */
    public static boolean validateIPRange(String sourceIP, String targetIP) {
        if (isIpV6(sourceIP)) {
            return IPv6Util.validateIPRange(sourceIP, targetIP);
        }
        return IPUtil.validateIPRange(sourceIP, targetIP);
    }

    /**
     * 根据骨干路由拆分IP网段子网，只有当源IP子网掩码位数大于等于24位且小于等于拆分的子网掩码位数时才进行拆分
     *
     * @param sourceIP 源IP，接受点分十进制IPv4、IPv4 掩码、IPv4/掩码位数三种格式
     * @param route    拆分的子网掩码位数
     * @return 拆分后的子网网段信息，以逗号分隔，每个子网以点分十进制IPv4 点分十进制掩码的形式表示。未拆分时返回null
     */
    public static String calculateSubNets(String sourceIP, int route) {

        if (isIpV6(sourceIP)) {
            return null;
        }
        return IPUtil.calculateSubNets(sourceIP, route);
    }

    /**
     * 计算网络地址
     *
     * @param ip 接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 三种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 网络地址（即子网起始IP）
     */
    public static String getNetworkAddress(String ip) {
        if (isIpV6(ip)) {
            return IPv6Util.getNetworkAddress(ip);
        }
        return IPUtil.getNetworkAddress(ip);
    }

    /**
     * 计算网络地址
     *
     * @param ip   点分十进制表示的IPv4地址
     * @param mask 点分十进制表示的子网掩码或十进制数值表示的子网掩码位数
     * @return 网络地址（即子网起始IP）
     */
    public static String getNetworkAddress(String ip, String mask) {
        if (isIpV6(ip)) {
            return IPv6Util.getNetworkAddress(ip, mask);
        }
        return IPUtil.getNetworkAddress(ip, mask);
    }

    /**
     * 根据掩码位数计算掩码
     *
     * @param masks 掩码位数
     * @return 点分十进制表示的掩码
     */
    public static String getMask(int masks) {
        return IPUtil.getMask(masks);
    }

    /**
     * 根据掩码位数计算子网大小
     *
     * @param mask 掩码数位
     * @return 子网IP数量
     */
    public static long getSubnetSize(int mask) {
        return IPUtil.getSubnetSize(mask);
    }

    /**
     * 计算第一个ip地址，并将其转换为long型数字
     *
     * @param ipRange IP地址段，接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 或者 IPv4-IPv4片段 四种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 起始IP的long型数值
     */
    public static BigInteger getBeginAddress(String ipRange) {
        if (isIpV6(ipRange)) {
            return IPv6Util.getBeginAddress(ipRange);
        }
        return BigInteger.valueOf(IPUtil.getBeginAddress(ipRange));
    }

    /**
     * 计算最后一位IP地址，将其转换为long型数字
     *
     * @param ipRange IP地址段，接受点分十进制IPv4 或者 IPv4 掩码 或者 IPv4/掩码位数 或者 IPv4-IPv4片段 四种格式，无掩码也无掩码位数的默认为32位掩码
     * @return 最后一位IP的long型数值
     */
    public static BigInteger getEndAddress(String ipRange) {
        if (isIpV6(ipRange)) {
            return IPv6Util.getEndAddress(ipRange);
        }
        return BigInteger.valueOf(IPUtil.getEndAddress(ipRange));
    }

    /**
     * 根据IP地址(点分十进制)、子网掩码获得子网起始地址
     *
     * @param num       掩码位数
     * @param ipAddress IP地址(点分十进制)
     * @return String 点分十进制子网起始地址
     */
    public static String getBeginSubnetAddress(int num, String ipAddress) {
        if (isIpV6(ipAddress)) {
            return IPv6Util.getBeginSubnetAddress(num, ipAddress);
        }
        return IPUtil.getBeginSubnetAddress(num, ipAddress);
    }


    /**
     * 根据子网地址(十进制)、子网掩码位数计算网段最后的IP地址
     *
     * @param num 掩码位数
     * @param ip  点分十进制IP
     * @return 点分十进制子网最后一位IP
     */
    public static String getEndSubnetAddress(int num, String ip) {
        if (isIpV6(ip)) {
            return IPv6Util.getEndSubnetAddress(num, ip);
        }
        return IPUtil.getEndSubnetAddress(num, ip);
    }

    /**
     * 转换为掩码位数
     *
     * @param mask 点分十进制掩码
     * @return 掩码位数
     */
    public static int subMaskToSubNum(String mask) {
        return IPUtil.subMaskToSubNum(mask);
    }

    /**
     * 将10进制整数形式转换成127.0.0.1形式的IP地址
     *
     * @param ip IP地址的数值表示
     * @return 点分十进制表示的IP地址
     */
    public static String longToIP(Long ip) {
        return IPUtil.longToIP(ip);
    }

    public static String numberToIP(BigInteger ip) {
        return IPv6Util.numberToIP(ip);
    }

    /**
     * 将127.0.0.1 形式的IP地址转换成10进制整数
     *
     * @param strIP 点分十进制表示的IP地址
     * @return IP地址的数值表示
     */
    public static BigInteger ipToNumber(String strIP) {
        if (isIpV6(strIP)) {
            return IPv6Util.ipToNumber(strIP);
        }
        return BigInteger.valueOf(IPUtil.ipToLong(strIP));
    }


    /**
     * 取出IP地址段中的所有IP
     *
     * @param ipRange IP地址段
     * @return IP列表
     */
    public static List<String> getIpList(String ipRange) {

        if (isIpV6(ipRange)) {
            return IPv6Util.getIpList(ipRange);
        }
        return IPUtil.getIpList(ipRange);
    }

    public static List<String> getIpList(String... ipRange) {
        List<String> ipList = new LinkedList<>();
        for (String ip : ipRange) {
            ipList.addAll(getIpList(ip));
        }
        return ipList;
    }

    public static List<String> getIpList(Collection<String> ipRange) {
        List<String> ipList = new LinkedList<>();
        for (String ip : ipRange) {
            ipList.addAll(getIpList(ip));
        }
        return ipList;
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
     * 计算反掩码   255.255.255.0  ==》 0.0.0.255
     *
     * @param mask 掩码255.255.255.0
     * @return 反掩码
     */
    public static String getAntiMask(String mask) {
        if ("0".equals(mask) || "0.0.0.0".equals(mask)) {
            return "255.255.255.255";
        }
        int int_mask = subMaskToSubNum(mask);
        return getAntiMask(int_mask);
    }

    /**
     * 计算反掩码   24  ==》 0.0.0.255
     *
     * @param num 掩码位数
     * @return 反掩码
     */
    public static String getAntiMask(int num) {
        return IPUtil.getAntiMask(num);
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