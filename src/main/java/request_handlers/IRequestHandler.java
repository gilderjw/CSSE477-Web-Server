package request_handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import server.Server;

public interface IRequestHandler {
	HttpResponse handleRequest(HttpRequest request, Server server);
}
