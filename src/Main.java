import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        // Het aanmaken van de grid LET OP 0 telt niet mee!
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        Grid grid1 = new Grid(Y_SIZE, X_SIZE, Z_SIZE);


        Queue<ExpandGrid> gridQueue = new LinkedList();

        Net layLine = new Net(1, 3);



        ExpandGrid gridditydoo = new ExpandGrid(grid, 1, 2, 2, 0);
        gridQueue.add(gridditydoo);
        // uitbreden van de grid
        ExpandGrid expandable = gridQueue.remove();
        grid.expandGrid(expandable.grid, expandable.number, expandable.x, expandable.y, expandable.z);






    }






}
