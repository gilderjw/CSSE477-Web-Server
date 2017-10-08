package response_creators;

import java.util.Calendar;
import java.util.Date;

import protocol.HttpResponse;
import protocol.Protocol;

public abstract class ResponseCreator {
	/**
	 * Convenience method for adding general header to the supplied response object.
	 *
	 * @param response
	 *            The {@link HttpResponse} object whose header needs to be filled
	 *            in.
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 */
	protected static void fillGeneralHeader(HttpResponse response, String connection) {
		// Lets add Connection header
		response.put(Protocol.CONNECTION, connection);

		// Lets add current date
		Date date = Calendar.getInstance().getTime();
		response.put(Protocol.DATE, date.toString());

		// Lets add server info
		response.put(Protocol.Server, Protocol.getServerInfo());

		// Lets add extra header with provider info
		response.put(Protocol.PROVIDER, Protocol.AUTHOR);
	}
}
