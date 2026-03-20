package ui;

import com.sun.nio.sctp.NotificationHandler;

import java.util.Arrays;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private final State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> signIn(params);
                case "login" -> rescuePet(params);
                case "help" -> signOut();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params){
        if (params.length >= 1) {
            state =
        }
    }

}
