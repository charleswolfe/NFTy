package us.gajo.stickapic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity {


	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		  	getMenuInflater().inflate(R.menu.activity_main, menu);
		  	return true;
	  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   
      
      //new intent
      Intent takePic = new Intent(getBaseContext(), ImagePickActivity.class);
      startActivity(takePic);
      
      
      Log.d("afterPic","So im in the main activcity");
    }

    
}
