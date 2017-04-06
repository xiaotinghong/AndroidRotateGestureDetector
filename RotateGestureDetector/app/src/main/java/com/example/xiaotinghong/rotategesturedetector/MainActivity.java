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

    List<ImageView> allImageViews;
    private ImageView selectedImageView;

    private RotateGestureDetector rotateGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add all the image view to the candidate list
        allImageViews = new LinkedList<>();
        allImageViews.add( (ImageView) findViewById(R.id.image_view_1) );
        allImageViews.add( (ImageView) findViewById(R.id.image_view_2) );
        allImageViews.add( (ImageView) findViewById(R.id.image_view_3) );

        selectedImageView = null;

        rotateGestureDetector = new RotateGestureDetector(this, new MoveListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let rotateGestureDetector detects all events
        if (rotateGestureDetector != null) {
            rotateGestureDetector.onTouchEvent(event);
        }

        // Decide which image view should be moved based on events
        updateSelectedImageView(event);

        return true;
    }


    private void updateSelectedImageView(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if(!rotateGestureDetector.isInProgress()) {
                    selectedImageView = selectImageViewOnEvent(event);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                selectedImageView = null;
                break;
            }
        }
    }

    private ImageView selectImageViewOnEvent (MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        for(ImageView imageView: allImageViews) {
            // Calculate the hit rect of the current image view
            Rect hitRect = new Rect();
            imageView.getHitRect(hitRect);

            // check if the touch point land within the hit rect
            if( hitRect.contains((int) touchX, (int) touchY) ) {
                return imageView;
            }
        }

        return null;
    }

    private boolean checkIfContainsInView(View view, float x, float y) {
        int[] screenPosition = new int[2];
        view.getLocationOnScreen(screenPosition);

        int topLeftX = screenPosition[0];
        int topLeftY = screenPosition[1];
        int bottomRightX = screenPosition[0] + view.getWidth();
        int bottomRightY = screenPosition[0] + view.getHeight();

        return x >= topLeftX && x <= bottomRightX && y >= topLeftY && y <= bottomRightY;
    }

    private class MoveListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            // The operations based on move touch event go here

            if(selectedImageView == null) {
                return false;
            }

            float deltaAngle = - detector.getDeltaRotation();
            selectedImageView.setRotation(selectedImageView.getRotation() + deltaAngle);

            return true;
        }
    }
}
