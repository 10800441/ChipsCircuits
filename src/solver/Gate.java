package solver;

public class Gate {
    final int number;
    final int x;
    final int y;
    final int z;

    public Gate(int number, int x, int y, int z) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString(){
        return "" + number;
    }

}