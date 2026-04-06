package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthD;
import model.GameD;
import model.GameSummary;
import model.UserD;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserD addUser(UserD user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
        storeUserPassword(user.username(), user.password());
        return new UserD(user.username(), user.password(), user.email());
    }

    public UserD getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String uname = rs.getString("username");
                        String pword = rs.getString("password");
                        String em = rs.getString("email");
                        return new UserD(uname, pword, em);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameD getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, game FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String json = rs.getString("game");
                        return new Gson().fromJson(json, GameD.class);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameSummary> listGames() throws DataAccessException {
        var result = new ArrayList<GameSummary>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, game FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        String json = rs.getString("game");
                        GameD game = new Gson().fromJson(json, GameD.class);
                        result.add(new GameSummary(
                                game.getGameID(),
                                game.getWhiteUsername(),
                                game.getBlackUsername(),
                                game.getGameName()
                        ));
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public AuthD createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String token = UUID.randomUUID().toString();
        AuthD auth = new AuthD(token, username);
        executeUpdate(statement, auth.authToken(), auth.username());
        return auth;
    }

    public AuthD getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String authTok = rs.getString("authToken");
                        String uname = rs.getString("username");
                        return new AuthD(authTok,uname);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, token);
    }


    public void clearUsers() throws DataAccessException {
        var statement = "DELETE FROM `user`";
        try {
            executeUpdate(statement);
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }


    }

    public void clearAuths() throws DataAccessException {
        var statement = "DELETE FROM `auth`";
        try {
            executeUpdate(statement);
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
    }

    public void clearGames() throws DataAccessException {
        var statement = "DELETE FROM `game`";
        try {
            executeUpdate(statement);
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        GameD newGame = new GameD(null,null, null, gameName, chessGame);
        String json = new Gson().toJson(newGame);

        int id =  executeUpdate(
                statement, null,
                null, newGame.getGameName(), json
        );
        GameD updatedGame = new GameD(id, null, null, gameName, chessGame);
        String updatedJson = new Gson().toJson(updatedGame);
        executeUpdate("UPDATE game SET game=? WHERE gameID=?", updatedJson, id);

        return id;
    }

    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {
        GameD game = getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }
        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new DataAccessException("Error: bad request");
        }
        if (playerColor.equals("WHITE")){
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            var statement = "UPDATE game SET whiteUsername=?, game=? WHERE gameID=?";
            ChessGame gameBoard = game.getGame();
            GameD updatedGame = new GameD(gameID, username, game.getBlackUsername(), game.getGameName(), gameBoard);
            String json = new Gson().toJson(updatedGame);
            executeUpdate(statement, username, json, gameID);
        } else {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            var statement = "UPDATE game SET blackUsername=?, game=? WHERE gameID=?";
            ChessGame gameBoard = game.getGame();
            GameD updatedGame = new GameD(gameID, game.getWhiteUsername(), username, game.getGameName(), gameBoard);
            String json = new Gson().toJson(updatedGame);
            executeUpdate(statement, username, json, gameID);
        }
    }
    void writeHashedPasswordToDatabase(String username, String hashedPassword) throws DataAccessException {
        var statement = "UPDATE user SET password=? WHERE username=?";
        executeUpdate(statement, hashedPassword, username);
    }

    String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return rs.getString("password");
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    void storeUserPassword(String username, String clearTextPassword) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        writeHashedPasswordToDatabase(username, hashedPassword);
    }
    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        var hashedPassword = readHashedPasswordFromDatabase(username);
        if (hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`username`),
              UNIQUE INDEX(email)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL UNIQUE,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username),
              CONSTRAINT fk_auth_user
                FOREIGN KEY (`username`)
                REFERENCES user(`username`)
                ON DELETE CASCADE
            )""",
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL UNIQUE,
              `game` JSON NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              UNIQUE INDEX(gameName),
              CONSTRAINT fk_game_white_user
                FOREIGN KEY (`whiteUsername`)
                REFERENCES user(`username`)
                ON DELETE CASCADE,
              CONSTRAINT fk_game_black_user
                FOREIGN KEY (`blackUsername`)
                REFERENCES user(`username`)
                ON DELETE CASCADE
            )"""
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }
}
