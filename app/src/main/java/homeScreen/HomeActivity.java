package homeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.androidwithmachinelearning.R;

import machineLearningKit.FaceDetectionActivity;
import machineLearningKit.TextRecognitionActivity;

public class HomeActivity extends AppCompatActivity {

    private CardView textRecognitionCardView;
    private CardView faceDetectionCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textRecognitionCardView=(CardView)findViewById(R.id.text_recognition_cardview);
        faceDetectionCardView=(CardView)findViewById(R.id.face_detection_cardview);
        textRecognitionActivity();
        faceDetectionCardView();
    }

    private void textRecognitionActivity(){
        textRecognitionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, TextRecognitionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void faceDetectionCardView(){
        faceDetectionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this, FaceDetectionActivity.class);
                startActivity(intent);
            }
        });
    }

}