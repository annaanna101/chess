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
            if (line.startsWith("redraw")){
                String[] tokens = line.split(" ");
                String color = tokens[2];
                //figure out how to get color
                DrawBoard.drawCorrectBoard(color);
            }
            if (line.startsWith("highlight")){
                String[] tokens = line.split(" ");
                String color = tokens[2];
                //make higlightboard
                DrawBoard.drawHighlightBoard(color);
            }
        }
    }

}
