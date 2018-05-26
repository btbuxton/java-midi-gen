package net.blabux.midigen.research.record;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.PipedOutputStream;

public class SourcePipe extends AbstractDataLine implements SourceDataLine {
    static final int BUFFER_SECONDS = 1;
    PipedOutputStream output;


    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        this.output = new PipedOutputStream();
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
        try {
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int write(byte[] b, int off, int len) {
        try {
            output.write(b, off, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return len - off;
    }

    public TargetDataLine asTargetDataLine() {
        return new TargetPipe(this, output);
    }

}
