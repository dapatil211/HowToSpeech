package Speech;

import java.util.ArrayList;

import oscP5.*;

public class Muse
{
	static Muse museOscServer;
	
	static OscP5 museServer;
	static int recvPort = 5000;
	
	static ArrayList concentrationValues = new ArrayList<Float>();
	
	static double[] concentrationArray;

	public static void start()
	{
		museOscServer = new Muse();
		museOscServer.museServer = new OscP5(museOscServer, recvPort);
	}
	
	void oscEvent(OscMessage msg)
	{
		if (msg.checkAddrPattern("/muse/elements/experimental/concentration")==true)
		{
			concentrationValues.add(msg.get(0).floatValue());
		}
	}
	
	public static void stopRecording()
	{
		museServer.stop();
	}
	
	public static void parseData()
	{
		concentrationArray = new double[concentrationValues.size()];
		for (int i = 0; i < concentrationValues.size(); i++)
		{
			concentrationArray[i] = ((Float) concentrationValues.get(i)).doubleValue() * 100.0;
		}
	}
}
