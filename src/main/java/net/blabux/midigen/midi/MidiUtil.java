package net.blabux.midigen.midi;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * I loathe static classes, but this is just a placeholder to get objects from
 * the Java midi framework. All methods will be refactored and moved eventually
 * to better contexts.
 * 
 * These methods start our in the example main class and will move here until
 * better homes can be found.
 * 
 * @author btbuxton
 *
 */
public class MidiUtil {

	/**
	 * Return a list of midi devices that can receive midi output
	 * 
	 * @return
	 */
	public static final Stream<MidiDevice> getMidiReceivers() {
		final Stream<Info> infos = Stream.of(MidiSystem.getMidiDeviceInfo());
		final Stream<MidiDevice> devices = infos.map((info) -> {
			try {
				return MidiSystem.getMidiDevice(info);
			} catch (MidiUnavailableException e) {
				throw new RuntimeException(e);
			}
		});
		return devices.filter((device) -> device.getMaxReceivers() != 0);
	}

	/**
	 * Given a string toFind, it will search for the first midi receiver it finds.
	 * If none is found, it returns the first midi receiver.
	 * 
	 * It can throw NoSuchElement if there are NO MidiDevices available at all
	 * 
	 * Not in love with the name
	 * 
	 * @param toFind
	 * @return
	 */
	public static final MidiDevice getMidiReceiversContainingName(String toFind) {
		Stream<MidiDevice> preferred = getMidiReceivers()
				.filter((device) -> device.getDeviceInfo().getName().contains(toFind));
		return preferred.findFirst().orElseGet(() -> MidiUtil.getMidiReceivers().findFirst().get());
	}
	
	/**
	 * Return names of midi receivers
	 * @return
	 */
	public static final List<String> getMidiReceiverNames() {
		Stream<MidiDevice> devices = getMidiReceivers();
		return devices.map((device) -> device.getDeviceInfo().getName()).collect(Collectors.toList());
	}

}
