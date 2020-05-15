package org.libvirt;

import org.libvirt.event.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

public final class TestJavaBindings extends TestCase {
    final int UUIDArray[] = { Integer.decode("0x00"), Integer.decode("0x4b"), Integer.decode("0x96"), Integer.decode("0xe1"),
            Integer.decode("0x2d"), Integer.decode("0x78"), Integer.decode("0xc3"), Integer.decode("0x0f"),
            Integer.decode("0x5a"), Integer.decode("0xa5"), Integer.decode("0xf0"), Integer.decode("0x3c"),
            Integer.decode("0x87"), Integer.decode("0xd2"), Integer.decode("0x1e"), Integer.decode("0x67") };

    private Connect conn;

    static {
        // do this once for each JVM instance, before connecting
        try {
            Library.initEventLoop();
        } catch (LibvirtException e) {
            // XXX warn
        }
    }

    protected void setUp() throws LibvirtException {
        conn = new Connect("test:///default", false);
    }

    protected void tearDown() throws LibvirtException {
        conn.close();
        conn = null;
    }

    public void testConnectionErrorCallback() throws LibvirtException {
        DummyErrorCallback cb = new DummyErrorCallback();
        conn.setConnectionErrorCallback(cb);

        try {
            conn.domainDefineXML("fail, miserably");
            fail("LibvirtException expected");
        } catch (LibvirtException e) {} // ignore

        assertTrue("Error callback was not called", cb.error);
    }

    public void testConnection() throws Exception {
        assertEquals("conn.getType()", "TEST", conn.getType());
        assertEquals("conn.getURI()", "test:///default", conn.getURI());
        assertEquals("conn.getMaxVcpus(xen)", 32, conn.getMaxVcpus("xen"));
        assertNotNull("conn.getHostName()", conn.getHostName());
        assertNotNull("conn.getCapabilities()", conn.getCapabilities());
        assertTrue("conn.getLibVersion() > 6000", conn.getLibVersion() > 6000);
        assertEquals("conn.getVersion()", 2, conn.getVersion());
        assertTrue("conn.isAlive", conn.isAlive());
        assertTrue("conn.isEncrypted", conn.isEncrypted() == 0);
        assertTrue("conn.isSecure", conn.isSecure() == 1);        
    }

    /*
     * Excercise the listCapabilities method of the Device class.
     */
    public void testDeviceListCapabilities() throws Exception {
        Device dev = this.conn.deviceLookupByName("computer");
        String[] caps = dev.listCapabilities();

        // check that all caps are non-empty strings
        for (String c: caps) {
            assertNotNull("capability is null", c);
            assertFalse("capability is empty", c.isEmpty());
        }
    }

    public void testNodeInfo() throws Exception {
        NodeInfo nodeInfo = conn.nodeInfo();
        assertEquals("nodeInfo.model", "i686", nodeInfo.model);
        assertEquals("nodeInfo.memory", 3145728, nodeInfo.memory);
        assertEquals("nodeInfo.cpus", 16, nodeInfo.cpus);
        assertEquals("nodeInfo.nodes", 2, nodeInfo.nodes);
        assertEquals("nodeInfo.sockets", 2, nodeInfo.sockets);
        assertEquals("nodeInfo.cores", 2, nodeInfo.cores);
        assertEquals("nodeInfo.threads", 2, nodeInfo.threads);
        // These are basically no-exception tests
        assertTrue("conn.getCellsFreeMemory", conn.getCellsFreeMemory(0, 10) > 0);
        assertTrue("conn.isConnectd", conn.isConnected());
        // Test Hypervisor does not support this.
        // assertTrue("conn.getFreeMemory", conn.getFreeMemory() > 0);
    }

