package net.blabux.midigen.research.record.takeone;

import javax.sound.sampled.*;

public class SourcePipe extends AbstractDataLine implements SourceDataLine {
    static final int BUFFER_SECONDS = 16;
    ReadWriteBuffer buffer;


    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        this.buffer = new ReadWriteBuffer(bufferSize);
        this.bufferSize = bufferSize;
        this.format = format;
        super.open();
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException {
        this.open(format, (int) (format.getFrameSize() * format.getFrameRate() * BUFFER_SECONDS));
        super.open();
    }

    @Override
    public void close() {
        super.close();
        this.buffer = null;

    }

    @Override
    public int write(byte[] b, int off, int len) {
        int write = 0;
        while (write == 0) {
            write = buffer.write(b, off, len);
            if (write > 0)
                return write;
            synchronized (this) {
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return write;
    }

    @Override
    public int available() {
        final int avail = bufferSize - buffer.available();
        System.out.println(String.format("SourcePipe avail=%d", avail));
        return avail;
    }

    public TargetDataLine asTargetDataLine() {
        final TargetPipe targetPipe = new TargetPipe(this);
        try {
            targetPipe.open();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        return targetPipe;
    }

    ReadWriteBuffer getBuffer() {
        return buffer;
    }
}
