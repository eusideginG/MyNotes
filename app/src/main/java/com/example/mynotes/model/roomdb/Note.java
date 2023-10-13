package com.example.mynotes.model.roomdb;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity(tableName = "note_table")
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "color")
    private int color;
    @ColumnInfo(name = "dateTime")
    private String dateTime;
    @ColumnInfo(name = "noteText")
    private String noteText;

    @Ignore
    public Note() {}

    public Note(String title, int color, String dateTime, String noteText) {
        this.title = title;
        this.color = color;
        this.dateTime = dateTime;
        this.noteText = noteText;
    }
    @Ignore
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        color = in.readInt();
        dateTime = in.readString();
        noteText = in.readString();
    }
    @Ignore
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() { return color; }

    public void setColor(int color) { this.color = color; }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }
    @Ignore
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeString(dateTime);
        parcel.writeString(noteText);
    }

    @Ignore
    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color=" + color +
                ", dateTime='" + dateTime + '\'' +
                ", noteText='" + noteText + '\'' +
                '}';
    }
}
