package com.cubosystems.findme.Helper;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

/**
 * Created by Administrator on 7/24/2018.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    Camera mCamera;
    SurfaceHolder holder;
    CameraSource cameraSource;

    public CameraView(Context context, Camera camera, CameraSource source){
        super(context);
        cameraSource = source;
        mCamera = camera;
        mCamera.setDisplayOrientation(90);

        //setting camera parameters
        Camera.Parameters params = mCamera.getParameters();
        params.setJpegQuality(100);
        params.setPreviewSize(352,288);
        mCamera.setParameters(params);

        //implementing holder
        holder = getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //setting the camera preview and starting it
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            cameraSource.start(surfaceHolder);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //when changed, stop the preview and start it again
        if(surfaceHolder.getSurface() == null){
            return;
        }

        try{
            mCamera.stopPreview();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            cameraSource.stop();
        }
    }
}
