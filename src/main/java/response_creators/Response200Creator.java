package response_creators;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;

import protocol.HttpResponse;
import protocol.Protocol;

public class Response200Creator extends ResponseCreator {

	/**
	 * Creates a {@link HttpResponse} object for sending the supplied file with
	 * supplied connection parameter.
	 *
	 * @param file
	 *            The {@link File} to be sent.
	 * @param connection
	 *            Supported values are {@link Protocol#OPEN} and
	 *            {@link Protocol#CLOSE}.
	 * @return A {@link HttpResponse} object represent 200 status.
	 */

	public static HttpResponse createResponse(File file, String connection) {

		HttpResponse response = new HttpResponse(Protocol.VERSION, Protocol.OK_CODE, Protocol.OK_TEXT,
				new HashMap<String, String>(), file);

		// Lets fill up header fields with more information
		fillGeneralHeader(response, connection);

		// Lets add last modified date for the file
		long timeSinceEpoch = file.lastModified();
		Date modifiedTime = new Date(timeSinceEpoch);
		response.put(Protocol.LAST_MODIFIED, modifiedTime.toString());

		// Lets get content length in bytes
		long length = file.length();
		response.put(Protocol.CONTENT_LENGTH, length + "");

		// Lets get MIME type for the file
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mime = fileNameMap.getContentTypeFor(file.getName());
		// The fileNameMap cannot find mime type for all of the documents, e.g. doc,
		// odt, etc.
		// So we will not add this field if we cannot figure out what a mime type is for
		// the file.
		// Let browser do this job by itself.
		if (mime != null) {
			response.put(Protocol.CONTENT_TYPE, mime);
		}

		return response;

	}

}
