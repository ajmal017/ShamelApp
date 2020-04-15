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
import com.arabdevelopers.shamelapp.activities_fragments.activity_notification.NotificationActivity;
import com.arabdevelopers.shamelapp.databinding.FavouriteRowBinding;
import com.arabdevelopers.shamelapp.databinding.LoadMoreRowBinding;
import com.arabdevelopers.shamelapp.databinding.NotificationRowBinding;
import com.arabdevelopers.shamelapp.models.AdsModel;
import com.arabdevelopers.shamelapp.models.NotificationDataModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;

    private Context context;
    private List<NotificationDataModel.Data.NotificationModel>  list;
    private NotificationActivity activity;
    public NotificationAdapter(Context context, List<NotificationDataModel.Data.NotificationModel>  list) {
        this.context = context;
        this.list = list;
        this.activity = (NotificationActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==DATA) {
            NotificationRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.notification_row, parent, false);
            return new Holder1(binding);


        }else
            {
                LoadMoreRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more_row,parent,false);
                return new LoadHolder(binding);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        NotificationDataModel.Data.NotificationModel model = list.get(position);

        if (holder instanceof Holder1)
        {
            Holder1 holder1 = (Holder1) holder;

            holder1.binding.setModel(model);

            holder1.binding.imageDelete.setOnClickListener(view -> {
                NotificationDataModel.Data.NotificationModel model2 = list.get(holder1.getAdapterPosition());

                activity.setItemDelete(model2,holder1.getAdapterPosition());

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
        private NotificationRowBinding binding;

        public Holder1(@NonNull NotificationRowBinding binding) {
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
