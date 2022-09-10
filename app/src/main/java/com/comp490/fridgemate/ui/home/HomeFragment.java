package com.comp490.fridgemate.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Adapters.RandomRecipeAdapter;
import com.comp490.fridgemate.Listeners.RandomRecipeResponseListener;
import com.comp490.fridgemate.MainActivity;
import com.comp490.fridgemate.Models.RandomRecipeApiResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    private FragmentHomeBinding binding;

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();

            recyclerView = getActivity().findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            randomRecipeAdapter = new RandomRecipeAdapter(getActivity(), response.recipes);
            recyclerView.setAdapter(randomRecipeAdapter);
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
        manager = new RequestManager((getActivity()));
        manager.getRandomRecipes(randomRecipeResponseListener);
        dialog.show();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}