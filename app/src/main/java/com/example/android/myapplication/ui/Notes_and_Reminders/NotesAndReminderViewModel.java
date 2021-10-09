package com.example.android.myapplication.ui.Notes_and_Reminders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotesAndReminderViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotesAndReminderViewModel() {


        mText = new MutableLiveData<>();
        //mText.setValue("No notes Availabe");



    }

    public LiveData<String> getText() {
        return mText;
    }
}