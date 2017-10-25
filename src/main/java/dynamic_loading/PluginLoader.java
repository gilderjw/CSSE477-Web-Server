package dynamic_loading;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginLoader {

	private Map<String, URL> pluginMap;
	private Path dir;
	private final String PLUGIN_LOCATION = "plugins";
	private File folder;
	
	static final Logger log = LogManager.getLogger(PluginLoader.class);

	public PluginLoader(Map<String, URL> pluginMap) throws FileNotFoundException {
		this.pluginMap = pluginMap;
		dir = Paths.get(PLUGIN_LOCATION);
		folder = dir.toFile();
		if (!folder.exists()) {
			log.error("Folder could not be created in PluginLoader");
			throw new FileNotFoundException();
		}
	}

	public void loadAvailablePlugins() {
		for (int i = 0; i < folder.listFiles().length; i++) {
			File current = folder.listFiles()[i];
			if (!FilenameUtils.getExtension(current.getName()).equals("war")
					&& !FilenameUtils.getExtension(current.getName()).equals("jar")) {
				continue;
			}
			
			// File is a jar or war type
			
			try {
				pluginMap.put(current.getPath(), current.toURI().toURL());
			} catch (MalformedURLException e) {
				log.error("Plugin could not be put into map in pluginLoader", e);
			}
		}
	}

}
