package com.jones.d424vacationplanner.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.entitities.Excursion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Class for the Excursion Recycler
public class ExcursionRecyclerAdapter extends RecyclerView.Adapter<ExcursionRecyclerAdapter.ExcursionViewHolder>{

    private List<Excursion> excursionList;
    private Context context;
    private int vacationId;

    // Construct the Excursion Recycler Adapter
    public ExcursionRecyclerAdapter(List<Excursion> excursionList, Context context, int vacationId) {

        this.excursionList = excursionList;
        this.context = context;
        this.vacationId = vacationId;
    }

    // Define the excursion view holder details and where values will be stored
    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDate, buttonEdit;

        public ExcursionViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewExcursionTitle);
            textViewDate = itemView.findViewById(R.id.textViewExcursionDate);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_excursion_item, parent, false);
        return new ExcursionViewHolder(view);
    }

    // Bind the view holder with the excursion details formatted in text.
    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {

        Excursion excursion = excursionList.get(position);

        // Format Excursion Date
        String formatedExcursionDate = formatDate(excursion.getExcursionDate());

        holder.textViewTitle.setText("Title: " + excursion.getTitle());
        holder.textViewDate.setText("Date: " + formatedExcursionDate);

        // Set Click Listener for Edit Button
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExcursionDetail.class);
            intent.putExtra("id", excursion.getId()); // Pass Excursion ID
            intent.putExtra("vacationId", vacationId); // Pass VacationID
            context.startActivity(intent);
        });

    }

    // Get a count of the excursions
    @Override
    public int getItemCount() {
        return excursionList.size();
    }

    // Date Format helper function
    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(date); // Format to only MM/dd/yyyy
    }

}
