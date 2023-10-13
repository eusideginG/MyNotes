package com.example.mynotes;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;
import com.example.mynotes.viewmodel.MenuViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private MenuViewModel mViewModel;
    ImageButton ibBack, ibDarkLightMode, ibAbout;
    Button bDownloadTxt, bBackup, bRestore, bSettings;
    ArrayList<Note> note;
    ArrayList<NoteList> noteList;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        initialize(view);
        return view;
    }

    private void initialize(View view) {
        mViewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        ibBack = view.findViewById(R.id.IBBackMenu);
        ibDarkLightMode = view.findViewById(R.id.IBChangeTheme);
        ibAbout = view.findViewById(R.id.IBAbout);
        bDownloadTxt = view.findViewById(R.id.BDownloadTxt);
        bBackup = view.findViewById(R.id.BBackup);
        bRestore = view.findViewById(R.id.BRestore);
        bSettings = view.findViewById(R.id.BSettings);

        // change night mode vectors
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.myNotes.nightMode", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("nightMode", true)) {
            ibDarkLightMode.setImageResource(R.drawable.light_mode_vector);
            Log.d("is dark", "");
        } else {
            ibDarkLightMode.setImageResource(R.drawable.dark_mode_vector);
            Log.d("is sunny", "");
        }

        listeners();
    }

    private void listeners() {
        ibBack.setOnClickListener(this); // close menu fragment when pressing the back arrow button
        ibDarkLightMode.setOnClickListener(this); // change theme mode (dark and light)
        ibAbout.setOnClickListener(this); // display a about fragment or popup
        bDownloadTxt.setOnClickListener(this); // download a txt file
        bBackup.setOnClickListener(this); // backup notes
        bRestore.setOnClickListener(this); // restore notes
        bSettings.setOnClickListener(this); // open settings fragment
    }

    private void changeTheMode() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("changeTheme", true);
        getParentFragmentManager().setFragmentResult("60", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void showAbout() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("openAbout", true);

        getParentFragmentManager().setFragmentResult("50", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void saveAsTxt() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "Notes.txt");

        Bundle bundle = new Bundle();
        bundle.putBoolean("downloadText", true);
        bundle.putParcelable("downloadTextIntent", intent);

        getParentFragmentManager().setFragmentResult("40", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void backup() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("startBackup", true);

        getParentFragmentManager().setFragmentResult("45", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void restore() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("startRestoring", true);

        getParentFragmentManager().setFragmentResult("46", bundle);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void openSettings() {
        // TODO add setting fragment
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.IBBackMenu) {
            requireActivity().onBackPressed();
        } else if (view.getId() == R.id.IBChangeTheme) {
            changeTheMode();
        } else if (view.getId() == R.id.IBAbout) {
            showAbout();
        } else if (view.getId() == R.id.BDownloadTxt) {
            saveAsTxt();
        } else if (view.getId() == R.id.BBackup) {
            backup();
        } else if (view.getId() == R.id.BRestore) {
            restore();
        } else if (view.getId() == R.id.BSettings) {
            openSettings();
        }
    }
}