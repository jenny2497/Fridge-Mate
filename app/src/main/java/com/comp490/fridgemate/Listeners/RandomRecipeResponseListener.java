package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiResponse response, String message);
    void didError(String message);
}
