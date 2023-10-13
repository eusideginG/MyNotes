package com.example.mynotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mynotes.model.repo.NoteRepo;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ViewAndEditViewModel extends AndroidViewModel {
    private String title, rgb, hex, text, rawColorPicked = "";
    private int noteColor, colorBtnState = 0, rawColor = 0;
    private Note note;
    private ArrayList<NoteList> noteLists;
    private final NoteRepo noteRepo;

    public ViewAndEditViewModel(@NonNull Application application) {
        super(application);
        noteRepo = new NoteRepo(application);
    }

    public int getRawColor() {
        return rawColor;
    }

    public void setRawColor(int rawColor) {
        this.rawColor = rawColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public String getHex() { return hex; }

    public void setHex(String hex) { this.hex = hex; }

    public int getNoteColor() { return noteColor; }

    public void setNoteColor(int noteColor) { this.noteColor = noteColor; }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Note getNote() { return note; }

    public void setNote(Note note) { this.note = note; }

    public ArrayList<NoteList> getNoteLists() {
        if (noteLists != null) {
            return noteLists;
        } else return new ArrayList<>();
    }

    public String getRawColorPicked() {
        return rawColorPicked;
    }

    public void setRawColorPicked(String rawColorPicked) {
        this.rawColorPicked = rawColorPicked;
    }

    public int getColorBtnState() {
        return colorBtnState;
    }

    public void setColorBtnState(int colorBtnState) {
        this.colorBtnState = colorBtnState;
    }

    public void setNoteLists(ArrayList<NoteList> noteLists) {
        this.noteLists = noteLists;
    }

    public void setNoteListByNoteId(int noteId) throws ExecutionException, InterruptedException {
        noteLists = (ArrayList<NoteList>) noteRepo.getNoteListByNoteId(noteId);
    }

    public ArrayList<Note> getAllNotes() throws ExecutionException, InterruptedException { return (ArrayList<Note>) noteRepo.getAllNotes(); }

    public boolean saveNote(Note note) throws ExecutionException, InterruptedException {
        return noteRepo.update(note);
    }

    public void saveNoteList(ArrayList<NoteList> noteList) throws ExecutionException, InterruptedException {
        noteList.stream()
                .filter(listItem -> listItem.getNoteId() != note.getId())
                .forEach(listItem -> listItem.setNoteId(note.getId()));
        noteRepo.update(note.getId(), noteList);
    }

}