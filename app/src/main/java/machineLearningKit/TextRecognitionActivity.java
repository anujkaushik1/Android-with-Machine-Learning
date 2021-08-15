package machineLearningKit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidwithmachinelearning.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class TextRecognitionActivity extends AppCompatActivity {

    private ImageView textRecognitionImageView;
    private TextView textRecognitionResultTextView;
    private MaterialButton selectImageGallery;
    private MaterialButton selectCamera;
    public static final int REQUEST_CODE_FILE_MANAGER_IMAGE =121;
    public static final int REQUEST_CODE_CAMERA=100;
    public static final int PERMISSION_REQUEST_CODE_CAMERA_EXTERNAL_STORAGE=70;
    private String permissions[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        textRecognitionImageView=(ImageView)findViewById(R.id.textRecognitionImageView);
        textRecognitionResultTextView=(TextView)findViewById(R.id.textRecognitionResult);
        selectImageGallery=(MaterialButton)findViewById(R.id.selectImageGallery);
        selectCamera=(MaterialButton)findViewById(R.id.selectCamera);

        permissions=new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        fileManager();
        selectCameraImage();




    }
    private void fileManager(){
        selectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");                   //help to get image with any extention like jpeg,png etc
                intent.setAction(Intent.ACTION_GET_CONTENT);  //allowing the user to pick one image from the gallery and return the URI
                startActivityForResult(intent, REQUEST_CODE_FILE_MANAGER_IMAGE);   //redirects to some another activity like camera , gallery etc
            }
        });
    }

    private boolean hasPermissions(Context context, String... permissions){  //we can pass more than one string
        if(context!=null && permissions!=null){
            for(String value : permissions){
                if(ActivityCompat.checkSelfPermission(context,value)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }

        }
        return true;
    }



        private void selectCameraImage(){
        selectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasPermissions(TextRecognitionActivity.this,permissions)){
                    ActivityCompat.requestPermissions(TextRecognitionActivity.this,permissions,PERMISSION_REQUEST_CODE_CAMERA_EXTERNAL_STORAGE);

                }else{
                    Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   //open camera
                    startActivityForResult(intent,REQUEST_CODE_CAMERA);
                }

            }
        });


        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode== PERMISSION_REQUEST_CODE_CAMERA_EXTERNAL_STORAGE){

            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   //open camera
                startActivityForResult(intent,REQUEST_CODE_CAMERA);
            }
            else{
                Toast toast=Toast.makeText(TextRecognitionActivity.this,"Permission is denied",Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {  //get the result like taken image from galley or camera
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_FILE_MANAGER_IMAGE){
            settingImageFileManager(data);
        }
        if(requestCode==REQUEST_CODE_CAMERA){
            settingImageCamera(data);
        }
    }

    private void settingImageFileManager(Intent data) {
        if (data != null) {
            textRecognitionImageView.setImageURI(data.getData());
            firebaseTextRecognizer(data.getData());

        }
    }
    private void settingImageCamera(Intent data) {
        if (data != null) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");  //get captured image
            textRecognitionImageView.setImageBitmap(captureImage);
            ByteArrayOutputStream bytes= new ByteArrayOutputStream();
            captureImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            String path= MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),captureImage,"val",null);
            Uri uri= Uri.parse(path);
            firebaseTextRecognizer(uri);

        }
    }
    private void firebaseTextRecognizer(Uri uri){

        FirebaseVisionImage firebaseVisionImage;
        try {
            firebaseVisionImage = FirebaseVisionImage.fromFilePath(getApplicationContext(), uri);

            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {

                            textRecognitionResultTextView.setText(result.getText());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(TextRecognitionActivity.this, "fail", Toast.LENGTH_LONG);
                            toast.show();
                            System.out.println(e.getMessage());
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}