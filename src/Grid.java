import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Grid {
    String[][][] grid;
    int[][] gateDatabase = new int[26][50];
    ArrayList<Net> netDatabase = new ArrayList<>();


    public Grid(int width, int height, int depth) {
        grid = new String[width][height][depth];
        makeNetDatabase();

        for (int i = 0; i < makeGateDatabase().length; i++) {
            addGate(i, makeGateDatabase()[i][1], makeGateDatabase()[i][2]);
        }

    }

    //copy constructor to make copies of the current grid
    public Grid(Grid oldGrid) {
        this(oldGrid.grid.length, oldGrid.grid[0].length, oldGrid.grid[0][0].length);
        for (int i = 0; i < oldGrid.grid[0][0].length; i++) {
            for (int k = 0; k < oldGrid.grid[0].length; k++) {
                for (int n = 0; n < oldGrid.grid.length; n++) {

                    grid[n][k][i] = oldGrid.grid[n][k][i];


                }
            }
        }

    }


    public ArrayList<ExpandGrid> expandGrid(Grid grid, int number, int x, int y, int z) {
        ArrayList miniqueue = grid.possible_lines(grid, number, x, y, z);

        for (int i = 0; i < miniqueue.size(); i++) {
            ExpandGrid miniqueue_element = (ExpandGrid) miniqueue.get(i);
            miniqueue_element.grid.printGrid();
        }
        return miniqueue;
    }

    public void printGrid() {


        for (int i = 1; i < grid.length; i++) {   //creation of height Y
            System.out.println("");
            for (int k = 1; k < grid[1].length; k++) {    //creation of width X

                if (grid[i][k][0] == null) {
                    System.out.print(" . ");
                } else {

                    String gridContent = grid[i][k][0];
                    char identifier = gridContent.charAt(0);

                    // Labeling bij het printen
                    // Voor gates
                    if (identifier == 'G' && grid[i][k][0].length() == 3) {
                        System.out.print(grid[i][k][0]);
                    } else if (identifier == 'G') {
                        System.out.print(" " + grid[i][k][0]);
                    } else if (identifier == 'L') {
                        System.out.print(" " + grid[i][k][0]);
                    }
                }
            }
        }
        System.out.println("");
    }

    public void addGate(int number, int y_coordinate, int x_coordinate) {
        grid[x_coordinate][y_coordinate][0] = "G" + number;
    }

    public void addLine(int number, int x, int y, int z) {
        grid[x][y][z] = "L" + number;
    }

    public ArrayList possible_lines(Grid input_grid, int number, int x, int y, int z) {
        ArrayList<ExpandGrid> miniqueue = new ArrayList<>();

        if (x + 1 > 0 && x + 1 < grid.length && grid[x + 1][y][z] == null) {
            Grid grid1 = new Grid(input_grid);
            grid1.addLine(number, x + 1, y, z);
            ExpandGrid newGrid = new ExpandGrid(grid1, number, x + 1, y, z);
            miniqueue.add(newGrid);
        }
        if (x - 1 > 0 && x - 1 < grid.length && grid[x - 1][y][z] == null) {
            Grid grid2 = new Grid(input_grid);
            grid2.addLine(number, x - 1, y, z);
            ExpandGrid newGrid = new ExpandGrid(grid2, number, x - 1, y, z);
            miniqueue.add(newGrid);
        }
        if (y + 1 > 0 && y + 1 < grid[0].length && grid[x][y + 1][z] == null) {
            Grid grid3 = new Grid(input_grid);
            grid3.addLine(number, x, y + 1, z);
            ExpandGrid newGrid = new ExpandGrid(grid3, number, x, y + 1, z);
            miniqueue.add(newGrid);
        }
        if (y - 1 > 0 && y - 1 < grid[0].length && grid[x][y - 1][z] == null) {
            Grid grid4 = new Grid(input_grid);
            grid4.addLine(number, x, y - 1, z);
            ExpandGrid newGrid = new ExpandGrid(grid4, number, x, y - 1, z);
            miniqueue.add(newGrid);
        }
        if (z + 1 > 0 && z + 1 < grid[0][0].length && grid[x][y][z + 1] == null) {
            Grid grid5 = new Grid(input_grid);
            grid5.addLine(number, x, y, z + 1);
            ExpandGrid newGrid = new ExpandGrid(grid5, number, x, y, z + 1);
            miniqueue.add(newGrid);
        }
        if (z - 1 > 0 && z - 1 < grid[0][0].length && grid[x][y][z - 1] == null) {
            Grid grid6 = new Grid(input_grid);
            grid6.addLine(number, x, y, z - 1);
            ExpandGrid newGrid = new ExpandGrid(grid6, number, x, y, z - 1);
            miniqueue.add(newGrid);
        }

        return miniqueue;
    }


    public int[][] makeGateDatabase() {
        try {
            BufferedReader rd = new BufferedReader(new FileReader("src/print1Gates.txt"));

            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");
                gateDatabase[Integer.valueOf(words[0])][1] = Integer.valueOf(words[1]);
                gateDatabase[Integer.valueOf(words[0])][2] = Integer.valueOf(words[2]);
            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }

        return gateDatabase;
    }


    // Read in the netdatabase from the file "print1Lines.txt"
    public void makeNetDatabase() {

        try {
            BufferedReader rd = new BufferedReader(new FileReader("src/print1Lines.txt"));

            String line;
            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(",");

                Net net = new Net(Integer.valueOf(words[0]), Integer.valueOf(words[1]));
                netDatabase.add(net);

            }
            rd.close();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }

    }
    public boolean endCondition(ExpandGrid currentGrid, int gateNumber) {
        boolean endcondition = false;

        int gateX = gateDatabase[gateNumber][1];
        int gateY = gateDatabase[gateNumber][2];

        if (currentGrid.x == gateX - 1 || currentGrid.x == gateX + 1) {
            if (currentGrid.y == gateY - 1 || currentGrid.y == gateY + 1) {
                if (currentGrid.z == 0 || currentGrid.z == 1) {
                    endcondition = true;
                }
            }


        }
        return endcondition;
    }
}