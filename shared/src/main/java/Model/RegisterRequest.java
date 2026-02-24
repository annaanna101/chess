package Model;

public record RegisterRequest(
        String username,
        String password,
        String email
) {}
