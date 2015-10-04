package Speech;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public static int movementGrade(){
		if(getBigMovementTime()/getTotalTime() < 0.2){
			if(getSmallMovementTime()/getTotalTime() > 0.13){
				// shaky
				return 60;
			}
			else{
				// stiff
				return 50;
			}
		}
		else{
			return (int)(100 - 100*Math.abs(getBigMovementTime()/getTotalTime() - 0.67));
		}
		
	}
	
	// golden time: total 120, small 15, big 22
	// golden: 30, small 3, big 20
	// shaky: 30, small 9, big 1,
	// small movements: 30, small 11, big 7; 30, 7, 11
	// no movement: 30, 3, 1

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
	
	public static int volumeGrader(List<Integer> volumes){
		int sumDX = 0;
		for(int i = 1; i < volumes.size(); i++){
			sumDX += Math.abs(volumes.get(i-1) - volumes.get(i));
		}
		int averageDX = sumDX / volumes.size();
		
		return (int) (100 - 10*Math.abs(averageDX - 5.0));
	}
	
	public static String numericToLetterGrade(int grade){
		if(grade >= 97){
			return "A+";
		}
		else if(grade >= 94){
			return "A";
		}
		else if(grade >= 90){
			return "A-";
		}
		else if(grade >= 87){
			return "B+";
		}
		else if(grade >= 84){
			return "B";
		}
		else if(grade >= 80){
			return "B-";
		}
		else if(grade >= 77){
			return "C+";
		}
		else if(grade >= 74){
			return "C";
		}
		else if(grade >= 70){
			return "C-";
		}
		else if(grade >= 67){
			return "D+";
		}
		else if(grade >= 64){
			return "D";
		}
		else if(grade >= 60){
			return "D-";
		}
		else return "F";
	}

	public static void main(String[] args)
	{
		// executeMyo();
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		stopMyo();
//		parseMyoData();
//		System.out.println("Total Time: " + totalTime);
//		System.out.println("Small Movement Time: " + smallMovementTime);
//		System.out.println("Big Movement Time: " + bigMovementTime);
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(14, 17, 14, 5, 20, 15, 17, 12, 23, 20, 9, 18, 31, 23, 18, 20, 17, 21, 21));
		System.out.println(volumeGrader(list));
	}

}
