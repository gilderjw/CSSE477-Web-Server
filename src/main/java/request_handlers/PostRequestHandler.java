package request_handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import response_creators.ResponseCreator;
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
		if (file.isDirectory()) {
			file = new File(rootDirectory + uri + "/" + Protocol.DEFAULT_FILE);
		}

		HttpResponse response;
		ResponseCreator rc = new ResponseCreator();
		rc.fillGeneralHeader(rc.getResponse(), Protocol.CLOSE)
			.setResponseVersion(Protocol.VERSION);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return rc.setResponseStatus(Protocol.BAD_REQUEST_CODE).setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
						.setResponseFile(null).getResponse();
			}
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true));
			writer.write(request.getBody());
			writer.close();
			response = rc.setResponseStatus(Protocol.OK_CODE).setResponsePhrase(Protocol.OK_TEXT).setResponseFile(file)
					.getResponse();
		} catch (IOException e) {
			e.printStackTrace();
			response = rc.setResponseStatus(Protocol.BAD_REQUEST_CODE).setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
					.setResponseFile(null).getResponse();
		}

		return response;
	}

}
