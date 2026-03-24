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
            if (result != null){
                System.out.println(result);
            }
            if("quit".equals(result)){
                System.exit(0);
            }
            if (line.startsWith("join") || line.startsWith("observe")){
                String[] tokens = line.split(" ");
                int seqId = Integer.parseInt(tokens[1]);
                Integer realGameID = client.getRealGameID(seqId);
                if (realGameID == null) {
                    System.out.println("Invalid game ID. Make sure to run 'list' first.");
                    continue;
                }
                if (result != null && !result.contains("Error")){
                    if (tokens.length >= 3){
                        String color = tokens[2];
                        DrawBoard.drawCorrectBoard(color);
                    } else {
                        String color = tokens[0];
                        DrawBoard.drawCorrectBoard(color);
                    }
                }

//                new GamePlay_Repl(client, realGameID).run();
            }
        }
    }

}
