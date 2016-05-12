package solver;

public class PoleCoordinates {
    int lineNum;
    final int x1;
    final int y1;
     int z1;
    final int x2;
    final int y2;
     int z2;

    public PoleCoordinates(int lineNum, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.lineNum = lineNum;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public String toString(){
        return "PC: " + x1 + x2;
    }
}