package dataaccess;

import model.AuthD;
import model.GameD;
import model.GameSummary;
import model.UserD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserD addUser(UserD user) throws DataAccessException {
        return null;
    }

    public UserD getUser(String username) throws DataAccessException {
        return null;
    }

    public GameD getGame(int gameID) throws DataAccessException {
        return null;
    }

    public Collection<GameSummary> listGames() throws DataAccessException {
        return List.of();
    }

    public AuthD createAuth(String username) throws DataAccessException {
        return null;
    }

    public AuthD getAuth(String token) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String token) throws DataAccessException {

    }

    public void clearUsers() throws DataAccessException {

    }

    public void clearAuths() throws DataAccessException {

    }

    public void clearGames() throws DataAccessException {

    }

    public int createGame(String gameName) throws DataAccessException {
        return 0;
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
              `game` JSON NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
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
