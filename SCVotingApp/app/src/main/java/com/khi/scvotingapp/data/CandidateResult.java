package com.khi.scvotingapp.data;

public class CandidateResult {

    private String name;
    private String program;
    private String partylist;
    private boolean isSelected = false;
    private int positionID;
    private int totalVotes;

    public String getName() {
        return name;
    }

    public String getProgram() {
        return program;
    }

    public String getPartylist() {
        return partylist;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPositionID() {
        return positionID;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public CandidateResult(String name, String program, String partylist, int positionID, int totalVotes) {
        this.name = name;
        this.program = program;
        this.partylist = partylist;
        this.positionID = positionID;
        this.totalVotes = totalVotes;
    }

}
