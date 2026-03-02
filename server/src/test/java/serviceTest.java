import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class serviceTest {
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;
    private static final String AUTH = "token";

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new DataAccess() {
            Map<Integer, GameD> games = new HashMap<>();
            Map<String, AuthD> auths = new HashMap<>();
            Map<String, UserD> users = new HashMap<>();
            int nextGameID = 1;
            @Override
            public UserD addUser(UserD user) throws DataAccessException {
                users.put(user.username(), user);
                return user;
            }

            @Override
            public UserD getUser(String username) throws DataAccessException {
                return users.get(username);
            }

            @Override
            public GameD getGame(int gameID) throws DataAccessException {
                return games.get(gameID);
            }

            @Override
            public Collection<GameSummary> listGames() throws DataAccessException {
                return games.values().stream()
                        .map(g -> new GameSummary(
                                g.getGameID(),
                                g.getGameName(),
                                g.getWhiteUsername(),
                                g.getBlackUsername()))
                                .toList();
            }

            @Override
            public AuthD createAuth(String username) throws DataAccessException {
                AuthD auth = new AuthD(AUTH, username);
                auths.put(AUTH, auth);
                return auth;
            }

            @Override
            public AuthD getAuth(String token) throws DataAccessException {
                return auths.get(token);
            }

            @Override
            public void deleteAuth(String token) throws DataAccessException {
                auths.remove(token);
            }

            @Override
            public void clearUsers() throws DataAccessException {
                users.clear();
            }

            @Override
            public void clearAuths() throws DataAccessException {
                auths.clear();
            }

            @Override
            public void clearGames() throws DataAccessException {
                games.clear();
            }

            @Override
            public int createGame(String gameName) throws DataAccessException {
                int id = nextGameID++;
                games.put(id, new GameD(id, null, null, gameName, new ChessGame()));
                return id;
            }

            @Override
            public void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException {
                GameD game = games.get(gameID);
                if (playerColor.equals("WHITE")){
                    game.setWhiteUser(username);
                } else if (playerColor.equals("BLACK")){
                    game.setBlackUser(username);
                }
            }
        };
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        dataAccess.createAuth("player1");
    }
    @Test
    void testCreateGameNull() throws DataAccessException{
        CreateRequest request = new CreateRequest(null, AUTH);
        Exception ex = assertThrows(DataAccessException.class, () ->{
            gameService.createGame(request, AUTH);
        });
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testCreateGamePositive() throws DataAccessException{
        CreateRequest request = new CreateRequest("Game1",AUTH);
        CreateResult result = gameService.createGame(request, AUTH);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void testListGamesPositive() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game3", AUTH), AUTH);
        Collection<GameSummary> games = gameService.listGame(AUTH);
        assertEquals(3, games.size());
    }

    @Test
    void testListGamesListNull() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        Exception ex = assertThrows(DataAccessException.class, () -> {
            gameService.listGame(null);
        });
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testClearGamesPositive() throws DataAccessException {
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        gameService.createGame(new CreateRequest("Game2", AUTH), AUTH);
        gameService.clearGames();
        Collection<GameSummary> games = gameService.listGame(AUTH);
        assertEquals(0, games.size());
    }

    // implement clear negative test?

    @Test
    void testJoinGamePositive() throws DataAccessException{
        gameService.createGame(new CreateRequest("Game1", AUTH), AUTH);
        JoinRequest joinRequest = new JoinRequest(1,"WHITE");
        gameService.joinGame(joinRequest,AUTH);
        GameD game = dataAccess.getGame(1);
        assertNotNull(game);
        assertEquals(game.getWhiteUsername(), dataAccess.getAuth(AUTH).username());
    }

    @Test
    void testJoinGameNullGameID(){
        Exception ex = assertThrows(DataAccessException.class, () -> {
            JoinRequest joinRequest = new JoinRequest(null, "WHITE");
            gameService.joinGame(joinRequest, AUTH);
        });
        assertTrue(ex.getMessage().contains("bad request"));
    }
    @Test
    void testRegister() throws DataAccessException {
        userService.register(new RegisterRequest("username", "password", "email"));
        assertNotNull(dataAccess.getUser("username"));
    }

    @Test
    void testRegisterUsernameNull(){
        Exception ex = assertThrows(DataAccessException.class, () -> {
            userService.register(new RegisterRequest(null, "p", "e"));
        });
        assertTrue(ex.getMessage().contains("bad request"));
    }

    @Test
    void testLogin() throws DataAccessException{
        userService.register(new RegisterRequest("username", "password", "email"));
        assertNotNull(userService.login(new LoginRequest("username", "password")));
    }

    @Test
    void testLoginUnauthorized(){
        Exception ex = assertThrows(DataAccessException.class, () -> {
            userService.register(new RegisterRequest("username", "password", "email"));
            userService.login(new LoginRequest("user", "password"));
        });
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void testLogout() throws DataAccessException {
        userService.logout(AUTH);
        assertNull(dataAccess.getAuth(AUTH));
    }

    @Test
    void testLogoutUnauthorized() {
        Exception ex = assertThrows(DataAccessException.class, () -> {
            userService.logout("falseAuth");
        });
        assertTrue(ex.getMessage().contains("unauthorized"));
    }

    @Test
    void testClearAll() throws DataAccessException{
        dataAccess.createAuth("user1");
        dataAccess.createAuth("user2");
        dataAccess.clearAuths();
        assertTrue(dataAccess.getAuth());
    }
}
