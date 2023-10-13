package com.example.mynotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.mynotes.model.repo.NoteRepo;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NoteViewModel extends AndroidViewModel {
    NoteRepo noteRepo;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        this.noteRepo = new NoteRepo(application);
    }

    public ArrayList<Note> getAllNotes() throws ExecutionException, InterruptedException { return (ArrayList<Note>) noteRepo.getAllNotes(); }
    public ArrayList<NoteList> getAllNoteLists() throws ExecutionException, InterruptedException { return (ArrayList<NoteList>) noteRepo.getAllNoteLists(); }

    public boolean saveNote(Note note) throws ExecutionException, InterruptedException {
        return noteRepo.insert(note);
    }

    public boolean saveNoteList(ArrayList<NoteList> noteList, String noteTitle) throws ExecutionException, InterruptedException {
        int noteId = noteRepo.getNoteId(noteTitle);
        noteList.forEach(listItem -> listItem.setNoteId(noteId));
        return noteRepo.insert(noteList);
    }

    public void deleteTableEntries() throws ExecutionException, InterruptedException {
        noteRepo.deleteNoteListEntries();
        noteRepo.deleteNoteEntries();
    }
}
