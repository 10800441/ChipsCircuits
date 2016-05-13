package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

import static solver.Grid.*;

public class Main {
    final static int X_SIZE = 19;
    final static int Y_SIZE = 14;
    final static int Z_SIZE = 7;

    public static void main(String[] args) {

        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);

        System.out.println("Calculating solution...");
        long time1 = System.currentTimeMillis();

        // Cration of a suboptial solution
        GridScore solution = generateSolution(grid);
        while (solution == null) {
            solution = generateSolution(grid);
        }

        // Hillclimbing

        System.out.println("Score " + solution.score);
        System.out.println("Initialising Iterative round");
        optimiseSolution(solution);

        long time2 = System.currentTimeMillis();
        System.out.println("It took " + (time2 - time1) + " miliseconds.");


    }






    private static GridScore astar(GridScore currentGrid, int lineNumber, int x1,int y1, int z1, int x2, int y2, int z2, GridScore trialGrid) {
        ArrayList<ExpandGrid> memory = new ArrayList<>();
        PriorityQueue<ExpandGrid> gridQueue = new PriorityQueue<>();
        Net net = currentGrid.netDatabase.get(lineNumber);

        // Indicating the startposition (the first gate)
        ExpandGrid firstLine = new ExpandGrid(trialGrid.grid, lineNumber, x1, y1, z1, 0, 0);
        gridQueue.add(firstLine);



        int counter = 0;
        // Expansion from the startposition
        while (!gridQueue.isEmpty() && counter < Y_SIZE * X_SIZE * Z_SIZE) {
            ArrayList<ExpandGrid> allChildren = trialGrid.grid.create_possible_lines(gridQueue.remove(), x2,y2, z2);

            // affects all generated expansions
            for (ExpandGrid childGrid : allChildren) {

                // Expansion must not already have occured
                boolean exist = false;
                for (int i = 0; i < memory.size(); i++) {
                    int memoryx = memory.get(i).x;
                    int memoryy = memory.get(i).y;
                    int memoryz = memory.get(i).z;
                    int memorysteps = memory.get(i).steps;
                    int memoryestimate = memory.get(i).estimate;
                    if (childGrid.x == memoryx && childGrid.y == memoryy && childGrid.z == memoryz &&
                            childGrid.steps == memorysteps && childGrid.estimate == memoryestimate) {
                        exist = true;
                        break;
                    }
                }

                if (exist == false) {
                    counter++;
                    // End condition
                    if (childGrid.estimate <= 1) {
                        return new GridScore(childGrid.grid, trialGrid.score + childGrid.steps, trialGrid.netDatabase);
                    }
                    gridQueue.add(childGrid);
                    memory.add(childGrid);
                }
            }
        }
        return null;
    }


    private static int[] countGateOccurrence(ArrayList<Net> nets) {
        int[] gateOccurrence = new int[26];

        for (int i = 0; i < nets.size(); i++) {
            int gate1 = nets.get(i).gate1;
            int gate2 = nets.get(i).gate2;
            gateOccurrence[gate1]++;
            gateOccurrence[gate2]++;
        }
        return gateOccurrence;
    }

    private static GridScore generateSolution(Grid grid) {
        ArrayList<Net> nets = grid.netDatabase;
        // a underestimate of the total length of all the lines combined
        int minimumScore = grid.totalMinimumScore(nets);
        System.out.println("minimunScore " + minimumScore);

        Collections.shuffle(nets);
        // Every gate has a maximum of 5 lines it can donate/recieve
        int[] occ = countGateOccurrence(nets);
        for (int i = 0; i < occ.length; i++) {
            if (occ[i] > 5) {
                System.out.println("There are no solutions.");
                break;
            }
        }
        System.out.println("Placing Poles...");


        GridScore currentGrid;
        ArrayList<PoleCoordinates>  poleArray = null;
        boolean error = true;
        currentGrid = new GridScore(grid, 0, nets);
        int totalPole = 0;


        while (error == true) {
            poleArray = new ArrayList<>();
            Collections.shuffle(nets);
            grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);
            currentGrid = new GridScore(grid, 0, nets);
            int layerNumber = 7;
            error = false;

            totalPole = 0;
            // Sequence will try to lay all poles
            for (int lineNumber = 0; lineNumber < nets.size(); lineNumber++) {
                Net net1 = nets.get(lineNumber);

                int[] coordinates = currentGrid.grid.create_line(net1, layerNumber, lineNumber);

                // The program was not able to lay all lines
                if (coordinates[0] == -1) {
                    error = true;
                    totalPole = 0;
                    break;
                } else {

                    totalPole += coordinates[5];
                    PoleCoordinates pole = new PoleCoordinates(lineNumber, coordinates[0], coordinates[1], coordinates[4], coordinates[2], coordinates[3], coordinates[4]);
                    poleArray.add(pole);

                    // Determining the height of the poles
                    int devisionNumber = (nets.size() / Z_SIZE) + 1;
                    if (lineNumber % devisionNumber == 0 && layerNumber > 0 && lineNumber > 0)
                        layerNumber--;
                }
            }
        }
        System.out.println("Succesfully placed poles.");

        // Creation of a disposable grid
        GridScore trialGrid = currentGrid;
        int lineNumber = 0;
        int counter = 0;
        //  The total length of all the A* lines (poles not included
        int totalALineLength = 0;

        while (lineNumber < grid.netDatabase.size()) {
            // After 20 attempts the whole sequence is canceled and the program will restart
            if (counter > 20) return null;
            for (lineNumber = 0; lineNumber < grid.netDatabase.size(); lineNumber++) {

                trialGrid = astar(currentGrid,  poleArray.get(lineNumber).lineNum,  poleArray.get(lineNumber).x1,
                        poleArray.get(lineNumber).y1,
                        poleArray.get(lineNumber).z1,
                        poleArray.get(lineNumber).x2,
                        poleArray.get(lineNumber).y2,
                        poleArray.get(lineNumber).z2, trialGrid);

                // Restart with a different order of nets if A* cannot lay all lines
                if (trialGrid == null) {
                    lineNumber = -1;
                    trialGrid = currentGrid;
                    Collections.shuffle(poleArray);
                    counter++;
                    // After a number of errors the pole-laying sequence is restarted
                    if (counter > nets.size() / 4) return null;
                    totalALineLength = 0;
                }
            }
            totalALineLength += trialGrid.score;
        }
        return new GridScore(trialGrid.grid, (totalALineLength + totalPole), trialGrid.netDatabase);
    }



    private static GridScore optimiseSolution(GridScore solution) {

        for(int lineNum = 0; lineNum < solution.netDatabase.size(); lineNum ++) {
            // Removal of an existing line
            solution = removeLine(solution, lineNum);

    
            Net net = solution.netDatabase.get(lineNum);

            int gate1 = net.gate1;
            int gate2 = net.gate2;


            System.out.println("FULL GRID: ");            //A*
            solution.grid.printGrid();
             solution = astar(solution.grid, gateDatabase[gate1][2];   ,solution.grid);


            System.out.println("NEW GRID: ");
            solution.grid.printGrid();
            System.out.println("Score " + solution.score);
            break;
        }
    return new GridScore(solution.grid, 0, solution.netDatabase);
    }


    private static GridScore removeLine(GridScore solution, int lineNum){
        int removeCount = 0;
        for (int i = 0; i <  solution.grid.grid[0][0].length; i++) {
            for (int k = 0; k < solution.grid.grid[0].length; k++) {
                for (int n = 0; n < solution.grid.grid.length; n++) {

                    String line = "L"+ lineNum;
                    if(solution.grid.grid[n][k][i] != null && solution.grid.grid[n][k][i].equals(line)){
                        solution.grid.grid[n][k][i] = null;
                        removeCount++;
                    }
                }
            }
        }
        return new GridScore(solution.grid, (solution.score - removeCount), solution.netDatabase);
    }

}




