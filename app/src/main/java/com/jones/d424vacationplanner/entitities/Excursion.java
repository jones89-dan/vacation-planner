package com.jones.d424vacationplanner.entitities;

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
    // details of the excursion.
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int vacationId; // Foreign key for vacations table
    public String title;
    public Date excursionDate;

    public Excursion(int vacationId, String title, Date excursionDate) {
        this.vacationId = vacationId;
        this.title = title;
        this.excursionDate = excursionDate;
    }
}
