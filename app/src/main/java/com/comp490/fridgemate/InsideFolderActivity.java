package com.comp490.fridgemate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Adapters.RandomRecipeAdapter;
import com.comp490.fridgemate.Listeners.RandomRecipeResponseListener;
import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Models.RandomRecipeApiResponse;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.databinding.FragmentSearchBinding;
import com.comp490.fridgemate.ui.search.SearchViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InsideFolderActivity extends AppCompatActivity {
    ProgressDialog dialog;
    RandomRecipeAdapter recipeAdapter;
    RecyclerView recyclerView;
    List<String> tags = new ArrayList<>();
    SearchView searchView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser;
    String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);


//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

      dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        searchView = findViewById(R.id.searchView_home);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //look for recipe in favorites
//                tags.clear();
//                tags.add(query);
//                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        String folderName = getIntent().getStringExtra("folderName");
        Log.d("foldername", folderName);

        // [START get_multiple_all]
        List<Recipe> recipesInFolder = new ArrayList<>();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = currentFirebaseUser.getUid();
        db.collection("users/" + user + "/categories/folders/" + folderName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Recipe newRecipe = new Recipe();
                                    Log.d("doc", document.getId() + " => " + document.getData());

                                    newRecipe.readyInMinutes = (int) (long) document.getData().get("readyInMinutes");
                                    newRecipe.servings = (int) (long) document.getData().get("servings");
                                    newRecipe.title = (String) document.getData().get("recipeName");
                                    newRecipe.id = (int) (long) document.getData().get("id");
                                    newRecipe.image = (String) document.getData().get("image");
                                    recipesInFolder.add(newRecipe);

                                    //todo: error handling - what if data upload got interrupted?  need to check if any is null
                                }
                                recyclerView = findViewById(R.id.recycler_random);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new GridLayoutManager(InsideFolderActivity.this, 1));
                                recipeAdapter = new RandomRecipeAdapter(InsideFolderActivity.this, recipesInFolder, recipeClickListener, true);
                                recyclerView.setAdapter(recipeAdapter);
                            } else {
                                Log.d("doc", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            // [END get_multiple_all]

//        manager.getRandomRecipes(randomRecipeResponseListener,tags);
//        dialog.show();


    }

//
//    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//            tags.clear();
//            tags.add(adapterView.getSelectedItem().toString());
//        manager.getRandomRecipes(randomRecipeResponseListener, tags);
//        dialog.show();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    };
//
//    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
//        @Override
//        public void onRecipeClicked(String id) {
//            startActivity(new Intent(getActivity(), RecipeDetailsActivity.class)
//                    .putExtra("id", id));
//        }
//    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(InsideFolderActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id));
        }
    };
}