package com.personal.tasks;

/**
 * For separate Server calls
 * 
 */
public abstract class PersonalDairyDbAsyncTask extends PersonalDairyAsyncTask {
    @Override
    protected Object onError(Exception exception) {
        return new PersonalDairyDBError();
    }
}
