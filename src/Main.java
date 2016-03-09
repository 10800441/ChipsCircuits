import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        int totalscore = 0;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<GridScore> finalOut = new ArrayList<>();
        GridScore currentscore = new GridScore(grid, 0);
        int totalSteps = 0;
     
        for(int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
            currentscore = astar(currentscore, lineNumber, totalSteps);

            finalOut.add(currentscore);
        }
        for(GridScore score: finalOut) {

            totalscore += score.gate2;
        }
       currentscore.grid.printGrid();
        System.out.println("Totalscore: " + totalscore);
    }


    private static GridScore astar(GridScore grid, int lineNumber, int totalSteps){
        for (int i = 0; i < grid.grid.netDatabase.size(); i++) {
            PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

            Net net = grid.grid.netDatabase.get(lineNumber);
            int startGate = net.gate1;
            int startGateX = grid.grid.gateDatabase[startGate][1];
            int startGateY = grid.grid.gateDatabase[startGate][2];

            ExpandGrid firstLine = new ExpandGrid(grid.grid, lineNumber,startGateY, startGateX, 0, 0, 0);
            gridQueue.add(firstLine);


            // uitbreden van de grid
            while (true) {
                ExpandGrid expandable = gridQueue.remove();


                ArrayList<ExpandGrid> miniQueue = grid.grid.expandGrid(expandable.grid, expandable.number, expandable.x, expandable.y, expandable.z, expandable.steps, net);
                for (ExpandGrid childGrid: miniQueue) {

                    if (grid.grid.endCondition(childGrid, net.gate2)) {

                        GridScore score = new GridScore(childGrid.grid, childGrid.steps);
                        return score;

                    }
                    gridQueue.add(childGrid);
                }

            }
        } return null;
    }

}






