package ui;

import com.sun.nio.sctp.NotificationHandler;

public class ChessClient implements NotificationHandler {

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
    }
}
