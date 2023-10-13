package com.example.mynotes.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.R;
import com.example.mynotes.ViewAndEditFragment;
import com.example.mynotes.model.repo.NoteRepo;
import com.example.mynotes.model.roomdb.Note;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NoteRVAdapter extends RecyclerView.Adapter<NoteRVAdapter.NoteViewHolder> {
    ArrayList<Note> allNotes;
    Activity activity;

    public NoteRVAdapter(ArrayList<Note> allNotes, Activity activity) {
        this.allNotes = allNotes;
        this.activity = activity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_main_display_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = allNotes.get(position);

        holder.VColorId.setBackgroundTintList(ColorStateList.valueOf(note.getColor()));
        holder.TVTitle.setText(note.getTitle());
        holder.TVDate.setText(ldtToDate(note.getDateTime()));
        holder.TVTime.setText(ldtToTime(note.getDateTime()));

        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            ViewAndEditFragment fragment = new ViewAndEditFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("note", note);
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager()
                    .beginTransaction().replace(R.id.RLMainActivityContainer, fragment).addToBackStack(null).commit();

        });

        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog);
            builder.setTitle(R.string.delete_confirmation)
                    .setMessage("Do you want to delete " + note.getTitle() + "  note ?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        NoteRepo noteRepo = new NoteRepo(activity.getApplication());
                        try {
                            noteRepo.delete(allNotes.get(position));
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        allNotes.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(0, allNotes.size());
                        Toast.makeText(activity.getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    })
                    .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.cancel()));

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_soft);
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.primaryText));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.primaryText));

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return allNotes.size();
    }

    private String ldtToDate(String ldt) {
        LocalDateTime localDateTime = LocalDateTime.parse(ldt);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDateTime.format(dateTimeFormatter);
    }

    private String ldtToTime(String ldt) {
        LocalDateTime localDateTime = LocalDateTime.parse(ldt);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE kk:mm");
        return localDateTime.format(dateTimeFormatter);
    }

    public void updateRV(ArrayList<Note> allNotes){
        this.allNotes = allNotes;
        notifyItemRangeChanged(0, allNotes.size());
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        View VColorId;
        TextView TVTitle, TVDate, TVTime;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            VColorId = itemView.findViewById(R.id.VItemColorId);
            TVTitle = itemView.findViewById(R.id.TVItemTitle);
            TVDate = itemView.findViewById(R.id.TVItemDate);
            TVTime = itemView.findViewById(R.id.TVItemTime);
        }
    }
}
