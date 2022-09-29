package com.comp490.fridgemate.ui.fridge;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp490.fridgemate.Listeners.AutocompleteIngredientsListener;
import com.comp490.fridgemate.Models.AutocompleteIngredientsResponse;
import com.comp490.fridgemate.R;
import com.comp490.fridgemate.RequestManager;
import com.comp490.fridgemate.databinding.FragmentFridgeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
    ListView fridgeIngredients;
    ArrayAdapter<String> fridgeIngredientsAdapter;

    List<String> apiFoods = new ArrayList<>();
    boolean needToCreateFridge;

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
                    String selection = (String)parent.getItemAtPosition(position);
                    fridgeItems.add(selection);
                    fridgeIngredientsAdapter.notifyDataSetChanged();
                    if (needToCreateFridge) {
                        Map<String, Object> fridgeData = new HashMap<>();
                        fridgeData.put("fridge", Arrays.asList(selection));
                        fridgeDocRef.set(fridgeData);
                        needToCreateFridge = false;
                    } else {
                        fridgeDocRef.update("fridge", FieldValue.arrayUnion(selection));
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

        fetchFromDatabase();



        return root;
    }

    private void fetchFromDatabase() {

        fridgeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fridgeItems = (ArrayList<String>) document.getData().get("fridge");
                        Log.d("TAG", "DocumentSnapshot data: " + fridgeItems);
                        fridgeIngredientsAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.fridge_ingredient_item, R.id.fridge_ingredient, fridgeItems) {
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                View inflatedView = super.getView(position, convertView, parent);
                                Button deleteIngredientButton = inflatedView.findViewById(R.id.delete_ingredient);
                                deleteIngredientButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("TAG", "item clicked " + fridgeItems.get(position));
                                        fridgeDocRef.update("fridge", FieldValue.arrayRemove(fridgeItems.get(position)));
                                        fridgeItems.remove(position);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}