package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mynotes.adapters.ListItemAdapter;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;
import com.example.mynotes.utils.MenuHelper;
import com.example.mynotes.viewmodel.ViewAndEditViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ViewAndEditFragment extends Fragment implements View.OnClickListener {
private final String TAG = this.getClass().getSimpleName();
    private ViewAndEditViewModel mViewModel;
    MenuHelper menuHelper;
    Note note;
    EditText etTitle, etNoteColor, etNoteText;
    Button bColorPicker;
    ImageButton ibAddListItem, ibMenu, ibColorRed, ibColorGreen, ibColorBlue, ibColorYellow, ibColorPurple;;
    RecyclerView rvAddList;
    LinearLayout llColorsPicker;
    Button bSave;
    ListItemAdapter listItemAdapter;
    Activity activity;

    public static ViewAndEditFragment newInstance() {
        return new ViewAndEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_and_edit, container, false);
        activity = (Activity) view.getContext();
        try {
            assert getArguments() != null;
            note = getArguments().getParcelable("note");
        } catch (Exception e) { Log.d(TAG, Objects.requireNonNull(e.getMessage())); }

        try {
            setView(view);
        } catch (ExecutionException | InterruptedException e) { throw new RuntimeException(e); }

        listeners();

        return view;
    }

    private void setView(View view) throws ExecutionException, InterruptedException {
        mViewModel = new ViewModelProvider(this).get(ViewAndEditViewModel.class);
        formatAndSetColor();
        ibMenu = view.findViewById(R.id.IBMenu);
        etTitle = view.findViewById(R.id.ETEditTitle);
        llColorsPicker = view.findViewById(R.id.LLColorsPicker);
        ibColorRed = view.findViewById(R.id.IBColorRed);
        ibColorGreen = view.findViewById(R.id.IBColorGreen);
        ibColorBlue = view.findViewById(R.id.IBColorBlue);
        ibColorYellow = view.findViewById(R.id.IBColorYellow);
        ibColorPurple = view.findViewById(R.id.IBColorPurple);
        etNoteColor = view.findViewById(R.id.ETEditColor);
        bColorPicker = view.findViewById(R.id.TBEditRgbHex);
        etNoteText = view.findViewById(R.id.ETEditNoteText);
        rvAddList = view.findViewById(R.id.RVEditNoteList);
        ibAddListItem = view.findViewById(R.id.IBEditAddListItem);
        bSave = view.findViewById(R.id.BSaveEdit);

        etTitle.setText(note.getTitle(), TextView.BufferType.EDITABLE);
        etNoteColor.setText(mViewModel.getRgb());
        etNoteText.setText(note.getNoteText());

        mViewModel.setNote(note);
        mViewModel.setTitle(note.getTitle());
        mViewModel.setText(note.getNoteText());
        mViewModel.setNoteListByNoteId(note.getId());

        setRV();
    }



    private void formatAndSetColor() {
        Color c = Color.valueOf(note.getColor());
        String red = String.valueOf(Math.round(c.red() * 255));
        String green = String.valueOf(Math.round(c.green() * 255));
        String blue = String.valueOf(Math.round(c.blue() * 255));

        String rgb = red + ", " + green + ", " + blue;
        String hex = String.format("#%02x%02x%02x", Math.round(c.red() * 255), Math.round(c.green() * 255), Math.round(c.blue() * 255));

        mViewModel.setRgb(rgb);
        mViewModel.setHex(hex);
    }

    private void listeners() {
        // 5 listeners for choosing color
        ibColorRed.setOnClickListener(this);
        ibColorGreen.setOnClickListener(this);
        ibColorBlue.setOnClickListener(this);
        ibColorYellow.setOnClickListener(this);
        ibColorPurple.setOnClickListener(this);

        ibMenu.setOnClickListener(view -> {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            MenuFragment menuFragment = new MenuFragment();
            appCompatActivity.getSupportFragmentManager()
                    .beginTransaction().replace(R.id.FLViewAndEditContainer, menuFragment)
                    .addToBackStack(null).commit();
        });
        // change hint from color edit text
        bColorPicker.setOnClickListener(view -> {
            switch (mViewModel.getColorBtnState()){
                case 1:
                    mViewModel.setColorBtnState(2);
                    bColorPicker.setText(R.string.colors);
                    etNoteColor.setHint(R.string.hex_code);
                    etNoteColor.setText("");
                    break;
                case 2:
                    mViewModel.setColorBtnState(0);
                    bColorPicker.setText(R.string.rgb);
                    llColorsPicker.setVisibility(View.VISIBLE);
                    etNoteColor.setVisibility(View.GONE);
                    break;
                default:
                    mViewModel.setColorBtnState(1);
                    bColorPicker.setText(R.string.hex);
                    llColorsPicker.setVisibility(View.GONE);
                    etNoteColor.setVisibility(View.VISIBLE);
                    etNoteColor.setHint(R.string.rgb_code);
                    etNoteColor.setText("");
                    break;
            }
        });

        ibAddListItem.setOnClickListener(view -> {
            // add item to recycle view
            NoteList noteListItem = new NoteList(0, "");
            ArrayList<NoteList> noteList = listItemAdapter.getNoteList();
            noteList.add(noteListItem);
            mViewModel.setNoteLists(noteList);

            listItemAdapter = new ListItemAdapter(mViewModel.getNoteLists());

            rvAddList.setAdapter(listItemAdapter);
            listItemAdapter.notifyItemInserted(mViewModel.getNoteLists().size() - 1);
        });

        bSave.setOnClickListener(view -> {
            // set title to view model
            if (checkAddTitle(etTitle.getText().toString())) {
                // set color state list to view model
                if (checkAddColor(etNoteColor.getText().toString())) {
                    // set text to view model
                    checkAddText(etNoteText.getText().toString());
                    // set note list to view model
                    checkAddNoteList(listItemAdapter.getNoteList());
                    // save the note
                    try {
                        saveNote();
                    } catch (ExecutionException | InterruptedException e) { throw new RuntimeException(e); }
                }
            }
        });
    }

    private void setRV() {
        listItemAdapter = new ListItemAdapter(mViewModel.getNoteLists());
        rvAddList.setAdapter(listItemAdapter);
        rvAddList.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private boolean checkAddTitle(String title) {
        try {
            if (title == null || title.equals("")) {
                etTitle.setError("Title required");
                return false;
            } else {
                ArrayList<Note> allNotes = mViewModel.getAllNotes();
                for (Note n : allNotes) {
                    if (n.getTitle().equals(title) && n.getId() != note.getId()) {
                        etTitle.setError("Title already exists");
                      return false;
                    }
                }
                mViewModel.setTitle(title);
                return true;
            }
        } catch (Exception e) {
            etTitle.setError("Title error");
            Log.d(TAG, "Title error: " + e.getMessage());
            return false;
        }

    }

    private boolean checkAddColor(String color) {
        if (!mViewModel.getRawColorPicked().equals("")) {
            // raw color
            try {
                mViewModel.setNoteColor(Color.parseColor(mViewModel.getRawColorPicked()));
                return true;
            } catch (Exception e) {
                etNoteColor.setError("Choose a color");
                Log.d(TAG, "Color conversion problem (hex) " + e.getMessage());
                return false;
            }
        }
        else if (color == null || color.equals("")) {
            mViewModel.setNoteColor(Color.parseColor("#ffef00"));
            return true;
        } else {
            switch (mViewModel.getColorBtnState()) {
                case 1:
                    // rgb
                    try {
                        String[] colorRGB = color.split(",");
                        String red = colorRGB[0].trim();
                        String green = colorRGB[1].trim();
                        String blue = colorRGB[2].trim();
                        int colorInt = Color.rgb(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
                        mViewModel.setNoteColor(colorInt);
                        return true;
                    } catch (Exception e) {
                        etNoteColor.setError("Wrong color value");
                        Log.d(TAG, "Color conversion problem (rgb) " + e.getMessage());
                        return false;
                    }
                case 2:
                    // hex
                    try {
                        mViewModel.setNoteColor(Color.parseColor(color));
                        return true;
                    } catch (Exception e) {
                        etNoteColor.setError("Wrong color value");
                        Log.d(TAG, "Color conversion problem (hex) " + e.getMessage());
                        return false;
                    }
                default:
                    Log.d(TAG, "Color picker problem");
                    return false;
            }
        }
    }

    private void checkAddText(String text) {
        try {
            if (text != null) {
                mViewModel.setText(text);
            } else {
                mViewModel.setText("");
            }
        } catch (Exception e) {
            etNoteText.setError("Text error");
            Log.d(TAG, "Text error: " + e.getMessage());
        }

    }

    private void checkAddNoteList(ArrayList<NoteList> noteList) {
        if (noteList != null) {
            mViewModel.setNoteLists(noteList);
        } else {
            mViewModel.setNoteLists(new ArrayList<>());
            Log.d(TAG, "Cannot set note list");
        }
    }

    private void saveNote() throws ExecutionException, InterruptedException {
        LocalDateTime localDateTime = LocalDateTime.now();

        note.setTitle(mViewModel.getTitle());
        note.setColor(mViewModel.getNoteColor());
        note.setDateTime(localDateTime.toString());
        note.setNoteText(mViewModel.getText());
        if (mViewModel.getNoteLists() == null) {
            mViewModel.saveNote(note);
        } else {
            mViewModel.saveNote(note);
            mViewModel.saveNoteList(mViewModel.getNoteLists());
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("update result", true);
        getParentFragmentManager().setFragmentResult("101", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View view) {
        ibColorRed.setImageResource(R.color.transparent);
        ibColorGreen.setImageResource(R.color.transparent);
        ibColorBlue.setImageResource(R.color.transparent);
        ibColorYellow.setImageResource(R.color.transparent);
        ibColorPurple.setImageResource(R.color.transparent);

        if (view.getId() == R.id.IBColorRed) {
            mViewModel.setRawColorPicked("#FF0000");
            ibColorRed.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorGreen) {
            mViewModel.setRawColorPicked("#00FF00");
            ibColorGreen.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorBlue) {
            mViewModel.setRawColorPicked("#0000FF");
            ibColorBlue.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorYellow) {
            mViewModel.setRawColorPicked("#FFFF00");
            ibColorYellow.setImageResource(R.drawable.check_vector);
        }
        else {
            mViewModel.setRawColorPicked("#FF00FF");
            ibColorPurple.setImageResource(R.drawable.check_vector);
        }
    }
}