package com.personal.photos;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.personal.common.DBAdapter;
import com.personal.common.JavaConstants;
import com.simple.useraccountdb.R;

public class PhotoFlipActivity extends Activity {

    public static final String DEBUG = "EVH";

    DBAdapter dbAdapter = null;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 101;
    
    ArrayList<Bitmap> mPages;
    PhotoFlipView pageCurlView;
    Cursor allIMGcursor;
    
	String savedMasterPasswordInPref;

	private SharedPreferences sharedPreferences;
    
    byte[] imgByte;
    
    boolean dbOprResult;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.photos);
	getActionBar().setDisplayHomeAsUpEnabled(true);

	
	pageCurlView = (PhotoFlipView) findViewById(R.id.dcgpagecurlPageCurlView1);

	mPages= pageCurlView.getmPages();
	
	// to define the directory path for the database on SD card
	String dbPath = Environment.getExternalStorageDirectory()
		+ getResources().getString(R.string.dbPath);
	// to define the name of the database and the table
	String dbName = getResources().getString(R.string.dbName);
	String tableName = getResources().getString(R.string.tableName);

	dbAdapter = new DBAdapter(getApplicationContext(), dbPath, dbName,
		tableName);

	sharedPreferences = getSharedPreferences(
		    getResources().getString(R.string.prefs_filename),
		    MODE_PRIVATE);

	    // if returns null, then the user is trying to login for the
	    // first time.
	    savedMasterPasswordInPref = sharedPreferences.getString(
		    JavaConstants.masterKey, null);
	
	Log.d(DEBUG, DEBUG + "entered AccActivity");

	dbAdapter.open();
	allIMGcursor = dbAdapter.getAllRecords(JavaConstants.TABLE_PHOTO, new String[]{JavaConstants.KEY_ID, JavaConstants.KEY_NAME, JavaConstants.KEY_TYPE, JavaConstants.KEY_IMG} );
	
	
	if(allIMGcursor!=null && allIMGcursor.moveToFirst() && mPages!=null)
	{
	    
		   do {
		       imgByte =allIMGcursor.getBlob(allIMGcursor.getColumnIndex(JavaConstants.KEY_IMG));
		       if(imgByte!=null)
		       {
			   
			   Log.d(DEBUG, DEBUG + "inside Add"+imgByte.hashCode());
			  /* mPages.add(BitmapFactory.decodeByteArray(dbAdapter.decrypt(savedMasterPasswordInPref, new String(imgByte)).getBytes(), 100, imgByte.length));*/
			   
			   mPages.add(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
			   
		       }
		       
		       
		   } while(allIMGcursor.moveToNext()); 
		   
	}
	
	dbAdapter.close();
	/*
	 * Intent intent = new Intent(this, StandaloneExample.class);
	 * intent.setAction(intent.ACTION_VIEW); startActivity(intent);
	 */

	OnClickListener onClickListener = new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub

		Button b = (Button) v;

	    }
	};

    }

    // this could be moved to a common utilities folder
    private void displayToast(String text) {
	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
		.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.adddelete, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	int id = item.getItemId();

	displayToast(item.getTitle().toString());

	if (id == R.id.action_add) {
	    
	    selectImage();
	    // redirect to create Bank page
	    
	    return true;
	} else if (id == R.id.action_delete) {
	   
	    
	    if(mPages!=null && mPages.size()>0)
	    deleteImage(pageCurlView.getmIndex());
	    // redirect to create Bank page
	    finish();
	    return true;
	} else if (id == R.id.action_settings) {
	    
	    finish();
	    /* System.exit(1);System.runFinalizersOnExit(true); */
	    return true;
	}

	return super.onOptionsItemSelected(item);
    }
    
    
    private void deleteImage(int imgdeleteIndex) {
	
	int counter=0;
	boolean deleted;
	dbAdapter.open();
	
	allIMGcursor = dbAdapter.getAllRecords(JavaConstants.TABLE_PHOTO, new String[]{JavaConstants.KEY_ID, JavaConstants.KEY_NAME, JavaConstants.KEY_TYPE, JavaConstants.KEY_IMG});
	
	if(allIMGcursor!=null && allIMGcursor.moveToFirst())
	{
	    
		   do {
		      
		       if(counter== imgdeleteIndex)
		       {
			   
			   Log.d(DEBUG, DEBUG + "Delete row/index"+counter);
			  /* mPages.add(BitmapFactory.decodeByteArray(dbAdapter.decrypt(savedMasterPasswordInPref, new String(imgByte)).getBytes(), 100, imgByte.length));*/
			   
			   deleted = dbAdapter.deletePhoto(allIMGcursor.getInt(allIMGcursor.getColumnIndex(JavaConstants.KEY_ID)));
			  
			   mPages = pageCurlView.getmPages();
			   // if deleted from DB remove the index
			   if(deleted)
			   mPages.remove(imgdeleteIndex);
			   pageCurlView.setmPages(mPages);
			   
		       }
		       counter++;
		       
		   } while(allIMGcursor.moveToNext()); 
		   
	}
	
	dbAdapter.close();
	
	}

    private void selectImage() {
	final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

	AlertDialog.Builder builder = new AlertDialog.Builder(PhotoFlipActivity.this);
	builder.setTitle("Add Photo");
	builder.setItems(items, new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int item) {
		if (items[item].equals("Take Photo")) {
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    File f = new File(android.os.Environment
			    .getExternalStorageDirectory(), "temp.jpg");
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		    startActivityForResult(intent, REQUEST_CAMERA);
		} else if (items[item].equals("Choose from Library")) {
		    Intent intent = new Intent(
			    Intent.ACTION_PICK,
			    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		    intent.setType("image/*");
		    startActivityForResult(
			    Intent.createChooser(intent, "Select File"),
			    SELECT_FILE);
		} else if (items[item].equals("Cancel")) {
		    dialog.dismiss();
		}
	    }
	});
	builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == RESULT_OK) {
	    if (requestCode == REQUEST_CAMERA) {
		File f = new File(Environment.getExternalStorageDirectory()
			.toString());
		for (File temp : f.listFiles()) {
		    if (temp.getName().equals("temp.jpg")) {
			f = temp;
			break;
		    }
		}
		try {
		    Bitmap bm;
		    int targetW = pageCurlView.getWidth();
		    int targetH = pageCurlView.getHeight();
		    // Get the dimensions of the bitmap
		    
		    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
		    
		    btmapOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(f.getAbsolutePath(), btmapOptions);
		    int photoW = btmapOptions.outWidth;
		    int photoH = btmapOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    btmapOptions.inJustDecodeBounds = false;
		    btmapOptions.inSampleSize = scaleFactor;
		    btmapOptions.inPurgeable = true;
		    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
			    btmapOptions);

		    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
		    // %%%%% to add into the view
		    dbAdapter.open();
		    dbOprResult = dbAdapter.addPhoto(bm,savedMasterPasswordInPref);
		    if(dbOprResult)
		    {
			 mPages.add(bm);
			    pageCurlView.setmPages(mPages);
		    }
		    dbAdapter.close();
		    
		    f.delete();
/*		    String path = android.os.Environment
			    .getExternalStorageDirectory()
			    + File.separator
			    + "Phoenix" + File.separator + "default";
		    
		    OutputStream fOut = null;
		    File file = new File(path, String.valueOf(System
			    .currentTimeMillis()) + ".jpg");
		    try {
			fOut = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
		    } catch (FileNotFoundException e) {
			e.printStackTrace();
		    } catch (IOException e) {
			e.printStackTrace();
		    } catch (Exception e) {
			e.printStackTrace();
		    }*/
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    } else if (requestCode == SELECT_FILE) {
		Uri selectedImageUri = data.getData();

		String tempPath = getPath(selectedImageUri, PhotoFlipActivity.this);
		Bitmap bm;
		int targetW = pageCurlView.getWidth();
		    int targetH = pageCurlView.getHeight();
		    // Get the dimensions of the bitmap
		    
		    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
		    
		    btmapOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(tempPath, btmapOptions);
		    int photoW = btmapOptions.outWidth;
		    int photoH = btmapOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    btmapOptions.inJustDecodeBounds = false;
		    btmapOptions.inSampleSize = scaleFactor;
		    btmapOptions.inPurgeable = true;

		    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
		
		// bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
		    // %%%%% to add into the view
		
		dbAdapter.open();
		dbOprResult = dbAdapter.addPhoto(bm,savedMasterPasswordInPref);
		    if(dbOprResult)
		    {
			 mPages.add(bm);
			    pageCurlView.setmPages(mPages);
		    }
		    dbAdapter.close();
	    }
	}
	
	
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    
}
