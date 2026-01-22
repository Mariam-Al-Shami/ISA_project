public class Probabilities {

    private static final double[] rollProb = {
        0.25,   // roll = 1
        0.375,  // roll = 2
        0.25,   // roll = 3
        0.0625, // roll = 4
        0.0625  // roll = 5
    };
    
    public static double getRollProbability(int roll) {
        if (roll >= 1 && roll <= 5) {
            return rollProb[roll - 1];
        }
        return 0.0;
    }
}
