package com.comp490.fridgemate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeFromIngredientsAdapter extends RecyclerView.Adapter<RecipeFromIngredientsViewHolder> {
    Context context;
    List<RecipeFromIngredientsResponse> list;
    RecipeClickListener listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    String user = currentFirebaseUser.getUid();
    DocumentReference groceryDocRef = db.collection("users/" + user + "/categories").document("grocery");


    public RecipeFromIngredientsAdapter(Context context, List<RecipeFromIngredientsResponse> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeFromIngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeFromIngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_recipe_from_ingredients, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFromIngredientsViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        holder.textView_missingIngredients.setText(String.valueOf(list.get(position).missedIngredientCount));
//        holder.textView_servings.setText(list.get(position).servings+ " Servings");
//        holder.textView_time.setText(list.get(position).readyInMinutes + " Minutes");
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);

        holder.recipe_from_ingredients_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id), true, "");
            }
        });
        List<String> missingIngredientsNames = new ArrayList<>();
        List<String> missingIngredientImages = new ArrayList<>();
        for (int i=0; i<list.get(position).missedIngredients.size(); i++) {
            missingIngredientsNames.add(list.get(position).missedIngredients.get(i).name);
            missingIngredientImages.add(list.get(position).missedIngredients.get(i).image.substring(48));
        }
        if (missingIngredientsNames.size() == 0) {
            missingIngredientsNames.add("No missing ingredients!");
        }
        ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<String>(context, R.layout.missing_ingredient_item, R.id.missing_ingredient, missingIngredientsNames) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View inflatedView = super.getView(position, convertView, parent);
                Button addToGroceries = inflatedView.findViewById(R.id.add_to_groceries);
                if (missingIngredientsNames.get(position).equals("No missing ingredients!")) {
                    addToGroceries.setVisibility(View.INVISIBLE);
                } else {
                    ImageView ingredientImage = inflatedView.findViewById(R.id.imageView_ingredient_grocery_image);
                    Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+ missingIngredientImages.get(position)).into(ingredientImage);

                    addToGroceries.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            groceryDocRef.update("grocery", FieldValue.arrayUnion(missingIngredientsNames.get(position)));
                            groceryDocRef.update("groceryImages", FieldValue.arrayUnion(missingIngredientImages.get(position)));
                            Toast.makeText(getContext(), "Added Ingredient to Grocery List", Toast.LENGTH_LONG).show();

                        }
                    });
                }

                return inflatedView;
            }
        };

        holder.listView_missing_ingredients.setAdapter(ingredientsAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class RecipeFromIngredientsViewHolder extends RecyclerView.ViewHolder {
    CardView recipe_from_ingredients_container;
    TextView textView_title, textView_missingIngredients;
    ImageView imageView_food;
    ListView listView_missing_ingredients;

    public RecipeFromIngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        recipe_from_ingredients_container = itemView.findViewById(R.id.recipe_from_ingredients_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_missingIngredients = itemView.findViewById(R.id.textView_missingIngredients);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        listView_missing_ingredients = itemView.findViewById(R.id.listView_missing_ingredients);


    }
}
