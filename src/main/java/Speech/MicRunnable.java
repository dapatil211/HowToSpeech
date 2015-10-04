package Speech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	AudioInputStream inputStream = null;
	List<Integer> volumes = null;
	String speech = "";

	public void finish() throws IOException {
//		System.out.println("STOP");
		microphone.stop();
		microphone.close();
		inputStream.close();
	}

	public void run() {
		try {
			List<SpeechResults> transcripts = new ArrayList<SpeechResults>();
			volumes = new ArrayList<Integer>();
			AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone.open(format);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] data = new byte[8000 * format.getFrameSize()];
			inputStream = new AudioInputStream(microphone);

			microphone.start();
			SpeechToText service = new SpeechToText();
			service.setUsernameAndPassword(WATSON_USERNAME, WATSON_PASSWORD);
			SpeechSession session = service.createSession();
			int offset = 0;
			int totalBytes = 0;
			while (true) {
				int numBytesRead = inputStream.read(data);
				totalBytes += numBytesRead;
//				System.out.println(totalBytes);
				int volume = Utilities.calculateRMSLevel(data);
				volumes.add(volume);
//				System.out.println("V " + volume);
				if (numBytesRead == 0 && out.size() > 100) {
//					System.out.println("In watson end");
					AudioInputStream outInputStream = new AudioInputStream(
							new ByteArrayInputStream(out.toByteArray()),
							format, out.size() / format.getFrameSize());
					out.reset();
					File fileOut = new File("./watson/sound.wav");
					AudioSystem.write(outInputStream,
							AudioFileFormat.Type.WAVE, fileOut);
					SpeechResults transcript = textToSpeech(service, fileOut);
					if(transcript != null){transcripts.add(transcript);};
					break;
				}
				out.write(data);
				if (offset == 64) {
					offset = -1;
//					System.out.println("In watson");
					AudioInputStream outInputStream = new AudioInputStream(
							new ByteArrayInputStream(out.toByteArray()),
							format, out.size() / format.getFrameSize());
					out.reset();
					File fileOut = new File("./watson/sound.wav");
					AudioSystem.write(outInputStream,
							AudioFileFormat.Type.WAVE, fileOut);
					SpeechResults transcript = textToSpeech(service, fileOut);
					transcripts.add(transcript);
				}
				offset++;
			}
			String text = Utilities.combineStrings(transcripts);
			speech = text;
//			System.out.println(text);
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("body", text);
//			Map<String, String> res = Utilities
//					.sendRequest(
//							"https://stream.watsonplatform.net/tone-analyzer-experimental/api/v1/tone",
//							params);
//			System.out.println(res);
//			System.out.println(volumes);
		} catch (IOException ex) {
			// Handle the error ...
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SpeechResults textToSpeech(SpeechToText service, File fileOut) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("audio", fileOut);
		params.put("content_type", "audio/wav");
		params.put("word_confidence", true);
		params.put("continuous", true);
		params.put("timestamps", true);
		params.put("inactivity_timeout", 30);
		params.put("max_alternatives", 1);
		SpeechResults transcript = null;
		try {
			transcript = service.recognize(params);
		} catch (Exception e) {
		}
//		System.out.println("Watson request");
		return transcript;
	}
}
