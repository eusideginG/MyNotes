package com.example.mynotes.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.R;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {
    private ArrayList<NoteList> noteList;

    public ListItemAdapter(ArrayList<NoteList> noteList) { this.noteList = noteList; }


    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_add_list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        NoteList listItem = noteList.get(position);

        holder.listType.setImageResource(
                listItem.getListType() == 1 ? R.drawable.circle_vector :
                listItem.getListType() == 2 ? R.drawable.square_vector :
                listItem.getListType() == 3 ? R.drawable.check_vector : 0 );
        holder.listType.setOnClickListener(view -> {
            int listType = listItem.getListType();
            switch (listType) {
                case 0:
                    listType = 1;
                    break;
                case 1:
                    listType = 2;
                    break;
                case 2:
                    listType = 3;
                    break;
                case 3:
                    listType = 0;
                    break;
            }
            listItem.setListType(listType);
            this.notifyItemChanged(position);
        });

        holder.listText.setText(listItem.getListText());

        holder.listText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                noteList.get(holder.getAdapterPosition()).setListText(holder.listText.getText().toString());
            }
        });

        holder.delListItem.setOnClickListener(view -> {
            this.noteList.remove(position);
            this.notifyItemRemoved(position);
            notifyItemRangeChanged(position, noteList.size());
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public ArrayList<NoteList> getNoteList() { return this.noteList; }

    static class ListItemViewHolder extends RecyclerView.ViewHolder {
        ImageButton listType, delListItem;
        EditText listText;
        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            listType = itemView.findViewById(R.id.IBListType);
            listText = itemView.findViewById(R.id.ETAddListItemText);
            delListItem = itemView.findViewById(R.id.IBDeleteAddListItem);
        }
    }
}
