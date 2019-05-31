package com.commons.common.utils.ipv6;

import com.commons.common.utils.IPUtil;
import com.commons.common.utils.IPv6Util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import static com.commons.common.utils.ipv6.IPv6Address.fromString;

public class Examples {
    public static void main(String[] args) throws Exception {

//        List<String> list = null;
//        list = IPv6Util.getIpList("21DA:D3:0:2F3B:2AA:FF:FE28:9C5A/126");
//        System.out.println(list);
//        list = IPv6Util.changeIPRangesToSubNets(1, true, "2001:0:9d38:953c:c72:564e:221d:fa1-2001:0:9d38:953c:c72:564e:221d:fd1", "2001:0:9d38:953c:c72:564e:221d:fd2", "2001:0:9d38:953c:c72:564e:221d:fd3", "2001:0:9d38:953c:c72:564e:221d:fd4", "2001:0:9d38:953c:c72:564e:221d:fd5");
//        System.out.println(list);
//        String[] sourceRanges = new String[]{"2001:0:9d38:953c:c72:564e:221d:fd1", "2001:0:9d38:953c:c72:564e:221d:fd2-2001:0:9d38:953c:c72:564e:221d:fd4"};
//        String[] toCheckRanges = new String[]{"2001:0:9d38:953c:c72:564e:221d:fd1", "2001:0:9d38:953c:c72:564e:221d:fd2-2001:0:9d38:953c:c72:564e:221d:fd3"};
//        boolean result = IPv6Util.checkIPRangeIncluded(sourceRanges, toCheckRanges);
//        System.out.println(result);
//        result = IPv6Util.checkIPRangeIntersected("2001:0:9d38:953c:c72:564e:221d:fd2", "2001:0:9d38:953c:c72:564e:221d:fd1-2001:0:9d38:953c:c72:564e:221d:fd5");
//        System.out.println(result);
//
//        System.out.println(IPv6Util.getBeginAddress("21DA:D3:0:2F3B:2AA:FF:FE28:9C5A"));
//        System.out.println(IPv6Util.getEndAddress("2001:0:9d38:953c:c72:564e:221d:fd2"));
//        System.out.println(IPv6Util.getBeginSubnetAddress(126,"2001:0:9d38:953c:c72:564e:221d:fd2"));
//        System.out.println(IPv6Util.getEndSubnetAddress(126,"2001:0:9d38:953c:c72:564e:221d:fd2"));
//        list = IPv6Util.getIpListByIpMask("2001:0:9d38:953c:c72:564e:221d:fd2",126);
//        System.out.println(list);
//        System.out.println(IPv6Util.getRangeIPCount("2001:0:9d38:953c:c72:564e:221d:fd2-2001:0:9d38:953c:c72:564e:221d:fd5"));
//        System.out.println(IPv6Util.getNetworkAddress("2001:0:9d38:953c:c72:564e:221d:fd2"));
//        System.out.println(IPv6Util.getNetworkAddress("2001:0:9d38:953c:c72:564e:221d:fd2","126"));
//        list = IPv6Util.getIPRangeDisjoint(sourceRanges, toCheckRanges);
//        System.out.println(list);
//        list = IPv6Util.getIPRangeIntersection(sourceRanges, toCheckRanges);
//        System.out.println(list);
//        System.out.println(IPv6Address.fromString("2001:0:9d38:953c:c72:564e:221d:fd2").toBigInteger().longValue());
//        System.out.println(IPv6Address.fromString("2001:0:9d38:953c:c72:564e:221d:fd2").toBigInteger().doubleValue());
//        System.out.println( new BigDecimal("4.25404882106334E37").toBigInteger());
//        System.out.println(IPv6AddressHelpers.ipToNumber("2001:0:9d38:953c:c72:564e:221d:fd2"));
//        System.out.println(IPv6Util.getBeginAddress("2001:0:9d38:953c:c72:564e:221d:fd2/126"));
//        System.out.println(IPv6Address.fromString("2001::ffff:ffff").toBigInteger());
//        System.out.println(IPv6Address.fromString("2001::ffff:ffff").toBigInteger().longValue());
//        System.out.println(IPv6Address.fromString("2001:0:9d38:953c:c72:564e:221d:fd2").toLongString());
//        System.out.println(IPv6AddressHelpers.ipToNumber("2001:0000:9d38:953c:0c72:564e:221d:0fd2"));
//        System.out.println(IPv6Address.fromBigInteger(new BigInteger("42540488210633393888707417617964273618")).toLongString());
//        IPv6AddressPool pool = IPv6AddressPool.fromRangeAndSubnet(IPv6AddressRange.fromFirstAndLast(fromString("::1"),
//                fromString("::5")),
//                new IPv6NetworkMask(128));
//        System.out.println(pool.isExhausted());
//        pool.deAllocate(IPv6Network.fromAddressAndMask(fromString("::8"), IPv6NetworkMask.fromPrefixLength(128)));
//        for (int i = 1; i <= 8; i++) {
////            pool = pool.allocate(IPv6Network.fromAddressAndMask(fromString("::" + i), IPv6NetworkMask.fromPrefixLength(128)));
//            System.out.println(pool.isFree(IPv6Network.fromAddressAndMask(fromString("::" + i), IPv6NetworkMask.fromPrefixLength(128))));
//        }
//        System.out.println(pool.isExhausted());

//        InetAddress a = InetAddress.getByName("2001:0:9d38:953c:c72:564e:221d:fd5");
//        byte[] bytes = a.getAddress();
//        System.out.println( new BigInteger(1, bytes));
//        IPv6Address a1= IPv6Address.fromString("2001:0:9d38:953c:c72:564e:221d:fd5");
//        IPv6Network n1=IPv6Network.fromString("2001:0:9d38:953c:c72:564e:221d:fd5/64");
//        IPv6Network n2= IPv6Network.fromTwoAddresses(IPv6Address.fromString("2001:0:9d38:953c:c72:564e:221d:fd5"),IPv6Address.fromString("2001:0:9d38:953c::6:4a0b"));
//        System.out.println(n2.getFirst());
//        System.out.println(n2.getLast());
//        System.out.println(n2.size());
//        System.out.println( a1.toBigInteger());
//        System.out.println(a1);
//        System.out.println(n1.getFirst());
//        System.out.println(n1.getLast());
//        System.out.println("====");
//        IPv6AddressRange r1=IPv6AddressRange.fromFirstAndLast(n1.getFirst(),n1.getLast());
//        System.out.println(r1.contains(IPv6Address.fromString("2001:0:9d38:953c::6:4a0b")));
//        for (IPv6Address iPv6Address : r1) {
//            System.out.println(iPv6Address);
//        }
//        System.out.println(n1.getNetmask());
//        System.out.println(n1.contains(a1));
//        Examples examples=new Examples();
//        final IPv6AddressPool pool = IPv6AddressPool.fromRangeAndSubnet(
//                IPv6AddressRange.fromFirstAndLast(IPv6Address.fromString("fe80::226:2dff:fefa:0"),
//                        IPv6Address.fromString("fe80::226:2dff:fefa:ffff")),
//                IPv6NetworkMask.fromPrefixLength(120));
    }

