package com.company;

/**
 * Created by marty_000 on 22-2-2016.
 */
public class Net {
    int gate1;
    int gate2;

    public Net(int gate1, int gate2) {
        this.gate1 = gate1;
        this.gate2 = gate2;
    }
    public int netFirst(){
        return gate1;
    }
    public int netSecond(){
        return gate2;
    }


}
