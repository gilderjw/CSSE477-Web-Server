/*
 * HttpResponse.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a response object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponse {
	static final Logger log = LogManager.getLogger(HttpResponse.class);
	
	private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;

	public HttpResponse() {
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void setFile(File file) {
		this.file = file;
	}

	// /**
	// * Constructs a HttpResponse object using supplied parameter
	// *
	// * @param version
	// * The http version.
	// * @param status
	// * The response status.
	// * @param phrase
	// * The response status phrase.
	// * @param header
	// * The header field map.
	// * @param file
	// * The file to be sent.
	// */
	// public HttpResponse(String version, int status, String phrase, Map<String,
	// String> header, File file) {
	// this.version = version;
	// this.status = status;
	// this.phrase = phrase;
	// this.header = header;
	// this.file = file;
	// }

	/**
	 * Gets the version of the HTTP.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the status code of the response object.
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets the status phrase of the response object.
	 * 
	 * @return the phrase
	 */
	public String getPhrase() {
		return phrase;
	}

	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	//
	// /**
	// * Returns the header fields associated with the response object.
	// *
	// * @return the header
	// */
	// public Map<String, String> getHeader() {
	// // Lets return the unmodifable view of the header map
	// return Collections.unmodifiableMap(header);
	// }

	/**
	 * Maps a key to value in the header map.
	 * 
	 * @param key
	 *            A key, e.g. "Host"
	 * @param value
	 *            A value, e.g. "www.rose-hulman.edu"
	 */
	public void put(String key, String value) {
		this.header.put(key, value);
	}

	/**
	 * Writes the data of the http response object to the output stream.
	 * 
	 * @param outStream
	 *            The output stream
	 * @throws Exception
	 */
	public void write(OutputStream outStream) {
		BufferedOutputStream out = new BufferedOutputStream(outStream, Protocol.CHUNK_LENGTH);

		// First status line
		String line = this.version + Protocol.SPACE + this.status + Protocol.SPACE + this.phrase + Protocol.CRLF;
		try {
			out.write(line.getBytes());
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());
		}

		// Write header fields if there is something to write in header field
		if (header != null && !header.isEmpty()) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				// Write each header field line
				line = key + Protocol.SEPERATOR + Protocol.SPACE + value + Protocol.CRLF;
				try {
					out.write(line.getBytes());
				} catch (IOException e) {
					log.error(e.getMessage());
					log.error(e.getStackTrace().toString());
				}
			}
		}

		// Write a blank line
		try {
			out.write(Protocol.CRLF.getBytes());
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());
		}

		// We are reading a file
		if (this.getStatus() == Protocol.OK_CODE && file != null) {
			// Process text documents
			FileInputStream fileInStream = null;
			try {
				fileInStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				log.error(e.getMessage());
				log.error(e.getStackTrace().toString());
			}
			BufferedInputStream inStream = new BufferedInputStream(fileInStream, Protocol.CHUNK_LENGTH);

			byte[] buffer = new byte[Protocol.CHUNK_LENGTH];
			int bytesRead = 0;
			// While there is some bytes to read from file, read each chunk and send to the
			// socket out stream
			try {
				while ((bytesRead = inStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				log.error(e.getStackTrace().toString());
			}
			// Close the file input stream, we are done reading
			try {
				inStream.close();
			} catch (IOException e) {
				log.error(e.getMessage());
				log.error(e.getStackTrace().toString());
			}
		}

		// Flush the data so that outStream sends everything through the socket
		try {
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error(e.getStackTrace().toString());
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------------------------------\n");
		buffer.append(this.version);
		buffer.append(Protocol.SPACE);
		buffer.append(this.status);
		buffer.append(Protocol.SPACE);
		buffer.append(this.phrase);
		buffer.append(Protocol.LF);

		for (Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}

		buffer.append(Protocol.LF);
		if (file != null) {
			buffer.append("Data: ");
			buffer.append(this.file.getAbsolutePath());
		}
		buffer.append("\n----------------------------------\n");
		return buffer.toString();
	}
	//
	// public void setHeader(String key, String value) {
	// this.header.put(key, value);
	// }

}
