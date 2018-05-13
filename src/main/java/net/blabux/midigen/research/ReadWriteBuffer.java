package net.blabux.midigen.research;

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
        return writeLoc - initialWriteLoc;
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
        return readLoc - initialReadLoc;
    }

    public int available() {
        return writeLoc - readLoc;
    }
}
