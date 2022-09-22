package com.comp490.fridgemate.ui.fridge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FridgeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FridgeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}