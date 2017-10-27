package dynamic_loading;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.Server;

public class PluginListener implements Runnable {

	private WatchService watcher;
	private Path dir;
	private final String LOCATION_TO_WATCH = "plugins";
	private PluginLoader loader;
	private Server serv;

	static final Logger log = LogManager.getLogger(PluginListener.class);

	public PluginListener(PluginLoader loader, Server serv) {
		this.loader = loader;
		this.serv = serv;

		try {
			this.watcher = FileSystems.getDefault().newWatchService();
			this.dir = Paths.get(this.LOCATION_TO_WATCH);
			this.dir.register(this.watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {
			log.error("Directory could not be registered to watcher", e);
		}
	}

	@Override
	public synchronized void run() {
		while (true) {
			WatchKey key;
			try {
				key = this.watcher.take();
				this.loader.loadAvailablePlugins();
				this.serv.setPlugins(this.loader.getPluginMappings());

				// this.pluginSet = this.loader.loadAvailablePlugins();
			} catch (InterruptedException e) {
				return;
			}

			// for (WatchEvent<?> event : key.pollEvents()) {
			// WatchEvent.Kind<?> kind = event.kind();
			//
			// if (kind == OVERFLOW) {
			// continue;
			// }
			//
			// // The filename is the context of the event.
			// @SuppressWarnings("unchecked")
			// WatchEvent<Path> ev = (WatchEvent<Path>) event;
			// Path filename = ev.context();
			// System.out.println(filename);
			//
			// // Verify that the new file is a JAR or WAR file.
			// URL currentURL = null;
			// try {
			// currentURL = filename.toUri().toURL();
			// // System.out.println(currentURL);
			// this.pluginSet.add(this.loader.loadPlugin(currentURL));
			// } catch (MalformedURLException e) {
			// log.error("Could not place plugin url in map in listener", e);
			// }
			// }

			// boolean valid = key.reset();
			// if (!valid) {
			// break;
			// }
		}
	}
}
