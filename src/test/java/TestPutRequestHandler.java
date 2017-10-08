import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import request_handlers.IRequestHandler;
import request_handlers.PutRequestHandler;
import server.Server;

public class TestPutRequestHandler {
	
	private File file;
	private Reader reader;
	
	@Before
	public void setup() throws IOException {
		file = new File("temp");
		file.createNewFile();
		Writer writer = new BufferedWriter(new FileWriter("temp"));
		
		writer.write("This is the temp file");
		writer.close();
	}

	@Test
	public void testPutRequestFileExists() throws UnsupportedEncodingException, Exception {
		String requestString = "PUT /temp HTTP/1.1\r\ncontent-length: 7\r\n\r\nNewText";
		
		HttpRequest request = HttpRequest.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));
		
		Server server = new Server("./", 8080);
		
		IRequestHandler handler = new PutRequestHandler();
		
		HttpResponse response = handler.handleRequest(request, server);
		reader = new BufferedReader(new FileReader("temp"));
		String line = ((BufferedReader) reader).readLine();
		
		assertEquals(Protocol.OK_TEXT, response.getPhrase());
		assertEquals("NewText", line);
	}
	
	@Test
	public void testPutRequestFileNonexistant() throws UnsupportedEncodingException, Exception {
		String requestString = "PUT /test HTTP/1.1\r\ncontent-length: 7\r\n\r\nnewTest";
		
		HttpRequest request = HttpRequest.read(new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8.name())));
		
		Server server = new Server("./", 8080);
		
		IRequestHandler handler = new PutRequestHandler();
		
		HttpResponse response = handler.handleRequest(request, server);
		reader = new BufferedReader(new FileReader("test"));
		String line = ((BufferedReader) reader).readLine();
		
		assertEquals(Protocol.OK_TEXT, response.getPhrase());
		assertEquals("newTest", line);
	}
	
	@After
	public void cleanup() throws IOException {
		file = new File("temp");
		file.delete();
		reader.close();
	}

}
