package machineLearningKit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidwithmachinelearning.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {

    private MaterialButton selectCamera;
    private MaterialButton selectImageGallery;
    private ImageView faceDetectionImageView;
    public static final int REQUEST_CODE_FILE_MANAGER_IMAGE =121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        selectImageGallery=(MaterialButton)findViewById(R.id.selectImageGallery);
        selectCamera=(MaterialButton)findViewById(R.id.selectCamera);
        faceDetectionImageView=(ImageView)findViewById(R.id.faceDetectionImageView);

        fileManager();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_FILE_MANAGER_IMAGE){
            settingImageFileManager(data);
        }
    }
    private void settingImageFileManager(Intent data) {
        if (data != null) {
            faceDetectionImageView.setImageURI(data.getData());   //set image in text view
            firebaseFaceDetector(data.getData());

        }
    }

    private void firebaseFaceDetector(Uri uri)  {
        FirebaseVisionImage firebaseVisionImage;



        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            Bitmap mutableBmp= bitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas= new Canvas(mutableBmp);
            firebaseVisionImage=FirebaseVisionImage.fromFilePath(FaceDetectionActivity.this,uri);

            FirebaseVisionFaceDetectorOptions options =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                            .build();


            FirebaseVisionFaceDetector detector= FirebaseVision.getInstance().getVisionFaceDetector(options);

            Task<List<FirebaseVisionFace>> result =
                    detector.detectInImage(firebaseVisionImage)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionFace>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionFace> faces) {

                                            Toast toast=Toast.makeText(FaceDetectionActivity.this,"Success",Toast.LENGTH_LONG);
                                            toast.show();

                                            for (FirebaseVisionFace face : faces) {
                                                Rect bounds = face.getBoundingBox();

                                                Paint paint= new Paint();
                                                paint.setColor(Color.YELLOW);
                                                paint.setStyle(Paint.Style.STROKE);
                                                canvas.drawRect(bounds,paint);

                                                float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees


                                                FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                                if (leftEar != null) {
                                                    FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                }


                                                List<FirebaseVisionPoint> leftEyeContour =
                                                        face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                                List<FirebaseVisionPoint> upperLipBottomContour =
                                                        face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();


                                                if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                    float smileProb = face.getSmilingProbability();
                                                }
                                                if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                    float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                }


                                                if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                    int id = face.getTrackingId();
                                                }
                                            }

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast toast= Toast.makeText(FaceDetectionActivity.this,"Please try again",Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    });



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}