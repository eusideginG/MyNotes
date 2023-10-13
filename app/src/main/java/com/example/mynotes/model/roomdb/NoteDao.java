package com.example.mynotes.model.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);
    @Insert
    void insert(ArrayList<NoteList> noteList);
    @Query("UPDATE note_table SET title = :title, color = :color, dateTime = :dateTime, noteText = :noteText WHERE id = :id")
    void update(String title, int color, String dateTime, String noteText, int id);
    @Delete
    void delete(Note note);
    @Delete
    void delete(NoteList noteList);
    @Query("DELETE FROM note_list_table WHERE noteId = :noteId")
    void deleteListByNoteId(int noteId);
    @Query("DELETE FROM note_table")
    void deleteNoteEntries();
    @Query("DELETE FROM note_list_table")
    void deleteNoteListEntries();
    @Query("SELECT * FROM note_table ORDER BY dateTime DESC")
    List<Note> getAllNotes();
    @Query("SELECT * FROM note_list_table;")
    List<NoteList> getAllNoteLists();
    @Query("SELECT * FROM note_list_table WHERE noteId = :noteId")
    List<NoteList> getListByNoteId(int noteId);
    @Query("SELECT id FROM note_table WHERE title = :title")
    int getNoteId(String title);
    @Query("SELECT * FROM note_list_table WHERE noteId = :noteId")
    List<NoteList> getNoteListByNoteId(int noteId);
}
