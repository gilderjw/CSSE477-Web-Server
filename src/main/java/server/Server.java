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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.Protocol;
import request_handlers.GetRequestHandler;
import request_handlers.HeadRequestHandler;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port) {
		this.rootDirectory = rootDirectory;
		this.port = port;
		this.stop = false;
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

	/**
	 * The entry method for the main server thread that accepts incoming TCP
	 * connection request and creates a {@link ConnectionHandler} for the request.
	 */
	@Override
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(this.port);

			// Now keep welcoming new connections until stop flag is set to true
			while (true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();

				// Come out of the loop if the stop flag is set
				if (this.stop)
					break;

				// Create a handler for this incoming connection and start the handler in a new
				// thread
				ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);
				handler.addHandler(Protocol.GET, new GetRequestHandler());
				handler.addHandler(Protocol.HEAD, new HeadRequestHandler());
				new Thread(handler).start();
			}
			this.welcomeSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
