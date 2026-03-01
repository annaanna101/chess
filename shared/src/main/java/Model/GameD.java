package Model;

import chess.ChessGame;

public class GameD{
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;
    public GameD(int gameID, String whiteUsername, String blackUsername,
                 String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }
    public int getGameID() {
        return gameID;
    }
    public String getBlackUsername() {
        return blackUsername;
    }
    public String getWhiteUsername() {
        return whiteUsername;
    }
    public ChessGame getGame() {
        return game;
    }
    public String getGameName() {
        return gameName;
    }
    public void setWhiteUser(String username) {
        this.whiteUsername = username;
    }

    public void setBlackUser(String username) {
        this.blackUsername = username;
    }
}
