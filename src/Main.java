import java.util.ArrayList;
import java.util.PriorityQueue;

public class Main {
    static int checkScore= 0;
    public static void main(String[] args) {

long start = System.currentTimeMillis();
            int X_SIZE = 19;
            int Y_SIZE = 14;
            int Z_SIZE = 7;
            Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
            ArrayList<Net> nets = grid.netDatabase;
            GridScore currentGrid = new GridScore(grid, 0, nets);
            int totalScore = 0;

            for (int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                currentGrid = astar(currentGrid, lineNumber);
                totalScore += currentGrid.score;
            }
            System.out.print(totalScore);
        long end = System.currentTimeMillis();
        System.out.print("Time " +  (end-start));
    }

    private static GridScore astar(GridScore currentGrid, int lineNumber){
        for (int i = 0; i < currentGrid.netDatabase.size(); i++) {
            PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

            Net net = currentGrid.netDatabase.get(lineNumber);
            int startGate = net.gate1;
            int startGateX = currentGrid.grid.gateDatabase[startGate][1];
            int startGateY = currentGrid.grid.gateDatabase[startGate][2];

            ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber,startGateY, startGateX, 0, 0, 0);
            gridQueue.add(firstLine);

            // uitbreden van de grid
            while (true) {

              ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), net);

                for (ExpandGrid childGrid:  allChildren) {


                    if ( childGrid.estimate < 1) {
                        childGrid.grid.printGrid();
                        checkScore += childGrid.steps;
                        System.out.println("");
                        System.out.print("\033[36m");
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                        System.out.print("\033[0m");

                        checkScore += childGrid.steps;
                        System.out.println("score of your program: total Length " + checkScore + " (till line L"  + childGrid.number + ")");
                        return new GridScore(childGrid.grid, childGrid.steps+1, currentGrid.netDatabase);
                    }
                    gridQueue.add(childGrid);
                }

            }


        } return null;
    }
}






