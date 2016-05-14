package solver;

public class Net {
    final Gate gate1;
    final Gate gate2;


    public Net(Gate gate1, Gate gate2) {
        this.gate1 = gate1;

        this.gate2 = gate2;
    }
    public String toString(){
        return "Net: " + gate1 + " " + gate2;

    }

}
