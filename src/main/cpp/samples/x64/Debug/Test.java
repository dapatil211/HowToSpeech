import java.io.*;
import java.util.*;

public class Test{

	public static void main(String[] args){

	try{
	Process p = Runtime.getRuntime().exec("hello-myo-VisualStudio2013.exe");


	try{
	Thread.sleep(10000);
	} catch(InterruptedException ie){
	System.err.println("interrupted");
	}

//	OutputStream stdin = p.getOutputStream();
//	stdin.write("a".getBytes());

	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
	out.write("a");
	out.flush();
	out.close();

	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

	String line;
	while((line = input.readLine()) != null){

		System.out.println(line);
	}

	}catch(IOException e){
	e.printStackTrace(System.err);
	System.err.println("rip");
	}
}


}
