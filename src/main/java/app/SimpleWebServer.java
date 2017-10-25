package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dynamic_loading.PluginListener;
import dynamic_loading.PluginLoader;
import dynamic_loading.PluginRunner;
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

	static final Logger log = LogManager.getLogger(SimpleWebServer.class);

	public static void main(String[] args) {

		Properties prop = new Properties();
		System.err.println(System.getProperty("user.dir"));
		try {
			prop.load(new FileInputStream("application.properties"));
		} catch (IOException e) {
			log.error("Problem loading properties file.", e);
		}

		String rootDirectory = prop.getProperty("root_directory");
		int port = Integer.parseInt((String) prop.get("port_number"));
		
		
		// Loading plugins with map
		Map<String, URL> plugins = new HashMap<>();
		PluginLoader pluginLoader = null;
		try {
			pluginLoader = new PluginLoader(plugins);
		} catch (FileNotFoundException e) {
			log.error("Could not create pluginLoader", e);
		}
		
		// Loads all currently available plugins to map to be run.
		pluginLoader.loadAvailablePlugins();
		
		PluginRunner pluginRunner = new PluginRunner(plugins);
		
		PluginListener pluginListener = new PluginListener(plugins, pluginRunner);
		Thread pluginThread = new Thread(pluginListener);
		pluginThread.start();
		

		// Create a run the server
		Server server = new Server(rootDirectory, port);
		server.registerRequestHandler(Protocol.POST, new PostRequestHandler());
		server.registerRequestHandler(Protocol.GET, new GetRequestHandler());
		server.registerRequestHandler(Protocol.HEAD, new HeadRequestHandler());
		server.registerRequestHandler(Protocol.DELETE, new DeleteRequestHandler());
		server.registerRequestHandler(Protocol.PUT, new PutRequestHandler());
		Thread runner = new Thread(server);
		runner.start();

		log.info("Simple Web Server started at port " + port + " and serving the " + rootDirectory + " directory ...");

		// Wait for the server thread to terminate
		try {
			runner.join();
		} catch (InterruptedException e) {
			log.error("Problem with runner.join() in SimpleWebServer.", e);
		}
	}
}
