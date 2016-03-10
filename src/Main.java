import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
public class Main {


    public static void main(String[] args) {

        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        int totalscore = 0;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<GridScore> finalOut = new ArrayList<>();
        ArrayList<Net> netDatabase1 = grid.netDatabase;
        GridScore currentscore = new GridScore(grid, 0,  grid.netDatabase);


        //first try at a hillclimber

       // for (int i = 0; i < 5; i++) {
         //   netDatabase1 = makeRandomChangeNet(netDatabase1);


            // use astar on all nets
            for (int lineNumber = 0; lineNumber < netDatabase1.size(); lineNumber++) {
                currentscore = astar(currentscore, lineNumber);
                finalOut.add(currentscore);
            }


            for (GridScore score : finalOut) {

                totalscore += score.gate2;
            }
            currentscore.grid.printGrid();
            System.out.println("Totalscore: " + totalscore);
        }
   // }
    private static ArrayList<Net> makeRandomChangeNet(ArrayList<Net> netDatabase){

        Random rgen = new Random();

        int index = rgen.nextInt(netDatabase.size());
        Net net = netDatabase.get(index);
        netDatabase.add(net);
        netDatabase.get(index);
        return netDatabase;
    }

    private static GridScore astar(GridScore grid, int lineNumber){
        for (int i = 0; i < grid.netDatabase.size(); i++) {
            PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

            Net net = grid.netDatabase.get(lineNumber);
            int startGate = net.gate1;
            int startGateX = grid.grid.gateDatabase[startGate][1];
            int startGateY = grid.grid.gateDatabase[startGate][2];

            ExpandGrid firstLine = new ExpandGrid(grid.grid, lineNumber,startGateY, startGateX, 0, 0, 0);
            gridQueue.add(firstLine);


            // uitbreden van de grid
            while (true) {
                // element uit de Queue

                ArrayList<ExpandGrid> allChildGrids = grid.grid.possible_lines(gridQueue.remove(), net);
                for (ExpandGrid childGrid: allChildGrids) {

                    if (grid.grid.endCondition(childGrid, net.gate2)) {

                        GridScore score = new GridScore(childGrid.grid, childGrid.steps, grid.netDatabase);
                        return score;

                    }
                    gridQueue.add(childGrid);
                }

            }
        } return null;
    }

}






