package org.libvirt;

import java.util.UUID;

import junit.framework.TestCase;

/**
 * libvirt tests not requiring an active connection
 */
public class TestLibvirtGlobals extends TestCase {
    public void testErrorCallback() throws Exception {
        DummyErrorCallback cb = new DummyErrorCallback();
        Connect.setErrorCallback(cb);
        try {
            Connect conn = new Connect("test:///someUrl", false);
        } catch (LibvirtException e) {
            // eat it
        }
        assertTrue("We should have caught an error", cb.error);
    }

    public void testLibrary() throws Exception {
        assertTrue("Library.getVersion() > 6000", Library.getVersion() > 6000);
    }
}
