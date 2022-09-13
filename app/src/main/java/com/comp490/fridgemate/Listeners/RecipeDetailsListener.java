package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);
    void didError(String message);
}
