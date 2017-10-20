import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import request_handlers.GetRequestHandler;
import server.Server;

public class TestGetRequestHandler {
	public static final String FILE_CONTENTS = "stuff";

	@Before
	public void setup() throws IOException {
		File f = new File("testGetDir");
		f.mkdir();
		f = new File("testGetDir/index.html");
		f.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
		writer.write(FILE_CONTENTS);
		writer.close();
	}

	@Test
	public void testGetFile() throws UnsupportedEncodingException, Exception {
		String requestString = "GET /testGetDir/index.html HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new GetRequestHandler().handleRequest(req, serv);

		File f = resp.getFile();
		String contents = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);
		assertEquals(FILE_CONTENTS, contents);
		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testGetDirectory() throws UnsupportedEncodingException, Exception {
		String requestString = "GET /testGetDir HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new GetRequestHandler().handleRequest(req, serv);

		File f = resp.getFile();
		String contents = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);
		assertEquals(FILE_CONTENTS, contents);
		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testGetDirectoryNoIndex() throws UnsupportedEncodingException, Exception {
		File f = new File("testGetDir/index.html");
		f.delete();
		String requestString = "GET /testGetDir HTTP/1.1\r\n\r\n";

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		Server serv = new Server("./", 8080);
		HttpResponse resp = new GetRequestHandler().handleRequest(req, serv);

		f = resp.getFile();

		assertEquals(Protocol.NOT_FOUND_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.NOT_FOUND_TEXT, resp.getPhrase());
	}

	@After
	public void cleanup() {
		File f = new File("testGetDir");
		f.delete();
		f = new File("testGetDir/index.html");
		f.delete();

	}
}
