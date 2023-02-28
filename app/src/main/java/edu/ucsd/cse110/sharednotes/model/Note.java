package edu.ucsd.cse110.sharednotes.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity(tableName = "notes")
public class Note {
    /** The title of the note. Used as the primary key for shared notes (even on the cloud). */
    @PrimaryKey
    @SerializedName("title")
    @NonNull
    public String title;

    /** The content of the note. */
    @SerializedName("content")
    @NonNull
    public String content;

    /**
     * When the note was last modified. Used for resolving local (db) vs remote (api) conflicts.
     * Defaults to 0 (Jan 1, 1970), so that if a note already exists remotely, its content is
     * always preferred to a new empty note.
     */
    @SerializedName(value = "updated_at", alternate = "updatedAt")
    public long updatedAt = 0;

    /** General constructor for a note. */
    public Note(@NonNull String title, @NonNull String content) {
        this.title = title;
        this.content = content;
    }

    public static Note fromJSON(String json) {
        TempNote tempNote = new Gson().fromJson(json, TempNote.class);
        Note note = new Note(tempNote.title, tempNote.content);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime datetime = LocalDateTime.parse(tempNote.updatedAt.substring(0, 19), formatter);
        note.updatedAt = datetime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return note;
    }
}

class TempNote {
    @PrimaryKey
    @SerializedName("title")
    @NonNull
    public String title;

    @SerializedName("content")
    @NonNull
    public String content;
    @SerializedName(value = "updated_at", alternate = "updatedAt")
    public String updatedAt;
    public TempNote(String title, String content, String updatedAt) {
        this.title = title;
        this.content = content;
        this.updatedAt = updatedAt;
    }
}
