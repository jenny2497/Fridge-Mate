package com.comp490.fridgemate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.comp490.fridgemate.ui.fridge.FridgeFragment;
import com.comp490.fridgemate.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text_Recognition extends AppCompatActivity {
    //UI Views
    private MaterialButton inputImageBtn;
    private MaterialButton recognizedTextBtn;
    private ShapeableImageView imageIv;
    private EditText recognizedTextEt;
    //TAG
    private static final String TAG = "Main_TAG";
    private Uri imageUri = null;
    //handle the result  of the  camera  permission
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE  = 101;
    //arrays of  permission  required to  pick  image from camera
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //progress dialog
    private ProgressDialog progressDialog;
    //Text Recognizer
    private TextRecognizer textRecognizer;
    android.widget.Button add_to_fridge_button;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser;
    DocumentReference fridgeDocRef;
    String user;
    ArrayList<String> fridgeItems = new ArrayList<>();
    ArrayList<String> fridgeImages = new ArrayList<>();
    ListView fridgeIngredients;
    ArrayAdapter<String> fridgeIngredientsAdapter;
    boolean taskFailed;

    List<String> apiFoods = new ArrayList<>();
    boolean needToCreateFridge;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_recognition);

        //input UI Views
        inputImageBtn = findViewById(R.id.inputImageBtn);//unputImageBtn
        recognizedTextBtn  = findViewById(R.id.recognizedBtn);
        add_to_fridge_button = findViewById(R.id.add_to_fridge);
        add_to_fridge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToAdd = String.valueOf(recognizedTextEt.getText());
                String imageToAdd = textToAdd + ".jpg";

                currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                user = currentFirebaseUser.getUid();
                fridgeDocRef = db.collection("users/" + user + "/categories").document("fridge");
                checkIfFridgeInDatabase();
                if (needToCreateFridge && !taskFailed) {
                    Map<String, Object> fridgeData = new HashMap<>();
                    fridgeData.put("fridge", Arrays.asList(textToAdd));
                    fridgeData.put("fridgeImages", Arrays.asList(imageToAdd));
                    fridgeDocRef.set(fridgeData).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(Text_Recognition.this, "Item added to fridge", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Text_Recognition.this, FridgeFragment.class));
                        }
                    });
                    needToCreateFridge = false;

                } else if (!taskFailed) { //we don't need to create the fridge in the database
                    fridgeDocRef.update("fridge", FieldValue.arrayUnion(textToAdd));
                    fridgeDocRef.update("fridgeImages", FieldValue.arrayUnion(imageToAdd)).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(Text_Recognition.this, "Item added to fridge", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Text_Recognition.this, FridgeFragment.class));
                        }
                    });

                } else {//task failed
                    Toast.makeText(Text_Recognition.this, "Could not connect to database", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Text_Recognition.this, FridgeFragment.class));

                }
            }
        });

        imageIv = findViewById(R.id.imageIv);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);
        //init arrays of oermission  required for camera
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions  =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //init TextRecognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //handle click, show  input  image  dialog
        inputImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }
        });

        recognizedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check  if  image is picked or not
                if(imageUri == null){
                    Toast.makeText(Text_Recognition.this, "Pick Image First", Toast.LENGTH_SHORT).show();
                }
                else{
                    recognizedTextFromImage();
                }
            }
        });
    }///////

    private void recognizedTextFromImage() {
        Log.d(TAG, "recognizedTextFromImage: ");
        progressDialog.setMessage("Preparing Image");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this,imageUri);
            progressDialog.setMessage("Recognizing text...");

            Task<Text> textTaskResult = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    progressDialog.dismiss();
                    String  recognizedText  = text.getText();
                    Log.d(TAG, "onSuccess: recognizedText" + recognizedText);
                    recognizedTextEt.setText(recognizedText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailure: ", e);
                    Toast.makeText(Text_Recognition.this,"Failed recognizing text due to "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            //exception  occured  while prepparing  input image
            Log.d(TAG, "recognizedTextFromImage: ",e);
            progressDialog.dismiss();
            Toast.makeText(this, "Failed preparing image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void  showInputImageDialog(){
        PopupMenu popupMenu =  new PopupMenu(this, inputImageBtn);
        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");

        popupMenu.show();
        //handle popup clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == 1){
                    Log.d(TAG, "onMenuItemClick: Camera Clicked...");
                    if(checkCameraPermissions()){
                        pickImageCamera();
                    }
                    else{
                        requestCameraPermissions();
                    }
                }
                else if(id == 2){
                    Log.d(TAG, "onMenuItemClick: Gallery Clicked");
                    if(checkStoragePermission()){
                        pickImageGallery();
                    }
                    else{
                        requestStoragePermission();
                    }

                }
                return true;
            }
        });

    }//end of showInputImageDialog

    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }//end of pickImageGallery

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive  the image, if picked
                    if(result.getResultCode() == Activity.RESULT_OK){
                        //image picked
                        Intent data  = result.getData();
                        imageUri  = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        //set to  imageView
                        imageIv.setImageURI(imageUri);
                    }
                    else{
                        //cancelled
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(Text_Recognition.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );//end of ActivityResultLauncher
    private void pickImageCamera(){
        Log.d(TAG, "pickImageCamera: ");
        ContentValues values  = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, " Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }//end of pickImageCamera

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we receive the image,if taken from  camera
                    if(result.getResultCode() == Activity.RESULT_OK){
                        //image  is taken  from  camera
                        Log.d(TAG, "onActivityResult: imageUri"+imageUri);
                        imageIv.setImageURI(imageUri);
                    }
                    else{
                        //cancelled
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(Text_Recognition.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );//end of cameraActivityResultLauncher
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private  boolean checkCameraPermissions(){
        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return cameraResult && storageResult;
    }
    private void  requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions,CAMERA_REQUEST_CODE);
    }
    //handle  result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera();
                    }
                    else{
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }

            }//
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickImageGallery();
                    }
                    else{
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }

            }//
            break;
        }//end of switch

    }//end of onRequestPermissionsResult
    private void checkIfFridgeInDatabase() {

        fridgeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    taskFailed = false;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        needToCreateFridge = false;
                    } else {
                        Log.d("TAG", "No such document");
                        needToCreateFridge = true;
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    needToCreateFridge = true;
                    taskFailed = true;
                }
            }
        });


    }


}//end of  class