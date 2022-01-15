package com.example.registrasivaksin.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.registrasivaksin.InputDao;
import com.example.registrasivaksin.VaccineDB;

import java.util.List;

public class InputRepository {

    private InputDao inputDao;
    private LiveData<List<InputDao>> allData;

    public InputRepository(Application application) {
        VaccineDB database = VaccineDB.getInstance(application);
        inputDao = database.inputDao();
        allData = inputDao.getAllData();
    }

    public void insert(InputDao note) {
        new InsertNoteAsyncTask(inputDao).execute(note);
    }

    public void update(InputDao note) {
        new UpdateNoteAsyncTask(inputDao).execute(note);
    }

    public void delete(InputDao note) {
        new DeleteNoteAsyncTask(inputDao).execute(note);
    }

    public void deleteAllNotes() {
        new DeleteAllNoteAsyncTask(inputDao).execute();
    }

    public LiveData<List<InputDao>> getAllData() {
        return allData;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<InputDao, Void, Void> {

        private InputDao inputDao;

        private InsertNoteAsyncTask(InputDao inputDao) {
            this.inputDao = inputDao;
        }

        @Override
        protected Void doInBackground(InputDao... modelInputs) {
            inputDao.insert(modelInputs[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<InputDao, Void, Void> {

        private InputDao inputDao;

        private UpdateNoteAsyncTask(InputDao inputDao) {
            this.inputDao = inputDao;
        }

        @Override
        protected Void doInBackground(InputDao... modelInputs) {
            inputDao.update(modelInputs[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<InputDao, Void, Void> {

        private InputDao inputDao;

        private DeleteNoteAsyncTask(InputDao inputDao) {
            this.inputDao = inputDao;
        }

        @Override
        protected Void doInBackground(InputDao... modelInputs) {
            inputDao.delete(modelInputs[0]);
            return null;
        }
    }

    private static class DeleteAllNoteAsyncTask extends AsyncTask<InputDao, Void, Void> {

        private InputDao inputDao;

        private DeleteAllNoteAsyncTask(InputDao inputDao) {
            this.inputDao = inputDao;
        }

        @Override
        protected Void doInBackground(InputDao... modelInputs) {
            inputDao.deleteAllData();
            return null;
        }
    }

}