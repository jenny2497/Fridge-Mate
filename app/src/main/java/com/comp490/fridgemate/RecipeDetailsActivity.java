package com.comp490.fridgemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comp490.fridgemate.Adapters.IngredientsAdapter;
import com.comp490.fridgemate.Adapters.InstructionsAdapter;
import com.comp490.fridgemate.Adapters.SimilarRecipeAdapter;
import com.comp490.fridgemate.Listeners.InstructionsListener;
import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Listeners.RecipeDetailsListener;
import com.comp490.fridgemate.Listeners.SimilarRecipesListener;
import com.comp490.fridgemate.Models.AnalyzedInstruction;
import com.comp490.fridgemate.Models.ExtendedIngredient;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.Models.RecipeDetailsResponse;
import com.comp490.fridgemate.Models.SimilarRecipeResponse;
import com.comp490.fridgemate.Models.Step;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailsActivity extends AppCompatActivity {
    int id;
    TextView textView_meal_name, textView_meal_source, textView_meal_servings, similar_recipe_label;
    ImageView imageView_meal_image, imageView_favorited_main;
    Button edit_button, delete_button;
    RecyclerView recycler_meal_ingredients, recycler_meal_similar, recycler_meal_instructions;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;
    FirebaseUser currentFirebaseUser;
    String user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference favoritedDocRef;
    boolean recipeIsFavorited;
    Map<String, Object> recipeData = new HashMap<>();
    boolean fromSpoonacular;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference recipeDocRef;
    String folderName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();
        findViews();
        Bundle extras = getIntent().getExtras();
        id = Integer.parseInt(extras.getString("id"));
        fromSpoonacular = extras.getBoolean("fromSpoonacular");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();
        if (fromSpoonacular) {
            Log.d("are we here", "yes");
            manager = new RequestManager(this);
            manager.getRecipeDetails(recipeDetailsListener, id);
            manager.getSimilarRecipes(similarRecipesListener, id);
            manager.getInstructions(instructionsListener, id);
            edit_button.setVisibility(View.INVISIBLE);
            delete_button.setVisibility(View.INVISIBLE);
        } else {
            Log.d("are we here", "no");
            similar_recipe_label.setVisibility(View.INVISIBLE);
            folderName = extras.getString("folderName");
            recycler_meal_similar.setVisibility(View.INVISIBLE);

            edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecipeDetailsActivity.this,
                            CreateRecipeActivity.class);
                    intent.putExtra("recipeId", (int) recipeData.get("id"));
                    startActivity(intent);
                }
            });
            recipeDocRef = db.collection("users/" + user + "/categories/folders/" + folderName).document(String.valueOf(id));
            Log.d("file path", "users/" + user + "/categories/folders/" + folderName + "/" + id);
            recipeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Recipe newRecipe = new Recipe();
                            Log.d("doc", document.getId() + " => " + document.getData());
                            try {
                                newRecipe.readyInMinutes = (int) (long) document.getData().get("readyInMinutes");
                                newRecipe.servings = (int) (long) document.getData().get("servings");
                                newRecipe.title = (String) document.getData().get("recipeName");
                                newRecipe.id = (int) (long) document.getData().get("id");
                                newRecipe.extendedIngredients = new ArrayList<ExtendedIngredient>();
                                List<String> ingredientOriginals = (List<String>) document.getData().get("ingredients");
                                List<String> parsedIngredients = (List<String>) document.getData().get("parsedIngredients");
                                List<String> ingredientImages = (List<String>) document.getData().get("ingredientsImages");
                                for (int i=0; i < parsedIngredients.size(); i++) {
                                    ExtendedIngredient toAdd = new ExtendedIngredient();
                                    toAdd.name = parsedIngredients.get(i);
                                    toAdd.original = ingredientOriginals.get(i);
                                    toAdd.image = ingredientImages.get(i);
                                    newRecipe.extendedIngredients.add(toAdd);
                                }

                                List<String> steps = (List<String>) document.getData().get("instructions");
                                AnalyzedInstruction analyzedInstruction = new AnalyzedInstruction();
                                ArrayList<Step> stepsInsideInstruction = new ArrayList<>();

                                for (int i = 0; i < steps.size(); i++) {
                                    Step toAdd = new Step();
                                    toAdd.step = steps.get(i);
                                    toAdd.number = i + 1;
                                    stepsInsideInstruction.add(toAdd);
                                }
                                analyzedInstruction.steps = stepsInsideInstruction;
                                ArrayList<AnalyzedInstruction> analyzedInstructions = new ArrayList<>();
                                analyzedInstructions.add(analyzedInstruction);
                                newRecipe.analyzedInstructions = analyzedInstructions;

                                newRecipe.preparationMinutes  = (int) (long) document.getData().get("preparationMinutes");
                                newRecipe.cookingMinutes = (int) (long) document.getData().get("cookingMinutes");
                                newRecipe.fromMyRecipes = (boolean) document.getData().get("fromMyRecipes");


                            } catch (Exception e){

                            }

                            newRecipe.image = (String) document.getData().get("image");
                            createRecipeDataForFavorites(newRecipe, true);
                            textView_meal_name.setText(newRecipe.title);
                            textView_meal_source.setText(newRecipe.sourceName);
                            textView_meal_servings.setText(String.valueOf(newRecipe.servings));

                            recycler_meal_instructions.setHasFixedSize(true);
                            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, newRecipe.analyzedInstructions);
                            recycler_meal_instructions.setAdapter(instructionsAdapter);

                            recycler_meal_ingredients.setHasFixedSize(true);
                            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this, newRecipe.extendedIngredients);
                            recycler_meal_ingredients.setAdapter(ingredientsAdapter);




                            StorageReference imgRef = storage.getReference();
                            StorageReference imgRefWithPath = imgRef.child("users/" + user + "/categories/folders/MyRecipes/" + newRecipe.id);
                            imgRefWithPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(imageView_meal_image);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Log.d("for some reason", "won't display");
                                }
                            });

                        } else {
                            Log.d("no doc", "where is it?");
                        }
                    } else {
                        Log.d("TAG", "Failed with: ", task.getException());
                    }
                }
            });


        }





       imageView_favorited_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipeIsFavorited) {
                    favoritedDocRef.delete();
                    imageView_favorited_main.setImageResource(R.drawable.ic_outline_favorite_border_24);

                } else {
                    addRecipeToDatabase();
                    imageView_favorited_main.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                recipeIsFavorited = !recipeIsFavorited;
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeIsFavorited) {
                    favoritedDocRef.delete();
                }
                if (folderName=="Favorites") { //then we have to get a recipeDocRef to the copy stored in MyRecipes
                    recipeDocRef = db.collection("users/" + user + "/categories/folders/MyRecipes").document(String.valueOf(id));
                }
                recipeDocRef.delete();
                Intent intent = new Intent(RecipeDetailsActivity.this,
                        MainActivity.class);
                startActivity(intent);

            }
        });
    }

    public void addRecipeToDatabase() {

            favoritedDocRef.set(recipeData);

    }

    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients=findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar = findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);
        textView_meal_servings = findViewById(R.id.textView_meal_servings);
        imageView_favorited_main = findViewById(R.id.imageView_favorited_main);
        similar_recipe_label = findViewById(R.id.similar_recipe_label);
        edit_button = findViewById(R.id.edit_button);
        delete_button = findViewById(R.id.delete_button);
    }

    private void createRecipeDataForFavorites(RecipeDetailsResponse response, boolean fromMyRecipes) {

        ArrayList ingredients = new ArrayList();
        ArrayList parsedIngredients = new ArrayList();
        ArrayList instructions = new ArrayList();
        ArrayList ingredientsImages = new ArrayList();
        if (response.extendedIngredients != null) {
            for (int i = 0; i < response.extendedIngredients.size(); i++) {
                String amount = String.valueOf(response.extendedIngredients.get(i).amount);
                String units = response.extendedIngredients.get(i).unit;
                String ingredientName = response.extendedIngredients.get(i).name;
                String ingredientImage = response.extendedIngredients.get(i).image;
                ingredients.add(amount + " " + units + " " + ingredientName);
                parsedIngredients.add(ingredientName);
                ingredientsImages.add(ingredientImage);
            }
            if (response.analyzedInstructions.size() > 0) {
                for (int i = 0; i < response.analyzedInstructions.get(0).steps.size(); i++) {
                    instructions.add(response.analyzedInstructions.get(0).steps.get(i).step);
                }
            }

            recipeData.put("recipeName", response.title);

            recipeData.put("ingredients", ingredients);
            recipeData.put("parsedIngredients", parsedIngredients);
            recipeData.put("ingredientsImages", ingredientsImages);
            recipeData.put("instructions", instructions);
            recipeData.put("readyInMinutes", response.readyInMinutes);
            recipeData.put("image", response.image);
            recipeData.put("fromMyRecipes", fromMyRecipes);
            recipeData.put("preparationMinutes", -1L);
            recipeData.put("cookingMinutes", -1L);
            recipeData.put("servings", response.servings);
            recipeData.put("id", response.id);

            favoritedDocRef = db.collection("users/" + user + "/categories/folders/Favorites").document(String.valueOf(response.id));
            favoritedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            recipeIsFavorited = true;
                            imageView_favorited_main.setImageResource(R.drawable.ic_baseline_favorite_24);
                            Log.d("favorited", "Document exists!");
                        } else {
                            recipeIsFavorited = false;
                            Log.d("not favorited", "Document does not exist!");
                            imageView_favorited_main.setImageResource(R.drawable.ic_outline_favorite_border_24);
                        }
                    } else {
                        Log.d("TAG", "Failed with: ", task.getException());
                    }
                }
            });
        }
    }
    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            textView_meal_name.setText(response.title);
            textView_meal_source.setText(response.sourceName);
            textView_meal_servings.setText(String.valueOf(response.servings));
            Picasso.get().load(response.image).into(imageView_meal_image);
            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
            if (response != null) {
                createRecipeDataForFavorites(response, false);


            }
        }
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetailsActivity.this, response, recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id, boolean fromSpoonacular, String folderName) {
            Intent intent = new Intent(RecipeDetailsActivity.this, RecipeDetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("id", id);
            extras.putBoolean("fromSpoonacular", fromSpoonacular);
            intent.putExtras(extras);
            startActivity(intent);

        }
    };

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<AnalyzedInstruction> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {

        }
    };
}