    public void testNetworkCreate() throws Exception {
        Network network1 = conn.networkCreateXML("<network>" + "  <name>createst</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e68</uuid>" + "  <bridge name='createst'/>"
                + "  <forward dev='eth0'/>" + "  <ip address='192.168.66.1' netmask='255.255.255.0'>" + "    <dhcp>"
                + "      <range start='192.168.66.128' end='192.168.66.253'/>" + "    </dhcp>" + "  </ip>"
                + "</network>");
        Network network2 = conn.networkDefineXML("<network>" + "  <name>deftest</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>" + "  <bridge name='deftest'/>"
                + "  <forward dev='eth0'/>" + "  <ip address='192.168.88.1' netmask='255.255.255.0'>" + "    <dhcp>"
                + "      <range start='192.168.88.128' end='192.168.88.253'/>" + "    </dhcp>" + "  </ip>"
                + "</network>");
        assertEquals("Number of networks", 2, conn.numOfNetworks());
        assertEquals("Number of listed networks", 2, conn.listNetworks().length);
        assertEquals("Number of defined networks", 1, conn.numOfDefinedNetworks());
        assertEquals("Number of listed defined networks", 1, conn.listDefinedNetworks().length);
        assertTrue("Network1 should not be persistent", network1.isPersistent() == 0);
        assertTrue("Network1 should not be active", network1.isActive() == 1);        
        assertTrue("Network2 should be active", network2.isActive() == 0);            
        this.validateNetworkData(network2);
        this.validateNetworkData(conn.networkLookupByName("deftest"));
        this.validateNetworkData(conn.networkLookupByUUID(UUIDArray));
        this.validateNetworkData(conn.networkLookupByUUIDString("004b96e1-2d78-c30f-5aa5-f03c87d21e67"));
        this.validateNetworkData(conn.networkLookupByUUID(UUID.fromString("004b96e1-2d78-c30f-5aa5-f03c87d21e67")));
        // this should throw an exception
        try {
            network1.create();
            fail("LibvirtException expected");
        } catch (LibvirtException e) {
            // eat it
        }
    }

    public void validateNetworkData(Network network) throws Exception {
        assertEquals("network.getName()", "deftest", network.getName());
        assertEquals("network.getBridgeName()", "deftest", network.getBridgeName());
        assertEquals("network.getUUIDString()", "004b96e1-2d78-c30f-5aa5-f03c87d21e67", network.getUUIDString());
        assertFalse("network.getAutostart()", network.getAutostart());
        assertNotNull("network.getConnect()", network.getConnect());
        assertNotNull("network.getUUID()", network.getUUID());
        assertNotNull("network.getXMLDesc()", network.getXMLDesc(0));
        // TODO Figure out why this crashes in Eclipse.
        // assertNotNull(Connect.connectionForNetwork(network));
        // assertTrue(Connect.connectionForNetwork(network) !=
        // network.getConnect());
    }

