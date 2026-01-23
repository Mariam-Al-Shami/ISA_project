import java.util.ArrayList;
import java.util.List;

public class State {

    public int[] board;
    public int blackOut;
    public int whiteOut;
    public boolean isBlackTurn;
    public boolean isSimulation = false;
    public int after_penalty = -1;

    public State() {
        this.board = new int[30];
        this.blackOut = 0;
        this.whiteOut = 0;
        this.isBlackTurn = true;
        initialBorad();
    }

    private void initialBorad() {
        for (int i = 0; i < 14; i++) {
            if (i % 2 == 0) {
                board[i] = 1; // black
            } else {
                board[i] = 2; // white
            }
        }
        for (int i = 14; i < 30; i++) {
            board[i] = 0;  // empty
        }
        //  board[29]=1;
    }

    public State(State original) {
        this.board = new int[30];
        for (int i = 0; i < 30; i++) {
            this.board[i] = original.board[i];
        }
        this.blackOut = original.blackOut;
        this.whiteOut = original.whiteOut;
        this.isBlackTurn = original.isBlackTurn;
        this.isSimulation = original.isSimulation;
        this.after_penalty = original.after_penalty;
    }

    public int rollSticks() {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            int stick = Math.random() < 0.5 ? 0 : 1;
            counter += stick;
        }
        if (counter == 0) {
            return 5;
        }
        return counter;
    }

    public boolean isWinner() {
        return (blackOut == 7 || whiteOut == 7);
    }

    public int getWinner() {
    if (blackOut == 7)
        return 1; 
    if (whiteOut == 7) 
        return 2; 
    return 0;
    }


    public void printBoard() {
        System.out.println("****************************************************");
        System.out.println("Turn: " + (isBlackTurn ? "Black (1)" : "White (2)"));
        System.out.print("  ");
        for (int i = 0; i < 10; i++) {
            System.out.print(getDisplay(i) + "(" + (i + 1) + ")" + " ");
        }
        System.out.println();
        System.out.println();

        System.out.print("  ");
        for (int i = 19; i >= 10; i--) {
            System.out.print(getDisplay(i) + "(" + (i + 1) + ")" + " ");
        }
        System.out.println();
        System.out.println();

        System.out.print("  ");
        for (int i = 20; i < 30; i++) {
            System.out.print(getDisplay(i) + "(" + (i + 1) + ")" + " ");
        }
        System.out.println();
        System.out.println();

        System.out.println("Out Board -> Black: " + blackOut + " | White: " + whiteOut);
        System.out.println("****************************************************");
    }

    private String getDisplay(int index) {
        if (board[index] == 1) {
            return " B";
        } else if (board[index] == 2) {
            return " W";
        } else {
            return " " + (index + 1);
        }
    }

    public void applyMove(int from, int roll) {
        int pawn = board[from];
        int to = from + roll;

        to = Rules.RulesFunction(this, from, to, roll);
        if (to >= 30) {
            board[from] = 0;
            if (pawn == 1) 
                blackOut++;
            else 
                whiteOut++;
        } 
        else {
            if (board[to] != 0 && board[to] != pawn) {
                int opponent = board[to];
                board[to] = pawn;
                board[from] = opponent;
            }
            else {
                board[to] = pawn;
                board[from] = 0;
            }
        }
        Rules.Apply_Penalty(this, roll, from, to);
        isBlackTurn = !isBlackTurn;
    }

    public List<State> getAllNextStates(int roll) {
        List<State> next_states = new ArrayList<>();
        int player = isBlackTurn ? 1 : 2;

        boolean old_simulation = this.isSimulation;
        this.isSimulation = true;

        int forced = -1;
        if (board[SenetHouse.FREE_BOX.getIndex()] == player)
            forced = SenetHouse.FREE_BOX.getIndex();
        if (board[SenetHouse.THREE_BOX.getIndex()] == player)
            forced = SenetHouse.THREE_BOX.getIndex();
        if (board[SenetHouse.TWO_BOX.getIndex()] == player)
            forced = SenetHouse.TWO_BOX.getIndex();

        if (forced != -1) {
            if (Move.canMove(this, forced, roll)) {
                State new_state = new State(this);
                new_state.isSimulation = true;
                new_state.applyMove(forced, roll);
                next_states.add(new_state);
            }
            this.isSimulation = old_simulation;
            return next_states;
        }

        for (int i = 0; i < 30; i++) {
            if (board[i] != player) continue;
            if (!Move.canMove(this, i, roll)) continue;

            State ns = new State(this);
            ns.isSimulation = true;
            ns.applyMove(i, roll);
            next_states.add(ns);
        }

        this.isSimulation = old_simulation;
        return next_states;
    }
}
