package ui;

import com.sun.nio.sctp.NotificationHandler;

import javax.management.Notification;
import java.util.Arrays;
import java.util.Scanner;

public class Prelogin_Repl implements NotificationHandler {
    private final ChessClient client;

    public Prelogin_Repl(String serverUrl){
        client = new ChessClient(serverUrl);
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
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
                if (client.getState() == State.SIGNEDIN){
                    new Postlogin_Repl(client).run();
                    System.out.print(client.help());
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye!");
    }

    public void notify(Notification notification){
        System.out.println(notification.getMessage());
        printPrompt();
    }


}
