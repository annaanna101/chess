package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    public final Map<Integer, List<Session>> connections = new HashMap<>();

    public synchronized void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, _ -> new ArrayList<>()).add(session);
    }

    public synchronized boolean remove(Integer gameID, Session session) {
        List<Session> sessions = connections.get(gameID);
        if (sessions == null){
            return false;
        }
        return sessions.removeIf(s -> s.equals(session));
    }


    public synchronized void broadcast(Integer gameId, Session excludeSession, Object notification) throws IOException {
        List<Session> sessions = connections.get(gameId);
        if (sessions == null){
            return;
        }
        String msg = new Gson().toJson(notification);
        for (Session c : new ArrayList<>(sessions)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
