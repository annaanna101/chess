package ui;

import chess.ChessGame;

import java.util.Scanner;

public class GamePlayRepl {
    private final ChessClient client;
    private final Integer gameID;

    public GamePlayRepl(ChessClient client, Integer gameID) {
        this.client = client;
        this.gameID = gameID;
    }
    private void printPrompt() {
        System.out.print("\n[GAMEPLAY] >>> ");
    }

    public void run(){
        //figure out how to get a chess game
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (client.getState() == State.SIGNEDIN) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            if (result != null){
                System.out.println(result);
            }
            if("leave".equals(result)){
                new PostLoginRepl(client).run();
            }
//            if (line.startsWith("join") || line.startsWith("observe")){
//                String[] tokens = line.split(" ");
//                int seqId;
//                if(tokens[1].matches("^\\d+$")){
//                    seqId = Integer.parseInt(tokens[1]);
//                } else {
//                    continue;
//                }
//                Integer realGameID = client.getRealGameID(seqId);
//                if (realGameID == null) {
//                    System.out.println("Invalid game ID. Make sure to run 'list' first.");
//                    continue;
//                }
//                if (result != null && !result.contains("Error")){
//                    if (tokens.length >= 3){
//                        String color = tokens[2];
//                        DrawBoard.drawCorrectBoard(color);
//                    } else {
//                        String color = tokens[0];
//                        DrawBoard.drawCorrectBoard(color);
//                    }
//                }
//                new GamePlayRepl(client, realGameID).run();
//            }
        }
    }

}
