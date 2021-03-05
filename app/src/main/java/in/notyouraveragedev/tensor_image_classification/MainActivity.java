package in.notyouraveragedev.tensor_image_classification;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.notyouraveragedev.tensor_image_classification.classifier.ImageClassifier;


public class MainActivity extends AppCompatActivity {


    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private ImageView imageView;
    private ListView listView;
    private ImageClassifier imageClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUIElements();
    }

    private void initializeUIElements() {
        imageView = findViewById(R.id.iv_capture);
        listView = findViewById(R.id.lv_probabilities);
        Button takepicture = findViewById(R.id.bt_take_picture);


        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Log.e("Image Classifier Error", "ERROR: " + e);
        }

        // adding on click listener to button
        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                Bitmap photo = decodeUriToBitmap(getBaseContext(),imageUri);
                imageView.setImageBitmap(photo);

                List<ImageClassifier.Recognition> predicitons = imageClassifier.recognizeImage(
                        photo, 0);

                final List<String> predicitonsList = new ArrayList<>();
                for (ImageClassifier.Recognition recog : predicitons) {
                    predicitonsList.add(recog.getName() + "  ::::::::::  " + recog.getConfidence());
                }

                // creating an array adapter to display the classification result in list view
                ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(
                        this, R.layout.support_simple_spinner_dropdown_item, predicitonsList);
                listView.setAdapter(predictionsAdapter);
            }

        }
        }



