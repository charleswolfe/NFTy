package us.gajo.stickapic;

import java.io.IOException;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TagWriteActivity extends Activity {
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;//
    
	private static String image_url;
	private NfcAdapter mAdapter;
	private Boolean mInWriteMode=false;
	private PopupWindow popUp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_scantag);
	    
	    Bundle extras = getIntent().getExtras();
	    image_url = extras.getString("image_url");
	    
	    mAdapter = NfcAdapter.getDefaultAdapter(this);
	    if(mAdapter != null) {
	    	this.enableWriteMode();
	    }
	    popUp = new PopupWindow(this);
	}
	
    private void enableWriteMode() {
    	mInWriteMode = true;
    	pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    
    	IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
    	IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef, tagDetected
        };

        mTechLists = new String[][] { new String[] { 
                NfcV.class.getName(),
                NfcF.class.getName(),
                NfcA.class.getName(),
                NfcB.class.getName()
        } };

    }

    @Override
    public void onResume() {
        super.onResume();  
        mAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }
	
    @Override
    public void onNewIntent(Intent intent) {
    	Log.d("NFTy","TAG DISCOVERED");
    	if(mInWriteMode) {
                    mInWriteMode = false;                                   
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    writeTag(tag);
                    Log.d("NFTy", "TAG WRITTEN");
    	}
    }

    private boolean writeTag(Tag tag) {
    	//popup so the user knows we are doing things
        LinearLayout layout = new LinearLayout(this);
        TextView tv = new TextView(this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText(R.string.writingtag);
        layout.addView(tv, params);
        popUp.setContentView(layout);        
        popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        popUp.update(50, 50, 300, 80);
        
     	NdefRecord rtdUriRecord1 = NdefRecord.createUri(this.image_url);  //use this    		
        NdefMessage message = new NdefMessage(rtdUriRecord1);
           
            try {
                    // see if tag is already NDEF formatted
                    Ndef ndef = Ndef.get(tag);
                    if (ndef != null) {
                            ndef.connect();
                            
                            if (!ndef.isWritable()) {
                                    //displayMessage("Read-only tag.");
                                    return false;
                            }
     
                            // work out how much space we need for the data
                            int size = message.toByteArray().length;
                            if (ndef.getMaxSize() < size) {
                                    //displayMessage("Tag doesn't have enough free space.");
                                    return false;
                            }
     
                            ndef.writeNdefMessage(message);
                            //displayMessage("Tag written successfully.");
                            return true;
                    } else {
                            // attempt to format tag
                            NdefFormatable format = NdefFormatable.get(tag);
                            if (format != null) {
                                    try {
                                            format.connect();
                                            format.format(message);
                                            //displayMessage("Tag written successfully!");
                                            return true;
                                    } catch (IOException e) {
                                            //displayMessage("Unable to format tag to NDEF.");
                                            return false;
                                    }
                            } else {
                                    //displayMessage("Tag doesn't appear to support NDEF format.");
                                    return false;
                            }
                    }
            } catch (Exception e) {
                    //displayMessage("Failed to write tag");
            	Log.e("tag", "Exception");
            }
            Log.i("tag", "tag written");
            popUp.dismiss();
            return false;
    }

	
}