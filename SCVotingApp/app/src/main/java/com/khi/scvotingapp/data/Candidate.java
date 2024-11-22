package com.khi.scvotingapp.data;

public class Candidate {

    private int id;
    private String name;
    private String program;
    private String partylist;
    private boolean isSelected = false;
    private int positionID;

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

    public int getId() {
        return id;
    }

    public int getPositionID() {
        return positionID;
    }

    public Candidate(int id, String name, String program, String partylist, int positionID) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.partylist = partylist;
        this.positionID = positionID;
    }

}
