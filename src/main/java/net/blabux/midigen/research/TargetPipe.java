package net.blabux.midigen.research;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class TargetPipe extends AbstractDataLine implements TargetDataLine {
    final SourcePipe source;

    public TargetPipe(SourcePipe source) {
        this.source = source;
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
        return source.getBuffer().read(b, off, len);
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
        return source.getBuffer().available();
    }

    @Override
    public void drain() {
        while (source.isRunning()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
