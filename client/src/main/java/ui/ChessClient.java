package ui;

import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
//implements NotificationHandler
public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private AuthD authToken;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public State getState(){
        return state;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.SIGNEDOUT){
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }

            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe();
                case "logout" -> logout();
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params){
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            try {
                RegisterRequest request = new RegisterRequest(username, password, email);
                RegisterResult result = server.register(request);
                if (result == null || result.authToken() == null) {
                    return "Registration failed: server returned null or invalid response.";
                }
                this.authToken = new AuthD(result.authToken(), result.username());
                state = State.SIGNEDIN;
                return String.format("Successful Registration. Your Username is: %s", result.username());

            } catch (Exception e) {
                return "Registration failed: " + e.getMessage();
            }
        }
        throw new RuntimeException("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String...params){
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = server.login(request);
            this.authToken = new AuthD(result.authToken(), result.username());
            state = State.SIGNEDIN;
            return String.format("You are successfully logged in as %s", username);
        }
        throw new RuntimeException("Expected: login <USERNAME> <PASSWORD>");
    }

    public String create(String...params){
        if (params.length >= 1){
            String gameName = params[0];
            CreateRequest request = new CreateRequest(gameName, authToken.authToken());
            CreateResult result = server.create(request);
            return String.format("Created game: %s", result);
        }
        throw new RuntimeException("Expected: create <NAME>");
    }

    public String clear(){
        server.clear();
        state = State.SIGNEDOUT;
        return "Server cleared successfully.";
    }
    // fix list (list has its own game ID's - map maybe?)
    public String list(){
        ListGameResult result = server.list_games(authToken);
        return String.format(String.valueOf(result));
    }

    public String join(String...params){
        if (params.length>= 2){
            int id = Integer.parseInt(params[0]);
            String playerColor = params[1];
            JoinRequest request = new JoinRequest(id, playerColor);
            server.joinGame(request, authToken);
            DrawBoard.drawCorrectBoard(request.playerColor());
            return String.format("You have now joined Game: %s as Team: %s", request.gameID(), request.playerColor());
        }
        throw new RuntimeException("Expected: join <ID> [WHITE|BLACK]");
    }

    public String observe(String...params){
        if (params.length >= 1){
            int id = Integer.parseInt(params[0]);
            server.observe(id);
            // watches the game at id
        }
        return null;
    }

    public String logout(){
        LogoutRequest request = new LogoutRequest(authToken.authToken());
        server.logout(request);
        state = State.SIGNEDOUT;
        return "You have been logged out. Goodbye!";
    }
    public ChessGame getGame(int gameID){
        return server.getGame(gameID, authToken);
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
