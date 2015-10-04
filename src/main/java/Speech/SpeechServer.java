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
		DefaultServlet defaultServlet = new DefaultServlet();
		ServletHolder holderPwd = new ServletHolder(defaultServlet);
		holderPwd.setInitParameter("resourceBase", "./src/resources/html");

		handler.addServlet(holderPwd, "/*");
		server.setHandler(handler);
		server.start();
	}
}
