package com.fixirman.provider.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fixirman.R;
import com.app.fixirman.databinding.VhCategoryBinding;
import com.fixirman.provider.model.categroy.Category;
import com.fixirman.provider.my_interfaces.AdapterOnClickListener;
import com.fixirman.provider.utils.AppConstants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private FragmentActivity context;
    private List<Category> list;
    private List<Integer> selectedItems;
    private AdapterOnClickListener clickListener;
    public CategoryAdapter(FragmentActivity context, List<Category> list) {
        this.context = context;
        this.list = list;
        selectedItems = new ArrayList<>();
    }
    public CategoryAdapter(FragmentActivity context, List<Category> list,List<Integer> selectedItems) {
        this.context = context;
        this.list = list;
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        VhCategoryBinding binding = DataBindingUtil.inflate(inflater, R.layout.vh_category,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Category model = list.get(position);
        holder.categoryBinding.tvCategoryName.setText(model.getTitle());
        Glide.with(context).load(AppConstants.HOST_URL+model.getImage()).into(holder.categoryBinding.ivUserImage);
        holder.categoryBinding.main.setOnClickListener(v -> {
            if(clickListener != null)
                clickListener.onItemClickListener(v,position);
            if(selectedItems.contains(model.getId())){
                selectedItems.remove(model.getId());
            }else{
                selectedItems.add(model.getId());
            }
            notifyItemChanged(position);
        });
        if(selectedItems.contains(model.getId())){
            holder.categoryBinding.ivSelected.show();
        }else{
            holder.categoryBinding.ivSelected.hide();
        }
    }
    public void itemClickListener(AdapterOnClickListener clickListener){
        this.clickListener = clickListener;
    }
    public List<Integer> getSelectedItems(){
        return selectedItems;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Category getItem(int position) {
        return list.get(position);
    }
}
