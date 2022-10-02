package com.comp490.fridgemate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Adapters.CreateAdapter;
import com.comp490.fridgemate.Adapters.IngredientsAdapter;
import com.comp490.fridgemate.Adapters.InstructionsAdapter;
import com.comp490.fridgemate.Adapters.SimilarRecipeAdapter;
import com.comp490.fridgemate.Listeners.InstructionsListener;
import com.comp490.fridgemate.Listeners.ParseIngredientsListener;
import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Listeners.RecipeDetailsListener;
import com.comp490.fridgemate.Listeners.SimilarRecipesListener;
import com.comp490.fridgemate.Models.InstructionsResponse;
import com.comp490.fridgemate.Models.ParseIngredientsResponse;
import com.comp490.fridgemate.Models.RecipeDetailsResponse;
import com.comp490.fridgemate.Models.SimilarRecipeResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRecipeActivity extends AppCompatActivity {
    int id;
    EditText editText_meal_name, editText_meal_servings, editText_cook_time, editText_prep_time;
    Button add_ingredient_button, add_instruction_button, save_button;
    ImageView imageView_create_meal_image;
    RecyclerView recycler_create_meal_ingredients, recycler_create_meal_instructions;
    CreateAdapter createAdapterInstructions, createAdapterIngredients;
    RequestManager manager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference recipeDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe);
        findViews();
        List<String> ingredientsPlaceholder = new ArrayList<>();
        ingredientsPlaceholder.add("");
        List<String> instructionsPlaceholder = new ArrayList<>();
        instructionsPlaceholder.add("");
        createAdapterIngredients = new CreateAdapter(CreateRecipeActivity.this, ingredientsPlaceholder, true);
        createAdapterInstructions = new CreateAdapter(CreateRecipeActivity.this, instructionsPlaceholder, false);
        manager = new RequestManager(this);
        recycler_create_meal_instructions.setAdapter(createAdapterInstructions);
        recycler_create_meal_instructions.setHasFixedSize(false);
        recycler_create_meal_instructions.setLayoutManager(new GridLayoutManager(this, 1));
        recycler_create_meal_ingredients.setAdapter(createAdapterIngredients);
        recycler_create_meal_ingredients.setHasFixedSize(false);
        recycler_create_meal_ingredients.setLayoutManager(new GridLayoutManager(this, 1));

        add_ingredient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsPlaceholder.add("");
                createAdapterIngredients.notifyDataSetChanged();
            }
        });
        add_instruction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsPlaceholder.add("");
                createAdapterInstructions.notifyDataSetChanged();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.parseIngredients(parseIngredientsListener, createAdapterIngredients.listToSave);
                //save to firebase after ingredients parsed

            }
        });

    }
    private void findViews() {
        editText_meal_name = findViewById(R.id.editText_meal_name);
        editText_meal_servings = findViewById(R.id.editText_meal_servings);
        add_ingredient_button = findViewById(R.id.add_ingredient_button);
        add_instruction_button=findViewById(R.id.add_instruction_button);
        imageView_create_meal_image = findViewById(R.id.imageView_create_meal_image);
        recycler_create_meal_ingredients = findViewById(R.id.recycler_create_meal_ingredients);
        recycler_create_meal_instructions = findViewById(R.id.recycler_create_meal_instructions);
        save_button = findViewById(R.id.save_button);
        editText_cook_time = findViewById(R.id.editText_cook_time);
        editText_prep_time = findViewById(R.id.editText_prep_time);

    }

    private final ParseIngredientsListener parseIngredientsListener = new ParseIngredientsListener() {
        @Override
        public void didFetch(List<ParseIngredientsResponse> response, String message) {
            List<String> parsedIngredients = new ArrayList();
            for (int i = 0; i <response.size();i++) {
                parsedIngredients.add(response.get(i).name);
            }
            String readyInMinutes = "-1";
            try {
                readyInMinutes = String.valueOf(Integer.valueOf(editText_cook_time.toString()) + Integer.valueOf(editText_prep_time.toString()));
            } catch (Exception E) {
                //todo: make toast that displays that units were entered for cook or prep time
            }
            Map<String, Object> recipeData = new HashMap<>();
            recipeData.put("recipeName", editText_meal_name.getText().toString());
            recipeData.put("ingredients", createAdapterIngredients.listToSave);
            recipeData.put("parsedIngredients", parsedIngredients);
            recipeData.put("instructions", createAdapterInstructions.listToSave);
            recipeData.put("readyInMinutes", readyInMinutes);
            recipeData.put("preparationMinutes", editText_prep_time.getText().toString());
            recipeData.put("cookingMinutes", editText_cook_time.getText().toString());
            recipeData.put("image", "adding later this is a todo");
            recipeData.put("fromSpoonacular", false);
            recipeData.put("servings", editText_meal_servings.getText().toString());
            recipeData.put("id", createAdapterInstructions.listToSave.hashCode());
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            String user = currentFirebaseUser.getUid();
            recipeDocRef = db.collection("users/" + user + "/categories/folders/MyRecipes").document(String.valueOf(id));
            recipeDocRef.set(recipeData);
            startActivity(new Intent(CreateRecipeActivity.this, MainActivity.class)
                    .putExtra("id", createAdapterInstructions.listToSave.hashCode()));

            //todo: add check to make sure we're not replacing a recipe that already exists
        }

        @Override
        public void didError(String message) {
            Log.d("tag", "couldn't fetch for whatever reason stuck here forever");
            Log.d("sad", message);
        }
    };

}
