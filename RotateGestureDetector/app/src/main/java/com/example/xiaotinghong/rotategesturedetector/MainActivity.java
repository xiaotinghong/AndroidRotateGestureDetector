package com.example.xiaotinghong.rotategesturedetector;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Rotate Gesture Detector";

    private ImageView imageView;

    private RotateGestureDetector rotateGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);

        rotateGestureDetector = new RotateGestureDetector(this, new MoveListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let rotateGestureDetector detects all events
        if (rotateGestureDetector != null) {
            rotateGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    private class MoveListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            // The operations based on rotate gesture go here
            if(imageView == null) {
                return false;
            }

            float deltaAngle = - detector.getDeltaRotation();
            imageView.setRotation((imageView.getRotation() + deltaAngle) % 360);

            return true;
        }
    }
}
