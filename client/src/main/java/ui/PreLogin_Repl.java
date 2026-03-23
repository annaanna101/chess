package ui;

import java.util.Scanner;

public class PreLogin_Repl {
    private final ChessClient client;

    public PreLogin_Repl(String serverUrl){
        client = new ChessClient(serverUrl);
    }

    private void printPrompt() {
        System.out.print("\n[LOGGED OUT] >>> ");
    }

    public void run(){
        System.out.println("Welcome to the CS 240 chess server! Type Help to get started");
//        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!"quit".equals(result)) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            if (result != null) {
                System.out.print(result);
            }
            if (client.getState() == State.SIGNEDIN){
                new PostLogin_Repl(client).run();
                System.out.print(client.help());
            }
        }
        System.out.println(" Goodbye!");
    }

}
