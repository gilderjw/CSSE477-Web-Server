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
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.SimpleWebServer;
import plugins.IPlugin;

public class PluginLoader {

	private Set<IPlugin> plugins;
	private Path dir;
	private final String PLUGIN_LOCATION = "plugins";
	private File folder;

	static final Logger log = LogManager.getLogger(PluginLoader.class);

	public PluginLoader() throws FileNotFoundException {
		this.plugins = new HashSet<>();

		this.dir = Paths.get(this.PLUGIN_LOCATION);
		this.folder = this.dir.toFile();
		if (!this.folder.exists()) {
			log.error("Folder could not be created in PluginLoader");
			throw new FileNotFoundException();
		}
	}

	public Set<IPlugin> loadAvailablePlugins() {
		for (int i = 0; i < this.folder.listFiles().length; i++) {
			File current = this.folder.listFiles()[i];
			if (!FilenameUtils.getExtension(current.getName()).equals("war")
					&& !FilenameUtils.getExtension(current.getName()).equals("jar")) {
				continue;
			}
			// File is a jar or war type

			try {
				URL url = current.toURI().toURL();
				this.plugins.add(this.loadPlugin(url));
			} catch (MalformedURLException e) {
				log.error("Error creating plugin from URL " + current.getPath() + "\n", e);
			}
		}
		return this.plugins;
	}

	public Map<String, IPlugin> getPluginMappings() {
		HashMap<String, IPlugin> ans = new HashMap<>();
		for (IPlugin p : this.plugins) {
			ans.put(SimpleWebServer.DEFAULT_PLUGIN, p);
		}

		return ans;
	}

	public IPlugin loadPlugin(URL url) {
		ClassLoader cl = URLClassLoader.newInstance(new URL[] { url }, this.getClass().getClassLoader());
		IPlugin plugin = null;
		String contents = null;

		System.out.println(url);

		InputStream stream = cl.getResourceAsStream("config.bruh");
		Scanner scan = new Scanner(stream);
		contents = scan.nextLine();
		try {
			cl.loadClass(contents);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scan.close();

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

		System.out.println("loaded plugin: " + url);
		return plugin;
	}
}