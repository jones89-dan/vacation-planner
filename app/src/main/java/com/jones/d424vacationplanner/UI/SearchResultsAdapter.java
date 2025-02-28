package com.jones.d424vacationplanner.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultsAdapter<T> extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    // Generic list to hold either vacations or excursions
    private List<T> searchResults;

    public SearchResultsAdapter(List<T> searchResults) {
        this.searchResults = searchResults;
    }

    public List<T> getSearchResults() {
        return searchResults;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = searchResults.get(position);

        // Format for Vacation/Excursion results
        if (item instanceof Vacation) {
            Vacation vacation = (Vacation) item;
            holder.textViewTitle.setText("Title: " + vacation.getTitle());
            holder.textViewDate.setText("Date Added: " + formatDate(vacation.getDateCreated()));
        } else if (item instanceof Excursion) {
            Excursion excursion = (Excursion) item;
            holder.textViewTitle.setText("Title: " + excursion.getTitle());
            holder.textViewDate.setText("Date Added: " + formatDate(excursion.getDateCreated()));
        }
    }


    // Get the number of items in search results
    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateResults(List<T> newResults) {
        this.searchResults = newResults;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }

    // Date format helper function
    private String formatDate(Long inputDate) {
        Date createdDate = new Date(inputDate);

        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(createdDate); // Format to only MM/dd/yyyy
    }
}

