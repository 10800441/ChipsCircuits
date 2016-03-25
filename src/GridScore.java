import java.util.ArrayList;

public class GridScore{
    final Grid grid;
    final int score;
    ArrayList<Net> netDatabase;

    public GridScore(Grid grid, int score, ArrayList<Net> netDatabase) {
        this.grid = grid;
        this.score = score;
        this.netDatabase = netDatabase;
    }
    public String toString(){
        return "Net: " + grid + " " + score;

    }

}