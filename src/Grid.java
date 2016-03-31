import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Grid {
    String[][][] grid;
    int[][] gateDatabase;
    ArrayList<Net> netDatabase = new ArrayList<>();


public Grid(int width, int height, int depth, int[][] gateDatabase, ArrayList<Net> netDatabase){
    grid = new String[width][height][depth];
    this.gateDatabase = gateDatabase;
    for(int i = 0; i < gateDatabase.length; i++){
        addGate(i, gateDatabase[i][1], gateDatabase[i][2]);
    }
    this.netDatabase = netDatabase;

    }



    public Grid(int width, int height, int depth) {
        this(width, height, depth, makeGateDatabase(), makeNetDatabase());
    }

    // copy constructor to make copies of the current grid
    public Grid(Grid oldGrid){
       this(oldGrid.grid.length, oldGrid.grid[0].length, oldGrid.grid[0][0].length, oldGrid.gateDatabase, oldGrid.netDatabase);
        for(int i = 0; i < oldGrid.grid[0][0].length; i++){
            for(int k = 0; k < oldGrid.grid[0].length; k++) {
                for (int n = 0; n < oldGrid.grid.length; n++) {

                    grid[n][k][i] = oldGrid.grid[n][k][i];
                }
            }
        }

    }


    public void printGrid() {
        for (int j = 0; j < grid[0][0].length; j++) {
            System.out.println("");
            int layer = j+1;

            System.out.println("");
            System.out.println("Grid layer: " + layer);
            for (int i = 1; i < grid.length; i++) {   //creation of height Y
                System.out.println("");
                for (int k = 1; k < grid[0].length; k++) {    //creation of width X
                    if (grid[i][k][j] == null) {
                        System.out.print(" . ");
                    } else {

                        String gridContent = grid[i][k][j];
                        char identifier = gridContent.charAt(0);

                        // Labeling bij het printen

                        if (identifier == 'G' && grid[i][k][j].length() == 3) {
                            System.out.print("\033[31m");
                            System.out.print(grid[i][k][j]);
                            System.out.print("\033[0m");
                        } else if (identifier == 'G') {
                            System.out.print("\033[31m");
                            System.out.print(" " + grid[i][k][j]);
                            System.out.print("\033[0m");
                        }

                        else if (identifier == 'L' && grid[i][k][j].length() == 3) {
                            System.out.print(grid[i][k][j]);
                        }
                        else if (identifier == 'L') {
                            System.out.print(" " + grid[i][k][j]);
                        }
                    }
                }
            }
        }
        System.out.println("");
    }

    public void addGate(int number, int y_coordinate, int x_coordinate) {
       grid[x_coordinate][y_coordinate][0] = "G"+ number;
    }

    public void addLine(int number, int x, int y, int z) {
        grid[x][y][z] = "L"+ number;
    }

    // provides an expandgrid for the create_possible_lines method
    public ExpandGrid addLine(Grid input_grid, int number, int x, int y, int z, int steps, Net net) {
        Grid copy_grid = new Grid(input_grid);
        copy_grid.addLine(number, x, y, z);
        int estimate = manhattanDistance(x, y, z, net);
        return new ExpandGrid(copy_grid, number, x, y, z, steps+1, estimate);
    }


    public ArrayList create_possible_lines(ExpandGrid inputExpandGrid, Net net) {
        Grid inputGrid = inputExpandGrid.grid;
        int number = inputExpandGrid.number;
        int x = inputExpandGrid.x;
        int y = inputExpandGrid.y;
        int z = inputExpandGrid.z;
        int steps = inputExpandGrid.steps;

        ArrayList<ExpandGrid> list = new ArrayList<>();

        if( x+1 > 0 && x+1 < grid.length && grid[x+1][y][z] == null) {
            list.add(addLine(inputGrid, number, x+1, y, z, steps, net));
        }
        if( x-1 > 0 && x-1 < grid.length && grid[x-1][y][z] == null) {
            list.add(addLine(inputGrid, number, x-1, y, z, steps, net));
        }
        if( y+1 > 0 && y+1 < grid[0].length && grid[x][y+1][z] == null) {
            list.add(addLine(inputGrid, number, x,  y+1, z, steps, net));
        }
        if( y-1 > 0 && y-1 < grid[0].length && grid[x][y-1][z] == null) {
            list.add(addLine(inputGrid, number, x, y-1, z, steps, net));
        }
        if( z+1 > 0 && z+1 < grid[0][0].length && grid[x][y][z+1] == null) {
            list.add(addLine(inputGrid, number, x, y,  z+1, steps, net));
        }
        if( z-1 > 0 && z-1 < grid[0][0].length && grid[x][y][z-1] == null) {
            list.add(addLine(inputGrid, number, x, y, z-1, steps, net));
        }
        return list;
    }

    public static int[][] makeGateDatabase() {
        int[][] gateDatabase = new int[100][100];
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
        }
        catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
        return  gateDatabase;
    }

    // Read in the net database from the file "print1Lines.txt"
    public static ArrayList<Net> makeNetDatabase() {
        ArrayList<Net> netDatabase = new ArrayList<>();
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
        return netDatabase;
    }



    public int manhattanDistance(int x, int y, int z, Net netGate){
        int gateNumber = netGate.gate2;
        int y2 = gateDatabase[gateNumber][1];
        int x2 = gateDatabase[gateNumber][2];

        return Math.abs(x2-x) + Math.abs(y2-y) + (z-1);
    }

}