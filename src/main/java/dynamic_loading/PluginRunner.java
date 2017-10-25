package dynamic_loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import plugins.IPlugin;

public class PluginRunner {

	Map<String, URL> plugins;

	public PluginRunner(Map<String, URL> map) {
		this.plugins = map;

		for (URL url : plugins.values()) {
			runPlugin(url);
		}
	}

	@SuppressWarnings("unchecked")
	public void runPlugin(URL url) {
		ClassLoader cl = URLClassLoader.newInstance(new URL[] { url }, getClass().getClassLoader());

		Class<?> clazz = null;
		try {
			clazz = Class.forName("myPackage.MyClass", true, cl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);

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
