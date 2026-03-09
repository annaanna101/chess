package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthD;
import model.GameD;
import model.GameSummary;
import model.UserD;
import org.eclipse.jetty.server.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{
    private int nextGameId = 1;
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserD addUser(UserD user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String json = new Gson().toJson(user);
        executeUpdate(statement, user.username(), user.password(), user.email(), json);
        return new UserD(user.username(), user.password(), user.email());
    }

    public UserD getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String json = rs.getString("json");
                        return new Gson().fromJson(json, UserD.class);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameD getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String json = rs.getString("json");
                        return new Gson().fromJson(json, GameD.class);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameSummary> listGames() throws DataAccessException {
        var result = new ArrayList<GameSummary>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        String json = rs.getString("json");
                        result.add(new Gson().fromJson(json, GameSummary.class));
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public AuthD createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String token = UUID.randomUUID().toString();
        AuthD auth = new AuthD(token, username);
        String json = new Gson().toJson(auth);
        executeUpdate(statement, auth.authToken(), auth.username(), json);
        return auth;
    }

    public AuthD getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        String json = rs.getString("json");
                        return new Gson().fromJson(json, AuthD.class);
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, token);
    }

    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    public void clearAuths() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, json) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        GameD newGame = new GameD(nextGameId, null, null, gameName, chessGame);
        String json = new Gson().toJson(newGame);
        executeUpdate(
                statement, newGame.getGameID(), newGame.getWhiteUsername(),
                newGame.getBlackUsername(), newGame.getGameName(), json
        );
        nextGameId ++;
        return newGame.getGameID();
    }

    public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {

    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`username`),
              UNIQUE INDEX(email),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL UNIQUE,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username),
              CONSTRAINT fk_auth_user
                FOREIGN KEY ('username')
                REFERENCES user('username')
                ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL UNIQUE,
              `game` JSON NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              UNIQUE INDEX(gameName)
              CONSTRAINT fk_game_white_user
                FOREIGN KEY (`whiteUsername`)
                REFERENCES user(`username`)
                ON DELETE CASCADE,
              CONSTRAINT fk_game_black_user
                FOREIGN KEY (`blackUsername`)
                REFERENCES user(`username`)
                ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
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
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
