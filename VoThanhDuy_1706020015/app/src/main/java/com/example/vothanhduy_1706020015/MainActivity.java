package com.example.vothanhduy_1706020015;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.vothanhduy_1706020015.Models.Product;
import com.example.vothanhduy_1706020015.Models.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    static List<Subject> subjectList = new ArrayList<>();
    static List<Product> productList = new ArrayList<>();
    static DatabaseReference myChilRef;

    Button btn_add;
    boolean isProduct = true;
    Button btnSync;
    ProgressBar loadingData;
    Button btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), Clean_App.class));

        init();
        //phần lưu thong tin tai khoản
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
        final DatabaseReference childUserRef = userRef.child(LoginActivity.IDgg);
        childUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue();
                if (data ==null){
                    if (isProduct){
                        childUserRef.setValue("product");
                    }else{
                        childUserRef.setValue("subject");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //end
        //==========================

        FirebaseDatabase myFire = FirebaseDatabase.getInstance();
        DatabaseReference myRef = myFire.getReference().child(LoginActivity.IDgg);
        myChilRef = myRef.child("AdvancedAndroidFinalTest");
        myChilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList.clear();
                productList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    loadingData.setVisibility(View.VISIBLE);
                    try
                    {
                        Subject subject = item.getValue(Subject.class);
                        Product product = item.getValue(Product.class);
                        String key = item.getKey();
                        assert subject != null;
                        if (subject.getSubject_code() != null) {
                            boolean dont = true;
                            isProduct = false;
                            for (Subject chil : subjectList) {
                                if (chil.getId() == subject.getId()) {
                                    dont = false;
                                    break;
                                }
                            }
                            if (dont) {
                                childUserRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Object data = dataSnapshot.getValue();
                                        if (data ==null){
                                                childUserRef.setValue("subject");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                subject.setKeyParent(key);
                                subjectList.add(subject);
                            }
                        }
                        assert product != null;
                        if (product.getProduct_name() != null) {
                            boolean dont = true;
                            for (Product chil : productList) {
                                if (chil.getId() == product.getId()) {
                                    dont = false;
                                    break;
                                }
                            }
                            if (dont) {
                                childUserRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Object data = dataSnapshot.getValue();
                                        if (data ==null){
                                            childUserRef.setValue("product");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                product.setKeyParent(key);
                                productList.add(product);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                loadingData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                if (isProduct){
                    intent.putExtra("type","product");
                }else
                {
                    intent.putExtra("type","subject");
                }
                startActivity(intent);
            }
        });
        adapter = new RecyclerAdapter(subjectList, productList, MainActivity.this, R.layout.recycler_custom_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    void init() {
        recyclerView = findViewById(R.id.list);
        btn_add = findViewById(R.id.main_btnAdd);
        loadingData = findViewById(R.id.indeterminateBar);
        btnSync = findViewById(R.id.main_btnSync);
        btn_logout = findViewById(R.id.main_btnLogout);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> mMap = new HashMap<>();
                mMap.put("google_id", String.valueOf(LoginActivity.IDgg));
                mMap.put("firebase_url", "https://vothanhduy1706020015.firebaseio.com/" + LoginActivity.IDgg);
                new CallAPI_AsynTask(MainActivity.this, mMap).execute("http://vidoandroid.vidophp.tk/api/FireBase/PushData");
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.mGoogleSignInClient.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
