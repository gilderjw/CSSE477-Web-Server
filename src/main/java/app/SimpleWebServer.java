package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dynamic_loading.PluginListener;
import dynamic_loading.PluginLoader;
import dynamic_loading.PluginRunner;
import protocol.Protocol;
import request_handlers.DeleteRequestHandler;
import request_handlers.GetRequestHandler;
import dynamic_loading.PluginLoader;
import plugins.IPlugin;
import request_handlers.PutRequestHandler;
import server.Server;

/**
 * The entry point of the Simple Web Server (SWS).
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SimpleWebServer {

	static final Logger log = LogManager.getLogger(SimpleWebServer.class);
	public static final String DEFAULT_PLUGIN = "DEFAULT_PLUGIN";

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
		Set<String> plugins = new HashSet<>();
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
		

		// Loading plugins with map
		PluginLoader pluginLoader = null;
		try {
			pluginLoader = new PluginLoader();
		} catch (FileNotFoundException e) {
			log.error("Could not create pluginLoader", e);
		}

		// Loads all currently available plugins to map to be run.
		Set<IPlugin> plugins = pluginLoader.loadAvailablePlugins();

		// PluginListener pluginListener = new PluginListener(plugins, pluginRunner);
		// Thread pluginThread = new Thread(pluginListener);
		// pluginThread.start();

		// Create a run the server
		Server server = new Server(rootDirectory, port);

		// TODO: only one thing loaded
		server.registerPlugin("DEFAULT_PLUGIN", plugins.toArray(new IPlugin[1])[0]);

		// server.registerRequestHandler(Protocol.POST, new PostRequestHandler());
		// server.registerRequestHandler(Protocol.GET, new GetRequestHandler());
		// server.registerRequestHandler(Protocol.HEAD, new HeadRequestHandler());
		// server.registerRequestHandler(Protocol.DELETE, new DeleteRequestHandler());
		// server.registerRequestHandler(Protocol.PUT, new PutRequestHandler());
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
