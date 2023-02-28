package edu.ucsd.cse110.sharednotes.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NoteAPI {
    // TODO: Implement the API using OkHttp!
    // TODO: Read the docs: https://square.github.io/okhttp/
    // TODO: Read the docs: https://sharednotes.goto.ucsd.edu/docs

    private volatile static NoteAPI instance = null;

    private OkHttpClient client;

    public NoteAPI() {
        this.client = new OkHttpClient();
    }

    public static NoteAPI provide() {
        if (instance == null) {
            instance = new NoteAPI();
        }
        return instance;
    }
    public void delete(String title) {
        // URLs cannot contain spaces, so we replace them with %20.
        title = title.replace(" ", "%20");

        var request = new Request.Builder()
                .url("https://sharednotes.goto.ucsd.edu/notes/" + title)
                .method("DELETE", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("DELETE", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put(String title, String msg) {
        // URLs cannot contain spaces, so we replace them with %20.
        title = title.replace(" ", "%20");

        MediaType MEDIA_TYPE_MARKDOWN
                = MediaType.parse("text/x-markdown; charset=utf-8");


        var request = new Request.Builder()
                .url("https://sharednotes.goto.ucsd.edu/notes/" + title)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, msg))
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * An example of sending a GET request to the server.
     *
     * The /echo/{msg} endpoint always just returns {"message": msg}.
     */
    public LiveData<Note> get(String title, LiveData<Note> liveNote) {
        // URLs cannot contain spaces, so we replace them with %20.
        title = title.replace(" ", "%20");

        var request = new Request.Builder()
                .url("https://sharednotes.goto.ucsd.edu/notes/" + title)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            MutableLiveData<Note> mlivedata = (MutableLiveData<Note>) liveNote;

            return mlivedata;
            Log.i("ECHO", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    return liveNote}
}
