import java.util.UUID;

import org.libvirt.*;

public class test {

    // Use this to flag errors in the test run.
    public static String FIXME = "\n<=====================================>\n<============== FIXME ================> ";

    public static void main(String[] args) {
        // Create the connection
        Connect conn = null;
        Network testNetwork = null;

        // The array below is to support the uuid lookup based on an array of
        // ints.
        // This is deprectaed, but is kept here until the code is removed.
        int UUIDArray[] = { Integer.decode("0x00"), Integer.decode("0x4b"), Integer.decode("0x96"),
                Integer.decode("0xe1"), Integer.decode("0x2d"), Integer.decode("0x78"), Integer.decode("0xc3"),
                Integer.decode("0x0f"), Integer.decode("0x5a"), Integer.decode("0xa5"), Integer.decode("0xf0"),
                Integer.decode("0x3c"), Integer.decode("0x87"), Integer.decode("0xd2"), Integer.decode("0x1e"),
                Integer.decode("0x67") };

        /*
         * Test Authentication
         * 
         * If you want to test authentication, uncomment the try catch block and
         * one of the conn lines. You need to configure your libvirtd for
         * remote/authenticated connections before this will work.
         */
        /*
         * ConnectAuth defaultAuth = new ConnectAuthDefault();
         * 
         * try{ conn = new Connect("test+tcp://localhost/default", defaultAuth,
         * 0); conn = new Connect("qemu+tcp://localhost/system", defaultAuth,
         * 0); conn = new Connect("test:///default", defaultAuth, 0);
         * System.out.println("Encrypted connection successful!"); } catch
         * (LibvirtException e){ System.out.println("exception caught:"+e);
         * System.out.println(e.getError()); }
         */

        try {
            conn = new Connect("test:///default", false);
        } catch (LibvirtException e) {
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }
        try {
            // Check nodeinfo
            NodeInfo nodeInfo = conn.nodeInfo();
            System.out.println("virNodeInfo.model:" + nodeInfo.model);
            System.out.println("virNodeInfo.memory:" + nodeInfo.memory);
            System.out.println("virNodeInfo.cpus:" + nodeInfo.cpus);
            System.out.println("virNodeInfo.nodes:" + nodeInfo.nodes);
            System.out.println("virNodeInfo.sockets:" + nodeInfo.sockets);
            System.out.println("virNodeInfo.cores:" + nodeInfo.cores);
            System.out.println("virNodeInfo.threads:" + nodeInfo.threads);

            // Exercise the information getter methods
            System.out.println("getHostName:" + conn.getHostName());
            System.out.println("getCapabilities:" + conn.getCapabilities());
            System.out.println("getMaxVcpus:" + conn.getMaxVcpus("xen"));
            System.out.println("getType:" + conn.getType());
            System.out.println("getURI:" + conn.getURI());
            System.out.println("getVersion:" + conn.getVersion());
            System.out.println("getLibVirVersion:" + conn.getLibVirVersion());

            // By default, there are 1 created and 0 defined networks

            // Create a new network to test the create method
            System.out.println("conn.networkCreateXML: "
                    + conn.networkCreateXML("<network>" + "  <name>createst</name>"
                            + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e68</uuid>" + "  <bridge name='createst'/>"
                            + "  <forward dev='eth0'/>" + "  <ip address='192.168.66.1' netmask='255.255.255.0'>"
                            + "    <dhcp>" + "      <range start='192.168.66.128' end='192.168.66.253'/>"
                            + "    </dhcp>" + "  </ip>" + "</network>"));

            // Same for the define method
            System.out.println("conn.networkDefineXML: "
                    + conn.networkDefineXML("<network>" + "  <name>deftest</name>"
                            + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>" + "  <bridge name='deftest'/>"
                            + "  <forward dev='eth0'/>" + "  <ip address='192.168.88.1' netmask='255.255.255.0'>"
                            + "    <dhcp>" + "      <range start='192.168.88.128' end='192.168.88.253'/>"
                            + "    </dhcp>" + "  </ip>" + "</network>"));

            // We should have 2:1 but it shows up 3:0 hopefully a bug in the
            // test driver
            System.out.println("numOfDefinedNetworks:" + conn.numOfDefinedNetworks());
            System.out.println("listDefinedNetworks:" + conn.listDefinedNetworks());
            for (String c : conn.listDefinedNetworks())
                System.out.println("	-> " + c);
            System.out.println("numOfNetworks:" + conn.numOfNetworks());
            System.out.println("listNetworks:" + conn.listNetworks());
            for (String c : conn.listNetworks())
                System.out.println("	-> " + c);

            // Look at the interfaces
            // TODO Post 0.5.1
            // System.out.println("numOfInterfaces:" + conn.numOfInterfaces());
            // System.out.println("listDefinedInterfaces:" +
            // conn.listInterfaces());
            // for(String c: conn.listInterfaces())
            // System.out.println("    -> "+c);

            // Define a new Domain
            System.out.println("conn.domainDefineXML:"
                    + conn.domainDefineXML("<domain type='test' id='2'>" + "  <name>deftest</name>"
                            + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>" + "  <memory>8388608</memory>"
                            + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>"
                            + "  <on_reboot>restart</on_reboot>" + "  <on_poweroff>destroy</on_poweroff>"
                            + "  <on_crash>restart</on_crash>" + "</domain>"));

            System.out.println("conn.domainCreateLinux:"
                    + conn.domainCreateLinux("<domain type='test' id='3'>" + "  <name>createst</name>"
                            + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>" + "  <memory>8388608</memory>"
                            + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>"
                            + "  <on_reboot>restart</on_reboot>" + "  <on_poweroff>destroy</on_poweroff>"
                            + "  <on_crash>restart</on_crash>" + "</domain>", 0));

            // Domain enumeration stuff
            System.out.println("numOfDefinedDomains:" + conn.numOfDefinedDomains());
            System.out.println("listDefinedDomains:" + conn.listDefinedDomains());
            for (String c : conn.listDefinedDomains())
                System.out.println("	" + c);
            System.out.println("numOfDomains:" + conn.numOfDomains());
            System.out.println("listDomains:" + conn.listDomains());
            for (int c : conn.listDomains())
                System.out.println("	-> " + c);

        } catch (LibvirtException e) {
            System.out.println(FIXME);
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }

        // Network Object

        try {
            // Choose one, they should have the exact same effect
            testNetwork = conn.networkLookupByName("deftest");
            System.out.println("networkLookupByName: " + testNetwork.getName());
            testNetwork = conn.networkLookupByUUID(UUIDArray);
            System.out.println("networkLookupByUUID: " + testNetwork.getName());
            testNetwork = conn.networkLookupByUUIDString("004b96e1-2d78-c30f-5aa5-f03c87d21e67");
            System.out.println("networkLookupByUUIDString: " + testNetwork.getName());
            testNetwork = conn.networkLookupByUUID(UUID.fromString("004b96e1-2d78-c30f-5aa5-f03c87d21e67"));
            System.out.println("networkLookupByUUID (Java UUID): " + testNetwork.getName());

            // Exercise the getter methods on the default network
            System.out.println("virNetworkGetXMLDesc:" + testNetwork.getXMLDesc(0));
            System.out.println("virNetworkGetAutostart:" + testNetwork.getAutostart());
            System.out.println("virNetworkGetBridgeName:" + testNetwork.getBridgeName());
            System.out.println("virNetworkGetName:" + testNetwork.getName());
            System.out.println("virNetworkGetUUID:" + testNetwork.getUUID() + " ");
            for (int c : testNetwork.getUUID())
                System.out.print(String.format("%02x", c));
            System.out.println();
            System.out.println("virNetworkGetName:" + testNetwork.getUUIDString());

            // Destroy and create the network
            System.out.println("virNetworkDestroy:");
            testNetwork.destroy();
            System.out.println("virNetworkCreate:");
            testNetwork.create();
        } catch (LibvirtException e) {
            System.out.println(FIXME);
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }
        // This should raise an excpetion
        try {
            System.out.println("virNetworkCreate (should error):");
            testNetwork.create();
        } catch (LibvirtException e) {
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }

        // Domain stuff

        try {
            // Domain lookup
            Domain testDomain = conn.domainLookupByID(1);
            System.out.println("domainLookupByID: " + testDomain.getName());
            testDomain = conn.domainLookupByName("test");
            System.out.println("domainLookupByName: " + testDomain.getName());
            testDomain = conn.domainLookupByUUIDString("004b96e1-2d78-c30f-5aa5-f03c87d21e67");
            System.out.println("domainLookupByUUIDString: " + testDomain.getName());
            testDomain = conn.domainLookupByUUID(UUIDArray);
            System.out.println("domainLookupByUUID: " + testDomain.getName());
            testDomain = conn.domainLookupByUUID(UUID.fromString("004b96e1-2d78-c30f-5aa5-f03c87d21e67"));
            System.out.println("domainLookupByUUID (JAVA UID): " + testDomain.getName());

            // Exercise the getter methods on the default domain
            System.out.println("virDomainGetXMLDesc:" + testDomain.getXMLDesc(0));
            System.out.println("virDomainGetAutostart:" + testDomain.getAutostart());
            System.out.println("virDomainGetConnect:" + testDomain.getConnect());
            System.out.println("virDomainGetID:" + testDomain.getID());
            System.out.println("virDomainGetInfo:" + testDomain.getInfo());
            System.out.println("virDomainGetMaxMemory:" + testDomain.getMaxMemory());
            // Should fail, test driver does not support it
            try {
                System.out.println("virDomainGetMaxVcpus:" + testDomain.getMaxVcpus());
                System.out.println(FIXME);
            } catch (LibvirtException e) {

            }
            System.out.println("virDomainGetName:" + testDomain.getName());
            System.out.println("virDomainGetOSType:" + testDomain.getOSType());
            System.out.println("virDomainGetSchedulerType:" + testDomain.getSchedulerType());
            System.out.println("virDomainGetSchedulerParameters:" + testDomain.getSchedulerParameters());
            // Iterate over the parameters the painful way
            for (SchedParameter c : testDomain.getSchedulerParameters()) {
                if (c instanceof SchedIntParameter)
                    System.out.println("Int:" + ((SchedIntParameter) c).field + ":" + ((SchedIntParameter) c).value);
                if (c instanceof SchedUintParameter)
                    System.out.println("Uint:" + ((SchedUintParameter) c).field + ":" + ((SchedUintParameter) c).value);
                if (c instanceof SchedLongParameter)
                    System.out.println("Long:" + ((SchedLongParameter) c).field + ":" + ((SchedLongParameter) c).value);
                if (c instanceof SchedUlongParameter)
                    System.out.println("Ulong:" + ((SchedUlongParameter) c).field + ":"
                            + ((SchedUlongParameter) c).value);
                if (c instanceof SchedDoubleParameter)
                    System.out.println("Double:" + ((SchedDoubleParameter) c).field + ":"
                            + ((SchedDoubleParameter) c).value);
                if (c instanceof SchedBooleanParameter)
                    System.out.println("Boolean:" + ((SchedBooleanParameter) c).field + ":"
                            + ((SchedBooleanParameter) c).value);
            }
            // Iterate over the parameters the easy way
            for (SchedParameter c : testDomain.getSchedulerParameters()) {
                System.out.println(c.getTypeAsString() + ":" + c.field + ":" + c.getValueAsString());
            }

            // test setting a scheduled parameter
            SchedUintParameter[] pars = new SchedUintParameter[1];
            pars[0] = new SchedUintParameter();
            pars[0].field = "weight";
            pars[0].value = 100;
            testDomain.setSchedulerParameters(pars);

            System.out.println("virDomainGetUUID:" + testDomain.getUUID());
            for (int c : testDomain.getUUID())
                System.out.print(String.format("%02x", c));
            System.out.println();
            System.out.println("virDomainGetUUIDString:" + testDomain.getUUIDString());
            // Should fail, unimplemented in test driver
            // System.out.println("virDomainGetVcpusInfo:" +
            // testDomain.getVcpusInfo());
            // Same as above
            // System.out.println("virDomainGetVcpusCpuMap:" +
            // testDomain.getVcpusCpuMaps());
            // Should test pinVcpu, when we test with real xen
            // Here
            // Attach default network to test domain
            // System.out.println("virDomainGetVcpusCpuMap:" +
            // testDomain.getVcpusCpuMaps());

            // Should test interfacestats and blockstats with real xen

            // Close the connection

            conn.close();
        } catch (LibvirtException e) {
            System.out.println(FIXME);
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }

        try {
            // We should get an exception, not a crash
            System.out.println(conn.getHostName());
        } catch (LibvirtException e) {
            System.out.println("exception caught:" + e);
            System.out.println(e.getError());
        }
        System.out.println("Fini!");
    }

}
