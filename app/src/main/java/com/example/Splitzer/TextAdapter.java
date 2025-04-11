package com.example.Splitzer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder> {

    private final List<String> lines;

    public TextAdapter(List<String> lines) {
        this.lines = lines;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView lineText;
        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            lineText = itemView.findViewById(R.id.textLine);
        }
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.lineText.setText(lines.get(position));
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }
}
