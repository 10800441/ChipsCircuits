public class ExpandGrid {
    final Grid grid;
    final int number;
    final int x;
    final int y;
    final int z;


    public ExpandGrid(Grid grid, int number, int x, int y, int z) {
        this.grid = grid;
        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public String toString(){
        return "CurrentGrid: " + number + " x: " + z + " y: " + y + " z: " + z;


    }

}
