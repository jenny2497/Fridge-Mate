package com.comp490.fridgemate.Listeners;

import com.comp490.fridgemate.Models.AnalyzedInstruction;

import java.util.List;

public interface InstructionsListener {
    void didFetch(List<AnalyzedInstruction> response, String message);
    void didError(String message);
}
