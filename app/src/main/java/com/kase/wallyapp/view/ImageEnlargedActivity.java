package com.kase.wallyapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kase.wallyapp.R;
import com.kase.wallyapp.retrofit.ApiClient;
import com.kase.wallyapp.retrofit.ApiInterface;
import com.kase.wallyapp.retrofit.model.Download;
import com.kase.wallyapp.retrofit.model.Photo;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageEnlargedActivity extends AppCompatActivity {

    ImageView image;
    Photo photo;
    TextView firstText;
    TextView secondText;
    Animation bottomToTop;
    Animation leftToRight;
    ImageButton downloadButton;
    ApiInterface dataService;
    ProgressDialog mProgressDialog;
    DownloadImage imageDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enlarged);
        image = findViewById(R.id.imageView2);
        photo = (Photo) getIntent().getSerializableExtra("image");
        firstText = findViewById(R.id.textView);
        secondText = findViewById(R.id.textView2);
        bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        leftToRight = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        firstText.setAnimation(bottomToTop);
        secondText.setAnimation(leftToRight);
        dataService = ApiClient.getClient().create(ApiInterface.class);
        imageDownloader = new DownloadImage(getApplicationContext().getContentResolver());

        Picasso.get()
                .load(photo.getUrls().getRegular())
                .resize(1500,1500)
                .centerCrop()
                .into(image);

        downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataService.getPhotoDownloadLink(photo.getId())
                        .enqueue(new Callback<Download>() {
                            @Override
                            public void onResponse(Call<Download> call, Response<Download> response) {
                                Download downloads = response.body();
                                imageDownloader.execute(downloads.getUrl());
                            }

                            @Override
                            public void onFailure(Call<Download> call, Throwable t) {

                            }
                        });
            }
        });
    }

    private ContentResolver contentResolver;

    private class DownloadImage extends AsyncTask {

        public DownloadImage(ContentResolver ctr){
            contentResolver = ctr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(ImageEnlargedActivity.this);
            mProgressDialog.setTitle(getString(R.string.image_downloading));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String imageURL = (String) objects[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            MediaStore.Images.Media.insertImage(contentResolver, (Bitmap) result, photo.getId(), getString(R.string.image_downloaded));
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
        }

    }
}
