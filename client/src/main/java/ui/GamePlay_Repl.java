package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class GamePlay_Repl {
    private final ChessClient client;
    private final Integer gameID;

    public GamePlay_Repl(ChessClient client, Integer gameID) {
        this.client = client;
        this.gameID = gameID;
    }

    public void run(){
        ChessGame game = client.getGame(gameID);
        drawBoard(game.getBoard());
    }

    private void drawBoard(ChessBoard board) {
    }

    private String flipBoard(){
        return  null;
    }
}
