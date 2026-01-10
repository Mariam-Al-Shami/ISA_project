import java.util.Scanner;

public class app
{
public static void main(String[ ] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Black pices --> Computer");
        System.out.println("White pices --> Human");
        System.out.println("====================");
        System.out.print("Choose start playing : (C) Computer OR (H) Human ? ");
        
       
        char input = scanner.next().toUpperCase().charAt(0);

        State state = new State(); 

        if(input == 'H') {
            state.isBlackTurn = false;
        } else if( input == 'C') {
            state.isBlackTurn = true; 
        }
        System.out.println("Start playing");
        System.out.println();
        while (!state.isWinner()) {
            System.out.print("Turn:");
            if(state.isBlackTurn) {
                System.out.println("(Computer / Black)");
            }
            else {
                System.out.println("(Human / White)");
            }
            state.printBoard();

            System.out.println(" ");
            System.out.println("Enter 'r' to roll");
            char numOfMove = scanner.next().toUpperCase().charAt(0);
            int roll= state.rollSticks();
            System.out.println(" Number of move = " + roll);
         
            System.out.println("Enter number of peice to move : ");
        
            while (true) {
                int move = scanner.nextInt()-1;
                int to = roll + move;
                System.out.println("from : "+ (move+1));
                System.out.println("to : " +(to+1));
            
                if(Move.canMove(state, move, roll)){
                    Move.updateBoard(state, move, roll);
                    break;
                }
                else{
                    System.out.println("You can't move , please enter another number");   
                }
            }
        }
    }
}