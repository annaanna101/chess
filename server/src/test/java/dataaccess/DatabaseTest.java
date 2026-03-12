package dataaccess;

import chess.ChessGame;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clearAuths();
        dataAccess.clearGames();
        dataAccess.clearUsers();
    }
    //add user
    @Test
    void addUserPositive() throws DataAccessException{
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        assertNotNull(dataAccess.getUser("testUser1"));
    }
    @Test
    void addUserSameUsername() throws DataAccessException{
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        assertThrows(DataAccessException.class, () -> dataAccess.addUser(user));
    }

    //get user
    @Test
    void getUserPositive() throws DataAccessException{
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        dataAccess.getUser("testUser1");
        assertEquals("testUser1", user.username());
    }
    @Test
    void getUserNeg() throws DataAccessException{
        assertNull(dataAccess.getUser("fakeUsername"));
    }
    // get game
    @Test
    void getGamePositive() throws DataAccessException {
        int gameId = dataAccess.createGame("Game1");
        GameD game = dataAccess.getGame(gameId);
        assertEquals("Game1", game.getGameName());
    }
    @Test
    void getGameNeg() throws DataAccessException{
        assertNull(dataAccess.getGame(42));
    }
    // listgames
    @Test
    void listGamesPos() throws DataAccessException{
        dataAccess.createGame("Game 1");
        dataAccess.createGame("Game 2");
        dataAccess.createGame("Game 3");
        Collection<GameSummary> games = dataAccess.listGames();
        assertEquals(3, games.size());
    }
    @Test
    void listGamesNeg() throws DataAccessException{
        Collection<GameSummary> games = dataAccess.listGames();
        assertEquals(0, games.size());
    }

    //createauth
    @Test
    void createAuthPositive() throws DataAccessException{
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        AuthD auth = dataAccess.createAuth("testUser1");
        assertNotNull(auth);
        assertNotNull(dataAccess.getAuth(auth.authToken()));
    }
    @Test
    void createAuthNeg(){
        assertThrows(DataAccessException.class, () -> dataAccess.createAuth("noUsername"));
    }
    //get auth
    @Test
    void getAuthPositive() throws DataAccessException{
        dataAccess.addUser(new UserD("u1", "p1", "e1"));
        AuthD auth = dataAccess.createAuth("u1");
        assertEquals("u1", dataAccess.getAuth(auth.authToken()).username());
    }
    @Test
    void getAuthNeg() throws DataAccessException{
        assertNull(dataAccess.getAuth("fakeAuthToken"));
    }
    //delete auth
    @Test
    void deleteAuthPositive() throws DataAccessException {
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        AuthD auth = dataAccess.createAuth("testUser1");
        dataAccess.deleteAuth(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));
    }
    //createGame
    @Test
    void createGamePositive() throws DataAccessException{
        int gameId = dataAccess.createGame("Game1");
        assertNotNull(dataAccess.getGame(gameId));
    }
    @Test
    void createGameNeg() throws DataAccessException{
        dataAccess.createGame("Game1");
        assertThrows(DataAccessException.class, () -> dataAccess.createGame("Game1"));
    }
    //updateGame
    @Test
    void updateGamePos() throws DataAccessException{
        UserD user = new UserD("testUser1", "password1", "email");
        dataAccess.addUser(user);
        int id = dataAccess.createGame("game1");
        dataAccess.updateGame(id, "WHITE", "testUser1");
        GameD game = dataAccess.getGame(id);
        assertEquals("testUser1", game.getWhiteUsername());
    }
    @Test
    void updateGameNeg() {
        assertThrows(DataAccessException.class, () ->
                new MySqlDataAccess().updateGame(42, "WHITE", "user"));
    }
    //verify user
    @Test
    void verifyUserPos() throws DataAccessException{
        dataAccess.addUser(new UserD("user1", "pass", "email"));

        assertTrue(dataAccess.verifyUser("user1", "pass"));
    }

    @Test
    void verifyUserNeg() throws DataAccessException {
        dataAccess.addUser(new UserD("user1", "pass", "email"));
        assertFalse(dataAccess.verifyUser("user1", "wrong"));
    }
}
