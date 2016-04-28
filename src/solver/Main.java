package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

import static solver.Grid.*;

public class Main {
    public static void main(String[] args) {

        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 7;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
        ArrayList<Net> nets = grid.netDatabase;
        Collections.shuffle(nets);

        int currentTotal = 10000;




        //while(true) {

            nets = mutateNets(nets);
            int totalScore = 0;

            GridScore currentGrid = new GridScore(grid, 0, nets);


            //for(int i = 0; i < nets.size(); i++) {
            //    Net net1 = nets.get(i);
            //    Grid.create_line(currentGrid, net1, 7, i);
            //}
            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net1 = nets.get(lineNumber);
                int layerNumber = 4;
                int[] coordinates = currentGrid.grid.create_line(net1, layerNumber, lineNumber);

                currentGrid = astar(currentGrid, lineNumber, layerNumber, coordinates);

                totalScore += currentGrid.score;
            }
        currentGrid.grid.printGrid();
            System.out.println("Score: " + totalScore);
           // if(totalScore < currentTotal){

           //     currentTotal = totalScore;


                System.out.print(totalScore);
           // }
        //}

    }

    private static GridScore astar(GridScore currentGrid, int lineNumber, int layerNumber, int[] coordinates) {

        int x1 = coordinates[0];
        int y1 = coordinates[1];
        int x2 = coordinates[2];
        int y2 = coordinates[3];

        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        Net net = currentGrid.netDatabase.get(lineNumber);
        int startGate = net.gate1;
        int startGateX = x1;
        int startGateY = y1;

        ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber, startGateY, startGateX, layerNumber, 0, 0);

        gridQueue.add(firstLine);

        // uitbreden van de grid
        int count = 0;
        while (count < 5000 && !gridQueue.isEmpty()) {

            ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), x2, y2);

            for (ExpandGrid childGrid : allChildren) {

                if (childGrid.estimate <= 1) {
                    return new GridScore(childGrid.grid, childGrid.steps + 1, currentGrid.netDatabase);
                }
                gridQueue.add(childGrid);
            }

            count++;
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






