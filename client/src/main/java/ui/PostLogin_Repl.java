package ui;

import java.util.Scanner;

public class PostLogin_Repl {
    private final ChessClient client;

    public PostLogin_Repl(ChessClient client){
        this.client = client;
    }

    private void printPrompt() {
        System.out.print("\n[LOGGED IN] >>> ");
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (client.getState() == State.SIGNEDIN) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.println(result);
            if(result.equals("quit")){
                System.exit(0);
            }
            if (line.startsWith("join") || line.startsWith("observe")){
                String[] tokens = line.split(" ");
                int gameId = Integer.parseInt(tokens[1]);
                new GamePlay_Repl(client, gameId).run();
            }
        }
    }

}
