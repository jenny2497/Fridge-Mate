package com.comp490.fridgemate;

import android.content.Context;

import com.comp490.fridgemate.Listeners.AutocompleteIngredientsListener;
import com.comp490.fridgemate.Listeners.InstructionsListener;
import com.comp490.fridgemate.Listeners.RandomRecipeResponseListener;
import com.comp490.fridgemate.Listeners.RecipeDetailsListener;
import com.comp490.fridgemate.Listeners.RecipeFromIngredientsListener;
import com.comp490.fridgemate.Listeners.SimilarRecipesListener;
import com.comp490.fridgemate.Models.AutocompleteIngredientsResponse;
import com.comp490.fridgemate.Models.InstructionsResponse;
import com.comp490.fridgemate.Models.RandomRecipeApiResponse;
import com.comp490.fridgemate.Models.RecipeDetailsResponse;
import com.comp490.fridgemate.Models.RecipeFromIngredientsResponse;
import com.comp490.fridgemate.Models.SimilarRecipeResponse;

import java.io.IOError;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public RequestManager(Context context) {
        this.context = context;

    }

    public void getRandomRecipes(RandomRecipeResponseListener listener, List<String> tags) {
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeApiResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key), "10", tags);
        call.enqueue(new Callback<RandomRecipeApiResponse>() {
            @Override
            public void onResponse(Call<RandomRecipeApiResponse> call, Response<RandomRecipeApiResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRecipeApiResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }


        });
    }

    public void getRecipeDetails(RecipeDetailsListener listener, int id) {
        CallRecipeDetails callRecipeDetails = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getSimilarRecipes(SimilarRecipesListener listener, int id) {
        CallSimilarRecipes callSimilarRecipes = retrofit.create(CallSimilarRecipes.class);
        Call<List<SimilarRecipeResponse>> call = callSimilarRecipes.callSimilarRecipe(id, "4", context.getString(R.string.api_key));
        call.enqueue(new Callback<List<SimilarRecipeResponse>>() {
            @Override
            public void onResponse(Call<List<SimilarRecipeResponse>> call, Response<List<SimilarRecipeResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<SimilarRecipeResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getInstructions(InstructionsListener listener, int id){
        CallInstructions callInstructions = retrofit.create(CallInstructions.class);
        Call<List<InstructionsResponse>> call = callInstructions.callInstructions(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsResponse>> call, Response<List<InstructionsResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getAutoCompleteIngredients(AutocompleteIngredientsListener listener, String query) {
        CallAutocompleteIngredients callAutocompleteIngredients = retrofit.create(CallAutocompleteIngredients.class);
        Call<List<AutocompleteIngredientsResponse>> call = callAutocompleteIngredients.callAutocompleteIngredients(query, "10", context.getString(R.string.api_key));
        call.enqueue(new Callback<List<AutocompleteIngredientsResponse>>() {
            @Override
            public void onResponse(Call<List<AutocompleteIngredientsResponse>> call, Response<List<AutocompleteIngredientsResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<AutocompleteIngredientsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());

            }
        });
    }

    public void getRecipeFromIngredients(RecipeFromIngredientsListener listener, List<String> query) {
        CallRecipeFromIngredients callRecipeFromIngredients = retrofit.create(CallRecipeFromIngredients.class);
        String ingredientsList = "";
        for (int i = 0; i <query.size(); i++) {
            ingredientsList += query.get(i);
            if (i != query.size() - 1) {
                ingredientsList += ",";
            }
        }
        Call<List<RecipeFromIngredientsResponse>> call = callRecipeFromIngredients.callRecipeFromIngredients(context.getString(R.string.api_key), ingredientsList,"2","true");
        call.enqueue(new Callback<List<RecipeFromIngredientsResponse>>() {
            @Override
            public void onResponse(Call<List<RecipeFromIngredientsResponse>> call, Response<List<RecipeFromIngredientsResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<RecipeFromIngredientsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());

            }
        });
    }
    private interface CallRandomRecipes {
        @GET("recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );
    }

    private interface CallRecipeDetails {
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
            @Path("id") int id,
            @Query("apiKey") String apiKey
        );
    }

    private interface CallSimilarRecipes{
        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipeResponse>> callSimilarRecipe(
               @Path("id") int id,
               @Query("number") String number,
               @Query("apiKey") String apiKey
        );
    }
    private interface CallInstructions {
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>> callInstructions(
          @Path("id") int id,
          @Query("apiKey") String apiKey
        );
    }

    private interface CallAutocompleteIngredients {
        @GET("food/ingredients/autocomplete")
        Call<List<AutocompleteIngredientsResponse>> callAutocompleteIngredients(
                @Query("query") String query,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }

    private interface CallRecipeFromIngredients {
        @GET("recipes/findByIngredients")
        Call<List<RecipeFromIngredientsResponse>> callRecipeFromIngredients(
                @Query("apiKey") String apiKey,
                @Query("ingredients") String ingredients,
                @Query("ranking") String ranking,
                @Query("ignorePantry") String ignorePantry


                );
    }
}
