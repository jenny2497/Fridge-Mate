package com.comp490.fridgemate.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Listeners.RecipeFromIngredientsListener;
import com.comp490.fridgemate.MainActivity;
import com.comp490.fridgemate.Models.RandomRecipeApiResponse;
import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RecipeDetailsActivity;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ProgressDialog dialog;
    RequestManager manager;
    RecipeFromIngredientsAdapter recipeFromIngredientsAdapter;
    RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    Spinner spinner;
    List<String> ingredientsInFridge = new ArrayList<>();
    FirebaseUser currentFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference fridgeDocRef;


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
            dialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            Log.d("Tag1", message);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


      dialog = new ProgressDialog((getActivity()));
        dialog.setTitle("Loading...");
        dialog.show();
        spinner = root.findViewById(R.id.spinner_tags);
        spinner.setVisibility(View.INVISIBLE);
//        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
//                root.getContext(),
//                R.array.tags,
//                R.layout.spinner_text
//        );
//        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(spinnerSelectedListener);
        manager = new RequestManager(root.getContext());
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String user = currentFirebaseUser.getUid();
        fridgeDocRef = db.collection("users/" + user + "/categories").document("fridge");
        fetchFromDatabase();
//        dialog.show();

        return root;
    }

    private void fetchFromDatabase() {

        fridgeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ingredientsInFridge = (ArrayList<String>) document.getData().get("fridge");
                        manager.getRecipeFromIngredients(recipeFromIngredientsListener, ingredientsInFridge);

                        Log.d("TAG", "DocumentSnapshot data: " + ingredientsInFridge);
                    } else {
                        Log.d("TAG", "No such document");
                        dialog.dismiss();
                        Toast.makeText(getContext(), "need to add ingredients to fridge", Toast.LENGTH_SHORT);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    Toast.makeText(getContext(), "couldn't retrieve fridge ingredients list", Toast.LENGTH_SHORT);

                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
//    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//            tags.clear();
//            tags.add(adapterView.getSelectedItem().toString());
//        manager.getRecipeFromIngredients(recipeFromIngredientsListener, tags);
//        dialog.show();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id, boolean fromSpoonacular, String folderName) {
            Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("id", id);
            extras.putBoolean("fromSpoonacular", true);
            intent.putExtras(extras);
            startActivity(intent);
        }
    };
}