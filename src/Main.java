public class Main {

    public static void main(String[] args) {
        // Het aanmaken van de grid LET OP 0 telt niet mee!
        int X_SIZE = 19;
        int Y_SIZE = 14;
        int Z_SIZE = 1;
        Grid grid = new Grid(Y_SIZE, X_SIZE, Z_SIZE);

        // Inlezen van een array als nieuwe grid

        Grid grid3 = new Grid(grid);
        grid.printGrid();

    }



}
