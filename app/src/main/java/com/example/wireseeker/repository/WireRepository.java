package com.example.wireseeker.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.wireseeker.database.Wire;
import com.example.wireseeker.database.WireDao;
import com.example.wireseeker.database.WireDatabase;

import java.util.List;

public class WireRepository {
        private WireDao mWireDao;
        private LiveData<List<Wire>> mAllWires;

        public WireRepository(Application application) {
            WireDatabase db = WireDatabase.getDatabase(application);
            mWireDao = db.WireDao();
            mAllWires = mWireDao.getAll();
        }

        public LiveData<List<Wire>> getAllWires() {
            return mAllWires;
        }

//        private static class InsertAsyncTask extends AsyncTask<Wire, Void, Void> {
//            private WireDao mAsyncDao;
//
//            InsertAsyncTask(WireDao WireDao) {
//                this.mAsyncDao = WireDao;
//            }
//
//            @Override
//            protected Void doInBackground(Wire... words) {
//                mAsyncDao.insert(Wires[0]);
//                return null;
//            }
//        }
    }
