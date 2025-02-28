package com.jones.d424vacationplanner.entitities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

// Vacation entity
@Entity(tableName = "vacations")
public class Vacation {
    //Part B - Encapsulation - Set object attributes to private and implement getter and setter methods
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String place;
    private Date startDate;
    private Date endDate;

    @ColumnInfo(name = "date_created")
    private long dateCreated;

    public Vacation(String title, String place, Date startDate, Date endDate) {
        this.title = title;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateCreated = System.currentTimeMillis();
    }

    public boolean matchesSearch(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase()) ||
                place.toLowerCase().contains(keyword.toLowerCase());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

}
