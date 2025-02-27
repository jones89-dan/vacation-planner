package com.jones.d424vacationplanner.entitities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

// Vacation entity
@Entity(tableName = "vacations")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String place;
    public Date startDate;
    public Date endDate;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

}
