package com.arabdevelopers.shamelapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_sub_department.SubDepartmentActivity;
import com.arabdevelopers.shamelapp.databinding.SubDeptRowBinding;
import com.arabdevelopers.shamelapp.models.DepartmentModel;

import java.util.List;

public class SubDepartmentAdapter extends RecyclerView.Adapter<SubDepartmentAdapter.Holder> {
    private List<DepartmentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private SubDepartmentActivity activity;
    public SubDepartmentAdapter(List<DepartmentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (SubDepartmentActivity) context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SubDeptRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.sub_dept_row,parent,false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DepartmentModel model = list.get(position);
        holder.binding.setModel(model);
        holder.itemView.setOnClickListener(view -> {
            DepartmentModel departmentModel = list.get(holder.getAdapterPosition());
            activity.setItemData(departmentModel);

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private SubDeptRowBinding binding;
        public Holder(@NonNull SubDeptRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
