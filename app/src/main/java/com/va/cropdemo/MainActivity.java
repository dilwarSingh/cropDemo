package com.va.cropdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageView oldImage, newImage;
    Button button;
    int PICK_SINGLE = 124;
    Uri sourceUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oldImage = findViewById(R.id.oldImage);
        newImage = findViewById(R.id.newImage);
        button = findViewById(R.id.button);

        oldImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Intent.ACTION_PICK);
                mIntent.setType("image/*");
                startActivityForResult(mIntent, PICK_SINGLE);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    File f = new File(getCacheDir() + "/temp.jpg");
                    f.createNewFile();
                    String path = f.getPath();
                    Uri destinationUri = Uri.parse(path);

                    UCrop.Options options = new UCrop.Options();
                    options.setCompressionQuality(70);
                    options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                    options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
                    options.setFreeStyleCropEnabled(true);
                    options.setToolbarColor(getResources().getColor(R.color.colorAccent));


                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(10, 10)
                            .withOptions(options)
                            .withMaxResultSize(640, 640)
                            .start(MainActivity.this);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_SINGLE) {
            try {
                sourceUri = data.getData();

                final InputStream imageStream = getContentResolver().openInputStream(sourceUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);


                File f = new File(this.getCacheDir(), "temp.jpg");
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                oldImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            try {
                final Uri imageUri = resultUri;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);


                File f = new File(this.getCacheDir(), "temp1.jpg");
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                newImage.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }


    }


}
