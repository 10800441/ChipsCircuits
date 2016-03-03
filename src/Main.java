import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);


        ArrayList<Grid> finalGridLines = new ArrayList<>();

        for (int i = 0; i < grid.netDatabase.size(); i++) {
            Queue<ExpandGrid> gridQueue = new LinkedList();
            Net net = grid.netDatabase.get(i);
            int startGate = net.gate1;
            int startGateX = grid.gateDatabase[startGate][1];
            int startGateY = grid.gateDatabase[startGate][2];


            ExpandGrid firstLine = new ExpandGrid(grid, i, startGateY, startGateX, 0);
            gridQueue.add(firstLine);

            // uitbreden van de grid
            while (gridQueue.peek() != null) {
                ExpandGrid expandable = gridQueue.remove();

                ArrayList<ExpandGrid> miniQueue = grid.expandGrid
                        (expandable.grid, expandable.number, expandable.x, expandable.y, expandable.z);
                for (int k = 0; k < miniQueue.size(); k++) {
                    gridQueue.add(miniQueue.get(k));


                }
                if (grid.endCondition(expandable, net.gate2)) {
                    finalGridLines.add(expandable.grid);
                    expandable.grid.printGrid();
                    grid = expandable.grid;
                    break;
                }
            }
        }
    }
}






