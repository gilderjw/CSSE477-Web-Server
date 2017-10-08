package response_creators;

import java.util.HashMap;

import protocol.HttpResponse;
import protocol.Protocol;

public class Response404Creator extends ResponseCreator {

	/**
	 * Creates a {@link HttpResponse} object for sending not found response.
	 *
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 404 status.
	 */

	public static HttpResponse createResponse(String connection) {
		HttpResponse response = new HttpResponse(Protocol.VERSION, Protocol.NOT_FOUND_CODE, Protocol.NOT_FOUND_TEXT,
				new HashMap<String, String>(), null);

		// Lets fill up the header fields with more information
		fillGeneralHeader(response, connection);

		return response;
	}

}
