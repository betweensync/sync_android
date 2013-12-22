package caramel.macc.andysync;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import caramel.macc.andysync.core.FileScannerFactory;
import caramel.macc.andysync.observer.DirectoryObserver;
import caramel.macc.andysync.observer.FileChangedEvent;
import caramel.macc.andysync.observer.FileChangedEventListener;
import caramel.macc.andysync.util.ConsoleLogger;
import caramel.macc.andysync.util.StringUtil;
import caramel.macc.andysync.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AndySyncActivity extends Activity implements ScanEventListener, FileChangedEventListener{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_andy_sync);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
//		findViewById(R.id.dummy_button).setOnTouchListener(
//				mDelayHideTouchListener);
		
		// find controls..
		this.findControls();
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	// for scan and observe..
	private FileScanner fileScanner;
	private DirectoryObserver dirObserver;
	
	private long started = 0;
	private long ended = 0;
	
	TextView txtDir;
	TextView txtThreadCount; 
	TextView txtElapsed; 
	TextView txtScanned; 
	EditText txtScanConsole;
	EditText txtObserveConsole;
	TextView txtKeyword; 
	EditText txtSearchedConsole;
	CheckBox chbKeyword;
	
	String keyword = "";
	
	String dir;
	int threadCount;
	
	private void findControls(){
		this.txtDir = (TextView)this.findViewById(R.id.txtDir);
		this.txtThreadCount = (TextView)this.findViewById(R.id.txtThreadCount);
		this.txtElapsed = (TextView)this.findViewById(R.id.txtElapsed);
		this.txtScanned = (TextView)this.findViewById(R.id.txtScanned);
		this.txtScanConsole = (EditText)this.findViewById(R.id.txtScanConsole);
		this.txtObserveConsole = (EditText)this.findViewById(R.id.txtObserveConsole);
		this.txtKeyword = (TextView)this.findViewById(R.id.txtKeyword);
		this.txtSearchedConsole = (EditText)this.findViewById(R.id.txtSearchedConsole);
		this.chbKeyword = (CheckBox)this.findViewById(R.id.chbKeyword);
	}
	
	public void onScanClick(View view){
		Log.i("andysync", "Scan button clicked..");
		this.txtScanConsole.setText("");
		
		this.dir = this.txtDir.getText().toString().trim();
		this.threadCount = Integer.parseInt(this.txtThreadCount.getText().toString());
		
		Log.i("andysync", "dir = " + this.dir);
		Log.i("andysync", "threadCount = " + this.threadCount);
		
		//this.fileScanner = FileScannerFactory.instance().createCuncurrentFileScanner(dir, threadCount );
		this.fileScanner = FileScannerFactory.instance().create(FileScanner.TYPE_OPTIMIZED, dir, threadCount );
		this.fileScanner.addScanEventListener(this);
		this.fileScanner.start();
		
		// saerch..
		this.keyword = this.txtKeyword.getText().toString().trim();
		this.txtSearchedConsole.setText("");
	}
	
	public void onObserveClick(View view){
		Log.i("andysync", "Observe button clicked..");
		this.txtObserveConsole.setText("* [" + this.dir + "] observation starts...");
		
//		// test..
//		File root = new File("/");
//		String[] dirs = root.list();
//		for(String d:dirs){
//			ConsoleLogger.log(this, txtObserveConsole, "- " + d);
//		}
		
		this.dirObserver = new DirectoryObserver(this.dir);
		//this.dirObserver.setHandler(this.getApplicationContext().g)
		this.dirObserver.addFileChangedEventListener(this);
		this.dirObserver.startWatching();
		
		// start test..		
		new FileObserverTest(this.dir).start();
	}
	
	static long scannedFileCount = 0;
	
	@Override
	public void onScanEvent(ScanEvent event) {
		switch (event.getType()) {
		case STARTED:
			this.started = System.currentTimeMillis();
			
			ConsoleLogger.log(this, this.txtScanConsole, "* Scan starts at " + StringUtil.convertLongToDateString(started, "yyyy-MM-dd hh:mm:ss.SSS"));
			ConsoleLogger.log(this, this.txtScanConsole, " - dir: " + dir);
			ConsoleLogger.log(this, this.txtScanConsole, " - thread Count: " + threadCount);
			
			this.scannedFileCount = 0;
			break;
		case SCANNED:
			if (event.getScanned().size() == 0)
				return;
			
			this.scannedFileCount += event.getScanned().size();
						
//			String parentDir = null;
//			for(SFile sf : event.getScanned().values()){
//				parentDir = sf.getParent();
//				break;
//			}
			
			if (this.chbKeyword.isChecked() && this.keyword.length() > 0){
				for(SFile sf : event.getScanned().values()){
					if (sf.getName().contains(this.keyword)){
						ConsoleLogger.log(this, this.txtSearchedConsole, sf.getAbsolutePath());
					}
				}
			}
			
			ConsoleLogger.showScanning(this, txtScanConsole, this.scannedFileCount);
			this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	txtScanned.setText(String.valueOf(scannedFileCount));
	            }
	        });	
			//ConsoleLogger.log(this, this.txtScanConsole, "    [" + parentDir + "] scanned files: " + event.getScanned().size());
			
			break;
		case ENDED:			
			this.ended = System.currentTimeMillis();
			ConsoleLogger.log(this, this.txtScanConsole, " - Elapsed Time(ms): " + (ended - started));
			ConsoleLogger.log(this, this.txtScanConsole, " - Scanned Files: " + this.scannedFileCount);
			ConsoleLogger.log(this, this.txtScanConsole, "Scan ends at " + StringUtil.convertLongToDateString(ended, "yyyy-MM-dd hh:mm:ss.SSS"));
			
			this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	txtElapsed.setText(String.valueOf(ended - started));
	            	txtScanned.setText(String.valueOf(scannedFileCount));
	            }
	        });		
			
			// list all..
			//this.listScannedFiles();
			
			this.fileScanner.stop();
			
			break;
		}
		
	}
	
	private void listScannedFiles(){
		ConsoleLogger.log(this, this.txtScanConsole, "------- scanned files -------" );
		
		for(String path:this.fileScanner.getSFileMap().keySet()){
			ConsoleLogger.log(this, this.txtScanConsole, "  + " + path );
		}
	}
	
	@Override
	public void onFileChanged(FileChangedEvent fcevent) {
		ConsoleLogger.log(this, this.txtObserveConsole, fcevent.toString());
		
		if (fcevent.getType() == FileChangedEvent.CREATE){
			File f = new File (fcevent.getPath());
			if(f.isDirectory()){
				DirectoryObserver dobserver = new DirectoryObserver(fcevent.getPath());
				dobserver.addFileChangedEventListener(this);
				dobserver.startWatching();
			}
		}
	}


}
