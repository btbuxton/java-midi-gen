package net.blabux.midigen.research.record;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class TargetPipe extends AbstractDataLine implements TargetDataLine {
    final SourcePipe source;
    final PipedInputStream input;

    public TargetPipe(SourcePipe source, PipedOutputStream output) {
        this.source = source;
        try {
            this.input = new PipedInputStream(output, source.getBufferSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            return input.read(b, off, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            return input.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drain() {
        try {
            input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
