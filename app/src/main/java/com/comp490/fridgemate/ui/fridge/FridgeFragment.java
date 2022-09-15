package com.comp490.fridgemate.ui.fridge;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp490.fridgemate.Listeners.AutocompleteIngredientsListener;
import com.comp490.fridgemate.Models.AutocompleteIngredientsResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.databinding.FragmentFridgeBinding;

import java.util.ArrayList;
import java.util.List;

public class FridgeFragment extends Fragment {

    private FragmentFridgeBinding binding;
    RequestManager manager;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView addFridgeItem;
    View root;

    List<String> apiFoods = new ArrayList<>();

    private final AutocompleteIngredientsListener autocompleteIngredientsListener = new AutocompleteIngredientsListener() {
        @Override
        public void didFetch(List<AutocompleteIngredientsResponse> response, String message) {
            apiFoods.clear();
            for (int i=0; i < response.size(); i++) {
                apiFoods.add(response.get(i).name);
            }
            adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, apiFoods);
            addFridgeItem.setAdapter(adapter);

            addFridgeItem.showDropDown();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FridgeViewModel fridgeViewModel =
                new ViewModelProvider(this).get(FridgeViewModel.class);

        binding = FragmentFridgeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        manager = new RequestManager((root.getContext()));

        addFridgeItem = root.findViewById(R.id.add_fridge_item);
        adapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, apiFoods);
        addFridgeItem.setAdapter(adapter);
        addFridgeItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //retrieve data s
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                manager.getAutoCompleteIngredients(autocompleteIngredientsListener, text);
                Log.d("TAG", "foodsApi is " + apiFoods);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}