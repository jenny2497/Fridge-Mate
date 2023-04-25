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
import com.comp490.fridgemate.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ListMealIngredientsBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView imageViewIngredients;

  @NonNull
  public final TextView textViewIngredientsQuantity;

  private ListMealIngredientsBinding(@NonNull CardView rootView,
      @NonNull ImageView imageViewIngredients, @NonNull TextView textViewIngredientsQuantity) {
    this.rootView = rootView;
    this.imageViewIngredients = imageViewIngredients;
    this.textViewIngredientsQuantity = textViewIngredientsQuantity;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ListMealIngredientsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListMealIngredientsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_meal_ingredients, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListMealIngredientsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imageView_ingredients;
      ImageView imageViewIngredients = ViewBindings.findChildViewById(rootView, id);
      if (imageViewIngredients == null) {
        break missingId;
      }

      id = R.id.textView_ingredients_quantity;
      TextView textViewIngredientsQuantity = ViewBindings.findChildViewById(rootView, id);
      if (textViewIngredientsQuantity == null) {
        break missingId;
      }

      return new ListMealIngredientsBinding((CardView) rootView, imageViewIngredients,
          textViewIngredientsQuantity);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
