package com.example.bestiize.testcentercrop;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import jp.satorufujiwara.player.VideoTexturePresenter;

/**
 * Created by Bestiize on 4/2/2017.
 */

public class CropVideoTextureViewAttacher implements View.OnLayoutChangeListener, OnGestureListener, View.OnTouchListener {
    private CropVideoTextureView videoView;
    private ImageView.ScaleType mScaleType;
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private int videoWidth = 0;
    private int videoHeight = 0;

    public CropVideoTextureViewAttacher(final CropVideoTextureView videoView, VideoTexturePresenter presenter) {
        Log.d("BEST22", "555");
        this.videoView = videoView;
        videoView.addOnLayoutChangeListener(this);
        presenter.addOnVideoSizeChangedListener(new VideoTexturePresenter.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
                videoHeight = height;
                videoWidth = width;
                Log.d("BEST222,", "video : w: " + width + " h: " + height);
                updateBaseMatrix();
            }
        });
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        update();


    }

    public void update() {
        updateBaseMatrix();
    }

    public void updateBaseMatrix() {
        if (videoWidth <= 0 && videoHeight <= 0) {
            return;
        }


        final float viewWidth = getImageViewWidth(videoView);
        final float viewHeight = getImageViewHeight(videoView);
        final int drawableWidth = videoWidth;
        final int drawableHeight = videoHeight;

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
        videoView.setTransform(matrix);


    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        //mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // Update our base matrix, as the bounds have changed
        //updateBaseMatrix(videoView);
    }

    private int getImageViewWidth(CropVideoTextureView videoView) {
        return videoView.getWidth() - videoView.getPaddingLeft() - videoView.getPaddingRight();
    }

    private int getImageViewHeight(CropVideoTextureView videoView) {
        return videoView.getHeight() - videoView.getPaddingTop() - videoView.getPaddingBottom();
    }

    @Override
    public void onDrag(float x, float y) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("BEST22", "ontouch");
        return false;
    }
}

