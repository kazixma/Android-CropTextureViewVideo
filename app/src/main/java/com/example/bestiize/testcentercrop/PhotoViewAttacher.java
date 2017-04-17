package com.example.bestiize.testcentercrop;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Bestiize on 3/24/2017.
 */

public class PhotoViewAttacher implements View.OnLayoutChangeListener, OnGestureListener,View.OnTouchListener {
    private ImageView imageView;
    private ImageView.ScaleType mScaleType;
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();

    public PhotoViewAttacher(ImageView imageView) {
        Log.d("BEST22","555");
        this.imageView = imageView;
        imageView.addOnLayoutChangeListener(this);
        this.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("BEST12","55555");
                return false;
            }
        });
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        update();


    }

    public void update() {
        updateBaseMatrix(imageView.getDrawable());
    }

    public void updateBaseMatrix(Drawable drawable) {
        if (drawable == null) {
            return;
        }


        final float viewWidth = getImageViewWidth(imageView);
        final float viewHeight = getImageViewHeight(imageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        Log.d("BEST555", "vw: " + viewWidth + " vh: " + viewHeight + " dh: " + drawableHeight + " dw: " + drawableWidth);


        mBaseMatrix.reset();

        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;

        if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            mBaseMatrix.postScale(scale, scale);
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        }

        resetMatrix();


    }

    private void resetMatrix() {
      //  mSuppMatrix.reset();
        setImageViewMatrix(getDrawMatrix());

    }
    private void setImageViewMatrix(Matrix matrix) {
        imageView.setImageMatrix(matrix);


    }
    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        //mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // Update our base matrix, as the bounds have changed
        updateBaseMatrix(imageView.getDrawable());
    }

    private int getImageViewWidth(ImageView imageView) {
        return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    @Override
    public void onDrag(float x, float y) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("BEST22","ontouch");
        return false;
    }
}
