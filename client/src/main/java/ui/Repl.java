package ui;

import com.sun.nio.sctp.NotificationHandler;

import javax.management.Notification;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to 240 chess. Type Help to get started");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try{
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(Notification notification){
        System.out.println(notification.getMessage());
        printPrompt();
    }
}
