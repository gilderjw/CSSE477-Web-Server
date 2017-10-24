package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import protocol.ProtocolException;
import request_handlers.IRequestHandler;
import response_creators.ResponseCreator;

/**
 * This class is responsible for handling a incoming request by creating a
 * {@link HttpRequest} object and sending the appropriate response be creating a
 * {@link HttpResponse} object. It implements {@link Runnable} to be used in
 * multi-threaded environment.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	static final Logger log = LogManager.getLogger(ConnectionHandler.class);
	
	private Server server;
	private Socket socket;
	private HashMap<String, IRequestHandler> handlers;

	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.handlers = new HashMap<>();
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return this.socket;
	}

	public void setHandlers(Map<String, IRequestHandler> handlers) {
		this.handlers = (HashMap<String, IRequestHandler>) handlers;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request and
	 * creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client (web
	 * browser).
	 */
	@Override
	public void run() {
		// TODO: This class is a classic example of how not to code things. Refactor
		// this code to make it
		// cohesive and extensible
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
		} catch (Exception e) {
			// Cannot do anything if we have exception reading input or output stream
			// May be have text to log this for further analysis?
			log.error("Couldn't get streams in connection handler.", e);
			return;
		}

		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		ResponseCreator rc = new ResponseCreator();
		rc.fillGeneralHeader(rc.getResponse(), Protocol.CLOSE)
			.setResponseVersion(Protocol.VERSION);
		try {
			request = HttpRequest.read(inStream);
		} catch (ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and create
			// response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if (status == Protocol.BAD_REQUEST_CODE) {
				response = rc.setResponseFile(null).setResponseStatus(Protocol.BAD_REQUEST_CODE)
					.setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
					.getResponse();
			} else if (status == Protocol.NOT_FOUND_CODE){
				response = rc.setResponseFile(null).setResponseStatus(Protocol.NOT_FOUND_CODE)
						.setResponsePhrase(Protocol.NOT_FOUND_TEXT)
						.getResponse();
			}
			
		} catch (Exception e) {
			log.error("Couldn't read stream in connection handler.", e);
			// For any other error, we will create bad request response as well
			response = rc
					.setResponseFile(null).setResponseStatus(Protocol.BAD_REQUEST_CODE)
					.setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
					.getResponse();
		}

		if (response != null) {
			// Means there was an error, now write the response object to the socket
			try {
				response.write(outStream);
			} catch (Exception e) {
				// We will ignore this exception
				log.error("Couldn't write to stream in connection handler.", e);
			}

			return;
		}

		// We reached here means no error so far, so lets process further
		try {
			// Fill in the code to create a response for version mismatch.
			// You may want to use constants such as Protocol.VERSION,
			// Protocol.NOT_SUPPORTED_CODE, and more.
			// You can check if the version matches as follows
			if (!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
				// Here you checked that the "Protocol.VERSION" string is not equal to the
				// "request.version" string ignoring the case of the letters in both strings
				// TODO: Fill in the rest of the code here
				response = rc.setResponseFile(null).setResponseStatus(Protocol.BAD_REQUEST_CODE)
						.setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
						.getResponse();
			} else {
				response = this.handlers.get(request.getMethod()).handleRequest(request, this.server);
			}
		} catch (Exception e) {
			log.error("Response couldn't be created or couldn't be handled.", e);
		}

		// TODO: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if (response == null) {
			response = rc.setResponseFile(null).setResponseStatus(Protocol.BAD_REQUEST_CODE)
					.setResponsePhrase(Protocol.BAD_REQUEST_TEXT)
					.getResponse();
		}

		try {
			// Write response and we are all done so close the socket
			response.write(outStream);
			// System.out.println(response);
			this.socket.close();
		} catch (Exception e) {
			// We will ignore this exception
			log.error("Socket couldn't be closed", e);
		}
	}
}
