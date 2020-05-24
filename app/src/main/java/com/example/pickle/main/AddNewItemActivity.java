package com.example.pickle.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickle.R;
import com.example.pickle.models.ProductModel;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class AddNewItemActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView _itemImageView;

   private EditText _itemName;
   private EditText _itemDesc;
   private EditText _itemBasePrice;
   private EditText _itemSellPrice;
   private EditText _itemMaxQtyPerUser;
   private EditText _itemOffers;
   private EditText _itemQty;
   private EditText _itemUnits;

   private ProgressBar _progressBar;

    private final String[] defType = new String[1];
    private final String[] defQtyType = new String[1];
    private final String[] defItemCategory = new String[1];

    private static int SOLID_QTY = R.array.solid;
    private static int LIQUID_QTY = R.array.liquid;

    Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        _itemName = findViewById(R.id.add_new_item_name);
        _itemDesc = findViewById(R.id.add_new_item_desc);
        _itemBasePrice = findViewById(R.id.add_new_item_base_price);
        _itemSellPrice = findViewById(R.id.add_new_item_sell_price);
        _itemMaxQtyPerUser = findViewById(R.id.add_new_item_max_per_user);
        _itemOffers = findViewById(R.id.add_new_item_offers);
        _itemQty = findViewById(R.id.add_new_item_qty);
        _itemUnits = findViewById(R.id.add_new_item_units);

        Spinner _qtyType = findViewById(R.id.add_new_item_qty_type);
        Spinner _itemTypeSolid = findViewById(R.id.add_new_item_type_solid);
        Spinner _itemTypeLiquid = findViewById(R.id.add_new_item_type_liquid);
        Spinner _itemCategory = findViewById(R.id.add_new_item_category);

        MaterialButton _saveBtn = findViewById(R.id.add_new_item_save);

        _itemImageView = findViewById(R.id.add_new_item_image);

        _progressBar = findViewById(R.id.add_new_item_progress_bar);

        _itemImageView.setOnClickListener(n -> {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(AddNewItemActivity.this);
        });

        defType[0] = "solid";
        defQtyType[0] = "kg";
        defItemCategory[0] = "Fruits";

        ArrayAdapter<CharSequence> itemCategoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.ProductCategory,
                android.R.layout.simple_spinner_dropdown_item
        );

        ArrayAdapter<CharSequence> qtyTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.type,
                android.R.layout.simple_spinner_dropdown_item
        );

        ArrayAdapter<CharSequence> qtyTypeSolid = ArrayAdapter.createFromResource(
                this,
                R.array.solid,
                android.R.layout.simple_spinner_dropdown_item
        );

        ArrayAdapter<CharSequence> qtyTypeLiquid = ArrayAdapter.createFromResource(
                this,
                R.array.liquid,
                android.R.layout.simple_spinner_dropdown_item
        );


        itemCategoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        _itemCategory.setAdapter(itemCategoryAdapter);
        _itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defItemCategory[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        qtyTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        _qtyType.setAdapter(qtyTypeAdapter);
        _qtyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defQtyType[0] = parent.getItemAtPosition(position).toString();
                if (defQtyType[0].equals("liquid")) {
                    _itemTypeLiquid.setVisibility(View.VISIBLE);
                    _itemTypeSolid.setVisibility(View.GONE);
                } else {
                    _itemTypeLiquid.setVisibility(View.GONE);
                    _itemTypeSolid.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        qtyTypeSolid.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        _itemTypeSolid.setAdapter(qtyTypeSolid);
        _itemTypeSolid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defQtyType[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        qtyTypeLiquid.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        _itemTypeLiquid.setAdapter(qtyTypeLiquid);
        _itemTypeLiquid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defQtyType[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _saveBtn.setOnClickListener(v -> {

            if (_itemName.getText().toString().isEmpty()) {
                _itemName.setError("should not be empty");
                _itemName.requestFocus();
                return;
            }

            if (_itemDesc.getText().toString().isEmpty()) {
                _itemDesc.setError("should not be empty");
                _itemDesc.requestFocus();
                return;
            }
            if (_itemBasePrice.getText().toString().isEmpty()) {
                _itemBasePrice.setError("should not be empty");
                _itemBasePrice.requestFocus();
                return;
            }
            if (_itemSellPrice.getText().toString().isEmpty()) {
                _itemSellPrice.setError("should not be empty");
                _itemSellPrice.requestFocus();
                return;
            }
            if (_itemMaxQtyPerUser.getText().toString().isEmpty()) {
                _itemMaxQtyPerUser.setError("should not be empty");
                _itemMaxQtyPerUser.requestFocus();
                return;
            }
            if (_itemOffers.getText().toString().isEmpty()) {
                _itemOffers.setError("should not be empty");
                _itemOffers.requestFocus();
                return;
            }
            if (_itemQty.getText().toString().isEmpty()) {
                _itemQty.setError("should not be empty");
                _itemQty.requestFocus();
                return;
            }
            if (_itemUnits.getText().toString().isEmpty()) {
                _itemUnits.setError("should not be empty");
                _itemUnits.requestFocus();
                return;
            }

            if (imageUri == null) {
                Toast.makeText(this, "select the image", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImage();
        });

    }


    private void uploadImage() {
        if (imageUri != null) {

            File newImageFile = new File(imageUri.getPath());

            try {
                compressedImageFile = new Compressor(AddNewItemActivity.this)
                        .setQuality(30).compressToBitmap(newImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            _progressBar.setVisibility(View.VISIBLE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            String pushKey = reference.push().getKey();


            StorageReference storage = FirebaseStorage.getInstance().getReference("ProductImages")
                    .child("thumbs/" + defItemCategory[0]);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] thumbData = baos.toByteArray();

            UploadTask uploadTask = storage.child(pushKey + ".jpeg").putBytes(thumbData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(uri -> {
                        String thumbImageDownloadUri = uri.toString();
                        uploadData(thumbImageDownloadUri, reference, pushKey);
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(AddNewItemActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                _progressBar.setVisibility(View.GONE);
            });
        }

    }

    private void uploadData(String thumbImageUri, DatabaseReference reference, String pushKey) {
        reference.child(defItemCategory[0] + "/" + pushKey).setValue(
                new ProductModel(
                        _itemName.getText().toString(),
                        _itemDesc.getText().toString(),
                        Integer.parseInt(_itemBasePrice.getText().toString()),
                        Integer.parseInt(_itemSellPrice.getText().toString()),
                        Integer.parseInt(_itemMaxQtyPerUser.getText().toString()),
                        _itemOffers.getText().toString(),
                        Integer.parseInt(_itemQty.getText().toString()),

                        defQtyType[0],
                        defType[0],
                        defItemCategory[0],

                        pushKey,
                        Integer.parseInt(_itemUnits.getText().toString()),
                        new Date(),
                        true,
                        "image uri",
                        thumbImageUri
                )
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddNewItemActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                _progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(AddNewItemActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            _progressBar.setVisibility(View.GONE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                if (imageUri != null) {
                    _itemImageView.setImageURI(imageUri);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
