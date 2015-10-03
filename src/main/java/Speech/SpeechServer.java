package Speech;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class SpeechServer {
	public static void main(String[] args) throws Exception{
		Server server = new Server(8080);
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		handler.addServlet(RecordServlet.class, "/");
		server.setHandler(handler);
		server.start();
	}
}
