package Speech;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

public class Utilities {

	public static Process myoProcess;
	public static List<String> myoOutputStrings = new ArrayList<String>();
	public static int totalTime;
	public static double smallMovementTime, bigMovementTime;
	public static double[] movementGraph;

	public static void executeMyo(){
			try {
				myoProcess = Runtime.getRuntime().exec("C:\\Users\\Anfernee Goon\\Desktop\\Programming\\SD Hacks\\HowToSpeech\\src\\main\\cpp\\samples\\x64\\Debug\\hello-myo-VisualStudio2013.exe");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void stopMyo(){
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(myoProcess.getOutputStream()));
			out.write("\n");
			out.flush();
			out.close();

			BufferedReader input = new BufferedReader(new InputStreamReader(
					myoProcess.getInputStream()));

			String line;
			while ((line = input.readLine()) != null) {
				myoOutputStrings.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public static void parseMyoData() {
		totalTime = Integer.parseInt(myoOutputStrings.get(0));
		smallMovementTime = Double.parseDouble(myoOutputStrings.get(1));
		bigMovementTime = Double.parseDouble(myoOutputStrings.get(2));

		ArrayList<Double> graphData = new ArrayList<Double>();
		for (int i = 3; i < myoOutputStrings.size(); i += 2) {
			double movementType = Double.parseDouble(myoOutputStrings.get(i));
			int length = Integer.parseInt(myoOutputStrings.get(++i));
			for (int j = 0; j < length; j++) {
				graphData.add(movementType);
			}
		}

		movementGraph = new double[graphData.size()];
		for (int i = 0; i < movementGraph.length; i++) {
			movementGraph[i] = graphData.get(i);
		}
	}

	public static int getTotalTime() {
		return totalTime;
	}

	public static double getSmallMovementTime() {
		return smallMovementTime;
	}

	public static double getBigMovementTime() {
		return bigMovementTime;
	}

	public static double[] getMovementGraphArray() {
		return movementGraph;
	}

	/**
	 * Calculates the volume of AudioData which may be buffered data from a
	 * data-line.
	 * 
	 * @param audioData
	 *            The byte[] you want to determine the volume of
	 * @return the calculated volume of audioData
	 */
	public static int calculateRMSLevel(byte[] audioData) {
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}

	/**
	 * Returns the number of bytes over interval for useful when figuring out
	 * how long to record.
	 * 
	 * @param seconds
	 *            The length in seconds
	 * @return the number of bytes the microphone will save.
	 */
	public static int getNumOfBytes(int seconds, AudioFormat format) {
		return getNumOfBytes((double) seconds, format);
	}

	/**
	 * Returns the number of bytes over interval for useful when figuring out
	 * how long to record.
	 * 
	 * @param seconds
	 *            The length in seconds
	 * @return the number of bytes the microphone will output over the specified
	 *         time.
	 */
	public static int getNumOfBytes(double seconds, AudioFormat format) {
		return (int) (seconds * format.getSampleRate()
				* format.getFrameSize() + .5);
	}

	public static void main(String[] args)
	{
		executeMyo();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopMyo();
		parseMyoData();
		System.out.println("Total Time: " + totalTime);
		System.out.println("Small Movement Time: " + smallMovementTime);
		System.out.println("Big Movement Time: " + bigMovementTime);
	}

}
