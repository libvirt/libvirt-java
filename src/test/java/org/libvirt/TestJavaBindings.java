package org.libvirt;

import org.libvirt.Domain.CheckpointListFlags;
import org.libvirt.event.*;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

        dom.getSchedulerParameters();

        SchedUintParameter[] pars = new SchedUintParameter[1];
        pars[0] = new SchedUintParameter();
        pars[0].field = "weight";
        pars[0].value = 100;
        dom.setSchedulerParameters(pars);

        dom.getSchedulerParameters();

        TypedParameter[] cpuStats = dom.getCPUStats(-1, 1);
        assertEquals(3, cpuStats.length);
        assertEquals("cpu_time", cpuStats[0].field);
        assertEquals("48772617035", cpuStats[0].getValueAsString());
        assertEquals("user_time", cpuStats[1].field);
        assertEquals("5540000000", cpuStats[1].getValueAsString());
        assertEquals("system_time", cpuStats[2].field);
        assertEquals("6460000000", cpuStats[2].getValueAsString());
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

        String mimetype;
        try {
            mimetype = dom.screenshot(str, 0);
        } catch (LibvirtException ex) {
            if (ex.getMessage().contains("test-screenshot.png': No such file or directory")) {
                System.err.format("testDomainScreenshot skipped (missing png file)");
                return;
            }
            throw ex;
        }

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

    public void testDomainMetadata() throws LibvirtException {
        Domain dom = conn.domainDefineXML("<domain type='test' id='2'>" + "  <name>metatest</name>"
                + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>" + "  <memory>8388608</memory>"
                + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
                + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>");

        dom.setMetadata(Domain.MetadataType.DESCRIPTION, "a description", null, null, Domain.ModificationImpact.CURRENT);
        dom.setMetadata(Domain.MetadataType.TITLE, "a title", null, null, Domain.ModificationImpact.CURRENT);
        String xml1 = "<test><property name=\"key\">value</property></test>";
        Pattern pattern1 = Pattern.compile("<test>\\s*<property\\s+name=\"key\">value</property>\\s*</test>");
        String uri1 = "https://libvirt.org/test.rng";
        dom.setMetadata(Domain.MetadataType.ELEMENT, xml1, "pfx", uri1, Domain.ModificationImpact.CURRENT);
        String xml2 = "<test><property name=\"key2\">value2</property></test>";
        Pattern pattern2 = Pattern.compile("<test>\\s*<property\\s+name=\"key2\">value2</property>\\s*</test>");
        String uri2 = "https://libvirt.org/othertest.rng";
        dom.setMetadata(Domain.MetadataType.ELEMENT, xml2, "pfx", uri2, Domain.ModificationImpact.CURRENT);

        assertEquals("a description", dom.getMetadata(Domain.MetadataType.DESCRIPTION, null, Domain.ModificationImpact.CURRENT));
        assertEquals("a title", dom.getMetadata(Domain.MetadataType.TITLE, null, Domain.ModificationImpact.CURRENT));
        assertTrue(pattern1.matcher(dom.getMetadata(Domain.MetadataType.ELEMENT, uri1, Domain.ModificationImpact.CURRENT)).matches());
        assertTrue(pattern2.matcher(dom.getMetadata(Domain.MetadataType.ELEMENT, uri2, Domain.ModificationImpact.CURRENT)).matches());
    }

    public void testDomainInterfaceAddresses() throws LibvirtException {
        if (conn.getLibVersion() < 5004000) { return; } // earlier versions do not support the call
        Domain dom = conn.domainDefineXML("<domain type='test' id='2'>" + "  <name>ifacetest</name>"
                + "  <uuid>7814e417-b628-4e6b-bbd3-a82cb15483f2</uuid>" + "  <memory>8388608</memory>"
                + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
                + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>");
        dom.create();

        try {
            Collection<DomainInterface> ifaces = dom.interfaceAddresses(Domain.InterfaceAddressesSource.VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_LEASE, 0);
            assertNotNull(ifaces);
            assertTrue(ifaces.isEmpty());
            // Can't really test this without a live network inside the guest. :-(
        } finally {
            dom.destroy();
            dom.undefine();
        }
    }

    /**
     * Helper function to create test domains
     * @param domainName - Domain name
     * @return Domain created
     * @throws LibvirtException
     */
    private Domain createDomainToCheckpointTest(String domainName)  throws LibvirtException{
        String domainXML = "<domain type='test'>\n" +
            "  <name>" + domainName + "</name>\n" +
            "  <memory unit='MiB'>512</memory>\n" +
            "  <vcpu>1</vcpu>\n" +
            "  <os>\n" +
            "    <type arch='x86_64'>hvm</type>\n" +
            "  </os>\n" +
            "  <devices>\n" +
            "    <disk type='file' device='disk'>\n" +
            "      <driver name='qemu' type='qcow2'/>\n" +
            "      <source file='/var/lib/libvirt/images/" + domainName +"-vda.qcow2'/>\n" +
            "      <target dev='vda' bus='virtio'/>\n" +
            "    </disk>\n" +
            "    <disk type='file' device='cdrom'>\n" +
            "      <driver name='qemu' type='raw'/>\n" +
            "      <source file='/var/lib/libvirt/images/test-checkpoint-create.iso'/>\n" +
            "      <target dev='hdc' bus='ide'/>\n" +
            "      <readonly/>\n" +
            "    </disk>\n" +
            "    <console type='pty'/>\n" +
            "  </devices>\n" +
            "</domain>";

        Domain domain = conn.domainDefineXML(domainXML);
        return domain;
    }

    /**
     * Check if throw an error when try to create a checkpoint in a inactive domain
     * @throws LibvirtException
     */
    public void testDomainCheckpointCreateThrowError() throws LibvirtException {
        Domain domain = createDomainToCheckpointTest("test-vm-checkpoint-create-throw-error");
        String domainCheckpointXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<domaincheckpoint>\n" +
            "    <name>test-checkpoint-create-1</name>\n" +
            "    <disks>\n" +
            "        <disk name=\"vda\" bitmap=\"test-checkpoint\" checkpoint=\"bitmap\"/>\n" +
            "        <disk name=\"hdc\" checkpoint=\"no\"/>\n" +
            "    </disks>\n" +
            "</domaincheckpoint>\n";
        LibvirtException virException = null;
        try {
            domain.checkpointCreateXML(domainCheckpointXML, 0);
            fail("Exception should be raised because the checkpoint can not perform in a stopped domain");
        } catch(LibvirtException e) {
            virException = e;
        }
        assertNotNull(virException);
    }
    
    /**
     * Check methods to create and destroy checkpoints of a domain
     * @throws LibvirtException
     */
    public void testDomainCheckpointCreateAndDestroy() throws LibvirtException {
        Domain domain = createDomainToCheckpointTest("test-vm-checkpoint-create");
        domain.create();
        assertEquals("The virtual machine should not have checkpoints", 0, domain.listAllCheckpoints(0).length);
        String domainCheckpointXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<domaincheckpoint>\n" +
            "    <name>test-checkpoint-create-1</name>\n" +
            "    <disks>\n" +
            "        <disk name=\"vda\" bitmap=\"test-checkpoint\" checkpoint=\"bitmap\"/>\n" +
            "        <disk name=\"hdc\" checkpoint=\"no\"/>\n" +
            "    </disks>\n" +
            "</domaincheckpoint>\n";
        DomainCheckpoint domainCheckpoint1 = domain.checkpointCreateXML(domainCheckpointXML, 0);

        assertEquals("The checkpoint was not created", 1, domain.listAllCheckpoints(0).length);
        domainCheckpointXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<domaincheckpoint>\n" +
            "    <name>test-checkpoint-create-2</name>\n" +
            "    <disks>\n" +
            "        <disk name=\"vda\" bitmap=\"test-checkpoint\" checkpoint=\"bitmap\"/>\n" +
            "        <disk name=\"hdc\" checkpoint=\"no\"/>\n" +
            "    </disks>\n" +
            "</domaincheckpoint>\n";
        DomainCheckpoint domainCheckpoint2 = domain.checkpointCreateXML(domainCheckpointXML, 0);
        assertEquals("The second checkpoint was not created", 2, domain.listAllCheckpoints(0).length);
        domainCheckpoint2.delete(DomainCheckpoint.CheckpointDeleteFlags.CHILDREN);
        assertEquals("The checkpoint 2 was not deleted", 1, domain.listAllCheckpoints(0).length);
        domainCheckpoint1.delete(DomainCheckpoint.CheckpointDeleteFlags.CHILDREN);
        assertEquals("The checkpoint 1 was not deleted", 0, domain.listAllCheckpoints(0).length);
    }

    /**
     * Check methods inside DomainCheckpoint class, like getName, getXMLDesc,...
     * @throws LibvirtException
     */
    public void testDomainCheckpointMethods() throws LibvirtException {
        Domain domain = createDomainToCheckpointTest("test-vm-checkpoint-methods");
        domain.create();
        String domainCheckpointXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<domaincheckpoint>\n" +
            "    <name>test-checkpoint-methods-1</name>\n" +
            "    <disks>\n" +
            "        <disk name=\"vda\" bitmap=\"test-checkpoint\" checkpoint=\"bitmap\"/>\n" +
            "        <disk name=\"hdc\" checkpoint=\"no\"/>\n" +
            "    </disks>\n" +
            "</domaincheckpoint>\n";
        DomainCheckpoint domainCheckpoint = domain.checkpointCreateXML(domainCheckpointXML, 0);
        assertEquals("The names should match", "test-checkpoint-methods-1", domainCheckpoint.getName());


        String domainCheckpointXMLDesc = domainCheckpoint.getXMLDesc(0);
        assertTrue("The XML should contain the tag <domaincheckpoint>", domainCheckpointXMLDesc.contains("<domaincheckpoint>"));
        assertTrue("The XML should contain the name of checkpoint", domainCheckpointXMLDesc.contains("test-checkpoint-methods-1"));
        assertTrue("The XML should contain one of disks to perform the checkpoint", domainCheckpointXMLDesc.contains("vda"));
    }

    /**
     * Check methods with hierarchy, like listAllChildren, getParent, etc.
     * @throws LibvirtException
     */
    public void testDomainCheckpointHierarchy() throws LibvirtException {
        int NUM_CHECKPOINTS = 10; // Should be bigger than 2
        Domain domain = createDomainToCheckpointTest("test-vm-checkpoint-testDomainCheckpointHierarchy");
        domain.create();
        DomainCheckpoint[] testCheckpoints = new DomainCheckpoint[NUM_CHECKPOINTS];
        String baseCheckpointName = "test-checkpoint-";
        for(int i = 0; i < NUM_CHECKPOINTS; i++) {
            String domainCheckpointXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<domaincheckpoint>  <name>" + baseCheckpointName + i + "</name>  </domaincheckpoint>\n"; // We avoid disks subelement to write less code
            testCheckpoints[i] = domain.checkpointCreateXML(domainCheckpointXML, 0);
        }

        // Test the lookup function
        DomainCheckpoint checkpointLookedup = domain.checkpointLookupByName("test-checkpoint-1");
        // Check if the parent is "test-checkpoint-0"
        assertEquals(testCheckpoints[0].getName(), checkpointLookedup.getParent(0).getName());


        DomainCheckpoint checkpointNotCreated = domain.checkpointLookupByName("not-created-checkpoint");
        assertNull(checkpointNotCreated);

        // Get all checkpoints in topological order
        DomainCheckpoint[] domainCheckpoints = domain.listAllCheckpoints(CheckpointListFlags.TOPOLOGICAL);
        assertEquals("One checkpoint was not created", NUM_CHECKPOINTS, domainCheckpoints.length);
        // The checkpoints order should be the same.
        for (int i = 0; i < NUM_CHECKPOINTS; i++) {
            assertEquals("The created checkpoints order should be the same - " + i, testCheckpoints[i].getName(), domainCheckpoints[i].getName());
        }
        assertNull(testCheckpoints[0].getParent(0));
        for (int i = 1; i < NUM_CHECKPOINTS; i++) {
            assertEquals(domainCheckpoints[i-1].getName(), domainCheckpoints[i].getParent(0).getName());
        }

        // Check checkpointListNames function
        String[] checkpointNames = domain.checkpointListNames(CheckpointListFlags.TOPOLOGICAL);
        assertEquals("One checkpoint was not created", NUM_CHECKPOINTS, checkpointNames.length);
        // The checkpoints order should be the same.
        for (int i = 0; i < NUM_CHECKPOINTS; i++) {
            assertEquals("The created checkpoints order should be the same - " + i, testCheckpoints[i].getName(), checkpointNames[i]);
        }

        // Check listAllChildren function
        DomainCheckpoint[] childrenFromFirst = domainCheckpoints[0].listAllChildren(CheckpointListFlags.DESCENDANTS);
        assertEquals("One checkpoint was not created", NUM_CHECKPOINTS - 1, childrenFromFirst.length);
        for(int i = 1; i < NUM_CHECKPOINTS; i++) {
            assertEquals(childrenFromFirst[i-1].getName(), domainCheckpoints[i].getName());
        }
    }
}
