package biz.binarysolutions.signature.share;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import biz.binarysolutions.android.lib.aaau.services.CheckUpdateService;
import biz.binarysolutions.signature.share.tasks.ReadCapturedFilesTask;
import biz.binarysolutions.signature.share.util.FileUtil;
import biz.binarysolutions.signature.share.util.PNGFile;
import biz.binarysolutions.signature.share.util.PreferencesLoader;

import com.flurry.android.FlurryAgent;

/**
 * 
 *
 */
public class MainActivity extends ListActivity {
	
	private static final String LIBRARY_URL = 
		"market://details?id=biz.binarysolutions.signature";
	
	private static final int CAPTURE_REQUEST_CODE = 0;
	
	private String signaturesFolder = null;
	
	private ArrayList<File>    files = new ArrayList<File>();
	private ArrayAdapter<File> adapter;
	
	private PreferencesLoader preferencesLoader;
	
	/**
	 * 
	 * @return
	 */
	private boolean isCaptureLibraryInstalled() {
	
	    PackageManager pm = getPackageManager();
	    PackageInfo    pi = null;
	    
	    try {
			pi = pm.getPackageInfo("biz.binarysolutions.signature", 0);
		} catch (NameNotFoundException e) {
			// do nothing
		}
		
		return pi != null;
	}
	
