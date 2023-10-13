package com.example.mynotes.model.repo;

import android.app.Application;
import android.util.Log;

import com.example.mynotes.model.roomdb.AppDatabase;
import com.example.mynotes.model.roomdb.Note;
import com.example.mynotes.model.roomdb.NoteDao;
import com.example.mynotes.model.roomdb.NoteList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NoteRepo {
    private final String TAG = this.getClass().getSimpleName();
    private final NoteDao noteDao;
    ExecutorService service;
    Future<?> future;
    List<Note> allNotes;
    List<NoteList> allNoteLists;
    List<NoteList> noteList;
    int noteId;

    public NoteRepo(Application application) {
        noteDao = AppDatabase.getInstance(application).noteDao();
    }
    public boolean insert(Note note) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.insert(note);
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();

    }
    public boolean insert(ArrayList<NoteList> noteList) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.insert(noteList);
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();

    }

    public boolean update(Note note) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.update(note.getTitle(), note.getColor(), note.getDateTime(), note.getNoteText(), note.getId());
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();
    }
    public boolean update(int noteId, ArrayList<NoteList> noteList) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.deleteListByNoteId(noteId);
            noteDao.insert(noteList);
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();
    }

    public boolean delete(Note note) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.delete(note);
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();
    }
    public boolean delete(NoteList noteList) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteDao.delete(noteList);
        });
        future.get();
        service.shutdown();
        return future.isDone() && !future.isCancelled();
    }

    public List<Note> getAllNotes() throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            allNotes = noteDao.getAllNotes();
        });
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        if (future.isDone() && !future.isCancelled()) {
            return allNotes;
        }
        return allNotes = null;
    }

    public List<NoteList> getAllNoteLists() throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            allNoteLists = noteDao.getAllNoteLists();
        });
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        if (future.isDone() && !future.isCancelled()) {
            return allNoteLists;
        }
        return allNoteLists = null;
    }

    public List<NoteList> getNoteList(int noteId) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            allNoteLists = noteDao.getListByNoteId(noteId);
        });
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        if (future.isDone() && !future.isCancelled()) {
            return allNoteLists;
        }
        return allNoteLists = null;
    }

    public int getNoteId(String title) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteId = noteDao.getNoteId(title);
        });
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        if (future.isDone() && !future.isCancelled()) {
            return noteId;
        }
        return noteId = 0;
    }

    public List<NoteList> getNoteListByNoteId(int noteId) throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(() -> {
            noteList = noteDao.getNoteListByNoteId(noteId);
        });
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        if (future.isDone() && !future.isCancelled()) {
            return noteList;
        }
        return noteList;
    }

    public boolean deleteNoteEntries() throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(noteDao::deleteNoteEntries);
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        return future.isDone() && !future.isCancelled();
    }

    public boolean deleteNoteListEntries() throws ExecutionException, InterruptedException {
        service = Executors.newSingleThreadExecutor();
        future = service.submit(noteDao::deleteNoteListEntries);
        future.get();
        service.shutdown();
        while (!future.isDone()) {
            Log.d(TAG, "Repository retrieving from database...");
        }
        return future.isDone() && !future.isCancelled();
    }
}
