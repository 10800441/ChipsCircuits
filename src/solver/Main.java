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

        int[] occ = countGateOccurrence(nets);
        for (int i = 0; i < occ.length; i++) {
            if (occ[i] > 5) {
                System.out.println("There are no solutions.");
                break;
            }
        }
        System.out.println("Calculating solution...");


        //while(true) {


        int totalScore = 0;

        GridScore currentGrid = new GridScore(grid, 0, nets);


        //for(int i = 0; i < nets.size(); i++) {
        //    Net net1 = nets.get(i);
        //    Grid.create_line(currentGrid, net1, 7, i);
        //}
        boolean error = true;
        PoleCoordinates[] poleCoordinates = null;
        int count = 0;
            currentGrid = new GridScore(grid, 0, nets);
            while (error == true) {
                poleCoordinates = new PoleCoordinates[nets.size()];
                Collections.shuffle(nets);
                grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
                currentGrid = new GridScore(grid, 0, nets);
                int layerNumber = 7;
                count++;
                error = false;
                for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                    Net net1 = nets.get(lineNumber);

                    int[] coordinates = currentGrid.grid.create_line(net1, layerNumber, lineNumber);
                    if (coordinates[0] == -1) {
                        error = true;
                        System.out.println("Pole error try: " + count);
                    } else {
                        poleCoordinates[lineNumber] = new PoleCoordinates(coordinates[0], coordinates[1], coordinates[4], coordinates[2], coordinates[3], coordinates[4]);

                        int devisionNumber = (nets.size() / Z_SIZE) + 1;
                        if (lineNumber % devisionNumber == 0 && layerNumber > 0 && lineNumber > 0)
                            layerNumber--;
                    }
                }
            }
            System.out.println("Succesfully placed poles.");
            GridScore gridWithPoles = currentGrid;

        boolean error1 = true;
        while(error1 == true) {
            error1 = false;
            for (int lineNumber = 0; lineNumber < poleCoordinates.length; lineNumber++) {

                currentGrid = astar(gridWithPoles, lineNumber, poleCoordinates[lineNumber]);
                if (currentGrid == null) {
                    error1 = true;
                    break;
                }
                totalScore += currentGrid.score;
            }
        }



                // if(totalScore < currentTotal){

                //currentTotal = totalScore;

                System.out.println("totals: " + totalScore);
                // }
                currentGrid.grid.printGrid();
                // }
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
            ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), x2, y2, z);
            for (ExpandGrid childGrid : allChildren) {

                if (childGrid.estimate <= 1) {
                    return new GridScore(childGrid.grid, childGrid.steps + 1, currentGrid.netDatabase);
                }
                gridQueue.add(childGrid);
            }
            count++;
        }
        if(count >= 5000 || gridQueue.isEmpty()) {
            System.out.println("Error: could not generate line " + lineNumber + ", " + net);
            currentGrid.grid.printGrid();
            return null;
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

    private static int[] countGateOccurrence(ArrayList<Net> nets) {
        int[] gateOccurrence = new int[26];

        for(int i = 0; i < nets.size(); i++ ) {
            int gate1 = nets.get(i).gate1;
            int gate2 = nets.get(i).gate2;
            gateOccurrence[gate1]++;
            gateOccurrence[gate2]++;
        }
        return gateOccurrence;
    }
}






