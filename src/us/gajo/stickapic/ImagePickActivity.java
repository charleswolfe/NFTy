package us.gajo.stickapic;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
 

public class ImagePickActivity extends Activity {
 
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private ImageView imageView;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Context context;

	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_takepic);

	    context = this;
	    imageView = (ImageView) findViewById(R.id.imageView1);
	    // create Intent to take a picture and return control to the calling application
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

	    // start the image capture Intent
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
//TODO use strings xml for name
		String app_name = getString(R.string.app_name);
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), app_name);
	

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    Log.d("image", "before saving image");
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	        Log.d("image",mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  Log.d("","in onActivity Result requestCode[" + Integer.toString(requestCode) + "] "); //i go back to main, then back to here?

		  
		  uploadActivity upit = new uploadActivity();
		  String image_url = upit.upload(fileUri);
		
		  
		  
	        imageView.setImageURI(null); 
	        imageView.setImageURI(fileUri);
	        
	        Intent tag_intent;
	        tag_intent = new Intent(this, TagWriteActivity.class);
	        tag_intent.putExtra("image_url", image_url); // set the image file name
        	startActivity(tag_intent);
	        
	       //Log.d("filUri", fileUri.toString());
	      
	        File mediaFile;
			try {
				mediaFile = new File(new URI(fileUri.toString()));
				//mediaFile.delete();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	        
	   
	    super.onActivityResult(requestCode, resultCode, data);
	  }
	
	
} 