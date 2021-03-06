/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 *
 * Copyright (C) 2012 Chandan Raj Rupakheti
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 */

package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import plugins.IPlugin;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	static final Logger log = LogManager.getLogger(Server.class);

	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	// private HashMap<String, IRequestHandler> handlers;
	private Map<String, IPlugin> plugins;
	private HashSet<String> bannedIPs;
	private HashMap<String, Integer> requestsInLastSecond;
	private long lastTime;
	private boolean dosProtect = true;

	public void setPlugins(Map<String, IPlugin> map) {
		this.plugins = map;
	}

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port) {
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.stop = true;
		this.plugins = new HashMap<>();
		this.bannedIPs = new HashSet<>();
		this.requestsInLastSecond = new HashMap<>();
	}

	/**
	 * Gets the root directory for this web server.
	 *
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return this.rootDirectory;
	}

	/**
	 * Gets the port number for this web server.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	public void setDosProtect(boolean on) {
		this.dosProtect = on;
	}

	public boolean isAllowed(String addr, Socket connectionSocket) throws IOException, InterruptedException {
		if (!this.dosProtect) {
			return true;
		}

		if (this.bannedIPs.contains(addr)) {
			connectionSocket.close();
			System.out.println("User " + addr + " denied");
			return false;
		} else {
			System.out.println(addr);
		}

		// If it has been 1 seconds, wipe requests
		if (System.currentTimeMillis() - lastTime >= 1000) {
			this.requestsInLastSecond.clear();
			lastTime = System.currentTimeMillis();
		}

		if (this.requestsInLastSecond.containsKey(addr)) {
			int requests = this.requestsInLastSecond.get(addr);
			if (++requests > 10) { // malicious user
				connectionSocket.close();
				this.bannedIPs.add(addr);

				Process p = Runtime.getRuntime().exec("sudo iptables -A INPUT -s " + addr + " -j DROP");
				p.waitFor();
				System.out.println("User " + addr + " banned");
				return false;
			}
			this.requestsInLastSecond.put(addr, requests);
		} else {
			this.requestsInLastSecond.put(addr, 1);
		}
		return true;
	}

	/**
	 * The entry method for the main server thread that accepts incoming TCP
	 * connection request and creates a {@link ConnectionHandler} for the request.
	 */
	@Override
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(this.port);
			this.stop = false;
			// Now keep welcoming new connections until stop flag is set to true
			lastTime = System.currentTimeMillis();
			while (true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				String addr = connectionSocket.getRemoteSocketAddress().toString();
				addr = addr.substring(1, addr.lastIndexOf(':'));

				// Come out of the loop if the stop flag is set
				if (this.stop)
					break;

				if (this.isAllowed(addr, connectionSocket)) {
					// Create a handler for this incoming connection and start the handler in a new
					// thread
					ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);

					handler.setPlugins(this.plugins);
					new Thread(handler).start();
				}
			}
			this.welcomeSocket.close();
		} catch (Exception e) {
			log.error("Welcome socket couldn't be closed", e);
		}
	}

	public void registerPlugin(String uri, IPlugin plugin) {
		this.plugins.put(uri, plugin);
	}

	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if (this.stop)
			return;

		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept() method
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), this.port);

			// We do not have any other job for this socket so just close it
			socket.close();
		} catch (Exception e) {
			log.error("Socket couldn't be closed", e);
		}
	}

	/**
	 * Checks if the server is stopeed or not.
	 *
	 * @return
	 */
	public boolean isStoped() {
		if (this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}

	public void logInfo(String info) {
		log.info(info);
	}

	public void logException(String info, Exception e) {
		log.error(info, e);
	}
}
