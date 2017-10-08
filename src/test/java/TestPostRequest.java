import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import request_handlers.PostRequestHandler;
import server.Server;

public class TestPostRequest {
	static String contents = "Text will be appended here:";
	static String appended = "appended text";

	@Before
	public void setup() throws IOException {
		File f = new File("temp");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f.getName()));
		writer.write(contents);
		writer.close();
	}

	@Test
	public void testPostRequestHandler() throws Exception {
		String requestString = "POST /temp HTTP/1.1\r\ncontent-length: " + appended.length() + "\r\n\r\n" + appended;

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		System.out.println(req);

		Server serv = new Server("./", 8080);
		HttpResponse resp = new PostRequestHandler().handleRequest(req, serv);

		File respFile = resp.getFile();

		String newcontents = new String(Files.readAllBytes(respFile.toPath()));

		// assertEquals((contents + appended).length(), respFile.length());

		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
		assertEquals(contents + appended, newcontents);
	}

	@After
	public void cleanup() {
		File f = new File("temp");
		f.delete();
	}
}
