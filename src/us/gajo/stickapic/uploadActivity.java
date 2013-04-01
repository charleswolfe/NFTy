package us.gajo.stickapic;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
 
public class uploadActivity extends Activity {
	private static final String TAG = "tag";

	private Context context;
	   
	 public String upload(Uri fileUri) {
		 
		 String image_url = "";
		
			try {
				image_url = goForUpload(fileUri);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		 Log.i("rets","image" + image_url);
		return image_url;
	 }
	 
	 private String goForUpload(final Uri fileUri) throws InterruptedException, ExecutionException
	 {
			    
		 String image_url= "";

		    ExecutorService executor = Executors.newSingleThreadExecutor();
		    Callable<String> callable = new Callable<String>() {
		        @Override
		        public String call() {       

				 String upLoadServerUri = " http://post.imageshack.us/upload_api.php";
				 String image_link = "";
	            String fileName = fileUri.toString();

	            HttpURLConnection conn = null;
	            DataOutputStream dos = null;
	            String lineEnd = "\r\n";
	            String twoHyphens = "--";
	            String boundary = "*****";
	            int bytesRead, bytesAvailable, bufferSize;
	            byte[] buffer;
	            int maxBufferSize = 1 * 1024 * 1024;
	            File sourceFile = new File(fileUri.getPath());

	            Log.i("uristring", fileUri.toString());
	            Log.w("file name are...", "" + sourceFile);
	            if (!sourceFile.isFile()) {
	                Log.e("uploadFile", "Source File Does not exist");
	            }

	            try { // open a URL connection to the Servlet
	            	Log.i("try","in try block");
	            	
	                FileInputStream fileInputStream = new FileInputStream(sourceFile);
	                URL url = new URL(upLoadServerUri);
	                conn = (HttpURLConnection) url.openConnection(); 
	                conn.setDoInput(true); 
	                conn.setDoOutput(true);
	                conn.setUseCaches(false);
	                conn.setRequestMethod("POST");
	                conn.setRequestProperty("Connection", "Keep-Alive");
	                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
	                dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(twoHyphens + boundary + lineEnd);

	                dos.writeBytes("Content-Disposition: form-data; name=\"key\""+ lineEnd);
	                dos.writeBytes(lineEnd);
	                dos.writeBytes("0DEHMRWX0f4c0191f3b6b39111fc4cf12a093cbd"); //TODO get from strings.xml
	                dos.writeBytes(lineEnd);
	                dos.writeBytes(twoHyphens + boundary + lineEnd);
	                
	                dos.writeBytes("Content-Disposition: form-data; name=\"fileupload\";filename=\""+ fileName + "\";" + lineEnd);
	                dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
	                dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
		            dos.writeBytes(lineEnd);

	                bytesAvailable = fileInputStream.available(); 

	                bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                buffer = new byte[bufferSize];

	                // read file and write it into form...
	                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	                while (bytesRead > 0) 
	                {
	                    dos.write(buffer, 0, bufferSize);
	                    bytesAvailable = fileInputStream.available();
	                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	                }
	                	
	                // send multipart form data necesssary after file data...
	                dos.writeBytes(lineEnd);
	                dos.writeBytes(twoHyphens + boundary + lineEnd);                
	                dos.writeBytes("Content-Disposition: form-data; name=\"tags\""+ lineEnd);
	                dos.writeBytes(lineEnd);
	                dos.writeBytes("stickapic,android,gajo.us");
	                dos.writeBytes(lineEnd);
	                dos.writeBytes(twoHyphens + boundary + lineEnd);	                
	                dos.writeBytes("Content-Disposition: form-data; name=\"format\""+ lineEnd);
	                dos.writeBytes(lineEnd);
	                dos.writeBytes("json");
	                dos.writeBytes(lineEnd);
	                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);	                

	                // Responses from the server (code and message)
	                int serverResponseCode = conn.getResponseCode();
	                String serverResponseMessage = conn.getResponseMessage();
	                InputStream servere = conn.getInputStream();	                
	                String json_ret = new Scanner(servere,"UTF-8").useDelimiter("\\A").next();
					JSONObject ret_object = (JSONObject) new JSONTokener(json_ret).nextValue();
					JSONObject links = ret_object.getJSONObject("links");
					image_link = links.getString("image_link");

	                // close the streams //
	                fileInputStream.close();
	                dos.flush();
	                dos.close();

	               
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }

		
	            return image_link;


			 }
		    };

		    Future<String> future = executor.submit(callable);
		    image_url =  future.get(); // returns 2
		    executor.shutdown();
		    
		 return image_url;
	 	}
}
