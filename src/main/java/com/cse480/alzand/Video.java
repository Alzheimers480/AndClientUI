package com.cse480.alzand;
import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;

import java.util.List;


public class Video extends CameraCaptureSession{

    @Override
    public CameraDevice getDevice() {
        return null;
    }

    @Override
    public int capture(CaptureRequest request, CaptureCallback listener, Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int captureBurst(List<CaptureRequest> requests, CaptureCallback listener, Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int setRepeatingRequest(CaptureRequest request, CaptureCallback listener, Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public int setRepeatingBurst(List<CaptureRequest> requests, CaptureCallback listener, Handler handler) throws CameraAccessException {
        return 0;
    }

    @Override
    public void stopRepeating() throws CameraAccessException {

    }

    @Override
    public void abortCaptures() throws CameraAccessException {

    }

    @Override
    public void close() {

    }
}
