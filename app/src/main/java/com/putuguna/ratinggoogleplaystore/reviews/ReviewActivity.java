package com.putuguna.ratinggoogleplaystore.reviews;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.putuguna.ratinggoogleplaystore.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewActivity extends AppCompatActivity {

    public static String EXTRA_PRODUCT_MODEL = "EXTRA_PRODUCT_MODEL";

    @BindView(R.id.tv_total_number_rating)
    TextView tvTotalNumberRating;
    @BindView(R.id.total_star_rating)
    MaterialRatingBar totalStarRating;
    @BindView(R.id.tv_total_pemberi_bintang)
    TextView tvTotalPemberiBintang;
    @BindView(R.id.ll_percentage_5)
    LinearLayout llPercentage5;
    @BindView(R.id.constrain_layout_5)
    ConstraintLayout constrainLayout5;
    @BindView(R.id.ll_percentage_4)
    LinearLayout llPercentage4;
    @BindView(R.id.constrain_layout_4)
    ConstraintLayout constrainLayout4;
    @BindView(R.id.ll_percentage_3)
    LinearLayout llPercentage3;
    @BindView(R.id.constrain_layout_3)
    ConstraintLayout constrainLayout3;
    @BindView(R.id.ll_percentage_2)
    LinearLayout llPercentage2;
    @BindView(R.id.constrain_layout_2)
    ConstraintLayout constrainLayout2;
    @BindView(R.id.ll_percentage_1)
    LinearLayout llPercentage1;
    @BindView(R.id.constrain_layout_1)
    ConstraintLayout constrainLayout1;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.rv_review)
    RecyclerView rvReview;

    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private ProductModel productModelGlobal;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ReviewAdapter adapter;

    public static void start(Context context, String productModel) {
        Intent starter = new Intent(context, ReviewActivity.class);
        starter.putExtra(EXTRA_PRODUCT_MODEL, productModel);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        productModelGlobal = new Gson().fromJson(getIntent().getStringExtra(EXTRA_PRODUCT_MODEL), ProductModel.class);

        //this method used to get the width of view
        progressDialog.setMessage("Count Width Of View");
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                setRatingByColor(productModelGlobal);
                getAllReview(productModelGlobal.getIdProduct());
            }
        }, 3000);
    }

    /**
     * Insert data review to collection of product
     * @param review
     */
    private void insertDataReview(ReviewModel review) {
        ReviewModel reviewModel = new ReviewModel(review.getName(), review.getReview(), review.getTimeStamp(), review.getTotalStarGiven());
        CollectionReference collectionReference = firebaseFirestore.collection("product");
        DocumentReference documentReference = collectionReference.document(productModelGlobal.getIdProduct());
        documentReference.collection("review")
                .add(reviewModel)
                .addOnSuccessListener(documentReference1 -> {
                    progressDialog.dismiss();
                    //after success, then update the rating in product
                    updateRating(review, productModelGlobal);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReviewActivity.this, "Failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * this method used to update rating of product
     *
     * @param reviewModel
     * @param productModel
     */
    private void updateRating(ReviewModel reviewModel, ProductModel productModel) {
        ProductModel rate = new ProductModel();
        rate.setIdProduct(productModel.getIdProduct());
        rate.setProductName(productModel.getProductName());

        //update stars
        double totalStars;
        int totalVoters = 0;
        if (reviewModel.getTotalStarGiven() == 1.0) {
            totalStars = 1.0 + (double) productModel.getStar1();
            rate.setStar1((int) totalStars);
            rate.setStar2(productModel.getStar2());
            rate.setStar3(productModel.getStar3());
            rate.setStar4(productModel.getStar4());
            rate.setStar5(productModel.getStar5());

            totalVoters = (int) (totalStars + productModel.getStar2() + productModel.getStar3() + productModel.getStar4() + productModel.getStar5());
            if (productModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 2.0) {
            totalStars = 1.0 + (double) productModel.getStar2();
            rate.setStar1(productModel.getStar1());
            rate.setStar2((int) totalStars);
            rate.setStar3(productModel.getStar3());
            rate.setStar4(productModel.getStar4());
            rate.setStar5(productModel.getStar5());

            totalVoters = (int) (totalStars + productModel.getStar1() + productModel.getStar3() + productModel.getStar4() + productModel.getStar5());
            if (productModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 3.0) {
            totalStars = 1.0 + (double) productModel.getStar3();
            rate.setStar1(productModel.getStar1());
            rate.setStar2(productModel.getStar2());
            rate.setStar3((int) totalStars);
            rate.setStar4(productModel.getStar4());
            rate.setStar5(productModel.getStar5());

            totalVoters = (int) (totalStars + productModel.getStar1() + productModel.getStar2() + productModel.getStar4() + productModel.getStar5());
            if (productModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 4.0) {
            totalStars = 1.0 + (double) productModel.getStar4();
            rate.setStar1(productModel.getStar1());
            rate.setStar2(productModel.getStar2());
            rate.setStar3(productModel.getStar3());
            rate.setStar4((int) totalStars);
            rate.setStar5(productModel.getStar5());

            totalVoters = (int) (totalStars + productModel.getStar1() + productModel.getStar2() + productModel.getStar3() + productModel.getStar5());
            if (productModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (reviewModel.getTotalStarGiven() == 5.0) {
            totalStars = 1.0 + (double) productModel.getStar5();
            rate.setStar1(productModel.getStar1());
            rate.setStar2(productModel.getStar2());
            rate.setStar3(productModel.getStar3());
            rate.setStar4(productModel.getStar4());
            rate.setStar5((int) totalStars);

            totalVoters = (int) (totalStars + productModel.getStar1() + productModel.getStar2() + productModel.getStar3() + productModel.getStar4());
            if (productModel.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        }

        //update rate
        int totalStar1 = rate.getStar1() * 1;
        int totalStar2 = rate.getStar2() * 2;
        int totalStar3 = rate.getStar3() * 3;
        int totalStar4 = rate.getStar4() * 4;
        int totalStar5 = rate.getStar5() * 5;

        double sumOfStars = totalStar1 + totalStar2 + totalStar3 + totalStar4 + totalStar5;
        double totalRating = sumOfStars / (double) totalVoters;
        DecimalFormat format = new DecimalFormat(".#");
        rate.setTotalRating(Double.parseDouble(format.format(totalRating)));

        CollectionReference collectionReference = firebaseFirestore.collection("product");
        collectionReference.document(productModel.getIdProduct())
                .set(rate)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(ReviewActivity.this, "Successfully update Rating", Toast.LENGTH_SHORT).show();
                    productModelGlobal = rate;
                    setRatingByColor(rate);
                    getAllReview(productModel.getIdProduct());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ReviewActivity.this, "Failed Update Rating : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method used to display rating by colors
     *
     * @param productModel
     */
    private void setRatingByColor(ProductModel productModel) {
        int widthView = constrainLayout1.getWidth();
        int totalAllVoters = productModel.getTotalVoters();
        int totalRateStar1 = productModel.getStar1();
        int totalRateStar2 = productModel.getStar2();
        int totalRateStar3 = productModel.getStar3();
        int totalRateStar4 = productModel.getStar4();
        int totalRateStar5 = productModel.getStar5();

        //convert to double
        double votersInDouble = (double) totalAllVoters;


        //RATING STAR 1
        double star1 = (double) totalRateStar1;
        double sum1 = (star1 / votersInDouble);
        int rating1 = (int) (sum1 * widthView);
        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0, 5, 0, 5);
        llPercentage1.setBackgroundColor(Color.parseColor("#ff6f31"));
        llPercentage1.setLayoutParams(layoutParams1);

        //RATING STAR 2
        double star2 = (double) totalRateStar2;
        double sum2 = (star2 / votersInDouble);
        int rating2 = (int) (sum2 * widthView);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.setMargins(0, 5, 0, 5);
        llPercentage2.setBackgroundColor(Color.parseColor("#ff9f02"));
        llPercentage2.setLayoutParams(layoutParams2);

        //RATING STAR 3
        double star3 = (double) totalRateStar3;
        double sum3 = (star3 / votersInDouble);
        int rating3 = (int) (sum3 * widthView);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams3.setMargins(0, 5, 0, 5);
        llPercentage3.setBackgroundColor(Color.parseColor("#ffcf02"));
        llPercentage3.setLayoutParams(layoutParams3);

        //RATING STAR 4
        double star4 = (double) totalRateStar4;
        double sum4 = (star4 / votersInDouble);
        int rating4 = (int) (sum4 * widthView);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams4.setMargins(0, 5, 0, 5);
        llPercentage4.setBackgroundColor(Color.parseColor("#9ace6a"));
        llPercentage4.setLayoutParams(layoutParams4);

        //RATING STAR 5
        double star5 = (double) totalRateStar5;
        double sum5 = (star5 / votersInDouble);
        int rating5 = (int) (sum5 * widthView);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams5.setMargins(0, 5, 0, 5);
        llPercentage5.setBackgroundColor(Color.parseColor("#57bb8a"));
        llPercentage5.setLayoutParams(layoutParams5);

        // menampilkan rating berdasarkan angka
        int totalBintangSatu = totalRateStar1 * 1;
        int totalBintangDua = totalRateStar2 * 2;
        int totalBintangTiga = totalRateStar3 * 3;
        int totalBintangEmpat = totalRateStar4 * 5;
        int totalBintangLima = totalRateStar5 * 5;

        double sumBintang = totalBintangSatu +
                totalBintangDua +
                totalBintangTiga +
                totalBintangEmpat +
                totalBintangLima;

        double rating = (sumBintang / votersInDouble);
        DecimalFormat format = new DecimalFormat(".#");

        tvTotalNumberRating.setText(String.valueOf(format.format(rating)));

        totalStarRating.setRating(Float.parseFloat(String.valueOf(rating)));
        tvTotalPemberiBintang.setText(String.valueOf(totalAllVoters) + " total");


    }

    /**
     * this method used to open dialog input review
     */
    private void openDialogReview() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);

        EditText etReview = dialog.findViewById(R.id.et_review);
        EditText etName = dialog.findViewById(R.id.et_name);
        MaterialRatingBar rate = dialog.findViewById(R.id.rate_star);
        Button btnKirimUlasan = dialog.findViewById(R.id.btn_send_review);

        btnKirimUlasan.setOnClickListener(v -> {
            dialog.dismiss();
            if (TextUtils.isEmpty(etReview.getText().toString())) {
                etReview.setError("Required field");
            } else {
                progressDialog.setMessage("Please wait ...");
                progressDialog.show();

                ReviewModel reviewModel = new ReviewModel();
                reviewModel.setName(etName.getText().toString());
                reviewModel.setReview(etReview.getText().toString());
                reviewModel.setTimeStamp(new Date());
                reviewModel.setTotalStarGiven(Math.round(rate.getRating()));
                insertDataReview(reviewModel);
            }
        });

        dialog.show();
    }

    /**
     * the method used to get all reviews in firebase firestore
     * @param idProduct
     */
    private void getAllReview(String idProduct) {
        progressBar.setVisibility(View.VISIBLE);
        rvReview.setVisibility(View.GONE);
        CollectionReference collectionReference = firebaseFirestore.collection("product");
        DocumentReference documentReference = collectionReference.document(idProduct);
        documentReference.collection("review")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    rvReview.setVisibility(View.VISIBLE);
                    if (task.getResult().isEmpty()) {
                    } else if (task.isSuccessful()) {
                        List<ReviewModel> listReview = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            ReviewModel reviewModel = new ReviewModel();
                            try {
                                reviewModel.setName(documentSnapshot.get("name").toString());
                                reviewModel.setReview(documentSnapshot.get("review").toString());
                                reviewModel.setTimeStamp(new Date(documentSnapshot.get("timeStamp").toString()));
                                reviewModel.setTotalStarGiven(Double.parseDouble(documentSnapshot.get("totalStarGiven").toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            listReview.add(reviewModel);
                            initListReview(listReview);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    //this method used to populating the recyclerview with data of reviews
    private void initListReview(List<ReviewModel> reviewModels) {
        adapter = new ReviewAdapter(reviewModels);
        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvReview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_review) {
            openDialogReview();
        }
        return super.onOptionsItemSelected(item);
    }
}
