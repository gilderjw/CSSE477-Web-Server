package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dynamic_loading.PluginListener;
import dynamic_loading.PluginLoader;
import plugins.IPlugin;
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
		String plugin_dir = prop.getProperty("plugin_dir");

		// Loading plugins with map
		PluginLoader pluginLoader = null;
		try {
			pluginLoader = new PluginLoader(plugin_dir);
		} catch (FileNotFoundException e) {
			log.error("Could not create pluginLoader", e);
		}

		// Create a run the server
		Server server = new Server(rootDirectory, port);

		Map<String, IPlugin> plugins = pluginLoader.loadAvailablePlugins();

		// Loads all currently available plugins to map to be run.
		PluginListener pluginListener = new PluginListener(pluginLoader, server, plugin_dir);
		Thread pluginThread = new Thread(pluginListener);
		pluginThread.start();

		// TODO: only one thing loaded
		// server.registerPlugin("DEFAULT_PLUGIN", plugins.toArray(new IPlugin[1])[0]);
		server.setPlugins(plugins);

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
