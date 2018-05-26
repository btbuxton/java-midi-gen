package net.blabux.midigen.research.record;

import javax.sound.sampled.*;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDataLine implements DataLine {
    AudioFormat format;
    int bufferSize;
    boolean isRunning;
    final List<LineListener> listeners;

    public AbstractDataLine() {
        bufferSize = 0;
        format = null;
        isRunning = false;
        this.listeners = new LinkedList<>();
    }

    @Override
    public void drain() {

    }

    @Override
    public void flush() {
        drain();
    }

    @Override
    public void start() {
        isRunning = true;
        update(LineEvent.Type.START);
    }

    @Override
    public void stop() {
        isRunning = false;
        update(LineEvent.Type.STOP);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isActive() {
        return isRunning();
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public int getFramePosition() {
        return 0;
    }

    @Override
    public long getLongFramePosition() {
        return 0;
    }

    @Override
    public long getMicrosecondPosition() {
        return 0;
    }

    @Override
    public float getLevel() {
        return AudioSystem.NOT_SPECIFIED;
    }

    @Override
    public Line.Info getLineInfo() {
        return null;
    }

    @Override
    public void open() throws LineUnavailableException {
        update(LineEvent.Type.OPEN);
    }

    @Override
    public void close() {
        update(LineEvent.Type.CLOSE);
    }

    @Override
    public boolean isOpen() {
        return getFormat() != null && getBufferSize() > 0;
    }

    @Override
    public Control[] getControls() {
        return new Control[0];
    }

    @Override
    public boolean isControlSupported(Control.Type control) {
        return false;
    }

    @Override
    public Control getControl(Control.Type control) {
        return null;
    }

    @Override
    public void addLineListener(LineListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLineListener(LineListener listener) {
        listeners.remove(listener);
    }

    //Call this and use listeners for recording
    void update(final LineEvent event) {
        listeners.forEach(listener -> listener.update(event));
    }

    void update(LineEvent.Type type) {
        update(new LineEvent(this, type, AudioSystem.NOT_SPECIFIED));
    }

}
