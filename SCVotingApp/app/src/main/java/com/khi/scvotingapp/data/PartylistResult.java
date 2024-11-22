package com.khi.scvotingapp.data;

public class PartylistResult {

    private String partylistName;
    private int totalVotes;

    public String getPartylistName() {
        return partylistName;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public PartylistResult(String partylistName, int totalVotes) {
        this.partylistName = partylistName;
        this.totalVotes = totalVotes;
    }

}
