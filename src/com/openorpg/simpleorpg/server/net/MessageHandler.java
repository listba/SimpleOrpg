package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.openorpg.simpleorpg.server.Map;
import com.openorpg.simpleorpg.server.Player;


public abstract class MessageHandler {

	private static final Logger messageLogger = Logger.getLogger(MessageHandler.class);
	protected final Logger logger = Logger.getLogger(getClass());
	protected static final HashMap<String, Map> maps = new HashMap<String, Map>();
	protected static final HashMap<Socket, Player> players = new HashMap<Socket, Player>();
	
	// Load in maps from the database
	public static void init() {
		maps.put("testmap", new Map());
		maps.put("testmap1", new Map());
		maps.put("testmap2", new Map());
	}
	
	public static MessageHandler create(String message) {
		String id = message;
		if (message.contains(":")) {
			id = message.split(":")[0].toUpperCase();
		}
		int idIndex = message.indexOf(":");
		String payload = "";
		if (idIndex+1 < message.length()) {
			payload = message.substring(idIndex+1);
		}
		
		if (id.equals("MOVE")) {
			return new MoveHandler(payload);
		} else if (id.equals("SAY")) {
			return new SayHandler(payload);
		} else {
			
		}
		
//		if (id.equals("JOIN_GAME")) {
//			return new JoinGameHandler();
//		} else if (id.equals("LEAVE_GAME")) {
//			return new LeaveGameHandler();
//		} else if (id.equals("JOIN_MAP")) {
//			return new JoinMapHandler();
//		} else {
			messageLogger.warn(id);
			return null;
//		}
	}
	
	protected synchronized void sendAllMapBut(Socket socket, String message) {
		Player yourPlayer = players.get(socket);
		Map map = maps.get(yourPlayer.getMapRef());
		
		for (Socket otherSocket : map.getPlayers().keySet()) {
			if (players.get(otherSocket).getId() != yourPlayer.getId()) {
				try {
					sendTo(otherSocket, message);
				} catch (Exception ex) {
					logger.error(ex);
				}
			}
		}
	}
	
	protected synchronized void sendAll(String message) {
		for (Socket playerSocket : players.keySet()) {
			try {
				sendTo(playerSocket, message);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}
	}
	
	protected void sendTo(Socket socket, String message) {
		if (socket != null && !socket.isClosed() && !message.isEmpty()) {
			try {
				PrintWriter playerOut = new PrintWriter(socket.getOutputStream(), true);
				playerOut.println(message);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}
	}
	
	
	public abstract void handleMessage(Socket socket);

}
