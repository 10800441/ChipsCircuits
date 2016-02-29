import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Grid {
    String[][][] grid;
    int[][] gateDatabase = new int[26][50];
    ArrayList<Net> netDatabase = new ArrayList<>();


    public Grid( int width, int height, int depth) {
        grid = new String[width][height][depth];
        makeNetDatabase();

        for(int i = 0; i < gateDatabase().length; i++){
            addGate(i, gateDatabase()[i][1],gateDatabase()[i][2]);
        }

    }
     //copy constructor to make copies of the current grid
    public Grid(Grid oldGrid){
       this(oldGrid.grid.length, oldGrid.grid[0].length, oldGrid.grid[0][0].length);
        for(int i = 0; i < oldGrid.grid[0][0].length; i ++){
            for(int k = 0; k < oldGrid.grid[0].length; k ++) {
                for (int n = 0; n < oldGrid.grid.length; n++) {

                    grid[n][k][i] = oldGrid.grid[n][k][i];


                }
            }
        }

 

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
                    } else  if (identifier == 'G') {
                        System.out.print(" " + grid[i][k][0]);
                    } else if (identifier == 'L') {
                    System.out.print(" " + grid[i][k][0]);
                    }
                }
            }
        }
    }

    public void addGate(int number, int y_coordinate, int x_coordinate) {
       grid[x_coordinate][y_coordinate][0] = "G"+ number;
    }
    public void addLine(int number, int x1, int y1, int x2, int y2) {
        grid[x1][y1][0] = "L"+ number;
        grid[x2][y2][0] = "L"+ number;


    }




    public int[][] gateDatabase() {

        //Print #1
        // format: gateDatabase[nummer][coordinaat]
        gateDatabase[1][1] = 2;
        gateDatabase[1][2] = 2;
        gateDatabase[2][1] = 7;
        gateDatabase[2][2] = 2;
        gateDatabase[3][1] = 11;
        gateDatabase[3][2] = 2;
        gateDatabase[4][1] = 16;
        gateDatabase[4][2] = 2;
        gateDatabase[5][1] = 4;
        gateDatabase[5][2] = 3;
        gateDatabase[6][1] = 13;
        gateDatabase[6][2] = 3;
        gateDatabase[7][1] = 15;
        gateDatabase[7][2] = 3;
        gateDatabase[8][1] = 15;
        gateDatabase[8][2] = 4;
        gateDatabase[9][1] = 9;
        gateDatabase[9][2] = 5;
        gateDatabase[10][1] = 2;
        gateDatabase[10][2] = 6;
        gateDatabase[11][1] = 5;
        gateDatabase[11][2] = 6;
        gateDatabase[12][1] = 12;
        gateDatabase[12][2] = 6;
        gateDatabase[13][1] = 17;
        gateDatabase[13][2] = 6;
        gateDatabase[14][1] = 14;
        gateDatabase[14][2] = 8;
        gateDatabase[15][1] = 17;
        gateDatabase[15][2] = 8;
        gateDatabase[16][1] = 3;
        gateDatabase[16][2] = 9;
        gateDatabase[17][1] = 7;
        gateDatabase[17][2] = 9;
        gateDatabase[18][1] = 10;
        gateDatabase[18][2] = 9;
        gateDatabase[19][1] = 12;
        gateDatabase[19][2] = 9;
        gateDatabase[20][1] = 16;
        gateDatabase[20][2] = 9;
        gateDatabase[21][1] = 2;
        gateDatabase[21][2] = 10;
        gateDatabase[22][1] = 3;
        gateDatabase[22][2] = 11;
        gateDatabase[23][1] = 10;
        gateDatabase[23][2] = 11;
        gateDatabase[24][1] = 2;
        gateDatabase[24][2] = 12;
        gateDatabase[25][1] = 13;
        gateDatabase[25][2] = 12;

        return  gateDatabase;
    }




    public void makeNetDatabase(){

        try {
            BufferedReader rd = new BufferedReader(new FileReader("src/print.txt"));


            String line;


            while (true) {
                line = rd.readLine();
                if (line == null) break;
                String[] words = line.split(" ");

                Net net = new Net(Integer.valueOf(words[0]), Integer.valueOf(words[1]));
                netDatabase.add(net);

                System.out.println(net);

            }
            rd.close();
        }
        catch (IOException ex) {
            System.err.println("Error: " + ex);
        }

    }





}