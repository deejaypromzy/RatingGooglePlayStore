package com.putuguna.ratinggoogleplaystore.product;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.putuguna.ratinggoogleplaystore.R;
import com.putuguna.ratinggoogleplaystore.reviews.ProductModel;
import com.putuguna.ratinggoogleplaystore.reviews.ReviewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnClickListenerAdapter {

    @BindView(R.id.rv_product)
    RecyclerView rvProduct;
    private ProductAdapter adapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        getProduct();
    }

    private void initView(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Load data product ...");
        progressDialog.show();
    }

    private void getProduct(){
        CollectionReference collectionReference = firebaseFirestore.collection("product");
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<ProductModel> list = new ArrayList<>();
                            for(DocumentSnapshot document : queryDocumentSnapshots){
                                ProductModel productModel = document.toObject(ProductModel.class);
                                list.add(productModel);
                            }
                            initListProduct(list);
                        }else{
                            Toast.makeText(MainActivity.this, "Product is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void initListProduct(List<ProductModel> productModelList){
        adapter = new ProductAdapter(productModelList);
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListenerAdapter(this);
        rvProduct.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProduct();
    }

    @Override
    public void onItemClicked(String product) {
        ReviewActivity.start(this, product);
    }
}
