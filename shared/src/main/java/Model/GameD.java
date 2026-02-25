package Model;

import chess.ChessGame;

public record GameD(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {
}
