package response_creators;

import java.util.HashMap;

import protocol.HttpResponse;
import protocol.Protocol;

public class DeleteResponse200Creator extends ResponseCreator {

	public static HttpResponse createResponse(boolean successfulDelete, String connection) {
		HttpResponse response;
		if (successfulDelete) {
			response = new HttpResponse(Protocol.VERSION, Protocol.OK_CODE, Protocol.DELETE_OK_TEXT,
				new HashMap<String, String>(), null);
		} else {
			response = new HttpResponse(Protocol.VERSION, Protocol.OK_CODE, Protocol.DELETE_NOK_TEXT,
					new HashMap<String, String>(), null);
		}
		
		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);

		return response;
	}

}
