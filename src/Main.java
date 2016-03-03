import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        // Het aanmaken van de grid LET OP 0 telt niet mee!
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);

        makeLines(grid);

    }

    private static void makeLines(Grid grid) {
        Queue<ExpandGrid> gridQueue = new LinkedList();
        ArrayList<Grid> finalGridLines = new ArrayList<>();

        for (int i = 0; i < grid.netDatabase.size(); i++) {

            Net net = grid.netDatabase.get(i);
            int startGate = net.gate1;
            int startGateX = grid.gateDatabase[startGate][1];
            int startGateY = grid.gateDatabase[startGate][2];


            ExpandGrid firstLine = new ExpandGrid(grid, i, startGateY, startGateX, 0);
            gridQueue.add(firstLine);

            // uitbreden van de grid
            while (gridQueue.peek() != null) {
                ExpandGrid expandable = gridQueue.remove();
                if (grid.endCondition(expandable, net.gate2)) {
                    finalGridLines.add(grid);
                    break;
                }
                ArrayList<ExpandGrid> miniQueue = grid.expandGrid(expandable.grid, expandable.number, expandable.x, expandable.y, expandable.z);
                for (int k = 0; k < miniQueue.size(); k++) {
                    gridQueue.add(miniQueue.get(k));


                }
            }

        }

    }
}






