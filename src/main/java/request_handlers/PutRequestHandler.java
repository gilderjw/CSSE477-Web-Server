package request_handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import response_creators.Response200Creator;
import server.Server;

public class PutRequestHandler implements IRequestHandler {

	public HttpResponse handleRequest(HttpRequest request, Server server) {
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);

		HttpResponse response;
		try {
			FileWriter fw = new FileWriter(file, false);
			fw.write(request.getBody());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		response = Response200Creator.createResponse(file, Protocol.CLOSE);
		
		return response;
	}

}
