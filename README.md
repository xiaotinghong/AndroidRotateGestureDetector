# AndroidRotateGestureDetector
An Android rotate gesture detector, and it's presented with the sample of rotating a imageview.

Since there is no build-in RotateGestureDetector in current Android Kit, I decided to write one myself. This RotateGestureDetector is created based on the official ScaleGestureDetector https://developer.android.com/reference/android/view/ScaleGestureDetector.html.

I also created a MoveGestureDetector here: https://github.com/xiaotinghong/AndroidMoveGestureDetector

The way you can use the RotateGestureDetector is very similar to use the ScaleGestureDetector since they have similar structure.

# Methouds
```
public float getDeltaRotation()

public boolean isInProgress() 

public float getFocusX() 

public float getFocusY() 

public float getCurrentSpan() 

public float getCurrentDiffX() 

public float getCurrentDiffY() 

public float getPreviousSpan()

public float getPreviousSpanX()

public float getPreviousSpanY() 

public long getTimeDelta()

public long getEventTime() 
```
# How to use it
To use the RotateGestureDetector, simply add the file "RotateGestureDetector.java" into your own project.

After that, it's the coding time :)

First, declare the RotateGestureDetector object in your activity or view.
```
private RotateGestureDetector rotateGestureDetector;
```
Then, extend the RotateGestureDetector.SimpleOnMoveGestureListener to let you do your own sepcial moves. Most of your own codes about what to react on the move gesture will go here.
```
private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
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
```
Assign this RotateListener to your RotateGestureDetector object.
```
@Override
protected void onCreate(Bundle savedInstanceState) {

    ...

    rotateGestureDetector = new RotateGestureDetector(this, new RotateListener());
}
```
Don't forget the last step, let the RotateGestureDetector object detect the touch event! There will be nothing happen on the rotate gesture if you miss this step.
```
@Override
public boolean onTouchEvent(MotionEvent event) {
    // Let rotateGestureDetector detects all events
    if (rotateGestureDetector != null) {
        rotateGestureDetector.onTouchEvent(event);
    }
    return true;
}
```
That's it. Enjoy!
