package ui;

import chess.ChessGame;

import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient client;

    public PostLoginRepl(ChessClient client){
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
                int seqId;
                if(tokens[1].matches("^\\d+$")){
                    seqId = Integer.parseInt(tokens[1]);
                } else {
                    continue;
                }
                Integer realGameID = client.getRealGameID(seqId);
                if (realGameID == null) {
                    System.out.println("Invalid game ID. Make sure to run 'list' first.");
                    continue;
                }
                if (result != null && !result.contains("Error")){
                    if (tokens.length >= 3){
                        String color = tokens[2];
                        ChessGame game = client.getCurrentGame();
                        DrawBoard.drawCorrectBoard(color, game, null, HighlightState.NOHIGHLIGHT);
                    } else {
                        String color = tokens[0];
                        ChessGame game = client.getCurrentGame();
                        DrawBoard.drawCorrectBoard(color, game, null, HighlightState.NOHIGHLIGHT);
                    }
                }
                new GamePlayRepl(client).run();
            }
        }
    }

}
