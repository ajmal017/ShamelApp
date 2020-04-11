package com.arabdevelopers.shamelapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_Main;
import com.arabdevelopers.shamelapp.databinding.MainDeptRowBinding;
import com.arabdevelopers.shamelapp.models.DepartmentModel;

import java.util.List;

public class MainDepartmentAdapter extends RecyclerView.Adapter<MainDepartmentAdapter.Holder> {
    private List<DepartmentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private int [] background = {R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4,R.drawable.icon5,R.drawable.icon6};
    private Fragment_Main fragment_main;
    public MainDepartmentAdapter(List<DepartmentModel> list, Context context, Fragment_Main fragment_main) {
        this.list = list;
        this.context = context;
        this.fragment_main = fragment_main;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainDeptRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_dept_row,parent,false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DepartmentModel model = list.get(position);
        holder.binding.setModel(model);
        int pos = position%background.length;
        holder.binding.ll.setBackgroundResource(background[pos]);
        holder.itemView.setOnClickListener(view -> {

            DepartmentModel departmentModel = list.get(holder.getAdapterPosition());
            fragment_main.setItemData(departmentModel);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private MainDeptRowBinding binding;
        public Holder(@NonNull MainDeptRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
