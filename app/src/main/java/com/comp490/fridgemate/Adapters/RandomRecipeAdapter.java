package com.comp490.fridgemate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.R;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder> {
    Context context;
    List<Recipe> list;
    RecipeClickListener listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser;
    String user;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    boolean inFavoritesFolder;
    boolean inMyRecipes;




    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener, boolean inFavoritesFolder, boolean inMyRecipes) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.inFavoritesFolder = inFavoritesFolder;
        this.inMyRecipes = inMyRecipes;
    }

    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        holder.textView_servings.setText(list.get(position).servings+ " Servings");
        holder.textView_time.setText(list.get(position).readyInMinutes + " Minutes");
        holder.recipeFromSpoonacular = !list.get(position).fromMyRecipes;
        Log.d(list.get(position).title, String.valueOf(holder.recipeFromSpoonacular));

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();
        holder.recipeDocRef = db.collection("users/" + user + "/categories/folders/Favorites").document(String.valueOf(list.get(position).id));
        holder.recipeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.recipeIsFavorited = true;
                        holder.imageView_favorited.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Log.d("favorited", "Document exists!");
                    } else {
                        holder.recipeIsFavorited = false;
                        Log.d("not favorited", "Document does not exist!");
                        holder.imageView_favorited.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    }
                } else {
                    Log.d("TAG", "Failed with: ", task.getException());
                }
            }
        });

        holder.imageView_favorited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.recipeIsFavorited) {
                    holder.recipeDocRef.delete();
                    holder.imageView_favorited.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    if (inFavoritesFolder) {
                       list.remove(holder.getAdapterPosition());
                       notifyDataSetChanged();
                    }

                } else {
                    addRecipeToDatabase(holder, holder.getAdapterPosition());
                    holder.imageView_favorited.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                holder.recipeIsFavorited = !holder.recipeIsFavorited;
            }
        });
        Log.d("recipe is from spoon", String.valueOf(holder.recipeFromSpoonacular));
        if (holder.recipeFromSpoonacular) {
            Picasso.get().load(list.get(position).image).into(holder.imageView_food);
        } else {
            StorageReference imgRef = storage.getReference();
            StorageReference imgRefWithPath = imgRef.child("users/" + user + "/categories/folders/MyRecipes/" + list.get(position).id);
            Log.d("let's see if we", String.valueOf(list.get(position).id));
            imgRefWithPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.imageView_food);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.d("for some reason", "won't display");
                }
            });
            //load picture from cloud storage
        }

        holder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String folderName;
            if (inFavoritesFolder) {
                folderName = "Favorites";
            } else if (inMyRecipes) {
                folderName = "MyRecipes";
            } else {
                folderName = "";
            }
            listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id), holder.recipeFromSpoonacular, folderName);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addRecipeToDatabase(RandomRecipeViewHolder holder, int position) {
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("recipeName", list.get(position).title);
        ArrayList ingredients = new ArrayList();
        ArrayList parsedIngredients = new ArrayList();
        ArrayList instructions = new ArrayList();
        ArrayList ingredientsImages = new ArrayList();
        if (list.get(position) != null) {
            for (int i=0; i < list.get(position).extendedIngredients.size(); i++) {
                String amount = String.valueOf(list.get(position).extendedIngredients.get(i).amount);
                if (amount == "0.0") {
                    amount = "";
                }
                String units = list.get(position).extendedIngredients.get(i).unit;
                if (units == null) {
                    units = "";
                }
                String ingredientName = list.get(position).extendedIngredients.get(i).name;
                ingredients.add(amount + " " + units + " " + ingredientName);
                parsedIngredients.add(ingredientName);
                ingredientsImages.add(list.get(position).extendedIngredients.get(i).image);
            }
            for (int i=0; i< list.get(position).analyzedInstructions.get(0).steps.size(); i++) {
                instructions.add(list.get(position).analyzedInstructions.get(0).steps.get(i).step);
            }

            recipeData.put("ingredients", ingredients);
            recipeData.put("parsedIngredients", parsedIngredients);
            recipeData.put("ingredientsImages", ingredientsImages);
            recipeData.put("instructions", instructions);
            recipeData.put("readyInMinutes", list.get(position).readyInMinutes);
            recipeData.put("preparationMinutes", list.get(position).preparationMinutes);
            recipeData.put("cookingMinutes", list.get(position).cookingMinutes);
            recipeData.put("image", list.get(position).image);
            recipeData.put("fromMyRecipes", new Boolean(!holder.recipeFromSpoonacular));
            recipeData.put("servings", list.get(position).servings);
            recipeData.put("id", list.get(position).id);
            holder.recipeDocRef.set(recipeData);
        }

    }

}
class RandomRecipeViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title, textView_servings, textView_time;
    ImageView imageView_food, imageView_favorited;
    DocumentReference recipeDocRef;
    boolean recipeIsFavorited;
    boolean recipeFromSpoonacular;


    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_time = itemView.findViewById(R.id.textView_time);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        imageView_favorited = itemView.findViewById(R.id.imageView_favorited_search);


    }

}
