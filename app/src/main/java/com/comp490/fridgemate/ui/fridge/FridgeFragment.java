package com.comp490.fridgemate.ui.fridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp490.fridgemate.CreateRecipeActivity;
import com.comp490.fridgemate.Listeners.AutocompleteIngredientsListener;
import com.comp490.fridgemate.Models.AutocompleteIngredientsResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.Text_Recognition;
import com.comp490.fridgemate.databinding.FragmentFridgeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FridgeFragment extends Fragment {

    private FragmentFridgeBinding binding;
    ProgressDialog dialog;

    RequestManager manager;
    ArrayAdapter<String> autoFillAdapter;
    AutoCompleteTextView addFridgeItem;
    View root;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser;
    DocumentReference fridgeDocRef;
    String user;
    ArrayList<String> fridgeItems = new ArrayList<>();
    ArrayList<String> fridgeImages = new ArrayList<>();
    ListView fridgeIngredients;
    ArrayAdapter<String> fridgeIngredientsAdapter;

    List<String> apiFoods = new ArrayList<>();
    boolean needToCreateFridge;


    ArrayAdapter<String> autoFillAdapterGroceries;
    AutoCompleteTextView addGroceryItem;

    DocumentReference groceryDocRef;
    ArrayList<String> groceryItems = new ArrayList<>();
    ArrayList<String> groceryImages = new ArrayList<>();
    ListView groceryIngredients;
    ArrayAdapter<String> groceryIngredientsAdapter;

    List<String> apiGroceries = new ArrayList<>();
    boolean needToCreateGroceries;



    private final AutocompleteIngredientsListener autocompleteIngredientsListener = new AutocompleteIngredientsListener() {
        @Override
        public void didFetch(List<AutocompleteIngredientsResponse> response, String message) {
            apiFoods.clear();
            for (int i=0; i < response.size(); i++) {
                apiFoods.add(response.get(i).name);
            }
            autoFillAdapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_dropdown_item_1line, apiFoods);
            addFridgeItem.setAdapter(autoFillAdapter);

            addFridgeItem.showDropDown();


            addFridgeItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String image = response.get(position).image;
                    String selection = (String)parent.getItemAtPosition(position);
                    fridgeItems.add(selection);
                    fridgeImages.add(image);
                    fridgeIngredientsAdapter.notifyDataSetChanged();
                    if (needToCreateFridge) {
                        Map<String, Object> fridgeData = new HashMap<>();
                        fridgeData.put("fridge", Arrays.asList(selection));
                        fridgeData.put("fridgeImages", Arrays.asList(image));
                        fridgeDocRef.set(fridgeData);
                        needToCreateFridge = false;
                    } else {
                        fridgeDocRef.update("fridge", FieldValue.arrayUnion(selection));
                        fridgeDocRef.update("fridgeImages", FieldValue.arrayUnion(image));
                    }
                    Log.d("test", selection);
                    addFridgeItem.setText("");
                }
            });
        }

        @Override
        public void didError(String message) {
            Toast.makeText(root.getContext(), message, Toast.LENGTH_SHORT);
        }
    };
    private final AutocompleteIngredientsListener autocompleteIngredientsListenerGroceries = new AutocompleteIngredientsListener() {
        @Override
        public void didFetch(List<AutocompleteIngredientsResponse> response, String message) {
            apiGroceries.clear();
            for (int i=0; i < response.size(); i++) {
                apiGroceries.add(response.get(i).name);
            }
            autoFillAdapterGroceries = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_dropdown_item_1line, apiGroceries);
            addGroceryItem.setAdapter(autoFillAdapterGroceries);

            addGroceryItem.showDropDown();


            addGroceryItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String image = response.get(position).image;
                    String selection = (String)parent.getItemAtPosition(position);
                    groceryItems.add(selection);
                    groceryImages.add(image);
                    groceryIngredientsAdapter.notifyDataSetChanged();
                    if (needToCreateGroceries) {
                        Map<String, Object> groceryData = new HashMap<>();
                        groceryData.put("grocery", Arrays.asList(selection));
                        groceryData.put("groceryImages", Arrays.asList(image));
                        groceryDocRef.set(groceryData);
                        needToCreateGroceries = false;
                    } else {
                        groceryDocRef.update("grocery", FieldValue.arrayUnion(selection));
                        groceryDocRef.update("groceryImages", FieldValue.arrayUnion(image));
                    }
                    Log.d("test", selection);
                    addGroceryItem.setText("");
                }
            });
        }

        @Override
        public void didError(String message) {
            Toast.makeText(root.getContext(), message, Toast.LENGTH_SHORT);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentFridgeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        dialog = new ProgressDialog((getActivity()));
        dialog.setTitle("Loading...");
        dialog.show();
        manager = new RequestManager((root.getContext()));

        addFridgeItem = root.findViewById(R.id.add_fridge_item);

        autoFillAdapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, apiFoods);
        addFridgeItem.setAdapter(autoFillAdapter);
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

        fridgeIngredients = root.findViewById(R.id.listView_fridge_ingredients);
        //get data from firebase and add to fridgeItems
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();
        fridgeDocRef = db.collection("users/" + user + "/categories").document("fridge");

        fetchFromDatabaseFridge();

        addGroceryItem = root.findViewById(R.id.add_grocery_item);

        autoFillAdapterGroceries = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, apiGroceries);
        addGroceryItem.setAdapter(autoFillAdapterGroceries);
        addGroceryItem.addTextChangedListener(new TextWatcher() {
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
                manager.getAutoCompleteIngredients(autocompleteIngredientsListenerGroceries, text);
                Log.d("TAG", "foodsApi is " + apiGroceries);
            }
        });

        groceryIngredients = root.findViewById(R.id.listView_grocery_list);
        //get data from firebase and add to fridgeItems
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();
        groceryDocRef = db.collection("users/" + user + "/categories").document("grocery");

        fetchFromDatabaseGrocery();

        Button addRecipe = root.findViewById(R.id.add_item_TR);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Text_Recognition.class));
            }
        });





        return root;
    }

    private void fetchFromDatabaseFridge() {

        fridgeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fridgeItems = (ArrayList<String>) document.getData().get("fridge");
                        fridgeImages = (ArrayList<String>) document.getData().get("fridgeImages");
                        Log.d("TAG", "DocumentSnapshot data: " + fridgeItems);
                        fridgeIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, fridgeItems) {
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                View inflatedView = super.getView(position, convertView, parent);
                                Button deleteIngredientButton = inflatedView.findViewById(R.id.delete_ingredient);
                                ImageView ingredientImage = inflatedView.findViewById(R.id.imageView_ingredient_fridge_image);
                                Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+ fridgeImages.get(position)).into(ingredientImage);

                                deleteIngredientButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("TAG", "item clicked " + fridgeItems.get(position));
                                        fridgeDocRef.update("fridge", FieldValue.arrayRemove(fridgeItems.get(position)));
                                        fridgeDocRef.update("fridgeImages", FieldValue.arrayRemove(fridgeImages.get(position)));
                                        fridgeItems.remove(position);
                                        fridgeImages.remove(position);
                                        fridgeIngredientsAdapter.notifyDataSetChanged();

                                    }
                                });
                                return inflatedView;
                            };
                        };

                        fridgeIngredients.setAdapter(fridgeIngredientsAdapter);
                        needToCreateFridge = false;
                    } else {
                        Log.d("TAG", "No such document");
                        fridgeIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, fridgeItems);
                        fridgeIngredients.setAdapter(fridgeIngredientsAdapter);
                        needToCreateFridge = true;
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    fridgeIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, fridgeItems);
                    fridgeIngredients.setAdapter(fridgeIngredientsAdapter);
                    needToCreateFridge = true;
                }
            }
        });


    }
    private void fetchFromDatabaseGrocery() {

        groceryDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        groceryItems = (ArrayList<String>) document.getData().get("grocery");
                        groceryImages = (ArrayList<String>) document.getData().get("groceryImages");
                        Log.d("TAG", "DocumentSnapshot data: " + groceryItems);
                        groceryIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, groceryItems) {
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                View inflatedView = super.getView(position, convertView, parent);
                                Button deleteIngredientButton = inflatedView.findViewById(R.id.delete_ingredient);
                                ImageView ingredientImage = inflatedView.findViewById(R.id.imageView_ingredient_fridge_image);
                                Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+ groceryImages.get(position)).into(ingredientImage);

                                deleteIngredientButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("TAG", "item clicked " + groceryItems.get(position));
                                        groceryDocRef.update("grocery", FieldValue.arrayRemove(groceryItems.get(position)));
                                        groceryDocRef.update("groceryImages", FieldValue.arrayRemove(groceryImages.get(position)));
                                        groceryItems.remove(position);
                                        groceryImages.remove(position);
                                        groceryIngredientsAdapter.notifyDataSetChanged();

                                    }
                                });
                                return inflatedView;
                            };
                        };

                        groceryIngredients.setAdapter(groceryIngredientsAdapter);
                        needToCreateGroceries = false;
                    } else {
                        Log.d("TAG", "No such document");
                        groceryIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, groceryItems);
                        groceryIngredients.setAdapter(groceryIngredientsAdapter);
                        needToCreateGroceries = true;
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    groceryIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, groceryItems);
                    groceryIngredients.setAdapter(groceryIngredientsAdapter);
                    needToCreateGroceries = true;
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}