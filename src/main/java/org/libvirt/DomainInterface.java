package org.libvirt;

import org.libvirt.jna.virDomainInterface;
import org.libvirt.jna.virDomainIpAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class DomainInterface {
    public static class InterfaceAddress {
        public final InetAddress address;
        public final int prefixLength;

        public InterfaceAddress(InetAddress addr, int prefixLength) {
            this.address = addr;
            this.prefixLength = prefixLength;
        }

        public InterfaceAddress(virDomainIpAddress addr) {
            switch (addr.type) {
                case Network.IP_Addr_Type.IP_ADDR_TYPE_IPV4:
                case Network.IP_Addr_Type.IP_ADDR_TYPE_IPV6:
                    try {
                        address = InetAddress.getByName(addr.addr);
                    } catch (UnknownHostException e) {
                        throw new IllegalArgumentException("Invalid IP address '" + addr.addr + "'");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported address type " + addr.type);
            }
            prefixLength = addr.prefix;
        }
    }

    public String name;
    public String hwAddr;
    public Collection<InterfaceAddress> addrs;

    public DomainInterface() {}

    public DomainInterface(virDomainInterface vdi) {
        name = vdi.name;
        hwAddr = vdi.hwaddr;
        addrs = vdi.naddrs == 0 ? Collections.emptyList()
                    : Arrays.stream((virDomainIpAddress[]) vdi.addrs.toArray(vdi.naddrs))
                            .map(InterfaceAddress::new)
                            .collect(Collectors.toList());
    }
}
