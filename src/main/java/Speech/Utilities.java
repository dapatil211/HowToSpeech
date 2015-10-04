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
	public static double[] movementGraph;

	public static void executeMyo(){
			try {
				myoProcess = Runtime.getRuntime().exec("hello-myo-VisualStudio2013.exe");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void stopMyo(){
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(myoProcess.getOutputStream()));
			out.write("a");
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
			double movementType = Double.parseDouble(myoOutputStrings.get(i));
			int length = Integer.parseInt(myoOutputStrings.get(++i));
			for(int j = 0; j < length; j++){
				graphData.add(movementType);
			}
		}
	
		movementGraph = new double[graphData.size()];
		for(int i = 0; i < movementGraph.length; i++){
			movementGraph[i] = graphData.get(i);
		}
	}
	
	public static int getTotalTime(){ return totalTime; }
	public static double getSmallMovementTime(){ return smallMovementTime; }
	public static double getBigMovementTime(){ return bigMovementTime; }
	public static double[] getMovementGraphArray(){ return movementGraph; }


	public static void main(String[] args) {

	}

}
