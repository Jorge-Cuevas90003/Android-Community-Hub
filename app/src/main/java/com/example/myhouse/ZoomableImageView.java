package com.example.myhouse;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();

    private float[] matrixValues = new float[9];
    private float currentZoom = 1f;
    private float oldDist = 1f;

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private ScaleGestureDetector scaleGestureDetector;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        setScaleType(ScaleType.MATRIX);


        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                float scale = detector.getScaleFactor();
                float newZoom = getCurrentZoom() * scale;


                newZoom = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));


                matrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());


                checkAndSetTranslation();

                setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {

                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);


                    checkAndSetTranslation();
                    setImageMatrix(matrix);
                } else if (mode == ZOOM) {

                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        matrix.set(savedMatrix);
                        matrix.postScale(scale, scale, mid.x, mid.y);


                        checkAndSetTranslation();
                        setImageMatrix(matrix);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        return true;
    }


    private void checkAndSetTranslation() {

        matrix.getValues(matrixValues);


        RectF drawableRect = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());


        matrix.mapRect(drawableRect);


        float deltaX = 0, deltaY = 0;


        if (drawableRect.width() <= viewRect.width()) {

            deltaX = (viewRect.width() - drawableRect.width()) / 2 - drawableRect.left;
        } else {

            if (drawableRect.left > 0) deltaX = -drawableRect.left;
            if (drawableRect.right < viewRect.width()) deltaX = viewRect.width() - drawableRect.right;
        }


        if (drawableRect.height() <= viewRect.height()) {

            deltaY = (viewRect.height() - drawableRect.height()) / 2 - drawableRect.top;
        } else {

            if (drawableRect.top > 0) deltaY = -drawableRect.top;
            if (drawableRect.bottom < viewRect.height()) deltaY = viewRect.height() - drawableRect.bottom;
        }


        matrix.postTranslate(deltaX, deltaY);
    }


    private float getCurrentZoom() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    private void midPoint(PointF point, MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        point.set(x, y);
    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    public void resetZoom() {
        matrix.reset();
        setImageMatrix(matrix);
    }
}