	/**
	 * 
	 * @param errorMessage
	 */
	private void displayErrorMessage(String errorMessage) {
		
		if (isFinishing()) {
			return;
		}
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.Error)
			.setMessage(errorMessage)
			.setPositiveButton(R.string.OK, null)
			.show();		
	}
	
	/**
	 * 
	 */
	private void displayInstallLibraryDialog() {
		
		if (isFinishing()) {
			return;
		}
		
		DialogInterface.OnClickListener listener = 
			new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Uri    uri    = Uri.parse(LIBRARY_URL);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				
				startActivity(intent);
			}
		};
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.LibraryMissing)
			.setMessage(R.string.LibraryMissingMessage)
			.setPositiveButton(R.string.Install, listener)
			.setNegativeButton(R.string.Cancel, null)
			.show();
	}
	
	/**
	 * 
	 * @param file
	 */
	private void displayRenameFileDialog(final File file) {
		
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_rename, null);
		
		final EditText editText = (EditText) view.findViewById(R.id.EditTextFileName);
		final String   fileName = FileUtil.stripExtension(file.getName()); 
		editText.setText(fileName);
		
		
		if (isFinishing()) {
			return;
		}
		
		DialogInterface.OnClickListener listener = 
			new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String newFileName = editText.getText().toString();
				if (newFileName != null) {
					if (!newFileName.equals(fileName) && 
						 newFileName.length() > 0) {
						
						newFileName += getString(R.string.PNG);
						
						File from = new File(file.getParent(), file.getName());
						File to   = new File(file.getParent(), newFileName);
						
						try {
							if (from.renameTo(to)) {
								readCapturedFiles();
							}
						} catch (Exception e) {
							// do nothing
						}
					}
				}
			}
		};
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.Rename)
			.setView(view)
			.setPositiveButton(R.string.OK, listener)
			.setNegativeButton(R.string.Cancel, null)
			.show();
	}

	/**
	 * 
	 */
	private String getSignaturesFolder() {
		
		String folderName = getString(R.string.app_folder);
		
		File externalStorage = Environment.getExternalStorageDirectory();
		if (externalStorage != null) {
			return FileUtil.getFullPath(externalStorage, folderName);
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 */
	private String getFileName() {
		
		String fileName = "" + (int) (System.currentTimeMillis() / 1000);
        return signaturesFolder + File.separator + fileName + ".png";
	}

	/**
	 * 
	 */
	private void setButtonListener() {
		
		Button button = (Button) findViewById(R.id.ButtonCaptureNewSignature);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Intent intent = 
		        	new Intent("biz.binarysolutions.signature.CAPTURE");
				
				String keyInvoiceId   = "biz.binarysolutions.signature.InvoiceId";
				String keyFileName    = "biz.binarysolutions.signature.FileName";
				String keyTitle       = "biz.binarysolutions.signature.Title";
				String keyStrokeWidth = "biz.binarysolutions.signature.StrokeWidth";
				String keyCrop        = "biz.binarysolutions.signature.Crop";
				String keyWidth       = "biz.binarysolutions.signature.Width";
				String keyHeight      = "biz.binarysolutions.signature.Height";
				
				String fileName = getFileName();
				
				String  title       = preferencesLoader.getTitle(); 
				int     strokeWidth = preferencesLoader.getStrokeWidth();
				boolean crop        = preferencesLoader.getCrop();
				String  width       = preferencesLoader.getWidth();
				String  height      = preferencesLoader.getHeight();
				
		        intent.putExtra(keyInvoiceId, "");
		        intent.putExtra(keyFileName, fileName);
		        intent.putExtra(keyTitle, title);
		        intent.putExtra(keyStrokeWidth, strokeWidth);
		        intent.putExtra(keyCrop, crop);
		        intent.putExtra(keyWidth, width);
		        intent.putExtra(keyHeight, height);
		        
		        intent.setComponent(
		    		new ComponentName(
						"biz.binarysolutions.signature", 
						"biz.binarysolutions.signature.Capture"
					)
		    	);
		        
		        if (isCaptureLibraryInstalled()) {
		        	startActivityForResult(intent, CAPTURE_REQUEST_CODE);
				} else {
					displayInstallLibraryDialog();
				}
			}
		});
	}
	
	/**
	 * 
	 */
	private void readCapturedFiles() {
		new ReadCapturedFilesTask(this).execute(signaturesFolder);
	}
	
    /**
	 * 
	 * @param readFiles
	 */
	private void populateListView(File[] readFiles) {

		files.clear();
		for (File file : readFiles) {
			files.add(new PNGFile(file));
		}
		
		adapter.notifyDataSetChanged();
	}

	/**
	 * 
	 */
	private void displayErrorDialog() {
		
		if (isFinishing()) {
			return;
		}
	
		new AlertDialog.Builder(this)
			.setTitle(R.string.Error)
			.setMessage(R.string.ErrorFolder)
			.setPositiveButton(R.string.OK, null)
			.show();
	}
	
	/**
	 * @param id 
	 * 
	 */
	private void viewFile(long id) {
		
		String title  = getString(R.string.View);
		Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri    uri    = Uri.fromFile(files.get((int) id));
        
        intent.setDataAndType(uri, "image/png");
        
        startActivity(Intent.createChooser(intent, title));
	}
	
	/**
	 * 
	 */
	private void shareFile(long id) {
		
		String title  = getString(R.string.Share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri    uri    = Uri.fromFile(files.get((int) id));
        
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        
        startActivity(Intent.createChooser(intent, title));		
	}
	
	/**
	 * 
	 * @param id
	 */
	private void renameFile(long id) {
		
		displayRenameFileDialog(files.get((int) id));
	}
	
	/**
	 * @param id 
	 * 
	 */
	private void deleteFile(long id) {
		
		try {
			boolean isDeleted = files.get((int) id).delete();
			if (isDeleted) {
				
				files.remove((int) id);
				adapter.notifyDataSetChanged();
			}
		} catch (SecurityException e) {
			// do nothing
		}
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		CheckUpdateService.start(this, getString(R.string.aaau_server_url));
        
        adapter = new ArrayAdapter<File>(
			this, 
			android.R.layout.simple_list_item_1, 
			files
		);
		setListAdapter(adapter);
        
        signaturesFolder = getSignaturesFolder();
        setButtonListener();
        registerForContextMenu(getListView());
        
        if (! isCaptureLibraryInstalled()) {
        	displayInstallLibraryDialog();
		}
        
        preferencesLoader = new PreferencesLoader(MainActivity.this);
    }
    
	@Override
	public void onResume() {
		super.onResume();
		
		readCapturedFiles();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_app_key)); 
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onCreateContextMenu
		(
				ContextMenu     menu, 
				View            view,
				ContextMenuInfo menuInfo
		) {
		super.onCreateContextMenu(menu, view, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = 
			(AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {

		case R.id.contextMenuItemView:
			viewFile(info.id);
			return true;

		case R.id.contextMenuItemShare:
			shareFile(info.id);
			return true;

		case R.id.contextMenuItemRename:
			renameFile(info.id);
			return true;			
			
		case R.id.contextMenuItemDelete:
			deleteFile(info.id);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	 	switch (item.getItemId()) {
	 	
	 	case R.id.menuItemSettings:
        	startActivity(new Intent(this, ApplicationSettings.class));
        	return true;
	 	
	    case R.id.menuItemAbout:
	    	startActivity(new Intent(this, AboutActivity.class));
	    	return true;
	    	
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == CAPTURE_REQUEST_CODE) {
			if (resultCode != RESULT_OK) {
				if (data != null) {
					
					String errorMessage = data.getStringExtra("biz.binarysolutions.signature.ErrorMessage");
					if (errorMessage != null) {
						displayErrorMessage(errorMessage);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param readFiles
	 */
	public void onCapturedFilesAvailable(File[] readFiles) {

		if (readFiles != null) {
			populateListView(readFiles);
		} else {
			displayErrorDialog();
		}
	}
}