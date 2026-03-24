package ui;

import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public Integer getRealGameID(int seqId) {
        GameSummary game = gameMap.get(seqId);
        if (game == null) return null;
        return game.gameID();
    }

    public String register(String... params){
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            try {
                RegisterRequest request = new RegisterRequest(username, password, email);
                RegisterResult result = server.register(request);
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
//            CreateRequest request = new CreateRequest(gameName, authToken.authToken());
            return String.format("Created game: %s", gameName);
        }
        throw new RuntimeException("Expected: create <NAME>");
    }

    public String clear(){
        server.clear();
        state = State.SIGNEDOUT;
        return "Server cleared successfully.";
    }

    private Map<Integer, GameSummary> gameMap = new LinkedHashMap<>();

    public String list() {
        ListGameResult result = server.list_games(authToken);

        gameMap.clear();
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (GameSummary game : result.games()) {
            gameMap.put(i, game);

            sb.append("Game Name: ")
                    .append(game.gameName())
                    .append("  Game ID: ")
                    .append(i)
                    .append("  White Player: ")
                    .append(game.whiteUsername())
                    .append("  Black Player: ")
                    .append(game.blackUsername())
                    .append("\n");

            i++;
        }

        return sb.toString();
    }

    public String join(String...params){
        if (params.length>= 2){
            int seqId = Integer.parseInt(params[0]);
            String playerColor = params[1];

            GameSummary game = gameMap.get(seqId);
            if (game == null){
                return "Invalid game ID";
            }
            int realGameId = game.gameID();
            JoinRequest request = new JoinRequest(realGameId, playerColor.toUpperCase());
            server.joinGame(request, authToken);
            return String.format("You have now joined Game: %s as Team: %s", seqId, request.playerColor().toUpperCase(Locale.ROOT));
        }
        throw new RuntimeException("Expected: join <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws RuntimeException {
        if (params.length < 1) return "No game ID provided";

        try {
            int seqId = Integer.parseInt(params[0]);
            GameSummary game = gameMap.get(seqId);

            if (game == null) {
                return "Game not found. Invalid Game ID";
            }

            return String.format("Observing game: %s (%s vs %s)",
                    seqId,
                    game.whiteUsername(),
                    game.blackUsername()
            );
        } catch (NumberFormatException e) {
            return "Invalid game ID";
        }
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
