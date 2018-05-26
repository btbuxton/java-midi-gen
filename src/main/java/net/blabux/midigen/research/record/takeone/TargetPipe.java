package net.blabux.midigen.research.record.takeone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class TargetPipe extends AbstractDataLine implements TargetDataLine {
    final SourcePipe source;
    final ReadWriteBuffer buffer;

    public TargetPipe(SourcePipe source) {
        this.source = source;
        this.buffer = source.getBuffer();
    }
    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        //ignore
        super.open();
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException {
        //ignore
        super.open();
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int read = 0;
        while (read == 0 && (source.getBuffer() != null || available() > 0)) {
            read = buffer.read(b, off, len);
            if (read > 0) return read;
            synchronized (this) {
                try {
                    this.wait(100);
                    System.out.println(String.format("read %d, running %b, avail %d", read, source.isRunning(), available()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return read;
    }

    @Override
    public AudioFormat getFormat() {
        return source.getFormat();
    }

    @Override
    public int getBufferSize() {
        return source.getBufferSize();
    }

    @Override
    public int available() {
        final int avail = buffer.available();
        System.out.println(String.format("TargetPipe avail=%d", avail));
        return avail;
    }

    @Override
    public void drain() {
        System.out.println("Draining...");
        while (source.isRunning()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
