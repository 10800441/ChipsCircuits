public class Main {

    public static void main(String[] args) {
        // Het aanmaken van de grid LET OP 0 telt niet mee!
        int X_SIZE = 19;
        int Y_SIZE = 14;
        Grid grid = new Grid(X_SIZE, Y_SIZE);
        grid.netDatabase();
        // Inlezen van een array als nieuwe grid
        for(int i = 0; i < grid.gateDatabase().length; i++){
            grid.addGate(i, grid.gateDatabase()[i][1],grid.gateDatabase()[i][2]);
        }
        grid.printGrid();

    }
}
