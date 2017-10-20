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
import server.Server;

public class TestDeleteRequestHandler {
	
	private File file, file1;
	private File dir;
	
	@Before
	public void setup() throws IOException {
		file = new File("temp");
		file.createNewFile();
		dir = new File("Directory");
		dir.mkdir();
		file1 = new File("Directory/index.html");
		file1.createNewFile();
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
	
	@Test
	public void testDeleteDirectory() throws UnsupportedEncodingException, Exception {
		String requestString = "DELETE /Directory HTTP/1.1\r\n\r\n";

		HttpRequest request = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server server = new Server("./", 8080);

		IRequestHandler handler = new DeleteRequestHandler();

		HttpResponse response = handler.handleRequest(request, server);

		assertEquals(Protocol.DELETE_OK_TEXT, response.getPhrase());
	}

	@Test
	public void testDeleteDirectoryFake() throws UnsupportedEncodingException, Exception {
		file1.delete();
		String requestString = "DELETE /Directory HTTP/1.1\r\n\r\n";

		HttpRequest request = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server server = new Server("./", 8080);

		IRequestHandler handler = new DeleteRequestHandler();

		HttpResponse response = handler.handleRequest(request, server);

		assertEquals(Protocol.NOT_FOUND_TEXT, response.getPhrase());
	}
	@After
	public void cleanup() {
		file = new File("temp");
		file.delete();
		file1.delete();
		dir.delete();

	}

}
