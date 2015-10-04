package Speech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	AudioInputStream inputStream =null;

	public void finish() throws IOException {
		microphone.stop();
		microphone.close();
		inputStream.close();
	}

	public void run() {
		try {
			List<SpeechResults> transcripts = new ArrayList<SpeechResults>();
			List<Integer> volumes = new ArrayList<Integer>();
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
			while (true) {
				int numBytesRead = inputStream.read(data);
				System.out.println(numBytesRead);
				volumes.add(Utilities.calculateRMSLevel(data));
				if (numBytesRead == 0) {
					AudioInputStream outInputStream = new AudioInputStream(
							new ByteArrayInputStream(out.toByteArray()),
							format, out.size() / format.getFrameSize());
					out.reset();
					File fileOut = new File("./watson/sound.wav");
					AudioSystem.write(outInputStream,
							AudioFileFormat.Type.WAVE, fileOut);
					SpeechResults transcript = textToSpeech(service, fileOut);
					transcripts.add(transcript);
					break;
				}
				out.write(data);
				if (offset == 8) {
					offset = -1;
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
			System.out.println(text);
			Map<String, String> params = new HashMap<String, String>();
			params.put("body", text);
			Map<String, String> res = Utilities.sendRequest("https://stream.watsonplatform.net/tone-analyzer-experimental/api/v1/tone", params);
			System.out.println(res);
			System.out.println(volumes);
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
		SpeechResults transcript = service.recognize(params);
		System.out.println("Watson request");
		return transcript;
	}
}
