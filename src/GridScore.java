import java.util.ArrayList;

public class GridScore{
    final Grid grid;
    final int gate2;
    ArrayList<Net> netDatabase;

    public GridScore(Grid grid, int gate2, ArrayList<Net> netDatabase) {
        this.grid = grid;
        this.gate2 = gate2;
        this.netDatabase = netDatabase;
    }
    public String toString(){
        return "Net: " + grid + " " + gate2;


    }

}