package response_creators;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import protocol.HttpResponse;
import protocol.Protocol;

public class ResponseCreator {

	private HttpResponse response;

	public ResponseCreator() {
		this.response = new HttpResponse();
		this.response.setHeader(new HashMap<String, String>());
	}

	public HttpResponse getResponse() {
		return this.response;
	}

	public ResponseCreator setResponseVersion(String version) {
		this.response.setVersion(version);
		return this;
	}

	public ResponseCreator setResponseStatus(int status) {
		this.response.setStatus(status);
		return this;
	}

	public ResponseCreator setResponsePhrase(String phrase) {
		this.response.setPhrase(phrase);
		return this;
	}

	public ResponseCreator setResponseFile(File file) {
		this.response.setFile(file);
		return this;
	}
	
	public ResponseCreator addHeader(String key, String value) {
		this.response.setHeader(key, value);
		return this;
	}

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
	public ResponseCreator fillGeneralHeader(HttpResponse response, String connection) {
		// Lets add Connection header
		response.put(Protocol.CONNECTION, connection);

		// Lets add current date
		Date date = Calendar.getInstance().getTime();
		response.put(Protocol.DATE, date.toString());

		// Lets add server info
		response.put(Protocol.Server, Protocol.getServerInfo());

		// Lets add extra header with provider info
		response.put(Protocol.PROVIDER, Protocol.AUTHOR);
		
		return this;
	}

}
