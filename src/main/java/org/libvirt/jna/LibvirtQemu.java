package org.libvirt.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface LibvirtQemu extends Library{
    LibvirtQemu INSTANCE = (LibvirtQemu) Native.loadLibrary(Platform.isWindows() ? "virt-qemu-0" : "virt-qemu", LibvirtQemu.class);

    int virDomainQemuMonitorCommand(DomainPointer virDomainPtr, String cmd, CStringByReference result, int flags);
    CString virDomainQemuAgentCommand(DomainPointer virDomainPtr, String cmd, int timeout,int flags);
}
