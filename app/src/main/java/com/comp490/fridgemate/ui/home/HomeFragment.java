package com.comp490.fridgemate.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Adapters.RandomRecipeAdapter;
import com.comp490.fridgemate.Adapters.RecipeFromIngredientsAdapter;
import com.comp490.fridgemate.Listeners.RandomRecipeResponseListener;
import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Listeners.RecipeFromIngredientsListener;
import com.comp490.fridgemate.MainActivity;
import com.comp490.fridgemate.Models.RandomRecipeApiResponse;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RecipeDetailsActivity;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ProgressDialog dialog;
    RequestManager manager;
    RecipeFromIngredientsAdapter recipeFromIngredientsAdapter;
    RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    Spinner spinner;

    private final RecipeFromIngredientsListener recipeFromIngredientsListener = new RecipeFromIngredientsListener() {


        @Override
        public void didFetch(List<RecipeFromIngredientsResponse> response, String message) {
            dialog.dismiss();

            recyclerView = getActivity().findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            recipeFromIngredientsAdapter = new RecipeFromIngredientsAdapter(getActivity(), response, recipeClickListener);
            recyclerView.setAdapter(recipeFromIngredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

      dialog = new ProgressDialog((getActivity()));
        dialog.setTitle("Loading...");

        spinner = root.findViewById(R.id.spinner_tags);
        spinner.setVisibility(View.INVISIBLE);
        manager = new RequestManager((getActivity()));
//        manager.getRandomRecipes(randomRecipeResponseListener,tags);
//        dialog.show();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(getActivity(), RecipeDetailsActivity.class)
                    .putExtra("id", id));
        }
    };
}