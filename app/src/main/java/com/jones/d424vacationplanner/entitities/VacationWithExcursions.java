package com.jones.d424vacationplanner.entitities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

// Vacation with Excursion class
public class VacationWithExcursions {
    @Embedded
    public Vacation vacation;

    @Relation(
            parentColumn = "id",
            entityColumn = "vacationId"
    )
    public List<Excursion> excursions;
}
