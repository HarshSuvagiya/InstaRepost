package com.jarvis.instarepost.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.models.HelpModel;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class HelpAdapter extends Adapter<HelpAdapter.MyViewHolder> {
    private ArrayList<HelpModel> helpModels;

    class MyViewHolder extends ViewHolder {
        ImageView E;
        TextView F;
        TextView G;

        MyViewHolder(View view) {
            super(view);
            this.E = (ImageView) view.findViewById(R.id.imgHelp);
            this.F = (TextView) view.findViewById(R.id.txtHelp);
            this.G = (TextView) view.findViewById(R.id.txtStep);
        }
    }

    public HelpAdapter(ArrayList<HelpModel> arrayList) {
        this.helpModels = arrayList;
    }

    public int getItemCount() {
        return this.helpModels.size();
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        HelpModel helpModel = (HelpModel) this.helpModels.get(i);
        myViewHolder.E.setImageResource(helpModel.getImage());
        myViewHolder.F.setText(helpModel.getHelp());
        myViewHolder.G.setText(String.valueOf(i + 1));
    }

    @NotNull
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_help, viewGroup, false));
    }
}
