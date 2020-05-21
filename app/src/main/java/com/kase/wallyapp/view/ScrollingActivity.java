package com.kase.wallyapp.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kase.wallyapp.R;
import com.kase.wallyapp.retrofit.ApiClient;
import com.kase.wallyapp.retrofit.ApiInterface;
import com.kase.wallyapp.retrofit.model.Photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrollingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    CustomImageAdapter adapter;
    CustomImageAdapter.OnPhotoClickedListener photoClickListener;

    ApiInterface dataService;
    Activity mActivity;

    ImageButton profileButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Context mContext;
    private int page = 1;
    private int visibleThreshold = 8;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode().setDuration(500));
        setContentView(R.layout.img_picker);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        mActivity = this;
        mContext = this;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        dataService = ApiClient.getClient().create(ApiInterface.class);

        photoClickListener = new CustomImageAdapter.OnPhotoClickedListener() {
            @Override
            public void photoClicked(Photo photo, ImageView imageView) {
                Intent intent = new Intent(getApplicationContext(), ImageEnlargedActivity.class);
                intent.putExtra("image", photo);
                setResult(RESULT_OK, intent);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(mActivity, imageView, "enlarge");
                startActivity(intent, options.toBundle());

            }
        };

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomImageAdapter(new ArrayList<Photo>(), this, photoClickListener);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager
                        .findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadPhotos();
                    loading = true;
                }

            }
        });

        loadPhotos();
    }

    private void loadPhotos() {
        progressBar.setVisibility(View.VISIBLE);

        dataService.getPhotos(page,null,"latest")
                .enqueue(new Callback<List<Photo>>() {
                    @Override
                    public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {

                        List<Photo> photos = response.body();
                        Log.d(getString(R.string.photos_tag), getString(R.string.photos_fetched)+ photos.size());
                        //add to adapter
                        page++;
                        adapter.addPhotos(photos);
                        progressBar.setVisibility(View.GONE);
                        loading = false;
                    }

                    @Override
                    public void onFailure(Call<List<Photo>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }
}
