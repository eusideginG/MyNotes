package com.example.mynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.adapters.NoteRVAdapter;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;
import com.example.mynotes.utils.MenuHelper;
import com.example.mynotes.viewmodel.NoteViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    MenuHelper menuHelper;
    NoteViewModel noteViewModel;
    RecyclerView recyclerView;
    ImageButton ibAdd;
    NoteRVAdapter noteRVAdapter;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 100) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        if (intent.getBooleanExtra("result", false)) {
                            setRV();
                        }
                    }
                }
                if (result.getResultCode() == 133) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        if (intent.getBooleanExtra("updateRV", false)) {
                            try {
                                noteRVAdapter.updateRV(noteViewModel.getAllNotes());
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                if (result.getResultCode() == RESULT_OK) {
                    OutputStream os;
                    try {
                        ArrayList<Note> allNotes = noteViewModel.getAllNotes();
                        ArrayList<NoteList> allNoteLists = noteViewModel.getAllNoteLists();
                        assert result.getData() != null;
                        os = getContentResolver().openOutputStream(Objects.requireNonNull(result.getData().getData()));
                        assert os != null;
                        for (int i = 0; i < allNotes.size(); i++) {
                            Note note = allNotes.get(i);
                            StringBuilder noteLists = new StringBuilder();
                            for (int j = 0; j < allNoteLists.size(); j++) {
                                if (note.getId() == allNoteLists.get(j).getNoteId()) {
                                    NoteList noteList = allNoteLists.get(j);

                                    noteLists
                                            .append("\tNoteType: ").append(
                                                    noteList.getListType() == 1 ? "⚫" :
                                                            noteList.getListType() == 2 ? "⬛" :
                                                                    noteList.getListType() == 3 ? "✔" : " ").append(", ")
                                            .append("NoteText: ").append(noteList.getListText()).append("\n");
                                }
                            }
                            String lineOfNote =
                                    "Note " + (i + 1) + ":\n" +
                                            "Title: " + note.getTitle() + ", " +
                                            "Color: " + note.getColor() + ", " +
                                            "Text: " + note.getNoteText() + ", " +
                                            "DateTime: " + note.getDateTime() + "\n" +
                                            "List: " + noteLists + "\n";
                            os.write(lineOfNote.getBytes());
                        }
                        os.close();
                        Log.d(TAG, "Text file downloaded successfully");
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        throw new RuntimeException(e);
                    }
                }

                Log.d(TAG, "result: " + result);
                Log.d(TAG, "result code: " + result.getResultCode());
            });

    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    OutputStream os;

                    try {
                        ArrayList<Note> allNotes = noteViewModel.getAllNotes();
                        ArrayList<NoteList> allNoteLists = noteViewModel.getAllNoteLists();
                        assert result.getData() != null;
                        os = getContentResolver().openOutputStream(Objects.requireNonNull(result.getData().getData()));
                        assert os != null;

                        for (int i = 0; i < allNotes.size(); i++) {
                            Note note = allNotes.get(i);
                            StringBuilder noteListRows = new StringBuilder();
                            for (int j = 0; j < allNoteLists.size(); j++) {
                                if (note.getId() == allNoteLists.get(j).getNoteId()) {
                                    NoteList noteList = allNoteLists.get(j);
                                    noteListRows.append(noteList.getListType())
                                            .append(",")
                                            .append(noteList.getListText());
                                    if (j != allNoteLists.size() - 1) noteListRows.append(",");
                                }
                            }
                            String noteEntry =
                                    note.getTitle() + "," +
                                            note.getColor() + "," +
                                            note.getDateTime() + "," +
                                            note.getNoteText() + "\n" +
                                            noteListRows + "\n";

                            os.write(noteEntry.getBytes());
                        }

                        os.close();
                        Log.d(TAG, "Backup successful");
                        toastSuccess("Backup successful");
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        throw new RuntimeException(e);
                    }
                }

                Log.d(TAG, "result: " + result);
                Log.d(TAG, "result code: " + result.getResultCode());
            });

    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        assert result.getData() != null;
                        InputStream inputStream = getContentResolver().openInputStream(Objects.requireNonNull(result.getData().getData()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String csvRow;
                        String noteTitle = null;
                        boolean noteAdded = false;
                        int i = 0;
                        noteViewModel.deleteTableEntries();
                        while ((csvRow = reader.readLine()) != null) {
                            String[] columns = csvRow.split(",");
                            if (i % 2 == 0) {
                                Note insertNote = new Note(columns[0], Integer.parseInt(columns[1]), columns[2], columns[3]);
                                noteTitle = columns[0];
                                noteAdded = noteViewModel.saveNote(insertNote);
                            } else {
                                String column = columns[0];
                                if (!column.isEmpty() && noteAdded) {
                                    int typeIndex = 0, textIndex = 1;
                                    ArrayList<NoteList> insertNoteList = new ArrayList<>();
                                    do {
                                        insertNoteList.add(new NoteList(Integer.parseInt(columns[typeIndex]), columns[textIndex]));
                                        typeIndex += 2;
                                        textIndex += 2;
                                    } while (textIndex <= columns.length);
                                    noteViewModel.saveNoteList(insertNoteList, noteTitle);
                                }
                            }
                            i++;
                        }
                        noteRVAdapter.updateRV(noteViewModel.getAllNotes());
                        Log.d(TAG, "Notes restored");
                        toastSuccess("Notes restored");
                    } catch (Exception e) {
                        Log.d(TAG + " error: ", String.valueOf(Objects.requireNonNull(e)));
                    }
                }
            });

    private void toastSuccess(String message) {
        Toast toastSuccess = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        TextView toastText = Objects.requireNonNull(toastSuccess.getView()).findViewById(android.R.id.message);
        toastText.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primaryText)));
        toastSuccess.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        listeners();
    }

    private void initialize() {
        // assign layout views
        menuHelper = new MenuHelper(this);
        menuHelper.setLayoutWrapper(R.id.RLMainActivityContainer);
        recyclerView = findViewById(R.id.RVMainLayout);
        ibAdd = findViewById(R.id.FABAdd);

        // assign objects
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // handle theme
        handleTheme();

        fragmentResultListener("101", this); // edit note
        fragmentResultListener("40", this); // download text
        fragmentResultListener("45", this); // backup notes in csv file
        fragmentResultListener("46", this); // restore notes from csv file
        fragmentResultListener("50", this); // open "About" dialog
        fragmentResultListener("60", this); // change theme

        setRV();
    }

    private void handleTheme() {
        sharedPreferences = getSharedPreferences("com.myNotes.nightMode", Context.MODE_PRIVATE);

        nightMode = sharedPreferences.getBoolean("nightMode", true);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void fragmentResultListener(String requestKey, LifecycleOwner lifecycleOwner) {
        getSupportFragmentManager().setFragmentResultListener(requestKey, lifecycleOwner, (requestK, result) -> {
            switch (requestK) {
                case "101":
                    // get fragment result and update the recycle view from main activity
                    boolean fragmentResult = result.getBoolean("update result", false);

                    if (fragmentResult) {
                        setRV();
                    }
                    break;
                case "40":
                    boolean startBackup = result.getBoolean("downloadText", false);
                    if (startBackup) {
                        Intent intent = result.getParcelable("downloadTextIntent");

                        // launch activity result launcher
                        activityResultLauncher.launch(intent);
                    }
                    break;
                case "45":
                    if (result.getBoolean("startBackup", false)) {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/csv");
                        intent.putExtra(Intent.EXTRA_TITLE, "Notes.csv");

                        activityResultLauncher2.launch(intent);
                    }
                    break;
                case "46":
                    if (result.getBoolean("startRestoring", false)) {
                        Intent intent46 = new Intent(Intent.ACTION_GET_CONTENT);
                        intent46.addCategory(Intent.CATEGORY_OPENABLE);
                        intent46.setType("text/csv");

                        activityResultLauncher3.launch(intent46);
                    }
                    break;
                case "50":
                    if (result.getBoolean("openAbout", false)) {
                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.about_pop_up);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners);
                        dialog.show();
                    }
                    break;
                case "60":
                    if (result.getBoolean("changeTheme", false)) {
                        if (nightMode) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("nightMode", false);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            editor = sharedPreferences.edit();
                            editor.putBoolean("nightMode", true);
                        }
                        editor.apply();
                    }
                    break;
                default: Log.d(TAG + " request code:", requestK);
            }
        });
    }

    private void setRV() {
        try {
            noteRVAdapter = new NoteRVAdapter(noteViewModel.getAllNotes(), this);
            recyclerView.setAdapter(noteRVAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG, "Cannot get the notes from database.");
            throw new RuntimeException(e);
        }
    }

    private void listeners() {
        menuHelper.menuBtnPressed();
        ibAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

}