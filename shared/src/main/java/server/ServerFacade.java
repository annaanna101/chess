package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request){
        var web_request = buildRequest("POST", "/user", request, "Content-Type", "application/json");
        var response = sendRequest(web_request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request){
        var web_request = buildRequest("POST", "/session", request, "Content-Type", "application/json");
        var response = sendRequest(web_request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout (LogoutRequest request){
        var web_request = buildRequest("DELETE", "/session", request, "Content-Type", "application/json");
        sendRequest(web_request);
    }

    public void clear (){
        var web_request = buildRequest("DELETE", "/db", null, "Content-Type", "application/json");
        sendRequest(web_request);
    }

    public CreateResult create(CreateRequest request){
        var web_request = buildRequest("POST", "/game", request, "Authorization", request.authToken());
        var response = sendRequest(web_request);
        return handleResponse(response, CreateResult.class);
    }

    public ListGameResult list_games(AuthD authToken){
        var web_request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .GET()
                .header("Authorization", authToken.authToken())
                .build();
        var response = sendRequest(web_request);
        return handleResponse(response, ListGameResult.class);
    }

    public void joinGame(JoinRequest request, AuthD authToken){
        var web_request = buildRequest("POST", "/game", request, "Authorization", authToken.authToken());
//        var web_request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + "/game"))
//                .PUT(makeRequestBody(request))
//                .header("Content-Type", "application/json")
//                .header("Authorization", authToken.authToken())
//                .build();;
        sendRequest(web_request);
    }

    public ChessGame getGame(int gameID, AuthD authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game/" + gameID))
                .GET()
                .header("Authorization", authToken.authToken()) // IMPORTANT
                .build();
        var response = sendRequest(request);
        return handleResponse(response, ChessGame.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String contentType, String value) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader(contentType, value);
        }
        return request.build();
    }
    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request) throws RuntimeException  {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException("HTTP request failed", ex);
        }
    }
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        int status = response.statusCode();
        if (!isSuccessful(status)) {
            String body = response.body();
            if (body != null) {
                throw RuntimeExceptionFromJson(body);
            }

            throw new RuntimeException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private RuntimeException RuntimeExceptionFromJson(String body) {
        try {
            // parse JSON if needed
            var jsonObj = new Gson().fromJson(body, Map.class);
            String msg = jsonObj.getOrDefault("message", "Unknown error").toString();
            return new RuntimeException(msg);
        } catch (Exception e) {
            return new RuntimeException("Failed to parse error response: " + body, e);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
