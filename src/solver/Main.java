package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 7;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<Net> nets = grid.netDatabase;
        Collections.shuffle(nets);

        int currentTotal = 1000;
        int counter = 0;
     //  while(counter < 1000){

       //     nets = mutateNets(nets);
            int totalScore = 0;
            GridScore currentGrid = new GridScore(grid, 0, nets);


            for (int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
                currentGrid = looseAstar(currentGrid, lineNumber);
                totalScore += currentGrid.score;
                System.out.println("total: " + totalScore);

            }
           /* for (int lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {
               currentGrid = tightAstar(currentGrid, lineNumber);
               totalScore += currentGrid.score;
           }

            if(totalScore < currentTotal){

                currentTotal = totalScore;

                currentGrid.grid.printGrid();
                System.out.print("total: " + totalScore);
                counter = 0;
            }
            else {
                counter++;
                System.out.println("total: " + (totalScore/ 100000));
*/            }




    private static GridScore looseAstar(GridScore currentGrid, int lineNumber){

            PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

            Net net = currentGrid.netDatabase.get(lineNumber);
            int startGate = net.gate1;
            int startGateX = currentGrid.grid.gateDatabase[startGate][1];
            int startGateY = currentGrid.grid.gateDatabase[startGate][2];

            ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber,startGateY, startGateX, 6, 0, 0);
            gridQueue.add(firstLine);


            // uitbreden van de grid

            while (!gridQueue.isEmpty()) {

                ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), net);

                for (ExpandGrid childGrid:  allChildren) {
                    System.out.println("L" + lineNumber);
                    System.out.println("S " + childGrid.steps);
                    System.out.println("E" + childGrid.estimate);
                    if ( childGrid.estimate < 1) {

                        return new GridScore(childGrid.grid, childGrid.steps+1, currentGrid.netDatabase);
                    }
                    gridQueue.add(childGrid);
                }

            }

        System.out.println("empty!!");
         return new GridScore(currentGrid.grid, 100000, currentGrid.netDatabase);

    }
    private static GridScore tightAstar(GridScore currentGrid, int lineNumber){

        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        Net net = currentGrid.netDatabase.get(lineNumber);
        int startGate = net.gate1;
        int startGateX = currentGrid.grid.gateDatabase[startGate][1];
        int startGateY = currentGrid.grid.gateDatabase[startGate][2];

        for (int j = 0; j < currentGrid.grid.grid[0][0].length; j++) {
            for (int i = 1; i < currentGrid.grid.grid.length; i++) {   //creation of height Y
                System.out.println("");
                for (int k = 1; k < currentGrid.grid.grid[0].length; k++) {    //creation of width X

                        String gridContent = currentGrid.grid.grid[i][k][j];
                        char identifier = gridContent.charAt(0);
                        int identifier2 = gridContent.charAt(1);


                        if (identifier == 'G' && identifier2 == lineNumber) {
                            System.out.println("check");

                            currentGrid.grid.grid[i][k][j] = null;
                        }
                    }
                }
            }



        ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber,startGateY, startGateX, 0, 0, 0);
        gridQueue.add(firstLine);


        // uitbreden van de grid

        while (!gridQueue.isEmpty()) {

            ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), net);

            for (ExpandGrid childGrid:  allChildren) {


                if ( childGrid.estimate < 1) {
childGrid.grid.printGrid();
                    return new GridScore(childGrid.grid, childGrid.steps+1, currentGrid.netDatabase);
                }
                gridQueue.add(childGrid);
            }


        }
        return new GridScore(currentGrid.grid, 100000, currentGrid.netDatabase);

    }



    private static ArrayList<Net> mutateNets(ArrayList<Net> nets1){
        Random rgen = new Random();

        int random1 = rgen.nextInt(nets1.size());
        int random2 = rgen.nextInt(nets1.size());
        Net interchangable = nets1.get(random2);
        nets1.set(random2, nets1.get(random1));
        nets1.set(random1, interchangable);
        return nets1;
        
    }
}






