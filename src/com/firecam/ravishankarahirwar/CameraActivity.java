/**
 * @author Ravishankar Ahirwar
 * @Date 10 September 2014
 * */
package com.firecam.ravishankarahirwar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private static final String TAG="CameraPreview";
    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    final int RQS_IMAGE1 = 1;
	 final int RQS_IMAGE2 = 2;
ImageView pic; Button LoadImageFromGallery,combineImage;
Uri source1, source2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        pic = (ImageView)findViewById(R.id.Layer1);
        combineImage = (Button)findViewById(R.id.combineImage);
        LoadImageFromGallery = (Button)findViewById(R.id.LoadImageFromGallery);
        
        combineImage.setOnClickListener(new OnClickListener(){

      	   @Override
      	   public void onClick(View arg0) {
      		 pic.setImageBitmap(ProcessingBitmap());
      	   }});
        LoadImageFromGallery.setOnClickListener(new OnClickListener(){

     	   @Override
     	   public void onClick(View arg0) {
     	    Intent intent = new Intent(Intent.ACTION_PICK,
     	      android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     	    startActivityForResult(intent, RQS_IMAGE1);
     	   }});
        mPicture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            	File pictureFile = getOutputMediaFile();
//            	galleryAddPic();
            	source2= Uri.fromFile(pictureFile);
                if (pictureFile == null){
                    Toast.makeText(CameraActivity.this, "Image retrieval failed.", Toast.LENGTH_SHORT)
                    .show();
                    return;
                }

                try {
                	
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                   

                    // Restart the camera preview.
//                    safeCameraOpenInView(mCameraView);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            }
        };
        
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        );
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
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
    
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }
    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }
    /**
     * Used to return the camera File output.
     * @return
     */
    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "UltimateCameraGuideApp");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

//        DialogHelper.showDialog( "Success!","Your picture has been saved!",getActivity());

        return mediaFile;
    }
    @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  if(resultCode == RESULT_OK){
	   switch (requestCode){
	   case RQS_IMAGE1:
		   source1 = data.getData();
		   pic.setImageURI(data.getData());
		   
	    break;
	   case RQS_IMAGE2:
	   
	    break;
	   }
	  }
	 }
    
    private Bitmap ProcessingBitmap(){
  	  Bitmap bm1 = null;
  	  Bitmap bm2 =  null;
  	  Bitmap newBitmap = null;
  	  
  	  try {
  	   bm1 = BitmapFactory.decodeStream(
  	     getContentResolver().openInputStream(source1));
  	   bm2 = BitmapFactory.decodeStream(
  	     getContentResolver().openInputStream(source2));
  	   
  	   int w;
  	   if(bm1.getWidth() >= bm2.getWidth()){
  	    w = bm1.getWidth();
  	   }else{
  	    w = bm2.getWidth();
  	   }
  	   
  	   int h;
  	   if(bm1.getHeight() >= bm2.getHeight()){
  	    h = bm1.getHeight();
  	   }else{
  	    h = bm2.getHeight();
  	   }
  	   
  	   Config config = bm1.getConfig();
  	   if(config == null){
  	    config = Bitmap.Config.ARGB_8888;
  	   }
  	   
  	   newBitmap = Bitmap.createBitmap(w, h, config);
  	   Canvas newCanvas = new Canvas(newBitmap);
  	   
  	   newCanvas.drawBitmap(bm1, 0, 0, null);
  	   
  	   Paint paint = new Paint();
  	   paint.setAlpha(130);
  	   newCanvas.drawBitmap(bm2, 0, 0, paint);
  	   
  	  } catch (FileNotFoundException e) {
  	   // TODO Auto-generated catch block
  	   e.printStackTrace();
  	  }
  	  
  	  return newBitmap;
  	 }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String mCurrentPhotoPath = null;
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}


