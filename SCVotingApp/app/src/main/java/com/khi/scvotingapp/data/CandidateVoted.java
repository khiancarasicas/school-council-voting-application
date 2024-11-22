package com.khi.scvotingapp.data;

public class CandidateVoted {

    private final String name;
    private final int positionID;

    public String getName() {
        return name;
    }

    public int getPositionID() {
        return positionID;
    }

    public CandidateVoted(String name, int positionID) {
        this.name = name;
        this.positionID = positionID;
    }

}
