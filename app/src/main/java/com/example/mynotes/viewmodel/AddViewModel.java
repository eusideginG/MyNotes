package com.example.mynotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mynotes.model.repo.NoteRepo;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AddViewModel extends AndroidViewModel {
    private String title, text, listText, rawColorPicked = "";
    private int listStyle, listNumber, noteColor, colorBtnState = 0;
    private ArrayList<NoteList> newNoteList;
    private NoteRepo noteRepo;

    public AddViewModel(@NonNull Application application) {
        super(application);

        noteRepo = new NoteRepo(application);
    }

    public ArrayList<NoteList> getAllNoteLists() throws ExecutionException, InterruptedException { return (ArrayList<NoteList>) noteRepo.getAllNoteLists(); }

    public void deleteTableEntries() throws ExecutionException, InterruptedException {
        noteRepo.deleteNoteListEntries();
        noteRepo.deleteNoteEntries();
    }

    public String getRawColorPicked() {
        return rawColorPicked;
    }

    public void setRawColorPicked(String rawColorPicked) {
        this.rawColorPicked = rawColorPicked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getListText() {
        return listText;
    }

    public int getColorBtnState() {
        return colorBtnState;
    }

    public void setColorBtnState(int colorBtnState) {
        this.colorBtnState = colorBtnState;
    }

    public void setListText(String listText) {
        this.listText = listText;
    }

    public int getListStyle() {
        return listStyle;
    }

    public void setListStyle(int listStyle) {
        this.listStyle = listStyle;
    }

    public int getNoteColor() { return this.noteColor; }

    public void setNoteColor(int noteColor) {
        this.noteColor = noteColor;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    public ArrayList<NoteList> getNewNoteList() {
        if (newNoteList == null) {
            return new ArrayList<>();
        }
        return this.newNoteList;
    }

    public ArrayList<Note> getAllNotes() throws ExecutionException, InterruptedException { return (ArrayList<Note>) noteRepo.getAllNotes(); }

    public void setNewNoteList(ArrayList<NoteList> newNoteList) { this.newNoteList =  newNoteList; }

    public boolean saveNote(Note note) throws ExecutionException, InterruptedException {
        return noteRepo.insert(note);
    }

    public void saveNoteList(ArrayList<NoteList> noteList) throws ExecutionException, InterruptedException {
        int noteId = noteRepo.getNoteId(title);
        noteList.forEach(listItem -> listItem.setNoteId(noteId));
        noteRepo.insert(noteList);
    }
    public boolean saveNoteList(ArrayList<NoteList> noteList, String noteTitle) throws ExecutionException, InterruptedException {
        int noteId = noteRepo.getNoteId(noteTitle);
        noteList.forEach(listItem -> listItem.setNoteId(noteId));
        return noteRepo.insert(noteList);
    }

    public int getNoteId(String title) throws ExecutionException, InterruptedException {
        return noteRepo.getNoteId(title);
    }
}