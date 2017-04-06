package com.example.xiaotinghong.rotategesturedetector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

/**
 * Created by xiaotinghong on 2017-04-06.
 */

public class RotateGestureDetector {
    private static final String TAG = "RotateGestureDetector";

    public interface OnRotateGestureListener {

        boolean onRotate(RotateGestureDetector detector);

        boolean onRotateBegin(RotateGestureDetector detector);

        void onRotateEnd(RotateGestureDetector detector);
    }

    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener {

        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        @Override
        public void onRotateEnd(RotateGestureDetector detector) {
            // Intentionally empty
        }
    }

    private final Context mContext;
    private final OnRotateGestureListener mListener;

    private float mFocusX;
    private float mFocusY;

    private float mCurrSpan;
    private float mPrevSpan;
    private float mCurrDiffX;
    private float mCurrDiffY;
    private float mPrevDiffX;
    private float mPrevDiffY;
    private long mCurrTime;
    private long mPrevTime;
    private boolean mInProgress;

    public RotateGestureDetector(Context context, @NonNull OnRotateGestureListener listener) {
        mContext = context;
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getActionMasked();

        final boolean streamComplete = action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            // Reset any scale in progress with the listener.
            // If it's an ACTION_DOWN we're beginning a new event stream.
            // This means the app probably didn't give us all the events. Shame on it.
            if (mInProgress) {
                mListener.onRotateEnd(this);
                mInProgress = false;
            }
            if (streamComplete) {
                return true;
            }
        }

        final boolean configChanged =
                action == MotionEvent.ACTION_POINTER_UP ||
                        action == MotionEvent.ACTION_POINTER_DOWN;
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? event.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
        final int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float focusX = sumX / div;
        final float focusY = sumY / div;

        // Determine average deviation from focal point
        float devSumX = 0, devSumY = 0;
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            devSumX += Math.abs(event.getX(i) - focusX);
            devSumY += Math.abs(event.getY(i) - focusY);
        }
        final float devX = devSumX / div;
        final float devY = devSumY / div;

        // Span is the average distance between touch points through the focal point;
        // i.e. the diameter of the circle with a radius of the average deviation from the focal point.
        final float spanX = devX * 2;
        final float spanY = devY * 2;
        final float span = (float) Math.sqrt(spanX * spanX + spanY * spanY);

        final float diffX = count >= 2 ? event.getX(0) - event.getX(1) : 0;
        final float diffY = count >= 2 ? event.getY(0) - event.getY(1) : 0;


        // Dispatch begin/end events as needed.
        // If the configuration changes, notify the app to reset its current state by beginning a fresh scale event stream.
        if (mInProgress && (span == 0 || configChanged)) {
            mListener.onRotateEnd(this);
            mInProgress = false;
        }
        if (configChanged) {
            mPrevDiffX = mCurrDiffX = diffX;
            mPrevDiffY = mCurrDiffY = diffY;
            mPrevSpan = mCurrSpan = span;
        }
        if (!mInProgress && span != 0) {
            mFocusX = focusX;
            mFocusY = focusY;
            mInProgress = mListener.onRotateBegin(this);
        }

        // Handle motion; focal point and span/scale factor are changing.
        if (action == MotionEvent.ACTION_MOVE) {
            mCurrDiffX = diffX;
            mCurrDiffY = diffY;
            mCurrSpan = span;
            mFocusX = focusX;
            mFocusY = focusY;

            boolean updatePrev = true;
            if (mInProgress) {
                updatePrev = mListener.onRotate(this);
            }

            if (updatePrev) {
                mPrevDiffX = mCurrDiffX;
                mPrevDiffY = mCurrDiffY;
                mPrevSpan = mCurrSpan;
            }
        }

        return true;
    }

    public boolean isInProgress() {
        return mInProgress;
    }

    public float getFocusX() {
        return mFocusX;
    }

    public float getFocusY() {
        return mFocusY;
    }

    public float getCurrentSpan() {
        return mCurrSpan;
    }

    public float getCurrentDiffX() {
        return mCurrDiffX;
    }

    public float getCurrentDiffY() {
        return mCurrDiffY;
    }

    public float getPreviousSpan() {
        return mPrevSpan;
    }

    public float getPreviousSpanX() {
        return mPrevDiffX;
    }

    public float getPreviousSpanY() {
        return mPrevDiffY;
    }

    public long getTimeDelta() {
        return mCurrTime - mPrevTime;
    }

    public long getEventTime() {
        return mCurrTime;
    }

    public float getDeltaRotation() {
        float anglePrev = (float) Math.atan2(mPrevDiffY, mPrevDiffX);
        float angleCurr = (float) Math.atan2(mCurrDiffY, mCurrDiffX);

        float deltaAngle = ((float) Math.toDegrees(anglePrev - angleCurr)) % 360;
        if (deltaAngle < -180.f) deltaAngle += 360.f;
        if (deltaAngle > 180.f) deltaAngle -= 360.f;

        return deltaAngle;
    }
}

