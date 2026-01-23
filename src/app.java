import java.util.Scanner;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class app {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SenetGUI();
        });

        Scanner scanner = new Scanner(System.in);

        System.out.println("Black pieces --> Computer");
        System.out.println("White pieces --> Human");
        System.out.println("====================");

        System.out.print("Enable debug mode? (Y/N): ");
        char debug = scanner.next().toUpperCase().charAt(0);
        Expectiminimax.debugMode = (debug == 'Y');

        System.out.println("====================");
        System.out.print("Choose start playing : (C) Computer OR (H) Human ? ");
        char input = scanner.next().toUpperCase().charAt(0);

        State state = new State();

        if (input == 'H') {
            state.isBlackTurn = false;
        } else if (input == 'C') {
            state.isBlackTurn = true;
        } else {
            System.out.println("Invalid input");
            return;
        }

        System.out.println("Start playing");
        System.out.println();

        while (!state.isWinner()) {

            System.out.print("Turn: ");
            if (state.isBlackTurn) {
                System.out.println("(Computer / Black)");
            } else {
                System.out.println("(Human / White)");
            }

            state.printBoard();
            System.out.println();

            int roll;

            if (!state.isBlackTurn) {

                System.out.println("Enter 'r' to roll");
                char r = scanner.next().toUpperCase().charAt(0);

                roll = state.rollSticks();
                System.out.println("Number of move = " + roll);
                if (!Move.canPlay(state, roll)) {
                    System.out.println("No valid moves, enter 'S' to skip");
                    char skip = scanner.next().toUpperCase().charAt(0);

                    while (skip != 'S') {
                        System.out.println("Invalid input, enter 'S' to skip");
                        skip = scanner.next().toUpperCase().charAt(0);
                    }
                    state.isBlackTurn = true;
                    continue;
                }

                System.out.println("Enter number of piece to move:");
                while (true) {
                    int move = scanner.nextInt() - 1;
                    if (Move.canMove(state, move, roll)) {
                        Move.updateBoard(state, move, roll);
                        break;
                    } 
                    else {
                        System.out.println("Invalid move, try again.");
                    }
                }

                continue;
            }
            else {
                roll = state.rollSticks();
                System.out.println("Computer rolled = " + roll);
                if (!Move.canPlay(state, roll)) {
                    System.out.println("Computer has no valid moves. Skipping turn...");
                    state.isBlackTurn = false;
                    continue;
                }
                System.out.println("Computer is thinking...");
                int depth = 3;
                int[] bestMove = Expectiminimax.getBestMove(state, depth, roll);
                if (bestMove == null) {
                    System.out.println("Computer has no valid moves. Skipping turn.");
                    state.isBlackTurn = false;
                    continue;
                }
                System.out.println("Computer moves from: " + (bestMove[0] + 1) +
                                   " to: " + (bestMove[1] + 1));

                Move.updateBoard(state, bestMove[0], roll);
                continue;
            }
        }
        System.out.println("Game Over!");
        int winner = state.getWinner();
        if (winner == 1) {
            System.out.println("GAMEOVER: COMPUTER WINS!");
        }
        else {
            System.out.println("GAMEOVER: YOU WIN! (This shouldn't happen...)");
        } 
    }
}
