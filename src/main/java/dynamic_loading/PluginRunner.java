package dynamic_loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import plugins.IPlugin;

public class PluginRunner {

	Set<String> plugins;
	
	static final Logger log = LogManager.getLogger(PluginRunner.class);

	public PluginRunner(Set<String> set) {
		this.plugins = set;

		Iterator <String> iterator = plugins.iterator();
		
		while (iterator.hasNext()) {
			String pathName = iterator.next();
			try {
				URL url = new URL(pathName);
				runPlugin(url);
			} catch (MalformedURLException e) {
				log.error("Could not make URL in pluginRunner", e);
			}
		}
	}

	public void runPlugin(URL url) {
		ClassLoader cl = URLClassLoader.newInstance(new URL[] { url }, getClass().getClassLoader());

		Class<?> clazz = null;
		try {
			clazz = Class.forName("plugins.IPlugin", true, cl);
		} catch (ClassNotFoundException e) {
			log.error("Could not find the class when running plugin", e);
		}
		Class<? extends IPlugin> runClass = clazz.asSubclass(IPlugin.class);

		Constructor<? extends IPlugin> ctor = null;
		try {
			ctor = (Constructor<? extends IPlugin>) runClass.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		IPlugin plugin = null;
		try {
			plugin = ctor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
		plugin.performPluginAction();
	}

}
