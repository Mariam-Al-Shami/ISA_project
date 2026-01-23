import java.util.List;

public class Expectiminimax {
    public enum Node_type {
        MAX,
        MIN,
        CHANCE
    }

    public static boolean debug = false;
    public static int visited = 0;

    public static int[] getBestMove(State state, int depth, int roll) {
        visited = 0;
        int player = state.isBlackTurn ? 1 : 2;
        double bestValue = state.isBlackTurn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        int[] bestMove = null;
        
        List<State> children = state.getAllNextStates(roll);

        for (State child : children) {
            int from = -1, to = -1;
            for (int i = 0; i < 30; i++) {
                if (state.board[i] == player && child.board[i] != player) {
                    from = i;
                }
                if (state.board[i] != player && child.board[i] == player ) {
                    to = i;
                }
            }
            double value = expectiminimax(child, depth - 1, Node_type.CHANCE, 0);
            if (state.isBlackTurn) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = new int[]{from, to};
                }
            } 
            else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = new int[]{from, to};
                }
            }
            if (debug) {
                System.out.println("[ROOT CHILD] from " + (from + 1) + " to " + (to + 1) +"value = " + value);
            }
        }
        System.out.println("Evaluation = " + bestValue);
        System.out.println("Nodes Visited = " + visited);
        return bestMove;
    }

    public static double expectiminimax(State state, int depth, Node_type type, int roll) {

        visited++;

        if (depth == 0 || state.isWinner()) {    
            int h = Heuristic.evaluate(state);

            if (debug) {
                System.out.println("[LEAF] depth=" + depth + " type=" + type +"heuristic = " + h);
            }
            return h;
        }

        switch (type) {
           
            case MAX: {
                double best = Double.NEGATIVE_INFINITY;
                
                List<State> children = state.getAllNextStates(roll);
               
                if (children.isEmpty()) {
                    State pass = new State(state);
                    pass.isBlackTurn = !state.isBlackTurn;
                    double v = expectiminimax(pass, depth - 1, Node_type.CHANCE, 0);
                    if (debug) {
                        System.out.println("[MAX] No moves → pass turn → value = " + v);
                    }
                    return v;
                }
                if (debug) {
                    System.out.println("[MAX] depth=" + depth + " children=" + children.size());
                }
                
                for (State child : children) {
                    double v = expectiminimax(child, depth - 1, Node_type.CHANCE, 0);

                    if (debug) {
                        System.out.println("   [MAX child] value = " + v);
                    }
                   
                    if (v > best) best = v;
                }
                if (debug) {
                    System.out.println("[MAX] depth=" + depth + " → return " + best);
                }
               
                return best;
            }

            case MIN: {
                double best = Double.POSITIVE_INFINITY;
                
                List<State> children = state.getAllNextStates(roll);
                
                if (children.isEmpty()) {
                    State pass = new State(state);
                    pass.isBlackTurn = !state.isBlackTurn;
                    double v = expectiminimax(pass, depth - 1, Node_type.CHANCE, 0);
                    if (debug) {
                        System.out.println("[MIN] No moves → pass turn → value = " + v);
                    }
                    return v;
                }
                if (debug) {
                    System.out.println("[MIN] depth=" + depth + " children=" + children.size());
                }
                
                for (State child : children) {
                    double v = expectiminimax(child, depth - 1, Node_type.CHANCE, 0);

                    if (debug) {
                        System.out.println("   [MIN child] value = " + v);
                    }

                    if (v < best) best = v;
                }
                if (debug) {
                    System.out.println("[MIN] depth=" + depth + " → return " + best);
                }
                return best;
            }

            case CHANCE: {
                double expected = 0.0;

                if (debug) {
                    System.out.println("[CHANCE] depth=" + depth + " start");
                }

                for (int r = 1; r <= 5; r++) {
                    double p = Probabilities.getRollProbability(r);
                    if (p == 0) continue;

                    Node_type nextType = state.isBlackTurn ? Node_type.MAX : Node_type.MIN;
                    double v = expectiminimax(state, depth - 1, nextType, r);
                    expected += p * v;

                    if (debug) {
                        System.out.println("   [CHANCE] roll=" + r + " p=" + p + " → v=" + v);
                    }
                }

                if (debug) {
                    System.out.println("[CHANCE] depth=" + depth + " → expected = " + expected);
                }

                return expected;
            }
        }

        return Heuristic.evaluate(state);
    }
}
