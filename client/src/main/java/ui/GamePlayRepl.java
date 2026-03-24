package ui;

import chess.ChessGame;

public class GamePlayRepl {
    private final ChessClient client;
    private final Integer gameID;

    public GamePlayRepl(ChessClient client, Integer gameID) {
        this.client = client;
        this.gameID = gameID;
    }

    public void run(){
        ChessGame game = new ChessGame();

    }

}
