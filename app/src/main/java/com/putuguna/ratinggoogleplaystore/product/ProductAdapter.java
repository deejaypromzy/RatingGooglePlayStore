package com.putuguna.ratinggoogleplaystore.product;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.putuguna.ratinggoogleplaystore.R;
import com.putuguna.ratinggoogleplaystore.reviews.ProductModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    public interface OnClickListenerAdapter {
        void onItemClicked(String product);
    }

    private OnClickListenerAdapter onClickListenerAdapter;
    private List<ProductModel> listProductModel;


    public ProductAdapter(List<ProductModel> listProductModel) {
        this.listProductModel = listProductModel;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        ProductModel model = listProductModel.get(position);

        holder.totalStarRating.setRating(Float.parseFloat(String.valueOf(model.getTotalRating())));
        holder.tvProductName.setText(model.getProductName());
        holder.cvItem.setOnClickListener(v -> {
            if (onClickListenerAdapter != null) {
                onClickListenerAdapter.onItemClicked(new Gson().toJson(model));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProductModel.size();
    }

    public void setOnClickListenerAdapter(OnClickListenerAdapter onClickListenerAdapter) {
        this.onClickListenerAdapter = onClickListenerAdapter;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.total_star_rating)
        MaterialRatingBar totalStarRating;
        @BindView(R.id.cv_item)
        CardView cvItem;

        public ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
