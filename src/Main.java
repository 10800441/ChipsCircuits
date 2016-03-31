import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 7;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<Net> nets1 = grid.netDatabase;
        int currentTotal = 10000;
        for (int i = 0; i < 100; i++) {

            ArrayList<Net> nets = randomNets(nets1);
            int totalScore = 0;
            GridScore currentGrid = new GridScore(grid, 0, nets);


            for (int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                currentGrid = astar(currentGrid, lineNumber);


                totalScore += currentGrid.score;
            }


            if(totalScore < currentTotal){

                currentTotal = totalScore;

                currentGrid.grid.printGrid();
                System.out.print(totalScore);
            }
        }

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

                        return new GridScore(childGrid.grid, childGrid.steps+1, currentGrid.netDatabase);
                    }
                    gridQueue.add(childGrid);
                }

            }


        } return null;
    }
    
    private static ArrayList<Net>randomNets(ArrayList<Net> nets1){
Random rgen = new Random();

        int random1 = rgen.nextInt(nets1.size());
        int random2 = rgen.nextInt(nets1.size());
        Net interchangable = nets1.get(random2);
        nets1.set(random2, nets1.get(random1));
        nets1.set(random1, interchangable);
        return nets1;
        
    }
}






