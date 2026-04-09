package ui;

import java.util.Scanner;

public class GamePlayRepl {
    private final ChessClient client;

    public GamePlayRepl(ChessClient client) {
        this.client = client;
    }
    private void printPrompt() {
        synchronized (System.out){
            System.out.print("\n[GAMEPLAY] >>> ");
        }
    }

    public void run() {
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
                return;
            }

        }
    }

}
