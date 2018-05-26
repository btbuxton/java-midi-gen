package net.blabux.midigen.research.record.takeone;

public class ReadWriteBuffer {
    final byte[] internal;
    final int size;

    volatile int readLoc;
    volatile int writeLoc;

    public ReadWriteBuffer(int size) {
        this.internal = new byte[size];
        this.size = size;
        this.readLoc = 0;
        this.writeLoc = 0;
    }

    public int write(byte[] b, int off, int len) {
        final int last = off + len;
        final int initialWriteLoc = writeLoc;
        final int cachedReadLoc = readLoc;
        for (int i=off; i < last; i++) {
            if ((writeLoc - cachedReadLoc) >= size) {
                break;
            }
            internal[writeLoc % size] = b[i];
            writeLoc++;
        }
        final int written = writeLoc - initialWriteLoc;
        System.out.println(String.format("write %d %d %d %d/%d", off, len, written, available(), size));
        return written;
    }

    public int read(byte[] b, int off, int len) {
        final int last = off + len;
        final int initialReadLoc = readLoc;
        final int cachedWriteLoc = writeLoc;
        for (int i=off; i < last; i++) {
            if (readLoc >= cachedWriteLoc) {
                break;
            }
            b[i] = internal[readLoc % size];
            readLoc++;
        }
        final int read = readLoc - initialReadLoc;
        System.out.println(String.format("read %d %d %d %d/%d", off, len, read, available(), size));
        return read;
    }

    public int available() {
        return writeLoc - readLoc;
    }
}
