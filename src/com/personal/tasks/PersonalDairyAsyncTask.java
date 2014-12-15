package com.personal.tasks;

import android.os.AsyncTask;



import com.personal.android.util.EventBus;
import com.personal.android.util.JavaConsts;

/**
 * Execute an AsyncTask and post the result onto the Bus if it is nonnull
 */
public abstract class PersonalDairyAsyncTask extends AsyncTask<Void, Void, Object> {

    @Override
    protected final Object doInBackground(Void... params) {
        try {
            return doHeavyLoading(params);
        } catch (Exception e) {
            e.printStackTrace();
            return onError(e);
        }
    }

    /**
     * Long running task that will be performed in a different thread
     * @param params
     * @return
     */
    protected abstract Object doHeavyLoading(Void ... params);

    /**
     * Generate a specific error object
     * @param exception the exception that occurred
     * @return
     */
    protected abstract Object onError(Exception exception);

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            EventBus.post(result);
        }
    }
}
