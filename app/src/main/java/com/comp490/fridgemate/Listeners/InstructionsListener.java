package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.InstructionsResponse;

import java.util.List;

public interface InstructionsListener {
    void didFetch(List<InstructionsResponse> response, String message);
    void didError(String message);
}
