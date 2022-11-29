package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.Models.RecipeFromIngredientsRoot;

import java.util.List;

public interface RecipeFromIngredientsListener {
    void didFetch(RecipeFromIngredientsRoot response, String message);
    void didError(String message);
}
