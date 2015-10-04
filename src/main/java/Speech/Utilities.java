package Speech;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Utilities {
	
	public static Process myoProcess;
	public static List<String> myoOutputStrings = new ArrayList<String>();
	public static int totalTime;
	public static double smallMovementTime, bigMovementTime;

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

			BufferedReader input = new BufferedReader(new InputStreamReader(myoProcess.getInputStream()));

			String line;
			while((line = input.readLine()) != null){
				myoOutputStrings.add(line);
			}

		}catch(IOException e){
			e.printStackTrace(System.err);
		}
	}
	
	public static void parseMyoData(){
		totalTime = Integer.parseInt(myoOutputStrings.get(0));
		smallMovementTime = Double.parseDouble(myoOutputStrings.get(1));
		bigMovementTime = Double.parseDouble(myoOutputStrings.get(2));
		
		ArrayList<Double> graphData = new ArrayList<Double>();
		for(int i = 3; i < myoOutputStrings.size(); i+=2){
			
		}
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
