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



        int totalScore = 0;

        GridScore currentGrid = new GridScore(grid, 0, nets);


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


        boolean noLine = false;
        int lineNumber = 0;
        while(lineNumber < poleCoordinates.length) {
            for ( lineNumber = 0; lineNumber < poleCoordinates.length; lineNumber++) {

                GridScore previousGrid = currentGrid;
                currentGrid = astar(currentGrid, lineNumber, poleCoordinates[lineNumber], noLine);
                noLine = false;
                if (currentGrid == null) {
                    noLine = true;
                    currentGrid = previousGrid;
                    lineNumber--;
                } else {
                    totalScore += currentGrid.score;
                }
            }
        }
        System.out.println("Succesfully created solution");
        System.out.println("Optimizing...");

/*
        GridScore iterativeRound = new GridScore(currentGrid.grid, 0, nets);
        int currentLowest = 10000;
        int notShorter = 0;
       // while(notShorter<100){
            for(int i = 0; i < poleCoordinates.length; i++){
                iterativeRound.grid =  removeLine(i,iterativeRound.grid);


            }

        iterativeRound.grid.printGrid();
        // }
*/


                // if(totalScore < currentTotal){

                //currentTotal = totalScore;

                System.out.println("totals: " + totalScore);
                // }
                // }
        }

    private static GridScore astar(GridScore currentGrid, int lineNumber, PoleCoordinates coordinates, boolean lowerTarget) {

        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();
        Net net = currentGrid.netDatabase.get(lineNumber);
        int targetZ;
        int startZ;
        if (lowerTarget && coordinates.z2 > 0)  {
            targetZ = coordinates.z2--;
            startZ = coordinates.z1;
        }
        else if(lowerTarget && (coordinates.z2 == 0) && coordinates.z1 > 0){
            targetZ = coordinates.z2;
            startZ= coordinates.z1--;
            System.out.println("end has hit the bottom");
        } else if(lowerTarget && (coordinates.z2 == 0) && coordinates.z1 == 0){
            targetZ = coordinates.z2;
            startZ = coordinates.z1;
            System.out.println("both have hit the bottom");

        } else{
            targetZ = coordinates.z2;
            startZ = coordinates.z1;
        }

        ExpandGrid firstLine = new ExpandGrid(currentGrid.grid, lineNumber, coordinates.x1, coordinates.y1, startZ, 0, 0);
        gridQueue.add(firstLine);


        // uitbreden van de grid
        int count = 0;
        while (!gridQueue.isEmpty()) {
            ArrayList<ExpandGrid> allChildren = currentGrid.grid.create_possible_lines(gridQueue.remove(), coordinates.x2,coordinates.y2,targetZ);
            for (ExpandGrid childGrid : allChildren) {

                if (childGrid.estimate <= 1) {

                    return new GridScore(childGrid.grid, childGrid.steps + 1, currentGrid.netDatabase);
                }
                gridQueue.add(childGrid);
            }
            count++;
        }

            System.out.println("Error: could not generate line " + lineNumber + ", " + net);

            return null;

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
private static Grid removeLine(int currentLineNumber, Grid currentGrid) {
    System.out.print("helloha");
    for (int j = 0; j < currentGrid.grid[0][0].length; j++) {
        for (int i = 1; i < currentGrid.grid.length; i++) {   //creation of height
            for (int k = 1; k < currentGrid.grid[0].length; k++) {    //creation of width X
                String symbol = "L" + currentLineNumber + "";
                System.out.println("j "+ j);
                System.out.println("i "+ i);
                System.out.println("k "+ k);
                if (currentGrid.grid[j][k][i].equals(symbol)) {
                // currentGrid.grid[0][k][] = null;
                }
            }
        }
    }
    return currentGrid;
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






