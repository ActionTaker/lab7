package edu.ucsd.cse110.sharednotes.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

public class NoteRepository {
    private final NoteDao dao;

    public NoteRepository(NoteDao dao) {
        this.dao = dao;
    }

    // Synced Methods
    // ==============

    /**
     * This is where the magic happens. This method will return a LiveData object that will be
     * updated when the note is updated either locally or remotely on the server. Our activities
     * however will only need to observe this one LiveData object, and don't need to care where
     * it comes from!
     *
     * This method will always prefer the newest version of the note.
     *
     * @param title the title of the note
     * @return a LiveData object that will be updated when the note is updated locally or remotely.
     */
    public LiveData<Note> getSynced(String title) {
        var note = new MediatorLiveData<Note>();

        Observer<Note> updateFromRemote = theirNote -> {
            var ourNote = note.getValue();
            if (ourNote == null || ourNote.updatedAt < theirNote.updatedAt) {
                upsertLocal(theirNote);
            }
        };

        // If we get a local update, pass it on.
        note.addSource(getLocal(title), note::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        note.addSource(getRemote(title), updateFromRemote);

        return note;
    }

    public void upsertSynced(Note note) {
        upsertLocal(note);
        upsertRemote(note);
    }

    // Local Methods
    // =============

    public LiveData<Note> getLocal(String title) {
        return dao.get(title);
    }

    public LiveData<List<Note>> getAllLocal() {
        return dao.getAll();

    }

    public void upsertLocal(Note note) {
        note.updatedAt = System.currentTimeMillis();
        dao.upsert(note);
    }

    public void deleteLocal(Note note) {
        dao.delete(note);
    }

    public boolean existsLocal(String title) {
        return dao.exists(title);
    }

    // Remote Methods
    // ==============

    public LiveData<Note> getRemote(String title) {
        NoteAPI api = new NoteAPI();

        return api.get(title, getLocal(title));
    }

    public void upsertRemote(Note note) {
        NoteAPI api = new NoteAPI();
        api.put(note.title, note.content);

        // TODO: Implement upsersRemote!
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
