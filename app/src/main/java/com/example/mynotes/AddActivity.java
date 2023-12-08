package com.example.mynotes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.adapters.ListItemAdapter;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;
import com.example.mynotes.utils.MenuHelper;
import com.example.mynotes.viewmodel.AddViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    MenuHelper menuHelper;
    AddViewModel addViewModel;
    EditText etTitle, etNoteColor, etNoteText;
    Button bColorPicker, bSave;
    ImageButton ibAddText, ibAddList, ibColorRed, ibColorGreen, ibColorBlue, ibColorYellow, ibColorPurple;
    RecyclerView rvAddList;
    FloatingActionButton ibAddListItem;
    RelativeLayout rlAddList;
    LinearLayout llColorsPicker;
    ListItemAdapter listItemAdapter;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == 100) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        if (intent.getBooleanExtra("result", false)) {
                            setRV();
                        }
                    }
                }
                if (result.getResultCode() == RESULT_OK) {
                    OutputStream os;
                    try {
                        ArrayList<Note> allNotes = addViewModel.getAllNotes();
                        ArrayList<NoteList> allNoteLists = addViewModel.getAllNoteLists();
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
                        ArrayList<Note> allNotes = addViewModel.getAllNotes();
                        ArrayList<NoteList> allNoteLists = addViewModel.getAllNoteLists();
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
                        addViewModel.deleteTableEntries();
                        while ((csvRow = reader.readLine()) != null) {
                            String[] columns = csvRow.split(",");
                            if (i % 2 == 0) {
                                Note insertNote = new Note(columns[0], Integer.parseInt(columns[1]), columns[2], columns[3]);
                                noteTitle = columns[0];
                                noteAdded = addViewModel.saveNote(insertNote);
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
                                    addViewModel.saveNoteList(insertNoteList, noteTitle);
                                }
                            }
                            i++;
                        }

                        Intent intent = new Intent();
                        intent.putExtra("updateRV", true);
                        setResult(133, intent);

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
        setContentView(R.layout.activity_add);

        initialize();

        listeners();
    }

    private void initialize() {
        menuHelper = new MenuHelper(this);
        menuHelper.setLayoutWrapper(R.id.RLAddNoteContainer);
        etTitle = findViewById(R.id.ETAddTitle);
        llColorsPicker = findViewById(R.id.LLColorsPicker);
        ibColorRed = findViewById(R.id.IBColorRed);
        ibColorGreen = findViewById(R.id.IBColorGreen);
        ibColorBlue = findViewById(R.id.IBColorBlue);
        ibColorYellow = findViewById(R.id.IBColorYellow);
        ibColorPurple = findViewById(R.id.IBColorPurple);
        etNoteColor = findViewById(R.id.ETAddColor);
        bColorPicker = findViewById(R.id.TBRgbHex);
        ibAddText = findViewById(R.id.IBAddText);
        ibAddList = findViewById(R.id.IBAddList);
        etNoteText = findViewById(R.id.ETNoteBody);
        rlAddList = findViewById(R.id.RLAddNoteListWrapper);
        rvAddList = findViewById(R.id.RVAddListItem);
        ibAddListItem = findViewById(R.id.FABAddListItem);
        bSave = findViewById(R.id.BSave);

        addViewModel = new ViewModelProvider(this).get(AddViewModel.class);

        fragmentResultListener("40", this); // download text
        fragmentResultListener("45", this); // backup notes in csv file
        fragmentResultListener("46", this); // restore notes from csv file
        fragmentResultListener("50", this); // open "About" dialog
        fragmentResultListener("60", this); // change theme

        // handle theme
        handleTheme();

        ibAddText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryVer)));

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

    private void listeners() {
        menuHelper.menuBtnPressed();

        // 5 listeners for choosing color
        ibColorRed.setOnClickListener(this);
        ibColorGreen.setOnClickListener(this);
        ibColorBlue.setOnClickListener(this);
        ibColorYellow.setOnClickListener(this);
        ibColorPurple.setOnClickListener(this);

        // change hint from color edit text
        bColorPicker.setOnClickListener(view -> {
            switch (addViewModel.getColorBtnState()){
                case 1:
                    addViewModel.setColorBtnState(2);
                    bColorPicker.setText(R.string.colors);
                    etNoteColor.setHint(R.string.hex_code);
                    etNoteColor.setText("");
                    break;
                case 2:
                    addViewModel.setColorBtnState(0);
                    bColorPicker.setText(R.string.rgb);
                    llColorsPicker.setVisibility(View.VISIBLE);
                    etNoteColor.setVisibility(View.GONE);
                    break;
                default:
                    addViewModel.setColorBtnState(1);
                    bColorPicker.setText(R.string.hex);
                    llColorsPicker.setVisibility(View.GONE);
                    etNoteColor.setVisibility(View.VISIBLE);
                    etNoteColor.setHint(R.string.rgb_code);
                    etNoteColor.setText("");
                    break;
            }
        });
        // image button text card button on click
        ibAddText.setOnClickListener(view -> {
            etNoteText.setVisibility(View.VISIBLE);
            rlAddList.setVisibility(View.GONE);

            ibAddText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryVer)));
            ibAddText.setImageTintList(ColorStateList.valueOf(getColor(R.color.primary)));

            ibAddList.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryItemBG)));
            ibAddList.setImageTintList(ColorStateList.valueOf(getColor(R.color.primaryBG)));
        });
        // image button list card button on click
        ibAddList.setOnClickListener(view -> {
            etNoteText.setVisibility(View.GONE);
            rlAddList.setVisibility(View.VISIBLE);

            ibAddList.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryVer)));
            ibAddList.setImageTintList(ColorStateList.valueOf(getColor(R.color.primary)));

            ibAddText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryItemBG)));
            ibAddText.setImageTintList(ColorStateList.valueOf(getColor(R.color.primaryBG)));
        });
        // add list item
        ibAddListItem.setOnClickListener(view -> {
            addViewModel.setListNumber(addViewModel.getListNumber() + 1);
            // add item to recycle view
            NoteList noteListItem = new NoteList(0, "");
            ArrayList<NoteList> noteList = listItemAdapter.getNoteList();
            noteList.add(noteListItem);
            addViewModel.setNewNoteList(noteList);

            listItemAdapter = new ListItemAdapter(addViewModel.getNewNoteList());

            rvAddList.setAdapter(listItemAdapter);
            listItemAdapter.notifyItemInserted(addViewModel.getNewNoteList().size() - 1);
        });
        // save note
        bSave.setOnClickListener(view -> {
            try {
                saveNote();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void fragmentResultListener(String requestKey, LifecycleOwner lifecycleOwner) {
        getSupportFragmentManager().setFragmentResultListener(requestKey, lifecycleOwner, (requestK, result) -> {
            switch (requestK) {
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
        listItemAdapter = new ListItemAdapter(addViewModel.getNewNoteList());
        rvAddList.setAdapter(listItemAdapter);
        rvAddList.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean checkAddTitle(String title) {
        try {
            if (title == null || title.equals("")) {
                etTitle.setError("Title required");
                return false;
            } else {
                ArrayList<Note> allNotes = addViewModel.getAllNotes();
                Optional<Note> nameFound = allNotes.stream().filter(note -> note != null && note.getTitle().equals(title)).findAny();
                if (nameFound.isPresent()) {
                    etTitle.setError("Title already exists");
                    return false;
                } else {
                    addViewModel.setTitle(title);
                    return true;
                }
            }
        } catch (Exception e) {
            etTitle.setError("Title error");
            Log.d(TAG, "Title error: " + e.getMessage());
            return false;
        }

    }
    private boolean checkAddColor(String color) {
        if (!addViewModel.getRawColorPicked().equals("")) {
            // raw color
            try {
                addViewModel.setNoteColor(Color.parseColor(addViewModel.getRawColorPicked()));
                return true;
            } catch (Exception e) {
                etNoteColor.setError("Choose a color");
                Log.d(TAG, "Color conversion problem (hex) " + e.getMessage());
                return false;
            }
        }
        else if (color == null || color.equals("")) {
            addViewModel.setNoteColor(Color.parseColor("#ffef00"));
            return true;
        } else {
            switch (addViewModel.getColorBtnState()) {
                case 1:
                    // rgb
                    try {
                        String[] colorRGB = color.split(",");
                        String red = colorRGB[0].trim();
                        String green = colorRGB[1].trim();
                        String blue = colorRGB[2].trim();
                        int colorInt = Color.rgb(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
                        addViewModel.setNoteColor(colorInt);
                        return true;
                    } catch (Exception e) {
                        etNoteColor.setError("Wrong color value");
                        Log.d(TAG, "Color conversion problem (rgb) " + e.getMessage());
                        return false;
                    }
                case 2:
                    // hex
                    try {
                        addViewModel.setNoteColor(Color.parseColor(color));
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
                addViewModel.setText(text);
            } else {
                addViewModel.setText("");
            }
        } catch (Exception e) {
            etNoteText.setError("Text error");
            Log.d(TAG, "Text error: " + e.getMessage());
        }

    }
    private void checkAddNoteList(ArrayList<NoteList> noteList) {
        if (noteList != null) {
            addViewModel.setNewNoteList(noteList);
        } else {
            addViewModel.setNewNoteList(new ArrayList<>());
            Log.d(TAG, "Cannot set note list");
        }
    }
    private void saveNote() throws ExecutionException, InterruptedException {
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
                    LocalDateTime localDateTime = LocalDateTime.now();

                    Note note = new Note(addViewModel.getTitle(), addViewModel.getNoteColor(), localDateTime.toString(), addViewModel.getText());
                    if (addViewModel.getNewNoteList() == null) {
                        addViewModel.saveNote(note);
                    } else {
                        addViewModel.saveNote(note);
                        addViewModel.saveNoteList(addViewModel.getNewNoteList());
                    }

                    clrView();

                    Intent intent = new Intent();
                    intent.putExtra("result", true);
                    setResult(100, intent);
                } catch (ExecutionException | InterruptedException e) { throw new RuntimeException(e); }
            }
        }
        super.onBackPressed();
    }
    private void clrView() {
        etTitle.setText("");
        etNoteColor.setText("");
        addViewModel.setColorBtnState(0);
        addViewModel.setRawColorPicked("");
        bColorPicker.setText(R.string.rgb);
        llColorsPicker.setVisibility(View.VISIBLE);
        etNoteText.setVisibility(View.GONE);
        etNoteText.setText("");
        listItemAdapter = new ListItemAdapter(new ArrayList<>());
    }

    @Override
    public void onClick(View view) {
        ibColorRed.setImageResource(R.color.transparent);
        ibColorGreen.setImageResource(R.color.transparent);
        ibColorBlue.setImageResource(R.color.transparent);
        ibColorYellow.setImageResource(R.color.transparent);
        ibColorPurple.setImageResource(R.color.transparent);

        if (view.getId() == R.id.IBColorRed) {
            addViewModel.setRawColorPicked("#FF0000");
            ibColorRed.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorGreen) {
            addViewModel.setRawColorPicked("#00FF00");
            ibColorGreen.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorBlue) {
            addViewModel.setRawColorPicked("#0000FF");
            ibColorBlue.setImageResource(R.drawable.check_vector);
        }
        else if (view.getId() == R.id.IBColorYellow) {
            addViewModel.setRawColorPicked("#FFFF00");
            ibColorYellow.setImageResource(R.drawable.check_vector);
        }
        else {
            addViewModel.setRawColorPicked("#FF00FF");
            ibColorPurple.setImageResource(R.drawable.check_vector);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            saveNote();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.onBackPressed();
    }
}