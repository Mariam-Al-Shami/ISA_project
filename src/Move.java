public class Move {
    public static boolean canMove (State state , int from , int roll)  {
        int peice = state.board[from];
        int to = from + roll;

        if(peice == 0) {
            return false;
        }
        if(state.isBlackTurn && peice != 1) {
            return false;
        }
        if(!state.isBlackTurn && peice !=2) {
            return false;
        }    
        if (to < 30 && state.board[to] == peice) {
            return false;
        }
        //if (Rules)
        return true ;
    }

    public static void updateBoard(State state, int from, int roll){
        int peice = state.board[from];
        int to = from + roll; 


        if (to>=30){
            if(peice == 1){
                state.blackOut++;
                state.board[from] = 0;
            }
            else{
                state.whiteOut++;
                state.board[from] = 0;
            }
        }
        else{
            if(state.board[to] != peice){
                int opponent = state.board[to];
                state.board[to] = peice;
                state.board[from] = opponent;
            }
            else{
                state.board[to] = peice;
            }
        }
        state.isBlackTurn = !state.isBlackTurn;
        System.out.println("\nBoard after move:"); 
        state.printBoard();
    }
}