    public void ipAddressConstruction() {

        final IPv6Address iPv6Address = fromString("fe80::226:2dff:fefa:cd1f");
        final IPv6Address iPv4MappedIPv6Address = fromString("::ffff:192.168.0.1");
    }


    public void ipAddressAdditionAndSubtraction() {
        final IPv6Address iPv6Address = fromString("fe80::226:2dff:fefa:cd1f");
        final IPv6Address next = iPv6Address.add(1);
        final IPv6Address previous = iPv6Address.subtract(1);
        System.out.println(next.toString()); // prints fe80::226:2dff:fefa:cd20
        System.out.println(previous.toString()); // prints fe80::226:2dff:fefa:cd1e
    }


    public void ipAddressRangeConstruction() {
        final IPv6AddressRange range = IPv6AddressRange.fromFirstAndLast(fromString("fe80::226:2dff:fefa:cd1f"),
                fromString("fe80::226:2dff:fefa:ffff"));
        System.out.println(range.contains(fromString("fe80::226:2dff:fefa:dcba"))); // prints true
    }


    public void ipAddressRangeConvertToSubnets() {
        final IPv6AddressRange range = IPv6AddressRange.fromFirstAndLast(fromString("::1:ffcc"),
                fromString("::2:0"));

        Iterator<IPv6Network> subnetsIterator = range.toSubnets();
        while (subnetsIterator.hasNext())
            System.out.println(subnetsIterator.next());

        // prints ::1:ffcc/126 ::1:ffd0/124 ::1:ffe0/123 ::2:0/128 (i.e. the minimal set of networks that define the original range)
    }


