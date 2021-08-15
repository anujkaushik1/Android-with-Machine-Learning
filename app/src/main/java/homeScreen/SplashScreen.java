package homeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.androidwithmachinelearning.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView imageVew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageVew=(ImageView)findViewById(R.id.splash_screen);

        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}