public class Rules {

    public static final int HOUSE_OF_HAPPINESS = 25; 
    public static final int HOUSE_OF_WATER = 26;
    public static final int HOUSE_OF_THREE = 27;
    public static final int HOUSE_OF_RE_ATOUM = 28;
    public static final int HOUSE_OF_HORUS = 29;
    public static final int HOUSE_OF_REBIRTH = 14;

    public static int applyAllRules(State state, int from, int to, int roll) {
        if (!houseOfHappinessRule(from, to)) {
            return from;
        }

        if (to == HOUSE_OF_WATER) {
            return houseOfWaterRule(state);
        }
        if (to == HOUSE_OF_THREE || from == HOUSE_OF_THREE) {
            return houseOfThreeTruthsRule(state, from, to, roll);
        }
        if (to == HOUSE_OF_RE_ATOUM || from == HOUSE_OF_RE_ATOUM) {
            return houseOfReAtoumRule(state, from, to, roll);
        }
        if (to == HOUSE_OF_HORUS) {
            return houseOfHorusRule(state, roll);
        }

        return to;
    }

    private static boolean houseOfHappinessRule(int from, int to) {
        if (from < HOUSE_OF_HAPPINESS && to > HOUSE_OF_HAPPINESS) {
            return false;
        }
        return true;
    }

    private static int houseOfWaterRule(State state) {
        System.out.println("Rule: Landed on Water (27) -> Return to Rebirth (15)");
        return findNearestEmptyBeforeRebirth(state);
    }

    private static int houseOfThreeTruthsRule(State state, int from, int to, int roll) {
        if (from != HOUSE_OF_THREE) {
            return HOUSE_OF_THREE;
        }
        if (roll == 3) {
            return 30; 
        } 
        else {
            System.out.println("Rule: Tried to move from 28 without a 3 -> Return to Rebirth");
            return findNearestEmptyBeforeRebirth(state);
        }
    }

    private static int houseOfReAtoumRule(State state, int from, int to, int roll) {
        if (from != HOUSE_OF_RE_ATOUM) {
            return HOUSE_OF_RE_ATOUM;
        }
        if (roll == 2) {
            return 30; 
        } 
        else {
            System.out.println("Rule: Tried to move from 29 without a 2 -> Return to Rebirth");
            return findNearestEmptyBeforeRebirth(state);
        }
    }

    private static int houseOfHorusRule(State state, int roll) {
        return 30;
    }

    public static int findNearestEmptyBeforeRebirth(State state) {
        for (int i = HOUSE_OF_REBIRTH; i >= 0; i--) {
            if (state.board[i] == 0) {
                return i;
            }
        }
        return 0; 
    }

    public static void applyPenaltyIfNecessary(State state, int roll, int movedPieceIndex, int finalTo) {
        int pieceVal = state.isBlackTurn ? 1 : 2;
        if (state.forcedPieceIndex != -1 && movedPieceIndex != state.forcedPieceIndex) {
            System.out.println("Penalty: Forced piece not moved!");
            movePieceToRebirth(state, state.forcedPieceIndex, pieceVal);
            state.forcedPieceIndex = -1; 
        }
        if (state.board[HOUSE_OF_HORUS] == pieceVal && movedPieceIndex != HOUSE_OF_HORUS && finalTo != HOUSE_OF_HORUS) {
            System.out.println("Penalty: You must move the piece on House of Horus (30)!");
            movePieceToRebirth(state, HOUSE_OF_HORUS, pieceVal);
        }
        if (state.board[HOUSE_OF_THREE] == pieceVal && movedPieceIndex != HOUSE_OF_THREE && finalTo != HOUSE_OF_THREE) {
            System.out.println("Penalty: Piece on 28 must move or return to Rebirth!");
            movePieceToRebirth(state, HOUSE_OF_THREE, pieceVal);
        }
        if (state.board[HOUSE_OF_RE_ATOUM] == pieceVal && movedPieceIndex != HOUSE_OF_RE_ATOUM && finalTo != HOUSE_OF_RE_ATOUM) {
            System.out.println("Penalty: Piece on 29 must move or return to Rebirth!");
            movePieceToRebirth(state, HOUSE_OF_RE_ATOUM, pieceVal);
        }
    }
    
    public static void movePieceToRebirth(State state, int pieceIndex, int pieceVal) {
        int safeSpot = findNearestEmptyBeforeRebirth(state);
        state.board[pieceIndex] = 0; 
        state.board[safeSpot] = pieceVal; 
        System.out.println("Piece returned to square: " + (safeSpot + 1));
    }
}