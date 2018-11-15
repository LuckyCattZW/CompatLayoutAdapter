package catt.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import catt.compat.layout.app.CompatLayoutActivity;
import org.jetbrains.annotations.Nullable;

public class MainActivityJava extends CompatLayoutActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        RecyclerView recyclerView = findViewById(R.id.MyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter());

    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private int[] array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final TextView viewById = holder.itemView.findViewById(R.id.MyItemTextView);
            viewById.setText("" + position);
            viewById.setTextColor(Color.WHITE);
            viewById.setBackgroundColor(Color.parseColor("#333333"));
        }

        @Override
        public int getItemCount() {
            return array.length;
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
