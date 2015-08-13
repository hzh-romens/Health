package com.romens.extend.scanner;

import android.content.Context;
import android.os.Handler;

import com.romens.extend.scanner.camera.CameraManager;

/**
 * Created by siery on 15/5/13.
 */
public interface ICaptureActivity {
    public Handler getHandler();

    public CameraManager getCameraManager();

    public Context getContext();
}
