package org.libvirt;

import org.libvirt.jna.virDomainJobInfo;

public class DomainJobInfo {
    protected int type;
    protected long timeElapsed;
    protected long timeRemaining;
    protected long dataTotal;
    protected long dataProcessed;
    protected long dataRemaining;
    protected long memTotal;
    protected long memProcessed;
    protected long memRemaining;
    protected long fileTotal;
    protected long fileProcessed;
    protected long fileRemaining;
    
    public DomainJobInfo(virDomainJobInfo info) {
        this.type = info.type;
        this.timeElapsed = info.timeElapsed;
        this.timeRemaining = info.timeRemaining;
        this.dataTotal = info.dataTotal;
        this.dataProcessed = info.dataProcessed;
        this.dataRemaining = info.dataRemaining;
        this.memTotal = info.memTotal;
        this.memProcessed = info.memProcessed;
        this.memRemaining = info.memRemaining;
        this.fileTotal = info.fileTotal;
        this.fileProcessed = info.fileProcessed;
        this.fileRemaining = info.fileRemaining;              
    }
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getTimeElapsed() {
        return timeElapsed;
    }
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    public long getTimeRemaining() {
        return timeRemaining;
    }
    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
    public long getDataTotal() {
        return dataTotal;
    }
    public void setDataTotal(long dataTotal) {
        this.dataTotal = dataTotal;
    }
    public long getDataProcessed() {
        return dataProcessed;
    }
    public void setDataProcessed(long dataProcessed) {
        this.dataProcessed = dataProcessed;
    }
    public long getDataRemaining() {
        return dataRemaining;
    }
    public void setDataRemaining(long dataRemaining) {
        this.dataRemaining = dataRemaining;
    }
    public long getMemTotal() {
        return memTotal;
    }
    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }
    public long getMemProcessed() {
        return memProcessed;
    }
    public void setMemProcessed(long memProcessed) {
        this.memProcessed = memProcessed;
    }
    public long getMemRemaining() {
        return memRemaining;
    }
    public void setMemRemaining(long memRemaining) {
        this.memRemaining = memRemaining;
    }
    public long getFileTotal() {
        return fileTotal;
    }
    public void setFileTotal(long fileTotal) {
        this.fileTotal = fileTotal;
    }
    public long getFileProcessed() {
        return fileProcessed;
    }
    public void setFileProcessed(long fileProcessed) {
        this.fileProcessed = fileProcessed;
    }
    public long getFileRemaining() {
        return fileRemaining;
    }
    public void setFileRemaining(long fileRemaining) {
        this.fileRemaining = fileRemaining;
    }
}
