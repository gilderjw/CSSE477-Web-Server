package plugins;

import request_handlers.IRequestHandler;

public interface IPlugin {

	public void performPluginAction();

	public void addHandler(String type, IRequestHandler handler);

	public IRequestHandler getHandler(String type);
}