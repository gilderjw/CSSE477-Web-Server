package response_creators;

import java.util.HashMap;

import protocol.HttpResponse;
import protocol.Protocol;

public class Response400Creator extends ResponseCreator {

	/**
	 * Creates a {@link HttpResponse} object for sending bad request response.
	 *
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 400 status.
	 */

	public static HttpResponse createResponse(String connection) {

		HttpResponse response = new HttpResponse(Protocol.VERSION, Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT,
				new HashMap<String, String>(), null);

		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);

		return response;
	}

}
