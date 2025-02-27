package com.jones.d424vacationplanner.search;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Part B - Inheritance - Parent class for VacationSearch and ExcursionSearch
// Part B - Polymorphism - Generic abstract class with a generic method, performSearch
public abstract class Search<T>{
    protected List<T> items;
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Constructor
    public Search() {
        // Empty constructor for subclasses that fetch from the database
    }
    public interface SearchCallback<T> {
        void onResults(List<T> results);
    }

    public abstract void performSearch(String keyword, SearchCallback<T> callback);


}
