import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import dynamic_loading.PluginLoader;
import dynamic_loading.PluginRunner;

public class TestPluginLoader {
	
	@Test
	public void testMapFilled() throws FileNotFoundException {
		Set<String> pluginSet = new HashSet<>();
		
		PluginLoader loader = new PluginLoader(pluginSet);
		
		assertEquals(0, pluginSet.size());
		
		loader.loadAvailablePlugins();
		
		assertEquals(1, pluginSet.size());
		
		Iterator<String> iter = pluginSet.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	@Test
	public void testPluginRunnerCorrectPath() throws FileNotFoundException {
		Set<String> pluginSet = new HashSet<>();
		
		PluginLoader loader = new PluginLoader(pluginSet);
		loader.loadAvailablePlugins();
		PluginRunner runner = new PluginRunner(pluginSet);
	}
}
