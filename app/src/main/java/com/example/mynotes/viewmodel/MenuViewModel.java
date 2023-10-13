package com.example.mynotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mynotes.model.repo.NoteRepo;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MenuViewModel extends AndroidViewModel {
    NoteRepo noteRepo;
    ArrayList<Note> allNotes;
    ArrayList<NoteList> allNoteLists;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        this.noteRepo = new NoteRepo(application);
    }

    public ArrayList<Note> getAllNotes() throws ExecutionException, InterruptedException {
        if (allNotes == null) {
            allNotes = (ArrayList<Note>) noteRepo.getAllNotes();
        }
        return allNotes;
    }

    public void setAllNotes(ArrayList<Note> allNotes) {
        this.allNotes = allNotes;
    }

    public ArrayList<NoteList> getAllNoteLists() throws ExecutionException, InterruptedException {
        if (allNoteLists == null) {
            allNoteLists = (ArrayList<NoteList>) noteRepo.getAllNoteLists();
        }
        return allNoteLists;
    }

    public void setAllNoteLists(ArrayList<NoteList> allNoteLists) {
        this.allNoteLists = allNoteLists;
    }
}