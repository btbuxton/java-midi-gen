package net.blabux.midigen.research;

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
        return buffer.write(b, off, len);
    }

    @Override
    public int available() {
        return bufferSize - buffer.available();
    }

    public TargetDataLine asTargetDataLine() {
        return new TargetPipe(this);
    }

    ReadWriteBuffer getBuffer() {
        return buffer;
    }
}
