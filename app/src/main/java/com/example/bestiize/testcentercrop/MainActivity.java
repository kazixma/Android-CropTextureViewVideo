package com.example.bestiize.testcentercrop;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import jp.satorufujiwara.player.VideoSource;
import jp.satorufujiwara.player.VideoTexturePresenter;
import jp.satorufujiwara.player.assets.AssetsVideoSource;

public class MainActivity extends AppCompatActivity {
    private boolean isplay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final TextureVideoView textureVideoView = (TextureVideoView) findViewById(R.id.crop_video);
//        textureVideoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
//
//        textureVideoView.setDataSource("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4");
//        textureVideoView.play();
//
//        Button button = (Button) findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isplay){
//                    textureVideoView.pause();
//                    isplay = false;
//                }else{
//                    isplay = true;
//                    textureVideoView.play();
//                }
//
//            }
//        });

        //  textureVideoView.seekTo(5000);

        //    PhotoView photoView =(PhotoView) findViewById(R.id.photo_view);
        //  photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final CropVideoTextureView photoView = (CropVideoTextureView) findViewById(R.id.crop_video);
//        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String pathUrl ="http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"inshot" + "/InShot_20170411_165958.mp4";
        Log.d("BEST555","path : "+path);
        final VideoTexturePresenter videoTexturePresenter = new VideoTexturePresenter(photoView);
        VideoSource source = AssetsVideoSource
                .newBuilder(Uri.parse(path), "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36")
                .bufferSegmentSize(64 * 2048)
                .bufferSegmentCount(2048)
                .build();
        videoTexturePresenter.setSource(source);
        videoTexturePresenter.prepare();
//        CropVideoTextureViewAttacher attacher = new CropVideoTextureViewAttacher(photoView, videoTexturePresenter);
//       // attacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photoView.setScaleType(CropVideoTextureView.ScaleType.CENTER_CROP);
        videoTexturePresenter.play();
        videoTexturePresenter.addOnVideoSizeChangedListener(new VideoTexturePresenter.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
                photoView.setmVideoHeight(height);
                photoView.setmVideoWidth(width);
                photoView.updateTextureViewSize();

                Log.d("BEST555", "" + width + "    " + height);
            }
        });

        Log.d("BEST22", "111");
    }
}