    public void testDomainCreate() throws Exception {
        Domain dom1 = conn.domainDefineXML("<domain type='test' id='2'>" + "  <name>deftest</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>" + "  <memory>8388608</memory>"
                + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
                + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>");

        Domain dom2 = conn.domainCreateLinux("<domain type='test' id='3'>" + "  <name>createst</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>" + "  <memory>8388608</memory>"
                + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
                + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>", 0);
        UUID dom2UUID = UUID.fromString("004b96e1-2d78-c30f-5aa5-f03c87d21e67");

        assertEquals("Number of domains", 2, conn.numOfDomains());
        assertEquals("Number of listed domains", 2, conn.listDomains().length);
        assertEquals("Number of defined domains", 1, conn.numOfDefinedDomains());
        assertEquals("Number of listed defined domains", 1, conn.listDefinedDomains().length);
        assertTrue("Domain1 should be persistent", dom1.isPersistent() == 1);
        assertTrue("Domain1 should not be active", dom1.isActive() == 0);        
        assertTrue("Domain2 should be active", dom2.isActive() == 1);              
        this.validateDomainData(dom2);
        this.validateDomainData(conn.domainLookupByName("createst"));
        this.validateDomainData(conn.domainLookupByUUID(UUIDArray));
        this.validateDomainData(conn.domainLookupByUUIDString("004b96e1-2d78-c30f-5aa5-f03c87d21e67"));
        this.validateDomainData(conn.domainLookupByUUID(UUID.fromString("004b96e1-2d78-c30f-5aa5-f03c87d21e67")));
        assertEquals("Domain is not equal to Domain retrieved by lookup",
                     dom2,
                     conn.domainLookupByUUID(dom2.getUUID()));
    }

    private void validateDomainData(Domain dom) throws Exception {
        assertEquals("dom.getName()", "createst", dom.getName());
        assertEquals("dom.getMaxMemory()", 8388608, dom.getMaxMemory());
        // Not supported by the test driver
        // assertEquals("dom.getMaxVcpus()", 2, dom2.getMaxVcpus()) ;
        assertEquals("dom.getOSType()", "linux", dom.getOSType());
        assertEquals("dom.getUUIDString()", "004b96e1-2d78-c30f-5aa5-f03c87d21e67", dom.getUUIDString());
        assertFalse("dom.getAutostart()", dom.getAutostart());
        assertNotNull("dom.getConnect()", dom.getConnect());
        assertNotNull("dom.getUUID()", dom.getUUID());
        assertNotNull("dom.getXMLDesc()", dom.getXMLDesc(0));
        assertNotNull("dom.getID()", dom.getID());

        // Execute the code Iterate over the parameters the easy way
        for (SchedParameter c : dom.getSchedulerParameters()) {
            System.out.println(c.getTypeAsString() + ":" + c.field + ":" + c.getValueAsString());
        }

        dom.getSchedulerParameters() ;
        
        SchedUintParameter[] pars = new SchedUintParameter[1];
        pars[0] = new SchedUintParameter();
        pars[0].field = "weight";
        pars[0].value = 100;
        dom.setSchedulerParameters(pars);
        
        dom.getSchedulerParameters() ;        
    }

    public void testInterfaces() throws Exception {
        assertEquals("numOfInterfaces:", 1, conn.numOfInterfaces());
        assertEquals("numOfInterfaces:", 0, conn.numOfDefinedInterfaces());
        assertEquals("listDefinedInterfaces:", "eth1", conn.listInterfaces()[0]);
        Interface virtInt = conn.interfaceLookupByName("eth1");
        assertNotNull(virtInt);
        assertEquals("virtInterfaceGetName", "eth1", virtInt.getName());
        assertEquals("virtInterfaceGetMACString", "aa:bb:cc:dd:ee:ff", virtInt.getMACString());
        assertNotNull("virtInterfaceGetXMLDesc", virtInt.getXMLDescription(0));
        assertTrue("virInterfaceIsActive", virtInt.isActive() == 1);         
        System.out.println(virtInt.getXMLDescription(0));

        String newXML = "<interface type='ethernet' name='eth2'>" + "<start mode='onboot'/>"
                + "<mac address='aa:bb:cc:dd:ee:fa'/>" + "<mtu size='1492'/>" + "<protocol family='ipv4'>"
                + "<ip address='192.167.0.5' prefix='24'/>" + "<route gateway='192.167.0.1'/>" + "</protocol>"
                + "</interface>";
        Interface virtInt2 = conn.interfaceDefineXML(newXML);
        assertNotNull(virtInt2);
        assertEquals("virtInterfaceGetName", "eth2", virtInt2.getName());
        assertEquals("virtInterfaceGetMACString", "aa:bb:cc:dd:ee:fa", virtInt2.getMACString());
        assertNotNull("virtInterfaceGetXMLDesc", virtInt2.getXMLDescription(0));
        virtInt2.undefine();
        virtInt2.free();
    }

    public void testAccessAfterClose() throws Exception {
        conn.close();
        assertTrue("conn.isConnected should be false", !conn.isConnected());
        LibvirtException virException = null;
        try {
            conn.getHostName();
        } catch (LibvirtException e) {
            virException = e;
        }
        assertNotNull(virException);
    }
    
    public void testStoragePool() throws Exception {
        StoragePool pool1 = conn.storagePoolDefineXML("<pool type='dir'>"
                + "  <name>pool1</name>"
                + "  <target>"
                + "    <path>/pool1</path>"
                + "  </target>"
                + "  <uuid>004c96e1-2d78-c30f-5aa5-f03c87d21e67</uuid>"
                + "</pool>", 0) ;
        StoragePool defaultPool = conn.storagePoolLookupByName("default-pool");
        assertEquals("numOfStoragePools:", 1, conn.numOfStoragePools());
        assertEquals("numOfDefinedStoragePools:", 1, conn.numOfDefinedStoragePools());        
        assertNotNull("The pool should not be null", pool1);
        assertNotNull("The default pool should not be null", defaultPool);   
        assertEquals("The names should match", defaultPool.getName(), "default-pool");
        assertEquals("The uids should match", pool1.getUUIDString(), "004c96e1-2d78-c30f-5aa5-f03c87d21e67"); 
        assertTrue("pool1 should be persistent", pool1.isPersistent() == 1);
        assertTrue("pool1 should not be active", pool1.isActive() == 0);        
        assertTrue("Domain2 should be active", defaultPool.isActive() == 1);         
    }

    public void testDomainEvents() throws Exception {
        final List<DomainEventType> events = new ArrayList<DomainEventType>();
        final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Library.runEventLoop();
                    } catch (LibvirtException e) {
                        fail("LibvirtException was thrown: " + e);
                    } catch (InterruptedException e) {
                    }
                }
            };
        t.setDaemon(true);
        t.start();

