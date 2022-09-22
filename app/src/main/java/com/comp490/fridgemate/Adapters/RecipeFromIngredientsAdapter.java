package com.comp490.fridgemate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Listeners.RecipeClickListener;
import com.comp490.fridgemate.Models.Recipe;
import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeFromIngredientsAdapter extends RecyclerView.Adapter<RecipeFromIngredientsViewHolder> {
    Context context;
    List<RecipeFromIngredientsResponse> list;
    RecipeClickListener listener;

    public RecipeFromIngredientsAdapter(Context context, List<RecipeFromIngredientsResponse> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeFromIngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeFromIngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFromIngredientsViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
//        holder.textView_servings.setText(list.get(position).servings+ " Servings");
//        holder.textView_time.setText(list.get(position).readyInMinutes + " Minutes");
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);

        holder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class RecipeFromIngredientsViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title, textView_servings, textView_time;
    ImageView imageView_food;

    public RecipeFromIngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        textView_servings = itemView.findViewById(R.id.textView_servings);
        textView_time = itemView.findViewById(R.id.textView_time);
        imageView_food = itemView.findViewById(R.id.imageView_food);


    }
}