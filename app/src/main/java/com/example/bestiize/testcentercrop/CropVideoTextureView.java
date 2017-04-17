package com.example.bestiize.testcentercrop;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.sprylab.android.widget.TextureVideoView;

import jp.satorufujiwara.player.VideoTextureView;

/**
 * Created by Bestiize on 4/2/2017.
 */

public class CropVideoTextureView extends VideoTextureView {

    private float mLastTouchX;
    private float mLastTouchY;

    private int pivotPointX;
    private int pivotPointY;

    private float viewWidth;
    private float viewHeight;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    private Matrix matrix;
    private float mVideoHeight;
    private float mVideoWidth;

    private ScaleType mScaleType;

    public CropVideoTextureView(Context context) {
        this(context, null);
    }

    public CropVideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        matrix = new Matrix();

    }

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM
    }

    public float getmVideoHeight() {
        return mVideoHeight;
    }

    public void setmVideoHeight(float mVideoHeight) {
        this.mVideoHeight = mVideoHeight;
    }

    public float getmVideoWidth() {
        return mVideoWidth;
    }

    public void setmVideoWidth(float mVideoWidth) {
        this.mVideoWidth = mVideoWidth;
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public void updateTextureViewSize() {
        viewWidth = getWidth();
        viewHeight = getHeight();

        Log.d("BEST555", "viewWidth : " + viewWidth + " viewHeight : " + viewHeight);

        if (mVideoWidth > mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);

            Log.d("BEST555", "scale X " + scaleX);


        } else if (mVideoHeight > mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
            Log.d("BEST555", "scale Y " + scaleY);

        }

//        if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
//            scaleX = viewHeight / mVideoHeight;
//
//        } else if (mVideoWidth > viewWidth && mVideoHeight == viewHeight) {
//
//
//        } else if (mVideoWidth > viewWidth && mVideoHeight == viewHeight) {
//
//        }


//        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
//            scaleX = mVideoWidth / viewWidth;
//            scaleY = mVideoHeight / viewHeight;
//            Log.d("BEST555", "condition 1 " + scaleX + " " + scaleY);
//        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
//            //scaleY = viewWidth / mVideoWidth;
//            scaleX = viewHeight / mVideoHeight;
//            Log.d("BEST555", "condition 2 " + scaleX + " " + scaleY);
//        } else if (viewWidth > mVideoWidth) {
//            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
//            Log.d("BEST555", "condition 3 " + scaleX + " " + scaleY);
//        } else if (viewHeight > mVideoHeight) {
//            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
//            Log.d("BEST555", "condition 4 " + scaleX + " " + scaleY);
//        }else{
//
//            Log.d("BEST555","condition 5"+mVideoHeight+" "+mVideoWidth);
//
//        }

        // Calculate pivot points, in our case crop from center
//        int pivotPointX;
//        int pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = (int) (viewWidth);
                pivotPointY = (int) (viewHeight);
                break;
            case CENTER_CROP:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
            default:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
        }

        updateMatrix();
    }

    private void updateMatrix() {

        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);
        setTransform(matrix);
        invalidate();


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = getActiveX(event);
                mLastTouchY = getActiveY(event);


                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = getActiveX(event);
                final float y = getActiveY(event);
                final float dx = x - mLastTouchX, dy = y - mLastTouchY;
//
                if (mVideoHeight > mVideoWidth) {
                    if (dy > 0) {
                        if (pivotPointY > 0) {

                            pivotPointY = pivotPointY - (int) dy;
                        }


                    } else if (dy < 0) {

                        if (pivotPointY < viewHeight) {
                            pivotPointY = pivotPointY - (int) dy;
                        }

                    }

                    if (pivotPointY < 0) {
                        pivotPointY = 0;
                    } else if (pivotPointY > viewHeight) {
                        pivotPointY = (int) viewHeight;
                    }


                } else if (mVideoWidth > mVideoHeight) {

                    if (dx > 0) {
                        if (pivotPointX > 0) {
                            pivotPointX = pivotPointX - (int) dx;
                        }

                    } else if (dx < 0) {
                        if (pivotPointX < viewWidth) {
                            pivotPointX = pivotPointX - (int) dx;
                        }

                    }

                    if (pivotPointX < 0) {
                        pivotPointX = 0;
                    } else if (pivotPointX > viewWidth) {
                        pivotPointX = (int) viewWidth;
                    }


                } else {


                }


                updateMatrix();

                Log.d("BEST444", "pivot x : " + pivotPointX + " pivot y : " + pivotPointY);
                mLastTouchX = x;
                mLastTouchY = y;
                Log.d("BEST555", "last x : " + mLastTouchX + " y " + mLastTouchY);
                Log.d("BEST555", "move : x : " + x + " y :" + y + " dx : " + dx + " dy : " + dy);


                break;

            }
            case MotionEvent.ACTION_UP: {

                break;
            }

            case MotionEvent.ACTION_CANCEL: {

                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {


                break;
            }
            default: {
                return false;
            }


        }


        return true;
    }

    private float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(0);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    private float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(0);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    @Override
    public void setVideoWidthHeightRatio(float widthHeightRatio) {
        //super.setVideoWidthHeightRatio(widthHeightRatio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