        LifecycleListener listener = new LifecycleListener() {
            @Override
            public int onLifecycleChange(Domain d, DomainEvent e)
            {
                events.add(e.getType());

                return 0;
            }
        };
        try {
            conn.addLifecycleListener(listener);

            Domain dom = conn.domainDefineXML("<domain type='test' id='2'>" + "  <name>deftest</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>" + "  <memory>8388608</memory>"
                + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
                + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>");

            dom.create();
            dom.suspend();
            dom.resume();
            dom.destroy();
            dom.undefine();

            // wait until (presumably) all events have been processed
            Thread.sleep(300);

            assertEquals(Arrays.asList(DomainEventType.DEFINED,
                                       DomainEventType.STARTED,
                                       DomainEventType.SUSPENDED,
                                       DomainEventType.RESUMED,
                                       DomainEventType.STOPPED,
                                       DomainEventType.UNDEFINED),
                         events);
        } finally {
            conn.removeLifecycleListener(listener);
            Library.stopEventLoop();
        }
    }

    public void testDomainScreenshot() throws Exception {
        long version = Library.getVersion();

        // virDomainScreenshot works since version 1.0.5 on test://
        // connections
        if (version < 1000005) {
            System.err.format("testDomainScreenshot skipped (libvirt version %d.%d.%d < 1.0.5)\n",
                              version / 1000000, version / 1000 % 1000, version % 1000);
            return;
        }

        Stream str = this.conn.streamNew(0);
        Domain dom = this.conn.domainLookupByName("test");

        assertFalse("Domain \"test\" not found", dom == null);

        String mimetype = dom.screenshot(str, 0);

        ByteBuffer bb = ByteBuffer.allocateDirect(8192);

        while (str.read(bb) != -1) // consume data
            bb.clear();

        // ensure that read() repeatedly returns -1 after EOF

        assertEquals("Stream is at EOF (1)", -1, str.read(bb));
        assertEquals("Stream is at EOF (2)", -1, str.read(bb));
        assertEquals("Stream is at EOF (3)", -1, str.read(bb));

        // ensure that a ClosedChannelException gets thrown when
        // trying to read() after closing the stream

        str.close();

        try {
            str.read(bb);
            fail("ClosedChannelException expected calling read() on a closed stream");
        } catch (ClosedChannelException expected) {
        }
    }
}
