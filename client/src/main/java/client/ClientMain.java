package client;

import chess.ChessGame;
import chess.ChessPiece;
import client.websocket.ResponseException;
import ui.PreLoginRepl;

public class ClientMain {
    public static void main(String[] args) throws ResponseException {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("♕ 240 Chess Client: ");
        String serverUrl = "http://localhost:8080";
        new PreLoginRepl(serverUrl).run();
    }
}
