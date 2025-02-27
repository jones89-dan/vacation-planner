package com.jones.d424vacationplanner.search;

import com.jones.d424vacationplanner.dao.ExcursionDAO;
import com.jones.d424vacationplanner.dao.VacationDAO;
import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;

import java.util.ArrayList;
import java.util.List;

// Part B - Inheritance - Child class that inherits from Search class
public class ExcursionSearch extends Search<Excursion> {
    private ExcursionDAO excursionDao;

    // Use to ExcursionDAO to interact with the database
    public ExcursionSearch(ExcursionDAO excursionDao) {
        this.excursionDao = excursionDao;
    }

    // Override from the parent class the parent class
    @Override
    public void performSearch(String keyword, SearchCallback<Excursion> callback) {
        executorService.execute(() -> {
            List<Excursion> results = excursionDao.searchExcursions("%" + keyword + "%");
            callback.onResults(results);
        });
    }
}
