package request_handlers;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import response_creators.DeleteResponse200Creator;
import response_creators.Response200Creator;
import response_creators.Response404Creator;
import server.Server;

public class DeleteRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request, Server server) {
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
					// Create Delete 200 OK response
					boolean successfulDelete = file.delete();
					response = DeleteResponse200Creator.createResponse(successfulDelete, Protocol.CLOSE);
				} else {
					// File does not exist so lets create 404 file not found code
					response = Response404Creator.createResponse(Protocol.CLOSE);
				}
			} else { // Its a file
						// Lets create 200 OK response
				boolean successfulDelete = file.delete();
				response = DeleteResponse200Creator.createResponse(successfulDelete, Protocol.CLOSE);
			}
		} else {
			// File does not exist so lets create 404 file not found code
			response = Response404Creator.createResponse(Protocol.CLOSE);
		}
		return response;
	}

}
