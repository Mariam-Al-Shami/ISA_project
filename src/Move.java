public class Move {

    public static boolean canMove(State state, int from, int roll) {
        int peice = state.board[from];
        int to = from + roll;

        if (peice == 0) {
            return false;
        }
        if (state.isBlackTurn && peice != 1) {
            return false;
        }
        if (!state.isBlackTurn && peice != 2) {
            return false;
        }
        if (to < 30 && state.board[to] == peice) {
            return false;
        }
        int finalDest = Rules.applyAllRules(state, from, to, roll);

        if (finalDest == from && to != from) {
            System.err.println("Move not allowed special rules");
            return false;
        }

        return true;
    }

    public static void updateBoard(State state, int from, int roll) {
      

        int peice = state.board[from];
        int to = from + roll;
        to = Rules.applyAllRules(state, from, to, roll);

        if (to >= 30) {
            if (peice == 1) {
                state.blackOut++;
                state.board[from] = 0;
            } else {
                state.whiteOut++;
                state.board[from] = 0;
            }
        } else {
            if (state.board[to] != peice) {
                int opponent = state.board[to];
                state.board[to] = peice;
                state.board[from] = opponent;
            } else {
                state.board[to] = peice;
                state.board[from] = 0;
            }
        }

        Rules.applyPenaltyIfNecessary(state, roll, from, to);
        state.isBlackTurn = !state.isBlackTurn;
        System.out.println("\nBoard after move:");
        state.printBoard();
    }

    public static boolean canPlay(State state, int roll) {
    for (int i = 0; i < 30; i++) {
        int piece = state.board[i];
        if (piece == 0) 
            continue;
        if (state.isBlackTurn && piece != 1) 
            continue;
        if (!state.isBlackTurn && piece != 2) 
            continue;
        if (canMove(state, i, roll)) {
            return true;
        }
    }
    return false;
}

}