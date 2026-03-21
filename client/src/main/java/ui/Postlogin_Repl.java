package ui;

import java.util.Scanner;

public class Postlogin_Repl {
    private final ChessClient client;

    public Postlogin_Repl(ChessClient client){
        this.client = client;
    }

    private void printPrompt() {
        System.out.print("\n[LOGGED IN] >>> ");
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (client.getState() == State.SIGNEDIN && !result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
