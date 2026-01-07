package game;

public class SenetState {

    public int[] board ;
    public int blackPicesOut ;
    public int whitePicesOut ;
    public boolean isBlckTurn ;

    public SenetState() {
        this.board = new int[30];
        this.blackPicesOut =0;
        this.whitePicesOut =0;
        this.isBlckTurn =true;
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
    }

    public SenetState(SenetState original){
        this.board = new int[30];

        for(int i=0 ; i<30 ;i++){
            this.board[i]=original.board[i];
        }
        this.blackPicesOut=original.blackPicesOut;
        this.whitePicesOut=original.whitePicesOut;
        this.isBlckTurn=original.isBlckTurn;
    }

    public boolean isTerminal() {
        return (blackPicesOut ==7 || whitePicesOut ==7);

    }


    public void printBoard() {
        System.out.println("****************************************************");
        System.out.println("Turn: " + (isBlckTurn ? "Black (1)" : "White (2)"));
        
    

 
    System.out.print("  ");
       for (int i = 0; i <10 ; i++) {
            System.out.print(getDisplay(i) + " ");
        }
        System.out.println();
          System.out.println();

     
        for (int i = 19; i >= 10; i--){
            System.out.print(getDisplay(i) + " ");
        }
        System.out.println();
          System.out.println();

        
        System.out.print("  ");
        for (int i = 20; i < 30; i++)  {
            System.out.print(getDisplay(i) + " ");
        }
        System.out.println();
          System.out.println();
        
        System.out.println("Off Board -> Black: " + blackPicesOut + " | White: " + whitePicesOut);
        System.out.println("****************************************************");    }


    private String getDisplay(int index) {
        if (board[index] == 1) {
            return " B";  
        } else if (board[index] == 2) {
            return " W";
        } else {
                      return " " + (index + 1); 
        }
    }
    

    public static void main(String[ ] args){
        SenetState state = new SenetState() ;
        state.printBoard();
    }

}


