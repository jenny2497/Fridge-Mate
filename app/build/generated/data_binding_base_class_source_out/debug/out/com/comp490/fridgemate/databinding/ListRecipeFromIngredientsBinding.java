// Generated by view binder compiler. Do not edit!
package com.comp490.fridgemate.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.comp490.fridgemate.MyListView;
import com.comp490.fridgemate.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ListRecipeFromIngredientsBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView imageViewFood;

  @NonNull
  public final MyListView listViewMissingIngredients;

  @NonNull
  public final CardView recipeFromIngredientsContainer;

  @NonNull
  public final TextView textViewMissingIngredients;

  @NonNull
  public final TextView textViewTitle;

  private ListRecipeFromIngredientsBinding(@NonNull CardView rootView,
      @NonNull ImageView imageViewFood, @NonNull MyListView listViewMissingIngredients,
      @NonNull CardView recipeFromIngredientsContainer,
      @NonNull TextView textViewMissingIngredients, @NonNull TextView textViewTitle) {
    this.rootView = rootView;
    this.imageViewFood = imageViewFood;
    this.listViewMissingIngredients = listViewMissingIngredients;
    this.recipeFromIngredientsContainer = recipeFromIngredientsContainer;
    this.textViewMissingIngredients = textViewMissingIngredients;
    this.textViewTitle = textViewTitle;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ListRecipeFromIngredientsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListRecipeFromIngredientsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_recipe_from_ingredients, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListRecipeFromIngredientsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imageView_food;
      ImageView imageViewFood = ViewBindings.findChildViewById(rootView, id);
      if (imageViewFood == null) {
        break missingId;
      }

      id = R.id.listView_missing_ingredients;
      MyListView listViewMissingIngredients = ViewBindings.findChildViewById(rootView, id);
      if (listViewMissingIngredients == null) {
        break missingId;
      }

      CardView recipeFromIngredientsContainer = (CardView) rootView;

      id = R.id.textView_missingIngredients;
      TextView textViewMissingIngredients = ViewBindings.findChildViewById(rootView, id);
      if (textViewMissingIngredients == null) {
        break missingId;
      }

      id = R.id.textView_title;
      TextView textViewTitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewTitle == null) {
        break missingId;
      }

      return new ListRecipeFromIngredientsBinding((CardView) rootView, imageViewFood,
          listViewMissingIngredients, recipeFromIngredientsContainer, textViewMissingIngredients,
          textViewTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
