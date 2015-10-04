package Speech;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicRunnable implements Runnable{
	public boolean isRunning = true;
	public void run() {
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		TargetDataLine microphone = null;
		try {
			microphone = AudioSystem.getTargetDataLine(format);
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
		}
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, 
				format); // format is an AudioFormat object
		try {
			microphone.open(format);
		} catch (LineUnavailableException ex) {
			// Handle the error ... 
		}
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		int numBytesRead = -1;
		byte[] data = new byte[microphone.getBufferSize() / 5];
		microphone.start();
		while (isRunning) {
			   // Read the next chunk of data from the TargetDataLine.
			   numBytesRead =  microphone.read(data, 0, data.length);
			   // Save this chunk of data.
			   out.write(data, 0, numBytesRead);
			   System.out.println(Arrays.toString(data));
			}
	}

}
