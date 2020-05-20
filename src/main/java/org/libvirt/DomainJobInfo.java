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

    public DomainJobInfo(final virDomainJobInfo info) {
        type = info.type;
        timeElapsed = info.timeElapsed;
        timeRemaining = info.timeRemaining;
        dataTotal = info.dataTotal;
        dataProcessed = info.dataProcessed;
        dataRemaining = info.dataRemaining;
        memTotal = info.memTotal;
        memProcessed = info.memProcessed;
        memRemaining = info.memRemaining;
        fileTotal = info.fileTotal;
        fileProcessed = info.fileProcessed;
        fileRemaining = info.fileRemaining;
    }

    public long getDataProcessed() {
        return dataProcessed;
    }

    public long getDataRemaining() {
        return dataRemaining;
    }

    public long getDataTotal() {
        return dataTotal;
    }

    public long getFileProcessed() {
        return fileProcessed;
    }

    public long getFileRemaining() {
        return fileRemaining;
    }

    public long getFileTotal() {
        return fileTotal;
    }

    public long getMemProcessed() {
        return memProcessed;
    }

    public long getMemRemaining() {
        return memRemaining;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public int getType() {
        return type;
    }

    public void setDataProcessed(final long dataProcessed) {
        this.dataProcessed = dataProcessed;
    }

    public void setDataRemaining(final long dataRemaining) {
        this.dataRemaining = dataRemaining;
    }

    public void setDataTotal(final long dataTotal) {
        this.dataTotal = dataTotal;
    }

    public void setFileProcessed(final long fileProcessed) {
        this.fileProcessed = fileProcessed;
    }

    public void setFileRemaining(final long fileRemaining) {
        this.fileRemaining = fileRemaining;
    }

    public void setFileTotal(final long fileTotal) {
        this.fileTotal = fileTotal;
    }

    public void setMemProcessed(final long memProcessed) {
        this.memProcessed = memProcessed;
    }

    public void setMemRemaining(final long memRemaining) {
        this.memRemaining = memRemaining;
    }

    public void setMemTotal(final long memTotal) {
        this.memTotal = memTotal;
    }

    public void setTimeElapsed(final long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void setTimeRemaining(final long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void setType(final int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("type:%d%ntimeElapsed:%d%ntimeRemaining:%d%n"
                           + "dataTotal:%d%ndataProcessed:%d%ndataRemaining:%d%n"
                           + "memTotal:%d%nmemProcessed:%d%nmemRemaining:%d%n"
                           + "fileTotal:%d%nfileProcessed:%d%nfileRemaining:%d%n",
                type, timeElapsed, timeRemaining,
                dataTotal, dataProcessed, dataRemaining,
                memTotal, memProcessed, memRemaining,
                fileTotal, fileProcessed, fileRemaining);
    }
}
