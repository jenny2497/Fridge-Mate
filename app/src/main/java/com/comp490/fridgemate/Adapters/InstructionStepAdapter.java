package com.comp490.fridgemate.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp490.fridgemate.Models.Step;
import com.comp490.fridgemate.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder> {
    Context context;
    List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {
        holder.textView_instructions_step_number.setText(String.valueOf(list.get(position).number));
        holder.textView_instructions_step_title.setText(list.get(position).step);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionStepViewHolder extends RecyclerView.ViewHolder {
    TextView textView_instructions_step_number, textView_instructions_step_title;
    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instructions_step_number = itemView.findViewById(R.id.textView_instructions_step_number);
        textView_instructions_step_title = itemView.findViewById(R.id.textView_instructions_step_title);
    }
}
