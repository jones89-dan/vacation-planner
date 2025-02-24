package com.jones.d424vacationplanner.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.entitities.Vacation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationRecyclerAdapter extends RecyclerView.Adapter<VacationRecyclerAdapter.VacationViewHolder> {

    private List<Vacation> vacationList;
    private Context context;


    public VacationRecyclerAdapter(List<Vacation> vacationList, Context context) {
        this.vacationList = vacationList;
        this.context = context; // Assign context
    }

    // View holder for vacation details
    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewPlace, textViewStartDate, textViewEndDate;
        Spinner spinnerNavigation;

        // Define the vacation view holder details and where values will be stored
        public VacationViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewStartDate = itemView.findViewById(R.id.textViewStartDate);
            textViewEndDate = itemView.findViewById(R.id.textViewEndDate);
            spinnerNavigation = itemView.findViewById(R.id.spinnerNavigation);

        }
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new VacationViewHolder(view);
    }

    // Bind the view holder with the vacation details formatted in text.
    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);

        String formattedStartDate = formatDate(vacation.startDate);
        String formattedEndDate = formatDate(vacation.endDate);

        holder.textViewTitle.setText("Title: " + vacationList.get(position).title);
        holder.textViewPlace.setText("Place : " + vacationList.get(position).place);
        holder.textViewStartDate.setText("Start: " + formattedStartDate);
        holder.textViewEndDate.setText("End: " + formattedEndDate);

        // Define options
        String[] pages = {"Select", "View/Edit", "Excursions"};

        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, pages);

        // Set the adapter to Spinner
        holder.spinnerNavigation.setAdapter(adapter);

        // Handle selection
        holder.spinnerNavigation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignore "Select Page"
                    navigateToPage(position,  vacation);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Get a count of the vacations
    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    // Date Format helper function
    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(date); // Format to only MM/dd/yyyy
    }

    private void navigateToPage(int pos, Vacation vacation) {
        Intent intent = null;
        switch (pos) {
            case 1: // View Details/Edit
                intent = new Intent(context, VacationDetail.class);
                intent.putExtra("id", vacation.id);
                break;
            case 2: // Excursions
                intent = new Intent(context, LIstVacationExcursions.class);
                intent.putExtra("id", vacation.id);
                break;
        }

        if (intent != null) {
            context.startActivity(intent);
        }
    }

}
