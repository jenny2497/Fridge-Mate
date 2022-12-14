package com.comp490.fridgemate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Adapters.CreateAdapter;
import com.comp490.fridgemate.Adapters.IngredientsAdapter;
import com.comp490.fridgemate.Adapters.InstructionsAdapter;
import com.comp490.fridgemate.Listeners.ParseIngredientsListener;
import com.comp490.fridgemate.Models.AnalyzedInstruction;
import com.comp490.fridgemate.Models.ExtendedIngredient;
import com.comp490.fridgemate.Models.ParseIngredientsResponse;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.Models.Step;
import com.comp490.fridgemate.ui.bookMark.BookMarkFragment;
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
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser currentFirebaseUser;
    String user;
    StorageReference storageReference;
    ActivityResultLauncher<String> activityResultLauncher;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe);
        findViews();
        storageReference = storage.getReference();
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

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();

        Intent intent = getIntent();
        id = intent.getIntExtra("recipeId", 5);
        if (id != 5) {
            //we are editing a recipe we already added
            Log.d(String.valueOf(id), "recipe");
            DocumentReference recipeDocRef = db.collection("users/" + user + "/categories/folders/MyRecipes").document(String.valueOf(id));
            recipeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

//                            Recipe newRecipe = new Recipe();
                            Log.d("doc", document.getId() + " => " + document.getData());
                            try {
                                editText_prep_time.setText(String.valueOf((int) (long) document.getData().get("preparationMinutes")));
                                editText_meal_servings.setText(String.valueOf((int) (long) document.getData().get("servings")));
                                editText_meal_name.setText((String) document.getData().get("recipeName"));
                                editText_cook_time.setText(String.valueOf((int) (long) document.getData().get("cookingMinutes")));


                                createAdapterIngredients.listToSave = (List<String>) document.getData().get("ingredients");
                                createAdapterIngredients.notifyDataSetChanged();

                                createAdapterInstructions.listToSave = (List<String>) document.getData().get("instructions");
                                createAdapterInstructions.notifyDataSetChanged();


                            } catch (Exception e){

                            }





                            StorageReference imgRef = storage.getReference();
                            StorageReference imgRefWithPath = imgRef.child("users/" + user + "/categories/folders/MyRecipes/" + id);
                            imgRefWithPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(imageView_create_meal_image);
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
        add_ingredient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAdapterIngredients.listToSave.add("");
                createAdapterIngredients.notifyDataSetChanged();
            }
        });
        add_instruction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAdapterInstructions.listToSave.add("");
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
        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
        if (result != null) {
            try {
                imageUri = result;
                imageView_create_meal_image.setImageURI(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            }
        });
        imageView_create_meal_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestImage();
            }
        });

    }

    private void requestImage() {

        activityResultLauncher.launch("image/*");
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
            String errorMessage = "Please make sure all sections are filled out.";
            List<String> parsedIngredients = new ArrayList();
            List<String> ingredientsImages = new ArrayList();
            String cookTime = editText_cook_time.getText().toString();
            Long cookTimeLong = -1L;
            Long prepTimeLong = -1L;
            boolean startIntent = true;
            String servingsString = editText_meal_servings.getText().toString();
            Long servings = -1L;
            try {
                cookTimeLong = Long.parseLong(cookTime);
                String prepTime = editText_prep_time.getText().toString();
                prepTimeLong = Long.parseLong(prepTime);
                servings = Long.parseLong(servingsString);
            } catch (Exception e) { // if nothing was entered or it cannot be converted to a long value
                startIntent = false;
                errorMessage = "Please enter cook time, prep time, and number of servings as integers.";
                //todo: make toast ordering person to add value to cooktime and preptime
            }

            for (int i = 0; i <response.size();i++) {
                parsedIngredients.add(response.get(i).name);
                ingredientsImages.add(response.get(i).image);
            }
            Long readyInMinutes = -1L;
            try {
                readyInMinutes = cookTimeLong + prepTimeLong;
            } catch (Exception E) {
                startIntent = false;
                //todo: make toast that displays that units were entered for cook or prep time
            }
            Map<String, Object> recipeData = new HashMap<>();
            String recipeName = editText_meal_name.getText().toString();
            if (recipeName.equals("") || cookTimeLong==-1L || prepTimeLong == -1L || servings == -1L ||
                    createAdapterIngredients.listToSave.get(0) == "" || createAdapterInstructions.listToSave.get(0) == "") {
                startIntent = false;

            } else {
                recipeData.put("recipeName", recipeName);
                recipeData.put("ingredients", createAdapterIngredients.listToSave);
                recipeData.put("parsedIngredients", parsedIngredients);
                recipeData.put("ingredientsImages", ingredientsImages);
                recipeData.put("instructions", createAdapterInstructions.listToSave);
                recipeData.put("readyInMinutes", readyInMinutes);
                recipeData.put("preparationMinutes", prepTimeLong);
                recipeData.put("cookingMinutes", cookTimeLong);
                recipeData.put("image", "adding later this is a todo");
                recipeData.put("fromMyRecipes", new Boolean(true));
                recipeData.put("servings", servings);
                recipeData.put("id", recipeName.hashCode());

                recipeDocRef = db.collection("users/" + user + "/categories/folders/MyRecipes").document(String.valueOf(recipeName.hashCode()));
                recipeDocRef.set(recipeData);
                saveImageInFirebase(String.valueOf(recipeName.hashCode()));

            }

            if (startIntent) {
                startActivity(new Intent(CreateRecipeActivity.this, MainActivity.class));
            } else {
                Toast.makeText(CreateRecipeActivity.this, errorMessage,
                        Toast.LENGTH_LONG).show();
            }



            //todo: add check to make sure we're not replacing a recipe that already exists
        }

        @Override
        public void didError(String message) {
            Log.d("tag", "couldn't fetch for whatever reason stuck here forever");
            Log.d("sad", message);
        }
    };
    private void saveImageInFirebase(String recipeId) {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait...");
            progressDialog.show();
            StorageReference reference = storageReference.child("users/" + user + "/categories/folders/MyRecipes/" + recipeId);
            try {
                reference.putFile(imageUri);
//                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                                progressDialog.setMessage("Saved" + (int) progress + "%");
//
//                            }
//                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
