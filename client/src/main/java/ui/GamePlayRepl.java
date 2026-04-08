package ui;

import chess.ChessGame;
import client.websocket.ResponseException;

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

    public void run() {
        //figure out how to get a chess game
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (client.getState() == State.SIGNEDIN && client.getGameState() != GamePlayState.NOGAMEPLAY) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.eval(line);
            if (result != null){
                System.out.println(result);
            }
            if (client.getGameState() == GamePlayState.NOGAMEPLAY){
//                    new PostLoginRepl(client).run();
                return;
            }
            if (line.startsWith("redraw")){
//                    String[] tokens = line.split(" ");
//                    String color = tokens[2];
//                    //figure out how to get color
//                    DrawBoard.drawCorrectBoard(color);
                System.out.println("redraw board");
            }
            if (line.startsWith("resign")){
                System.out.println("Are you sure you want to resign [Y/N]");
                String confirm = scanner.nextLine().trim().toUpperCase();

                if(confirm.equals("Y")){
                    try{
                        client.resign();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                } else if (confirm.equals("N")){
                    System.out.println("Okay! Resignation Cancelled");
                } else {
                    System.out.println("Error: Could not resign from game.");
                }
            }
            if (line.startsWith("highlight")){
                String[] tokens = line.split(" ");
                String color = tokens[2];
                //make higlightboard
//                DrawBoard.drawHighlightBoard(color);
                System.out.println("draw highlighted board");
            }

        }
    }

}
