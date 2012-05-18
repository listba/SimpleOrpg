package com.openorpg.simpleorpg.server.net;

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
	
	
	public abstract void handleMessage(Socket socket);

}