    public void ipNetworkConstruction() {
        final IPv6AddressRange range = IPv6AddressRange.fromFirstAndLast(fromString("fe80::226:2dff:fefa:0"),
                fromString("fe80::226:2dff:fefa:ffff"));
        final IPv6Network network = IPv6Network.fromString("fe80::226:2dff:fefa:0/112");
        System.out.println(range.equals(network)); // prints true
    }


    public void ipNetworkCalculation() {
        final IPv6Network strangeNetwork = IPv6Network.fromString("fe80::226:2dff:fefa:cd1f/43");

        System.out.println(strangeNetwork.getFirst()); // prints fe80::
        System.out.println(strangeNetwork.getLast()); // prints fe80:0:1f:ffff:ffff:ffff:ffff:ffff
        System.out.println(strangeNetwork.getNetmask().asPrefixLength()); // prints 43
        System.out.println(strangeNetwork.getNetmask().asAddress()); // prints ffff:ffff:ffe0::
    }


    public void ipNetworkSplitInSmallerSubnets() {
        final IPv6Network network = IPv6Network.fromString("1:2:3:4:5:6:7:0/120");

        Iterator<IPv6Network> splits = network.split(IPv6NetworkMask.fromPrefixLength(124));
        while (splits.hasNext())
            System.out.println(splits.next());

        // prints 1:2:3:4:5:6:7:0/124, 1:2:3:4:5:6:7:10/124, 1:2:3:4:5:6:7:20/124, ... until 1:2:3:4:5:6:7:f0/124 (16 in total)
    }


    public void ipNetworkNotationChoices() {
        IPv6Network prefixLengthNotation = IPv6Network.fromString("::1/16");
        IPv6Network addressNotation =
                IPv6Network.fromAddressAndMask(fromString("::"), IPv6NetworkMask.fromAddress(fromString("ffff::")));
        System.out.println(prefixLengthNotation.equals(addressNotation)); // prints true
        System.out.println(prefixLengthNotation); // prints ::/16
        System.out.println(prefixLengthNotation.getFirst() + "/" + prefixLengthNotation.getNetmask().asAddress()); // prints ::/ffff::
    }

    public void ipNetworkMaskConstruction() {
        final IPv6NetworkMask slash40Network = IPv6NetworkMask.fromPrefixLength(40);
        System.out.println(slash40Network.asAddress()); // prints ffff:ffff:ff00::
        System.out.println(slash40Network.asPrefixLength()); // prints 40

        final IPv6NetworkMask slash40NetworkConstructedFromAddressNotation = IPv6NetworkMask.fromAddress(
                fromString("ffff:ffff:ff00::"));
        System.out.println(slash40Network.equals(slash40NetworkConstructedFromAddressNotation)); // prints true

        final IPv6NetworkMask invalidNetworkMask = IPv6NetworkMask.fromAddress(fromString("0fff::")); // fails
    }


    public void ipAddressNetworkMasking() {
        final IPv6Address iPv6Address = fromString("fe80::226:2dff:fefa:cd1f");

        final IPv6Address masked = iPv6Address.maskWithNetworkMask(IPv6NetworkMask.fromPrefixLength(40));
        System.out.println(masked.toString()); // prints fe80::

        final IPv6Address maximum = iPv6Address.maximumAddressWithNetworkMask(IPv6NetworkMask.fromPrefixLength(40));
        System.out.println(maximum.toString()); // prints fe80:0:ff:ffff:ffff:ffff:ffff:ffff
    }


    public void poolExample() {

        final IPv6AddressPool pool = IPv6AddressPool.fromRangeAndSubnet(
                IPv6AddressRange.fromFirstAndLast(fromString("fe80::226:2dff:fefa:0"),
                        fromString("fe80::226:2dff:fefa:ffff")),
                IPv6NetworkMask.fromPrefixLength(120));
        System.out.println(pool.isFree(IPv6Network.fromString("fe80::226:2dff:fefa:5ff/120"))); // prints true

        final IPv6AddressPool newPool = pool.allocate(IPv6Network.fromString("fe80::226:2dff:fefa:5ff/120"));
        System.out.println(newPool.isFree(IPv6Network.fromString("fe80::226:2dff:fefa:5ff/120"))); // prints false
    }

}