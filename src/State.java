

public class State {

    public int[] board ;
    public int blackOut ;
    public int whiteOut ;
    public boolean isBlackTurn ;

    public State() {
        this.board = new int[30];
        this.blackOut =0;
        this.whiteOut =0;
        this.isBlackTurn =true;
        initialBorad();
    }

    private void initialBorad() {
        for(int i = 0 ; i < 14 ; i++){
            if(i%2 == 0){
                board[i]= 1; //black
            } else {
                board[i] =2 ; //white
            }
        }
        for (int i = 14 ; i < 30 ; i++){
            board[i]= 0;  //empty
        }
    }

    public State(State original){
        this.board = new int[30];

        for(int i=0 ; i<30 ;i++){
            this.board[i]=original.board[i];
        }
        this.blackOut=original.blackOut;
        this.whiteOut=original.whiteOut;
        this.isBlackTurn=original.isBlackTurn;
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
        return (blackOut ==7 || whiteOut ==7);
    }

    public void printBoard() {
        System.out.println("****************************************************");
        System.out.println("Turn: " + (isBlackTurn ? "Black (1)" : "White (2)"));
        System.out.print("  ");
       for (int i = 0; i <10 ; i++) {
            System.out.print(getDisplay(i) + "(" + (i+1) + ")" + " ");
        }
        System.out.println();
          System.out.println();

        System.out.print("  ");
        for (int i = 19; i >= 10; i--){
            System.out.print(getDisplay(i) + "(" + (i+1) + ")" + " ");
        }
        System.out.println();
        System.out.println();
        System.out.print("  ");
        for (int i = 20; i < 30; i++)  {
            System.out.print(getDisplay(i) + "(" + (i+1) + ")" + " ");
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
}