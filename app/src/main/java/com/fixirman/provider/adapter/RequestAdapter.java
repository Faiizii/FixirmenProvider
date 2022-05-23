package com.fixirman.provider.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fixirman.R;
import com.app.fixirman.databinding.VhRequestBinding;
import com.bumptech.glide.request.RequestOptions;
import com.fixirman.provider.model.request.RequestDetail;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.view.activity.RequestDetailActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Vh>{

    private FragmentActivity context;
    private List<RequestDetail> list;

    public RequestAdapter(FragmentActivity context, List<RequestDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VhRequestBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.vh_request,parent,false);
        return new Vh(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        RequestDetail model = list.get(position);
        holder.binding.setModel(model);
        Glide.with(context).load(AppConstants.HOST_URL+model.getUserImage())
                .apply(new RequestOptions().error(R.drawable.default_user)
                                .fitCenter())
                .into(holder.binding.ivUserImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        public VhRequestBinding binding;
        public Vh(@NonNull VhRequestBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.btnDetail.setOnClickListener(v->{
                Intent i = new Intent(context, RequestDetailActivity.class);
                i.putExtra("object",list.get(getAdapterPosition()));
                context.startActivity(i);
            });
        }
    }
}
