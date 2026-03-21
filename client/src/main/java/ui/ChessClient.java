package ui;

import com.sun.nio.sctp.NotificationHandler;
import model.LoginRequest;
import model.RegisterRequest;
import model.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

//    public String signIn(String... params){
//        if (params.length >= 1) {
//            try{
//                if (server.signin());
//            }
//        }
//    }
    public String register(String... params){
        if (params.length >= 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = server.register(request);
            return String.format("Successful Registration. Your Username is: %s", result.username());
        }
        throw new execption;
    }

    public String login(String...params){
        try{
            if (params.length >= 1) {
                LoginRequest request = new LoginRequest(username, password);
                server.login(request);
                state = State.SIGNEDIN;
            }
        } catch (Exception e) {
            //fix
            throw new RuntimeException(e);
        }
    }


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

}
