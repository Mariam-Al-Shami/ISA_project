public class Heuristic {

    public static int evaluate(State state) {

        int score = 0;
        score += (state.blackOut - state.whiteOut) * 500;

        for (int i = 0; i < 30; i++) {
            if (state.board[i] == 1) score += i * 5;
            if (state.board[i] == 2) score -= i * 5;
        }

        if (state.board[SenetHouse.ROADBLOCK.getIndex()] == 1) 
            score += 40;
        if (state.board[SenetHouse.ROADBLOCK.getIndex()] == 2) 
            score -= 40;

        if (state.board[SenetHouse.RETURN_BOX.getIndex()] == 1) 
            score -= 80;
        if (state.board[SenetHouse.RETURN_BOX.getIndex()] == 2) 
            score += 80;

        if (state.board[SenetHouse.THREE_BOX.getIndex()] == 1) 
            score += 10;
        if (state.board[SenetHouse.THREE_BOX.getIndex()] == 2) 
            score -= 10;

        if (state.board[SenetHouse.TWO_BOX.getIndex()] == 1) 
            score += 10;
        if (state.board[SenetHouse.TWO_BOX.getIndex()] == 2) 
            score -= 10;

        if (state.board[SenetHouse.FREE_BOX.getIndex()] == 1) 
            score += 200;
        if (state.board[SenetHouse.FREE_BOX.getIndex()] == 2) 
            score -= 200;

        if (state.board[SenetHouse.NEW_BEGINNING.getIndex()] == 1) 
            score -= 50;
        if (state.board[SenetHouse.NEW_BEGINNING.getIndex()] == 2) 
            score += 50;

        double blackMobility = 0;
        double whiteMobility = 0;

        for (int roll = 1; roll <= 5; roll++) {
            double probability = Probabilities.getRollProbability(roll);
            if (probability == 0) 
                continue;
            State state_black = new State(state);
            state_black.isBlackTurn = true;
            state_black.isSimulation = true; 
            blackMobility += state_black.getAllNextStates(roll).size() * probability;

            State state_white = new State(state);
            state_white.isBlackTurn = false;
            state_white.isSimulation = true;   
            whiteMobility += state_white.getAllNextStates(roll).size() * probability;
        }
        score += (int)((blackMobility - whiteMobility) * 10);

        return score; 
    }
}
