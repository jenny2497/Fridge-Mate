package com.comp490.fridgemate.ui.bookMark;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp490.fridgemate.R;
import com.comp490.fridgemate.databinding.FragmentFridgeBinding;
import com.comp490.fridgemate.ui.fridge.FridgeViewModel;

public class BookMarkFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark,  container,false);
    }

}
