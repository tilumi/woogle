package tw.jms.loyal;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;

public class WebSearchEngine {

	private static int port;

	public static void main(String[] args) throws Exception {
		port = 8090;
		WebSearchEngine searchEngine = new WebSearchEngine();
		searchEngine.start();
		System.out.println("Search engine started on: " + port);

	}

	public int start() throws Exception {
		WebAppContext context = new WebAppContext();
		context.setDescriptor(new ClassPathResource("webSearchEngine.xml")
				.getURI().toString());

		Server server = new Server();

		context.setWar("webapp");

		ServerConnector http = new ServerConnector(server);
		http.setPort(port);
		server.addConnector(http);

		server.setHandler(context);
		server.setStopAtShutdown(true);
		server.start();
		
		return port;
	}
}
