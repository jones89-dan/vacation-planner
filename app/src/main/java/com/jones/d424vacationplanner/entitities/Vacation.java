package com.jones.d424vacationplanner.entitities;

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

    public Vacation(String title, String place, Date startDate, Date endDate) {
        this.title = title;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
