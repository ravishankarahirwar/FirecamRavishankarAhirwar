/**
 * 
 */
package com.firecam.ravishankarahirwar.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author ravi
 *
 */
public class CameraUtil {

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    
}
