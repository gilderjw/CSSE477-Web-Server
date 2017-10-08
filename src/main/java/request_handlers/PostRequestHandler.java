package request_handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import response_creators.Response200Creator;
import response_creators.Response400Creator;
import server.Server;

public class PostRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request, Server server) {
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);

		HttpResponse response;

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return Response400Creator.createResponse(Protocol.CLOSE);
			}
		}

		if (file.isDirectory()) {
			response = Response400Creator.createResponse(Protocol.CLOSE);
		} else { // Its a file
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true));
				writer.write(request.getBody());
				writer.close();
				response = Response200Creator.createResponse(file, Protocol.CLOSE);
			} catch (IOException e) {
				e.printStackTrace();
				response = Response400Creator.createResponse(Protocol.CLOSE);
			}
		}

		return response;
	}

}
