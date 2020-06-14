package com.arabdevelopers.shamelapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.databinding.SpinnerRowBinding;
import com.arabdevelopers.shamelapp.models.DepartmentModel;

import java.util.List;

public class SpinnerNationalityCityAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private LayoutInflater inflater;
    public SpinnerNationalityCityAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,viewGroup,false);
        binding.setTitle(list.get(i));
        return binding.getRoot();
    }
}
