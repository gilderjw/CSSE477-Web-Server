import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import request_handlers.HeadRequestHandler;
import server.Server;

public class TestHeadRequest {

	@Before
	public void setup() throws IOException {
		File dir = new File("headDir");
		dir.mkdir();
		File f = new File("headDir/index.html");
		f.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
		writer.write("This text should not be returned in the HEAD request");
		writer.close();
	}

	@Test
	public void testHeadRequestHandler() throws Exception {
		String requestString = "HEAD /headDir/index.html HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new HeadRequestHandler().handleRequest(req, serv);

		assertEquals(null, resp.getFile());
		assertEquals(Protocol.OK_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testHeadRequestHandlerNoExist() throws Exception {
		String requestString = "HEAD /fakenews HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new HeadRequestHandler().handleRequest(req, serv);

		assertEquals(null, resp.getFile());
		assertEquals(Protocol.NOT_FOUND_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.NOT_FOUND_TEXT, resp.getPhrase());
	}

	@Test
	public void testHeadRequestHandlerDirectory() throws Exception {
		String requestString = "HEAD /headDir HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new HeadRequestHandler().handleRequest(req, serv);

		assertEquals(null, resp.getFile());
		assertEquals(Protocol.OK_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testHeadRequestHandlerDirectoryNoExist() throws Exception {
		File f = new File("headDir/index.html");
		f.delete();
		String requestString = "HEAD /headDir HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new HeadRequestHandler().handleRequest(req, serv);

		assertEquals(null, resp.getFile());
		assertEquals(Protocol.NOT_FOUND_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.NOT_FOUND_TEXT, resp.getPhrase());
	}

	@After
	public void cleanup() {
		File f = new File("headDir/index.html");
		f.delete();
		f = new File("headDir");
		f.delete();
	}
}
