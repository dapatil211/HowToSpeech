package Speech;

import java.util.ArrayList;

import oscP5.*;

public class Muse
{
	static Muse museOscServer;
	
	OscP5 museServer;
	static int recvPort = 5000;
	
	static ArrayList concentrationValues = new ArrayList<Double>();

	public static void main(String [] args)
	{
		museOscServer = new Muse();
		museOscServer.museServer = new OscP5(museOscServer, recvPort);
	}
	
	void oscEvent(OscMessage msg)
	{
		if (msg.checkAddrPattern("/muse/elements/experimental/concentration")==true)
		{
			System.out.print(msg.get(0).floatValue() + "\n");
			concentrationValues.add(msg.get(0).floatValue());
		}
	}
}
