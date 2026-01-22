public class Move {

    public static boolean canMove(State state, int from, int roll) {

        int pawn = state.board[from];
        int to = from + roll;

        if (pawn == 0) return false;
        if (state.isBlackTurn && pawn != 1) 
            return false;
        if (!state.isBlackTurn && pawn != 2) 
            return false;

        if (to < 30 && state.board[to] == pawn) 
            return false;

        int final_destination = Rules.RulesFunction(state, from, to, roll);

        if (final_destination == from && to != from) {
            if (!state.isSimulation)
                System.err.println("Move not allowed special rules");
            return false;
        }

        return true;
    }

    public static void updateBoard(State state, int from, int roll) {
        int pawn = state.board[from];
        int to = from + roll;

        to = Rules.RulesFunction(state, from, to, roll);

        if (to >= 30) {
            state.board[from] = 0;
            if (pawn == 1)
                state.blackOut++;
            else 
                state.whiteOut++;
        } 
        else {
            if (state.board[to] != pawn) {
                int opponent = state.board[to];
                state.board[to] = pawn;
                state.board[from] = opponent;
            } 
            else {
                state.board[to] = pawn;
                state.board[from] = 0;
            }
        }

        if (!state.isSimulation) {
            Rules.Apply_Penalty(state, roll, from, to);
            state.isBlackTurn = !state.isBlackTurn;
            System.out.println("\nBoard after move:");
            state.printBoard();
        }
    }

    public static boolean canPlay(State state, int roll) {
        for (int i = 0; i < 30; i++) {
            int pawn = state.board[i];
            if (pawn == 0) continue;
            if (state.isBlackTurn && pawn != 1) continue;
            if (!state.isBlackTurn && pawn != 2) continue;
            if (canMove(state, i, roll)) return true;
        }
        return false;
    }
}
