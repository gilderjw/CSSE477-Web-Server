package request_handlers;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import response_creators.Response200NoPayloadCreator;
import response_creators.Response404Creator;
import server.Server;

public class HeadRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request, Server server) {

		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);

		HttpResponse response;

		// Check if the file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if (file.exists()) {
					// Lets create 200 OK response
					response = Response200NoPayloadCreator.createResponse(file, Protocol.CLOSE);
				} else {
					// File does not exist so lets create 404 file not found code
					response = Response404Creator.createResponse(Protocol.CLOSE);
				}
			} else { // Its a file
						// Lets create 200 OK response
				response = Response200NoPayloadCreator.createResponse(file, Protocol.CLOSE);
			}
		} else {
			// File does not exist so lets create 404 file not found code
			response = Response404Creator.createResponse(Protocol.CLOSE);
		}
		return response;

	}

}
