package com.arabdevelopers.shamelapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_Favourite;
import com.arabdevelopers.shamelapp.databinding.FavouriteRowBinding;
import com.arabdevelopers.shamelapp.databinding.LoadMoreRowBinding;
import com.arabdevelopers.shamelapp.models.AdsModel;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;

    private Context context;
    private List<AdsModel>  list;
    private Fragment_Favourite fragment_favourite;
    public FavouriteAdapter(Context context, List<AdsModel>  list, Fragment_Favourite fragment_favourite) {
        this.context = context;
        this.list = list;
        this.fragment_favourite = fragment_favourite;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==DATA) {
            FavouriteRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.favourite_row, parent, false);
            return new Holder1(binding);


        }else
            {
                LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more_row,parent,false);
                return new LoadHolder(binding);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AdsModel model = list.get(position);

        if (holder instanceof Holder1)
        {
            Holder1 holder1 = (Holder1) holder;

            holder1.binding.setModel(model);

            holder1.binding.imageDislike.setOnClickListener(view -> {
                AdsModel model2 = list.get(holder1.getAdapterPosition());

                fragment_favourite.setDislikeItemData(model2,holder1.getAdapterPosition());

            });


            holder1.binding.tvShow.setOnClickListener(view -> {
                AdsModel model2 = list.get(holder1.getAdapterPosition());

                fragment_favourite.setItemData(model2,holder1.getAdapterPosition());

            });






        }else if (holder instanceof LoadHolder) {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadHolder.binding.progBar.setIndeterminate(true);
        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder1 extends RecyclerView.ViewHolder {
        private FavouriteRowBinding binding;

        public Holder1(@NonNull FavouriteRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreRowBinding binding;

        public LoadHolder(@NonNull LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (list.get(position)==null)
        {
            return LOAD;
        }else
        {
            return DATA;
        }
    }
}
