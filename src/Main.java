import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // Het aanmaken van de grid LET OP 0 telt niet mee!
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);

        // Inlezen van een array als nieuwe grid

        ArrayList miniqueue = grid.possible_lines(grid, 1,2,2,0);

        for(int i = 0; i < miniqueue.size(); i++) {
            Grid miniqueue_element = (Grid) miniqueue.get(i);
            miniqueue_element.printGrid();
        }




    }



}
