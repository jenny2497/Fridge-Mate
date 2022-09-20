package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.AutocompleteIngredientsResponse;

import java.util.List;

public interface AutocompleteIngredientsListener {
    void didFetch(List<AutocompleteIngredientsResponse> response, String message);
    void didError(String message);
}
