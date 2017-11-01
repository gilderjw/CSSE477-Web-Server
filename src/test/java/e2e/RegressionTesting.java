package e2e;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RegressionTesting {
	public static final int PORT = 80;

	public static HttpURLConnection request(String type, String file, String[][] headers, String body) {
		HttpURLConnection connection = null;
		try {

			// Create connection
			URL url = new URL("http://35.199.2.29:" + PORT + "/" + file);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(type);

			if (headers != null) {
				for (String[] s : headers) {
					connection.setRequestProperty(s[0], s[1]);
				}
			}

			connection.setDoOutput(true);

			if (body != null) {

				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(body);
				wr.close();

			}

			connection.connect();

			return connection;

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	@BeforeClass
	public static void setUp() throws InterruptedException, FileNotFoundException {

	}

	@AfterClass
	public static void tearDown() throws IOException, InterruptedException {

		request("DELETE", "delete.txt", null, null);
		request("DELETE", "get.txt", null, null);
		request("DELETE", "post.txt", null, null);
		request("DELETE", "put.txt", null, null);

		File postTxt = new File("./webtest/post.txt");
		postTxt.delete();
		postTxt.createNewFile();
		FileWriter writer = new FileWriter(postTxt);
		writer.write("post that good stuff here:");
		writer.close();

		File rm = new File("./webtest/post.fakenews");
		rm.delete();

		File del = new File("./webtest/delete.txt");
		del.createNewFile();
		writer = new FileWriter(del);
		writer.write("delete me");
		writer.close();

		File put = new File("./webtest/put.txt");
		put.createNewFile();
		writer = new FileWriter(put);
		writer.write("replace me");
		writer.close();

		rm = new File("./webtest/put.fakenews");
		rm.delete();

	}

	@Test(timeout = 1000)
	public void testGet() throws IOException {
		fail();
		// good get
		HttpURLConnection connection = RegressionTesting.request("GET", "get.txt", null, null);
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder(); // or StringBuffer if Java

		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals("Got this file, fam\r\n", response.toString());

		// 404 get
		connection = RegressionTesting.request("GET", "get.fakenews", null, null);
		assertEquals(404, connection.getResponseCode());

	}

	@Test(timeout = 1000)
	public void testPost() throws IOException {
		String stuffToPost = "posted stuff";

		// exist POST
		HttpURLConnection connection = RegressionTesting.request("POST", "post.txt", null, stuffToPost);
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals("post that good stuff here:" + stuffToPost + "\r\n", response.toString());

		// noexist post
		connection = RegressionTesting.request("POST", "post.fakenews", null, stuffToPost);
		is = connection.getInputStream();
		rd = new BufferedReader(new InputStreamReader(is));
		response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals(stuffToPost + "\r\n", response.toString());
	}

	@Test(timeout = 1000)
	public void testDelete() throws IOException {

		// get thing

		HttpURLConnection connection = RegressionTesting.request("GET", "delete.txt", null, null);
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder(); // or StringBuffer if Java

		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals("delete me\r\n", response.toString());

		// remove thing
		connection = RegressionTesting.request("DELETE", "delete.txt", null, null);
		assertEquals(200, connection.getResponseCode());

		// it's not there
		connection = RegressionTesting.request("GET", "delete.txt", null, null);
		assertEquals(404, connection.getResponseCode());

		// noexist DELETE
		connection = RegressionTesting.request("DELETE", "delete.fakenews", null, null);
		assertEquals(404, connection.getResponseCode());
	}

	@Test(timeout = 1000)
	public void testPut() throws IOException {
		String stuffToPost = "put stuff";
		// exist POST
		HttpURLConnection connection = RegressionTesting.request("PUT", "put.txt", null, stuffToPost);
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals(stuffToPost + "\r\n", response.toString());

		// noexist post
		connection = RegressionTesting.request("PUT", "put.fakenews", null, stuffToPost);
		is = connection.getInputStream();
		rd = new BufferedReader(new InputStreamReader(is));
		response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		rd.close();
		assertEquals(200, connection.getResponseCode());
		assertEquals(stuffToPost + "\r\n", response.toString());
	}
}
