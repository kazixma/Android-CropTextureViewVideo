package com.example.bestiize.testcentercrop;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Bestiize on 3/24/2017.
 */

public class PhotoView extends ImageView {
    private PhotoViewAttacher attacher;
    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);



    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        attacher.setScaleType(scaleType);
    }
}
