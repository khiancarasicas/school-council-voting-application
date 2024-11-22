package com.khi.scvotingapp.data;

public class Position {

    private int id;
    private String name;
    private int maxVote;

    public Position(int id, String name, int maxVote) {
        this.id = id;
        this.name = name;
        this.maxVote = maxVote;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxVote() {
        return maxVote;
    }

}
