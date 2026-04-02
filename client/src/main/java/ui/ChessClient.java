package ui;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;
import server.ServerFacade;
import websocket.messages.NotificationMessage;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private String clientName = null;
    private State state = State.SIGNEDOUT;
    private AuthD authToken;
    private final WebSocketFacade ws;

    private GamePlayState gameState = GamePlayState.NOGAMEPLAY;

    public ChessClient(String serverUrl) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, this);
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
            if (state == State.SIGNEDIN) {
                updateList();
            }
            if (state == State.SIGNEDOUT){
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }

            if (gameState != GamePlayState.NOGAMEPLAY){
                return switch (cmd) {
                    case "Redraw Chess Board" -> redraw(params);
                    case "Leave" -> leave(params);
                    case "Make Move" -> makeMove(params);
                    case "Resign" -> resign(params);
                    case "Highlight Legal Moves" -> highlight(params);
                    default ->  help();
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

    public void notify(NotificationMessage notification){
        System.out.println(notification.message());
        System.out.println(">>> ");
    }

    private String highlight(String[] params) {
        return null;
    }

    private String resign(String[] params) {
        if (gameState == GamePlayState.OBSERVING){
            return "Error: You cannot resign when observing a game.";
        }
        return null;
    }

    private String makeMove(String[] params) {
        if (gameState == GamePlayState.OBSERVING){
            return "Error: You cannot make a move when observing a game.";
        }
        return null;
    }

    private String leave(String[] params) {
        //do stuff

        gameState = GamePlayState.NOGAMEPLAY;
        return null;
    }

    private String redraw(String[] params) {
        return null;
    }

    public Integer getRealGameID(int seqId) {
        GameSummary game = gameMap.get(seqId);
        if (game == null) {
            return null;
        }
        return game.gameID();
    }

    public String register(String... params){
        if (params.length == 3) {
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
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = server.login(request);
            this.authToken = new AuthD(result.authToken(), result.username());
            state = State.SIGNEDIN;
            clientName = String.join("-", username);
            return String.format("You are successfully logged in as %s", username);
        }
        throw new RuntimeException("Expected: login <USERNAME> <PASSWORD>");
    }

    public String create(String...params){
        if (params.length == 1){
            String gameName = params[0];
            CreateRequest request = new CreateRequest(gameName, authToken.authToken());
            server.create(request);
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

    public void updateList() {
        ListGameResult result = server.listGames(authToken);

        gameMap.clear();

        int i = 1;
        for (GameSummary game : result.games()) {
            gameMap.put(i, game);
            i++;
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (GameSummary game : gameMap.values()) {

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
        if (params.length == 2){
            int seqId;
            if (params[0].matches("^\\d+$")){
                seqId = Integer.parseInt(params[0]);
            } else {
                return "Invalid game ID. Use Integer";
            }
            String playerColor = params[1];
            GameSummary game = gameMap.get(seqId);
            if (game == null){
                return "Invalid game ID";
            }
            int realGameId = game.gameID();
            JoinRequest request = new JoinRequest(realGameId, playerColor.toUpperCase());
            server.joinGame(request, authToken);
            gameState = GamePlayState.PLAYING;
            ws.joinedGame(clientName);
            return String.format("You have now joined Game: %s as Team: %s", seqId, request.playerColor().toUpperCase(Locale.ROOT));
        }
        throw new RuntimeException("Expected: join <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws RuntimeException {
        if (params.length < 1) {
            return "No game ID provided";
        }
        if (params.length != 1) {
            return "Expected: observe [GameID]";
        }

        try {
            int seqId;
            if(params[0].matches("^\\d+$")){
                seqId = Integer.parseInt(params[0]);
            } else {
                return "Invalid game ID. Use Integer";
            }

            GameSummary game = gameMap.get(seqId);

            if (game == null) {
                return "Game not found. Invalid Game ID";
            }
            gameState = GamePlayState.OBSERVING;
            ws.joinedGame(clientName);
            return String.format("Observing game: %s (%s (white) vs %s (black))",
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


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        if (gameState != GamePlayState.NOGAMEPLAY){
            return """
                    Redraw - Redraws the chess board
                    Leave - Removes the user from the game
                    Make move - Allows the user to make a move
                    Resign - The user forfeits the game and the game is over
                    Highlight Legal Moves - Highlights all legal moves
                    help - with possible game play commands
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
