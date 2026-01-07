package game;

public class SenetState {

    public int[] board ;
    public int blackPicesOff ;
    public int whitePicesOff ;
    public boolean isBlckTurn ;

    public SenetState() {
        this.board = new int[30];
        this.blackPicesOff =0;
        this.whitePicesOff =0;
        this.isBlckTurn =true;
        initializeBorad();
    }

    private void initializeBorad() {
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
        this.blackPicesOff=original.blackPicesOff;
        this.whitePicesOff=original.whitePicesOff;
        this.isBlckTurn=original.isBlckTurn;
    }

    public boolean isTerminal() {
        return (blackPicesOff ==7 || whitePicesOff ==7);

    }

    private String printSquare(int index){

        int squareNum = index +1 ;
        String content;

        if( board[index] == 1) 
            content= "B";
        else if(board[index] == 2)
            content= "W";
        else 
            content= ".";
        
        
        if ( squareNum ==15 || squareNum ==26 || squareNum ==27 || 
            squareNum ==28 ||squareNum ==29 ||squareNum ==30 ) {
                 
                return "[" +content + "]";
            }
        
        return " " + content + " ";
         
    }

    public void printBoard(){
        System.out.println("*************************************");
        System.out.println("Turn: "+ (isBlckTurn ? "?Black (1) " : "White (2)"));

       
        System.out.println("  ");
        
        for(int i=0 ; i < 10 ; i++){
            System.out.print(printSquare(i)+" ");
        }
        System.out.println();
        System.out.println();
    

        for(int i=10 ; i <20 ; i++){
            System.out.print(printSquare(i)+" ");
        }
        System.out.println();
        System.out.println();
 

        for(int i=20 ; i < 30 ; i++){
            System.out.print(printSquare(i)+" ");
        }
        System.out.println();
        System.out.println();
       

        System.out.println("Off Board --> Black: " + blackPicesOff + " | White: " + whitePicesOff);
        System.out.println("*************************************");

    }

    public static void main(String[ ] args){
        SenetState state = new SenetState() ;
        state.printBoard();
    }

}
