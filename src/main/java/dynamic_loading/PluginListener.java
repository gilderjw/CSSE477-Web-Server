package dynamic_loading;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginListener implements Runnable {
	
	private WatchService watcher;
	private Path dir;
	private final String LOCATION_TO_WATCH = "plugins";
	private Set<String> pluginSet;
	private PluginRunner runner;
	
	static final Logger log = LogManager.getLogger(PluginListener.class);
	
	public PluginListener(Set<String> pluginSet, PluginRunner runner) {
		this.pluginSet = pluginSet;
		this.runner = runner;
		try {
			watcher = FileSystems.getDefault().newWatchService();
			dir = Paths.get(LOCATION_TO_WATCH);
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {
			log.error("Directory could not be registered to watcher", e);
		}
	}

	@Override
	public synchronized void run() {
		while (true) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				return;
			}
			
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				
				if (kind == OVERFLOW) {
					continue;
				}
				
				// The filename is the context of the event.
				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
				Path filename = ev.context();
				
				// Verify that the new file is a JAR or WAR file.
				try {
					Path child = dir.resolve(filename);
					String mime = Files.probeContentType(child);
					if (!mime.equals("application/x-java-applet") 
							&& !mime.equals("application/x-zip")) {
						log.error("New file " + filename + " is not a JAR or WAR file.");
						continue;
					}
				} catch (IOException e) {
					log.error("Watchevent poll threw exception", e);
				}
				
				URL currentURL = null;
				int currentSize = pluginSet.size();
				try {
					currentURL = Paths.get(filename.toString()).toUri().toURL();
					pluginSet.add(filename.toString());
				} catch (MalformedURLException e) {
					log.error("Could not place plugin url in map in listener", e);
				}
				
				// Means that the plugin was not already inside the map, and needs to be started.
				if (currentSize != pluginSet.size()) {
					runner.runPlugin(currentURL);
				}
				
						
			}
			
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}
	
	

}
