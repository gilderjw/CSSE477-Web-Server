package dynamic_loading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import plugins.IPlugin;

public class PluginLoader {

	private Map<String, IPlugin> plugins;
	private Path dir;
	private final String PLUGIN_LOCATION = "plugins";
	private File folder;

	static final Logger log = LogManager.getLogger(PluginLoader.class);

	public PluginLoader() throws FileNotFoundException {
		this.plugins = new HashMap<>();

		this.dir = Paths.get(this.PLUGIN_LOCATION);
		this.folder = this.dir.toFile();

		File f = new File(this.PLUGIN_LOCATION);
		if (!f.exists()) {
			f.mkdir();
			// f.setExecutable(true, false);
			f.setReadable(true, false);
			f.setWritable(true, false);
		}

		if (!this.folder.exists()) {
			log.error("Folder could not be created in PluginLoader");
			throw new FileNotFoundException();
		}

	}

	public Map<String, IPlugin> loadAvailablePlugins() {
		for (int i = 0; i < this.folder.listFiles().length; i++) {
			File current = this.folder.listFiles()[i];
			if (!FilenameUtils.getExtension(current.getName()).equals("war")
					&& !FilenameUtils.getExtension(current.getName()).equals("jar")) {
				continue;
			}
			// File is a jar or war type

			try {
				URL url = current.toURI().toURL();
				this.loadPlugin(url);
			} catch (MalformedURLException e) {
				log.error("Error creating plugin from URL " + current.getPath() + "\n", e);
			}
		}
		return this.plugins;
	}

	public Map<String, IPlugin> getPluginMappings() {
		return this.plugins;
	}

	@SuppressWarnings("unchecked")
	public void loadPlugin(URL url) {
		ClassLoader cl = URLClassLoader.newInstance(new URL[] { url }, this.getClass().getClassLoader());
		IPlugin plugin = null;
		String contents = null;
		String location = null;

		System.out.println(url);

		InputStream stream = cl.getResourceAsStream("config.bruh");
		Scanner scan = new Scanner(stream);

		while (scan.hasNext()) {
			// Location <space> class
			String line = scan.nextLine();
			location = line.split(" ")[0];
			contents = line.split(" ")[1];
			try {
				cl.loadClass(contents);
			} catch (ClassNotFoundException e1) {
				log.error("Class not found from config file", e1);
			}

			Class<? extends IPlugin> runClass;
			try {
				runClass = (Class<? extends IPlugin>) cl.loadClass(contents);

				Constructor<? extends IPlugin> ctor = null;
				try {
					ctor = runClass.getConstructor();
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}

				try {
					plugin = ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				log.error("plugin cannot be loaded: ", e);
			}

			this.plugins.put(location, plugin);

			System.out.println("loaded plugin: " + url);
		}
		scan.close();
	}
}