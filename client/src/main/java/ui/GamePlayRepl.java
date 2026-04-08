package ui;

import chess.ChessGame;
import chess.ChessPosition;
import client.websocket.ResponseException;

import java.util.Scanner;

public class GamePlayRepl {
    private final ChessClient client;

    public GamePlayRepl(ChessClient client) {
        this.client = client;
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
                DrawBoard.drawCorrectBoard(client.getTeamColor(), client.getCurrentGame(), null, HighlightState.NOHIGHLIGHT);
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
                if (tokens[0] != null){
                    ChessPosition start = client.highlight(tokens);
                    DrawBoard.drawCorrectBoard(client.getTeamColor(), client.getCurrentGame(), start, HighlightState.HIGHLIGHT);
                } else {
                    System.out.print("Error: Expected [piece location]");
                }
            }

        }
    }

}
