package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;

import java.util.List;

public interface RecipeFromIngredientsListener {
    void didFetch(List<RecipeFromIngredientsResponse> response, String message);
    void didError(String message);
}
