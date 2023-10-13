package com.example.mynotes.model.roomdb;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_list_table", foreignKeys =
        {@ForeignKey(entity = Note.class, parentColumns = "id", childColumns = "noteId",
                onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)})
public class NoteList {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int noteId;
    private int listType;
    private String listText;

    public NoteList(int noteId, int listType, String listText) {
        this.noteId = noteId;
        this.listType = listType;
        this.listText = listText;
    }

    @Ignore
    public NoteList(int listType, String listText) {
        this.listType = listType;
        this.listText = listText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public String getListText() {
        return listText;
    }

    public void setListText(String listText) {
        this.listText = listText;
    }

    @Ignore
    @NonNull
    @Override
    public String toString() {
        return "NoteList{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", listType=" + listType +
                ", listText='" + listText + '\'' +
                '}';
    }
}
