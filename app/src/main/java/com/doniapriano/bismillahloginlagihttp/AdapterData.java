package com.doniapriano.bismillahloginlagihttp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData> {

    Context context;
    List<DataModel> listDataModel;

    public AdapterData(Context context, List<DataModel> listDataModel) {
        this.context = context;
        this.listDataModel = listDataModel;
    }

    @NonNull
    @Override
    public AdapterData.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view,parent,false);
        HolderData holderData = new HolderData(view);
        return holderData;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterData.HolderData holder, int position) {
        holder.tvName.setText(listDataModel.get(position).getName());
        holder.tvAuthors.setText(listDataModel.get(position).getAuthors());
    }

    @Override
    public int getItemCount() {
        return listDataModel.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView tvName, tvAuthors;
        public HolderData(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthors = itemView.findViewById(R.id.tv_authors);
        }
    }
}
