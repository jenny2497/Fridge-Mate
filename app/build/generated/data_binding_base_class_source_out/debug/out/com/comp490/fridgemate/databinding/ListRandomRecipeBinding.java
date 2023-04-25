// Generated by view binder compiler. Do not edit!
package com.comp490.fridgemate.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public final class ListRandomRecipeBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final LinearLayout favoritedLinearLayout;

  @NonNull
  public final ImageView imageViewFavoritedSearch;

  @NonNull
  public final ImageView imageViewFood;

  @NonNull
  public final CardView randomListContainer;

  @NonNull
  public final TextView textViewFavorited;

  @NonNull
  public final TextView textViewServings;

  @NonNull
  public final TextView textViewTime;

  @NonNull
  public final TextView textViewTitle;

  private ListRandomRecipeBinding(@NonNull CardView rootView,
      @NonNull LinearLayout favoritedLinearLayout, @NonNull ImageView imageViewFavoritedSearch,
      @NonNull ImageView imageViewFood, @NonNull CardView randomListContainer,
      @NonNull TextView textViewFavorited, @NonNull TextView textViewServings,
      @NonNull TextView textViewTime, @NonNull TextView textViewTitle) {
    this.rootView = rootView;
    this.favoritedLinearLayout = favoritedLinearLayout;
    this.imageViewFavoritedSearch = imageViewFavoritedSearch;
    this.imageViewFood = imageViewFood;
    this.randomListContainer = randomListContainer;
    this.textViewFavorited = textViewFavorited;
    this.textViewServings = textViewServings;
    this.textViewTime = textViewTime;
    this.textViewTitle = textViewTitle;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ListRandomRecipeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListRandomRecipeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_random_recipe, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListRandomRecipeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.favorited_linear_layout;
      LinearLayout favoritedLinearLayout = ViewBindings.findChildViewById(rootView, id);
      if (favoritedLinearLayout == null) {
        break missingId;
      }

      id = R.id.imageView_favorited_search;
      ImageView imageViewFavoritedSearch = ViewBindings.findChildViewById(rootView, id);
      if (imageViewFavoritedSearch == null) {
        break missingId;
      }

      id = R.id.imageView_food;
      ImageView imageViewFood = ViewBindings.findChildViewById(rootView, id);
      if (imageViewFood == null) {
        break missingId;
      }

      CardView randomListContainer = (CardView) rootView;

      id = R.id.textView_favorited;
      TextView textViewFavorited = ViewBindings.findChildViewById(rootView, id);
      if (textViewFavorited == null) {
        break missingId;
      }

      id = R.id.textView_servings;
      TextView textViewServings = ViewBindings.findChildViewById(rootView, id);
      if (textViewServings == null) {
        break missingId;
      }

      id = R.id.textView_time;
      TextView textViewTime = ViewBindings.findChildViewById(rootView, id);
      if (textViewTime == null) {
        break missingId;
      }

      id = R.id.textView_title;
      TextView textViewTitle = ViewBindings.findChildViewById(rootView, id);
      if (textViewTitle == null) {
        break missingId;
      }

      return new ListRandomRecipeBinding((CardView) rootView, favoritedLinearLayout,
          imageViewFavoritedSearch, imageViewFood, randomListContainer, textViewFavorited,
          textViewServings, textViewTime, textViewTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
