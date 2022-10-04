package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.ParseIngredientsResponse;

import java.util.List;

public interface ParseIngredientsListener {
    void didFetch(List<ParseIngredientsResponse> response, String message);
    void didError(String message);
}
