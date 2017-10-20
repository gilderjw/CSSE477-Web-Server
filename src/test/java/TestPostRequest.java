import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		File dir = new File("./dir");
		dir.mkdir();
		File f = new File("./dir/index.html");
		f.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(f.getPath()));
		writer.write(contents);
		writer.close();
	}

	@Test
	public void testPostRequestHandlerDir() throws Exception {
		String requestString = "POST /dir HTTP/1.1\r\ncontent-length: " + appended.length() + "\r\n\r\n" + appended;

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		// System.out.println(req);

		Server serv = new Server("./", 8080);
		HttpResponse resp = new PostRequestHandler().handleRequest(req, serv);
		String newcontents = new String(Files.readAllBytes(resp.getFile().toPath()));
		
		assertEquals(Protocol.OK_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
		assertEquals(contents + appended, newcontents);
	}

	@Test
	public void testPostRequestHandler() throws Exception {
		String requestString = "POST /dir/index.html HTTP/1.1\r\ncontent-length: " + appended.length() + "\r\n\r\n"
				+ appended;

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

//		System.out.println(req);

		Server serv = new Server("./", 8080);
		HttpResponse resp = new PostRequestHandler().handleRequest(req, serv);

		File respFile = resp.getFile();

		String newcontents = new String(Files.readAllBytes(respFile.toPath()));

		// assertEquals((contents + appended).length(), respFile.length());

		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
	}

	@Test
	public void testPostRequestNew() throws Exception {
		String requestString = "POST /tempNoExist HTTP/1.1\r\ncontent-length: " + appended.length() + "\r\n\r\n"
				+ appended;

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

//		System.out.println(req);

		Server serv = new Server("./", 8080);
		HttpResponse resp = new PostRequestHandler().handleRequest(req, serv);

		File respFile = resp.getFile();

		String newcontents = new String(Files.readAllBytes(respFile.toPath()));

		assertEquals(200, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
		assertEquals(appended, newcontents);
		respFile.delete();
	}

	@Test
	public void testPostRequestNewDir() throws Exception {
		File f = new File("dir/index.html");
		f.delete();
		String requestString = "POST /dir HTTP/1.1\r\ncontent-length: " + appended.length() + "\r\n\r\n" + appended;

		HttpRequest req = HttpRequest
				.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));

		// System.out.println(req);

		Server serv = new Server("./", 8080);
		HttpResponse resp = new PostRequestHandler().handleRequest(req, serv);
		File tmp = resp.getFile();
		String contents = new String(Files.readAllBytes(Paths.get(tmp.getPath())), StandardCharsets.UTF_8);

		assertEquals(Protocol.OK_CODE, resp.getStatus());
		assertEquals(Protocol.VERSION, resp.getVersion());
		assertEquals(Protocol.OK_TEXT, resp.getPhrase());
		assertEquals(appended, contents);
	}

	@After
	public void cleanup() {
		File f = new File("dir/index.html");
		f.delete();
		f = new File("dir");
		f.delete();
	}
}
