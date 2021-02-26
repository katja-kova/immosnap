package com.example.immosnap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immosnap.data.ImmoObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewOfferActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1234;
    private String currentPhotoPath = "";
    private ArrayList<ImmoObject> immoList;

    ImageView imgNew;
    RadioButton rdbSale;
    RadioButton rdbRent;
    EditText edtTitle;
    EditText edtAddress;
    EditText edtPrice;
    EditText edtRoom;
    EditText edtCommission;
    EditText edtDescription;
    Button btnOffer;
    ImageButton btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);

        mapping();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(NewOfferActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });

        btnOffer.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                immoList = (ArrayList<ImmoObject>) intent.getSerializableExtra("list");

                // setzen Defautj-Image, wenn man kein Foto gemacht hat
                String img = "rent6";
                // prüfen, ob man ein Foto schon gemacht hat
                if (!currentPhotoPath.isEmpty()){
                    img = currentPhotoPath;
                }
                String title = edtTitle.getText().toString();
                int price = Integer.parseInt(edtPrice.getText().toString());
                String address = edtAddress.getText().toString();
                boolean toRent = false;
                if (rdbRent.isChecked()){
                    toRent = true;
                }
                int room = Integer.parseInt(edtRoom.getText().toString());
                String description = edtDescription.getText().toString();
                int commission = Integer.parseInt(edtCommission.getText().toString());

                // make new object
                ImmoObject immoObject = new ImmoObject(img, title, price, address, toRent, room, description, commission);
                immoList.add(immoObject);
                updateJSONFile(immoList);

                Toast.makeText(v.getContext(), R.string.newOffer_Toast, Toast.LENGTH_SHORT).show();
                //onBackPressed();
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            dispatchTakePictureIntent();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //imgNew.setImageBitmap(bitmap);
            previewPicture();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                System.out.println(photoFile.getAbsolutePath());
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Exception dispatchTakePictureIntent");
            }
            // Continue only if the File was successfully created
            // directory must exist!
            if (photoFile != null) {
                // System.out.println("Photo: " + photoFile.getAbsolutePath());
                Uri photoURI = FileProvider.getUriForFile(this,
                        "photoprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // erzeuge Dateiname für die Aufnahme
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Standard directory in which to place pictures that are available to
        // the user. Note that this is primarily a convention for the top-level
        // public directory, as the media scanner will find and collect pictures
        // in any directory: Android/data/com.example.takephoto/files/Pictures
        // (siehe file_paths.xml)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // ... JPEG_2020...jpg
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        // Save file path
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void previewPicture() {
        Drawable d;

        d = Drawable.createFromPath(currentPhotoPath);
        //Bitmap b = ((BitmapDrawable)d).getBitmap();
        //final int THUMBNAILSIZE = 64;
        //Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentPhotoPath), THUMBNAILSIZE, THUMBNAILSIZE);

        //int width = 200;
        //int height = 200;
        //Bitmap bitmapResized = Bitmap.createScaledBitmap(thumbImage, width,height, false);
        //d =  new BitmapDrawable(getResources(), bitmapResized);
        //d =  new BitmapDrawable(getResources(), thumbImage);

        //ImageView imageView = findViewById(R.id.thumb);
        imgNew.setImageDrawable(d);

        System.out.println("preview: " + currentPhotoPath);
    }

    public void updateJSONFile(ArrayList<ImmoObject> list){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OutputStream outputStream = openFileOutput("immoobject.json", MODE_PRIVATE);
            objectMapper.writeValue(outputStream, list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void mapping(){
        imgNew = (ImageView) findViewById(R.id.imageViewNew);
        rdbSale = (RadioButton) findViewById(R.id.radioButtonSale);
        rdbRent = (RadioButton) findViewById(R.id.radioButtonRent);
        edtTitle = (EditText) findViewById(R.id.editTextTitle);
        edtAddress = (EditText) findViewById(R.id.editTextAddress);
        edtPrice = (EditText) findViewById(R.id.editTextNumberPrice);
        edtRoom = (EditText) findViewById(R.id.editTextNumberRoom);
        edtCommission = (EditText) findViewById(R.id.editTextNumberCommission);
        edtDescription = (EditText) findViewById(R.id.editTextDescription);
        btnOffer = (Button) findViewById(R.id.buttonOffer);
        btnCamera = (ImageButton) findViewById(R.id.imageButtonCamera);
    }
}