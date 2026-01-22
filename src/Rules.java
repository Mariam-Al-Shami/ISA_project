public class Rules { 

    private static final int SQUARE_26 = SenetHouse.ROADBLOCK.getIndex();
    private static final int SQUARE_27 = SenetHouse.RETURN_BOX.getIndex();
    private static final int SQUARE_28 = SenetHouse.THREE_BOX.getIndex();
    private static final int SQUARE_29 = SenetHouse.TWO_BOX.getIndex();
    private static final int SQUARE_30 = SenetHouse.FREE_BOX.getIndex();

    public static int RulesFunction(State state, int CurrentState, int NextState, int roll) {

        if (!roadblockRule(state, CurrentState, NextState)) {
            return CurrentState;
        }

        if (NextState == SQUARE_27) {
            return returnBoxRule(state);
        }

        if (NextState == SQUARE_28 || CurrentState == SQUARE_28) {
            return threeBoxRule(state, CurrentState, NextState, roll);
        }

        if (NextState == SQUARE_29 || CurrentState == SQUARE_29) {
            return twoBoxRule(state, CurrentState, NextState, roll);
        }

        if (NextState == SQUARE_30 || CurrentState == SQUARE_30) {
            return freeBoxRule(state, CurrentState, NextState, roll);
        }

        return NextState;
    }

    static boolean roadblockRule(State state, int CurrentState, int NextState) {
        if (CurrentState < SQUARE_26 && NextState > SQUARE_26) {
            if (!state.isSimulation)
                System.out.println("Can't skip square 26");
            return false;
        }
        return true;
    }

    static int returnBoxRule(State state) {
        if (!state.isSimulation)
            System.out.println("Back to square 15");
        return Find_Empty_Place(state);
    }

    static int threeBoxRule(State state, int CurrentState, int NextState, int roll) {

        if (CurrentState != SQUARE_28) {
            return SQUARE_28;
        }

        if (roll == 3) {
            return 30; // خارج اللوحة
        } else {
            if (!state.isSimulation)
                System.out.println("Back to square 15");
            return Find_Empty_Place(state);
        }
    }

    static int twoBoxRule(State state, int CurrentState, int NextState, int roll) {

        if (CurrentState != SQUARE_29) {
            return SQUARE_29;
        }

        if (roll == 2) {
            return 30;
        } else {
            if (!state.isSimulation)
                System.out.println("Back to square 15");
            return Find_Empty_Place(state);
        }
    }

    static int freeBoxRule(State state, int CurrentState, int NextState, int roll) {

        if (CurrentState != SQUARE_30) {
            return SQUARE_30;
        }

        return 30;
    }

    public static int Find_Empty_Place(State state) {
        for (int i = SenetHouse.NEW_BEGINNING.getIndex(); i >= 0; i--) {
            if (state.board[i] == 0) {
                return i;
            }
        }
        return 0;
    }

    public static void Apply_Penalty(State state, int roll, int movedState, int finalNextState) {

        int player = state.isBlackTurn ? 1 : 2;

        if (state.after_penalty != -1 && movedState != state.after_penalty) {
            if (!state.isSimulation)
                System.out.println("Penalty: This piece not moved");
            Back_TO_15(state, state.after_penalty, player);
            state.after_penalty = -1;
        }

        if (state.board[SQUARE_28] == player && movedState != SQUARE_28 && finalNextState != SQUARE_28) {
            if (!state.isSimulation)
                System.out.println("Penalty: Piece on square 28 must move");
            Back_TO_15(state, SQUARE_28, player);
        }

        if (state.board[SQUARE_29] == player && movedState != SQUARE_29 && finalNextState != SQUARE_29) {
            if (!state.isSimulation)
                System.out.println("Penalty: Piece on square 29 must move");
            Back_TO_15(state, SQUARE_29, player);
        }

        if (state.board[SQUARE_30] == player && movedState != SQUARE_30 && finalNextState != SQUARE_30) {
            if (!state.isSimulation)
                System.out.println("Penalty: Piece on square 30 must move");
            Back_TO_15(state, SQUARE_30, player);
        }
    }

    public static void Back_TO_15(State state, int Index, int Value) {
        int empty = Find_Empty_Place(state);
        state.board[Index] = 0;
        state.board[empty] = Value;

        if (!state.isSimulation)
            System.out.println("Back to square: " + (empty + 1));
    }
}
