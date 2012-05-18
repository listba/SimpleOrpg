package com.openorpg.simpleorpg.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.openorpg.simpleorpg.server.net.JoinGameHandler;
import com.openorpg.simpleorpg.server.net.LeaveGameHandler;
import com.openorpg.simpleorpg.server.net.MessageHandler;

public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ServerSocket serverSocket;
		int port = 1234;
		
		MessageHandler.init();
		
		try {
			serverSocket = new ServerSocket(port);
			logger.info("Listening on port " + port);
			
			// Accept a new connection
			new Thread() {
				public void run() {
					while (true) {
						try {
							final Socket clientSocket = serverSocket.accept();
							clientSocket.setTcpNoDelay(true);
							logger.info("Accepted " + clientSocket.getInetAddress().getHostAddress());
							
							new Thread() {
								public void run() {
									BufferedReader in = null;
									
									try {
										in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));										
										MessageHandler joinHandler = new JoinGameHandler();
										joinHandler.handleMessage(clientSocket);
										
										String receivedLine;
										while ((receivedLine = in.readLine()) != null) {
											logger.info(receivedLine);
											MessageHandler handler = MessageHandler.create(receivedLine);
											if (handler != null) {
												handler.handleMessage(clientSocket);
											}
										}
										in.close();
										
									} catch (Exception ex) {
										logger.error(ex);
									}
									MessageHandler leaveHandler = new LeaveGameHandler();
									leaveHandler.handleMessage(clientSocket);
									logger.info("Closed " + clientSocket.getInetAddress().getHostAddress());
									
								}
							}.start();
						} catch (Exception ex) {
							logger.error(ex);
						}
					}
				}
			}.start();
			
		} catch (Exception ex) {
			logger.fatal(ex);
		}
	}

}
