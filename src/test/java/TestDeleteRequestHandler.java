import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import request_handlers.DeleteRequestHandler;
import request_handlers.IRequestHandler;
import response_creators.DeleteResponse200Creator;
import server.Server;

public class TestDeleteRequestHandler {
	
	// Test response creator and requesthandler
	
	private File file;
	
	@Before
	public void setup() throws IOException {
		file = new File("temp");
		file.createNewFile();
	}

	@Test
	public void testDelete200ResponseCreatorOK() {
		boolean successful = true;
		
		HttpResponse response = DeleteResponse200Creator.createResponse(successful, null);
		
		assertEquals(response.getVersion(), Protocol.VERSION);
		assertEquals(response.getStatus(), Protocol.OK_CODE);
		assertEquals(response.getPhrase(), Protocol.DELETE_OK_TEXT);
		assertEquals(response.getFile(), null);
	}
	
	@Test
	public void testDelete200ResponseCreatorNOK() {
		boolean successful = false;
		
		HttpResponse response = DeleteResponse200Creator.createResponse(successful, null);
		
		assertEquals(Protocol.VERSION, response.getVersion());
		assertEquals(Protocol.OK_CODE, response.getStatus());
		assertEquals(Protocol.DELETE_NOK_TEXT, response.getPhrase());
		assertEquals(null, response.getFile());
	}
	
	@Test
	public void testHandleDeleteRequest() throws UnsupportedEncodingException, Exception {
		String requestString = "DELETE /temp HTTP/1.1\r\n\r\n";
		
		HttpRequest request = HttpRequest.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));
		
		Server server = new Server("./", 8080);
		
		IRequestHandler handler = new DeleteRequestHandler();
		
		HttpResponse response = handler.handleRequest(request, server);
		
		assertEquals(Protocol.DELETE_OK_TEXT, response.getPhrase());
	}
	
	@Test
	public void testHandleDeleteRequestNotFound() throws UnsupportedEncodingException, Exception {
		String requestString = "DELETE /badFile HTTP/1.1\r\n\r\n";
		
		HttpRequest request = HttpRequest.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));
		
		Server server = new Server("./", 8080);
		
		IRequestHandler handler = new DeleteRequestHandler();
		
		HttpResponse response = handler.handleRequest(request, server);
		
		assertEquals(Protocol.NOT_FOUND_TEXT, response.getPhrase());
	}
	
	@After
	public void cleanup() {
		file = new File("temp");
		file.delete();
	}

}
