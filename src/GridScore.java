
public class GridScore{
    final Grid grid;
    final int gate2;

    public GridScore(Grid grid, int gate2) {
        this.grid = grid;
        this.gate2 = gate2;
    }
    public String toString(){
        return "Net: " + grid + " " + gate2;


    }

}