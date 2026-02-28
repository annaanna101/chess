package Model;

public record GameSummary(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {}
