import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        for (int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {


        grid = breathFirstSearch(grid, lineNumber);
    }

    }

    private static Grid breathFirstSearch(Grid grid, int lineNumber) {


     


            Queue<ExpandGrid> gridQueue = new LinkedList();
            Net net = grid.netDatabase.get(lineNumber);
            int startGate = net.gate1;
            int startGateX = grid.gateDatabase[startGate][1];
            int startGateY = grid.gateDatabase[startGate][2];


            ExpandGrid firstLine = new ExpandGrid(grid, lineNumber, startGateY, startGateX, 0);
            gridQueue.add(firstLine);

            // uitbreden van de grid
            while (gridQueue.peek() != null) {
                ExpandGrid expandable = gridQueue.remove();

                ArrayList<ExpandGrid> miniQueue = grid.expandGrid
                        (expandable.grid, expandable.number, expandable.x, expandable.y, expandable.z);
                for (ExpandGrid childGrid : miniQueue) {
                    if (grid.endCondition(expandable, net.gate2)) return childGrid.grid;
                    gridQueue.add(childGrid);


                }


        } return null;
    }

}






