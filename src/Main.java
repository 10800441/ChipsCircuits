import java.util.ArrayList;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {


        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 7;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<Net> nets = grid.netDatabase;
        GridScore currentGrid = new GridScore(grid, 0, nets);
int totalScore = 0;

        for(int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
            currentGrid = astar(currentGrid, lineNumber);
            totalScore +=  currentGrid.score;
        }
        System.out.print(totalScore);
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

            int conter = 0;
            long timet =0;

            int conter2 = 0;

            // uitbreden van de grid
            while (true) {

                conter2 ++;

                long start = System.currentTimeMillis();
              ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), net);


                long end = System.currentTimeMillis();
                timet += end-start;
                for (ExpandGrid childGrid:  allChildren) {

                    conter ++;
                    if (currentGrid.grid.endCondition(childGrid, net.gate2)) {


                      childGrid.grid.printGrid();
                        System.out.println("cont: " + conter + "/ " + conter2 +" T " + timet );

                        return new GridScore(childGrid.grid, childGrid.steps+1, currentGrid.netDatabase);
                    }

                    gridQueue.add(childGrid);
                }

            }


        } return null;
    }
}






