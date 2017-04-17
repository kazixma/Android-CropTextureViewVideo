package com.example.bestiize.testcentercrop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by Bestiize on 4/2/2017.
 */

public class TextureVideoView extends TextureView implements TextureView.SurfaceTextureListener {

    // Indicate if logging is on
    public static final boolean LOG_ON = true;

    // Log tag
    private static final String TAG = TextureVideoView.class.getName();

    private MediaPlayer mMediaPlayer;

    private float mVideoHeight;
    private float mVideoWidth;

    private boolean mIsDataSourceSet;
    private boolean mIsViewAvailable;
    private boolean mIsVideoPrepared;
    private boolean mIsPlayCalled;

    private ScaleType mScaleType;
    private State mState;

    private float mLastTouchX;
    private float mLastTouchY;

    private int pivotPointX;
    private int pivotPointY;

    private float viewWidth;
    private float viewHeight;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    private Matrix matrix;
    private int mActivePointerId = INVALID_POINTER_ID;

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM
    }

    public enum State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    public TextureVideoView(Context context) {
        super(context);
        initView();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        matrix = new Matrix();
        initPlayer();
        setScaleType(ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);


    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    private void updateTextureViewSize() {
        viewWidth = getWidth();
        viewHeight = getHeight();


        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth;
            scaleY = mVideoHeight / viewHeight;
            Log.d("BEST555", "condition 1 " + scaleX + " " + scaleY);
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            //scaleY = viewWidth / mVideoWidth;
            scaleX = viewHeight / mVideoHeight;
            Log.d("BEST555", "condition 2 " + scaleX + " " + scaleY);
        } else if (viewWidth > mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
            Log.d("BEST555", "condition 3 " + scaleX + " " + scaleY);
        } else if (viewHeight > mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
            Log.d("BEST555", "condition 4 " + scaleX + " " + scaleY);
        }

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

    float dx, dy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = getActiveX(event);
                mLastTouchY = getActiveY(event);
                //   dx = pivotPointX - mLastTouchX;
                // dy = pivotPointY - mLastTouchY;


                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = getActiveX(event);
                final float y = getActiveY(event);
                final float dx = x - mLastTouchX, dy = y - mLastTouchY;
//
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
                updateMatrix();

                Log.d("BEST444", "pivot x : " + pivotPointX);
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

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
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

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mIsVideoPrepared = false;
        mIsPlayCalled = false;
        mState = State.UNINITIALIZED;
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(String)
     */
    public void setDataSource(String path) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(path);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(android.content.Context, android.net.Uri)
     */
    public void setDataSource(Context context, Uri uri) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(context, uri);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(java.io.FileDescriptor)
     */
    public void setDataSource(AssetFileDescriptor afd) {
        initPlayer();

        try {
            long startOffset = afd.getStartOffset();
            long length = afd.getLength();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), startOffset, length);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void prepare() {
        try {
            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            mVideoWidth = width;
                            mVideoHeight = height;
                            updateTextureViewSize();
                        }
                    }
            );
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mState = State.END;
                    log("Video has ended.");

                    if (mListener != null) {
                        mListener.onVideoEnd();
                    }
                }
            });

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mIsVideoPrepared = true;
                    if (mIsPlayCalled && mIsViewAvailable) {
                        log("Player is prepared and play() was called.");
                        play();
                    }

                    if (mListener != null) {
                        mListener.onVideoPrepared();
                    }
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     * <p>
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void play() {
        if (!mIsDataSourceSet) {
            log("play() was called but data source was not set.");
            return;
        }

        mIsPlayCalled = true;

        if (!mIsVideoPrepared) {
            log("play() was called but video is not prepared yet, waiting.");
            return;
        }

        if (!mIsViewAvailable) {
            log("play() was called but view is not available yet, waiting.");
            return;
        }

        if (mState == State.PLAY) {
            log("play() was called but video is already playing.");
            return;
        }

        if (mState == State.PAUSE) {
            log("play() was called but video is paused, resuming.");
            mState = State.PLAY;
            mMediaPlayer.start();
            return;
        }

        if (mState == State.END || mState == State.STOP) {
            log("play() was called but video already ended, starting over.");
            mState = State.PLAY;
            mMediaPlayer.seekTo(0);
            mMediaPlayer.start();
            return;
        }

        mState = State.PLAY;
        mMediaPlayer.start();
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (mState == State.PAUSE) {
            log("pause() was called but video already paused.");
            return;
        }

        if (mState == State.STOP) {
            log("pause() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("pause() was called but video already ended.");
            return;
        }

        mState = State.PAUSE;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    public void stop() {
        if (mState == State.STOP) {
            log("stop() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
            log("stop() was called but video already ended.");
            return;
        }

        mState = State.STOP;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
    }

    /**
     * @see android.media.MediaPlayer#setLooping(boolean)
     */
    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    /**
     * @see android.media.MediaPlayer#seekTo(int)
     */
    public void seekTo(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    /**
     * @see android.media.MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    static void log(String message) {
        if (LOG_ON) {
            Log.d(TAG, message);
        }
    }

    private MediaPlayerListener mListener;

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
     */
    public void setListener(MediaPlayerListener listener) {
        mListener = listener;
    }

    public interface MediaPlayerListener {

        public void onVideoPrepared();

        public void onVideoEnd();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);
        mIsViewAvailable = true;
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
            log("View is available and play() was called.");
            play();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}