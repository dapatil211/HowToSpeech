package Speech;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;

public class MicRunnable implements Runnable {
	private static final String WATSON_PASSWORD = "fKRNzdfpHadQ";
	private static final String WATSON_USERNAME = "21d3a7d1-b2c5-471b-a66c-d20885130dda";

	TargetDataLine microphone = null;
	public void finish(){
		microphone.stop();
		microphone.close();
	}
	public void run() {
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		try {
			microphone = AudioSystem.getTargetDataLine(format);
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
		}

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 
		try {
			microphone.open(format);
		} catch (LineUnavailableException ex) {
			// Handle the error ...
		}
		SpeechToText service = new SpeechToText();
		service.setUsernameAndPassword(WATSON_USERNAME, WATSON_PASSWORD);
		SpeechSession session = service.createSession();
		AudioInputStream inputStream = new AudioInputStream(microphone);
		microphone.start();
		File fileOut = new File("./watson/sound.wav");
		try {
			AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, fileOut);
		} catch (IOException e) {
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("audio", fileOut);
		params.put("content_type", "audio/wav");
		params.put("word_confidence", false);
		params.put("continuous", true);
		params.put("timestamps", true);
		params.put("inactivity_timeout", 30);
		params.put("max_alternatives", 1);
		SpeechResults transcript = service.recognize(params);

		System.out.println(transcript);
	}
}
