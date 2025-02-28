package com.jones.d424vacationplanner.entitities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

// Entity for Excursions
@Entity(tableName = "excursions",
        // Foreign key details for the Vacation class
        foreignKeys = @ForeignKey(entity = Vacation.class,
                parentColumns = "id",
                childColumns = "vacationId",
                onDelete = ForeignKey.RESTRICT))

public class Excursion {
    //Part B - Encapsulation - Set object attributes to private and implement getter and setter methods
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int vacationId; // Foreign key for vacations table
    private String title;
    private Date excursionDate;

    @ColumnInfo(name = "date_created")
    private long dateCreated;

    public Date getExcursionDate() {
        return excursionDate;
    }

    public void setExcursionDate(Date excursionDate) {
        this.excursionDate = excursionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Excursion(int vacationId, String title, Date excursionDate) {
        this.vacationId = vacationId;
        this.title = title;
        this.excursionDate = excursionDate;
        this.dateCreated = System.currentTimeMillis();
    }

    public boolean matchesSearch(String keyword) {
        return title.toLowerCase().contains(keyword.toLowerCase());
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
