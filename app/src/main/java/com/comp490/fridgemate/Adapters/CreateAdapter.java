package com.comp490.fridgemate.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.comp490.fridgemate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAdapter extends RecyclerView.Adapter<CreateViewHolder> {
    Context context;
    public List<String> listToSave;
    boolean ingredients;



    public CreateAdapter(Context context, List<String> list, boolean ingredients) { //this is either an ingredient or an instruction, either way it is a string
        this.context = context;
        this.listToSave = list;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public CreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreateViewHolder(LayoutInflater.from(context).inflate(R.layout.create_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreateViewHolder holder, int position) {
        if (ingredients) {
            holder.editText_item.setHint("Add Ingredient and Amount");
        } else {
            holder.editText_item.setHint("Add Instruction");
        }

        holder.editText_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                listToSave.set(holder.getAdapterPosition(), editable.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listToSave.size();
    }


}
class CreateViewHolder extends RecyclerView.ViewHolder {
    EditText editText_item;

    public CreateViewHolder(@NonNull View itemView) {
        super(itemView);
        editText_item = itemView.findViewById(R.id.editText_item);
    }

}
