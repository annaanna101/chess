package server;

import java.util.UUID;

public class GenerateToken {
    public static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
