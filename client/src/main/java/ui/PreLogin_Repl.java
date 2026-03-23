package ui;

import java.util.Scanner;

public class PreLogin_Repl {
    private final ChessClient client;

    public PreLogin_Repl(String serverUrl){
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
            result = client.eval(line);
            System.out.print(result);
            if (client.getState() == State.SIGNEDIN){
                new PostLogin_Repl(client).run();
                System.out.print(client.help());
            }
        }
        System.out.println("Goodbye!");
    }

}
