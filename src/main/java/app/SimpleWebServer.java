package app;

import protocol.Protocol;
import request_handlers.DeleteRequestHandler;
import request_handlers.GetRequestHandler;
import request_handlers.HeadRequestHandler;
import request_handlers.PostRequestHandler;
import request_handlers.PutRequestHandler;
import server.Server;

/**
 * The entry point of the Simple Web Server (SWS).
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SimpleWebServer {
	public static void main(String[] args) throws InterruptedException {
		// TODO: Server configuration, ideally we want to read these from an application.properties file
		String rootDirectory = "web"; 
		int port = 8080;

		// Create a run the server
		Server server = new Server(rootDirectory, port);
		server.registerRequestHandler(Protocol.POST, new PostRequestHandler());
		server.registerRequestHandler(Protocol.GET, new GetRequestHandler());
		server.registerRequestHandler(Protocol.HEAD, new HeadRequestHandler());
		server.registerRequestHandler(Protocol.DELETE, new DeleteRequestHandler());
		server.registerRequestHandler(Protocol.PUT, new PutRequestHandler());
		Thread runner = new Thread(server);
		runner.start();

		
		// TODO: Instead of just printing to the console, use proper logging mechanism.
		// SL4J/Log4J are some popular logging framework
		System.out.format("Simple Web Server started at port %d and serving the %s directory ...%n", port, rootDirectory);
		
		// Wait for the server thread to terminate
		runner.join();
	}
}
