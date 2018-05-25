package com.putuguna.ratinggoogleplaystore.reviews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.putuguna.ratinggoogleplaystore.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> listReview;

    public ReviewAdapter(List<ReviewModel> listReview) {
        this.listReview = listReview;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel model = listReview.get(position);

        holder.totalStarRating.setRating(Float.parseFloat(String.valueOf(model.getTotalStarGiven())));
        holder.tvDescReview.setText(model.getReview());
        holder.tvTglRating.setText(String.valueOf(model.getTimeStamp()));
        holder.tvNamaPasien.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nama_pasien)
        TextView tvNamaPasien;
        @BindView(R.id.total_star_rating)
        MaterialRatingBar totalStarRating;
        @BindView(R.id.tv_tgl_rating)
        TextView tvTglRating;
        @BindView(R.id.tv_desc_review)
        TextView tvDescReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
