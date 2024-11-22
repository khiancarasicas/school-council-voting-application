package com.khi.scvotingapp.data;

import java.util.Date;

public class Election {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Date currentDate;  // Added current date from SQL

    public Election(String title, String description, Date startDate, Date endDate, Date currentDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentDate = currentDate;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }
}
