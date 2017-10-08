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
import response_creators.Response200NoPayloadCreator;
import server.Server;

public class TestHeadRequest {

	@Before
	public void setup() throws IOException {
		File f = new File("temp");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f.getName()));
		writer.write("This text should not be returned in the HEAD request");
		writer.close();
	}

	@Test
	public void test200NoPayloadCreator() {
		File f = new File("temp");

		HttpResponse resp = Response200NoPayloadCreator.createResponse(f, Protocol.CLOSE);
		assertEquals(null, resp.getFile());
		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testHeadRequestHandler() throws Exception {
		String requestString = "HEAD /temp HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new HeadRequestHandler().handleRequest(req, serv);

		assertEquals(null, resp.getFile());
		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@After
	public void cleanup() {
		File f = new File("temp");
		f.delete();
	}
}
