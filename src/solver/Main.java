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
            int layerNumber = 7;
            PoleCoordinates[] poleCoordinates = new PoleCoordinates[nets.size()];

            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net1 = nets.get(lineNumber);

                int[] coordinates = currentGrid.grid.create_line(net1, layerNumber, lineNumber);
                poleCoordinates[lineNumber] = new PoleCoordinates(coordinates[0], coordinates[1], coordinates[4], coordinates[2], coordinates[3], coordinates[4]);
                System.out.println("Z!!! " + coordinates[4]);
                if(lineNumber % 5 == 0 && layerNumber > 0 && lineNumber > 0)
                    layerNumber--;
            }

             for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                currentGrid = astar(currentGrid, lineNumber, poleCoordinates[lineNumber]);

                totalScore += currentGrid.score;
            }



        currentGrid.grid.printGrid();
           // if(totalScore < currentTotal){

           //     currentTotal = totalScore;


                System.out.print(totalScore);
           // }
        //}

    }

    private static GridScore astar(GridScore currentGrid, int lineNumber, PoleCoordinates coordinates) {

        int x1 = coordinates.x1;
        int y1 = coordinates.y1;
        int z = coordinates.z1;
        int x2 = coordinates.x2;
        int y2 = coordinates.y2;


        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();

        Net net = currentGrid.netDatabase.get(lineNumber);
        int startGateX = y1;
        int startGateY = x1;

        ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber, startGateY, startGateX, z, 0, 0);

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






