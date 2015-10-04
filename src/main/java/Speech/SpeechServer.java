package Speech;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SpeechServer {
	public static void main(String[] args) throws Exception{
		Server server = new Server(8080);
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		handler.addServlet(RecordServlet.class, "/record");
		
		//static files
		DefaultServlet defaultServlet = new DefaultServlet();
		ServletHolder staticHolder = new ServletHolder(defaultServlet);
		staticHolder.setInitParameter("resourceBase", "./src/resources");
		handler.addServlet(staticHolder, "/");
		
		server.setHandler(handler);
		server.start();
	}
}
