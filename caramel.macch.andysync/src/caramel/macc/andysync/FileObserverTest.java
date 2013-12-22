package caramel.macc.andysync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.util.Log;

public class FileObserverTest extends Thread{
	String baseDir = "/";
	int interval = 1000;
	
	public FileObserverTest(String baseDir){
		this.baseDir = baseDir;
	}
	
	public void run(){
		Log.i("observe test", "dir observation starts..[" + this.baseDir + "]");
		
		try {
			File f = new File(this.baseDir + File.separator + "sub-dir1");
			if (f.exists()){
				this.delete(f);
			}
			
			this.list();
			
			String path = this.baseDir + File.separator + "test-file";
			
			Thread.sleep(interval);
			Log.i("observe test", "1. createNewFile [" + path + "]");
			// create a file..
			f = new File(path);
			f.createNewFile();			
			Thread.sleep(interval);
			
			Log.i("observe test", "1. renameTo [" + path + "]");
			// create a file..
			f = new File(path);
			path = path.replace("test-file", "test-file-rename");
			f.renameTo(new File(path));
			Thread.sleep(interval);			
			
			Log.i("observe test", "2. delete [" + path + "]");
			// delete a file..
			f.delete();			
			Thread.sleep(interval);			
			
			// dir create..
			path = this.baseDir + File.separator + "sub-dir1";
			f = new File(path);
			f.mkdirs();
			//f.createNewFile();			
			Log.i("observe test", "3. mkdirs [" + path + "]");	
			Thread.sleep(interval);
			
			//this.list();
			
			// sub file crate under new dir..
			path += File.separator +  "test-file-sub";
			f = new File(path);
			//f.mkdirs();
			f.createNewFile();
			Log.i("observe test", "4. createNewFile [" + path + "]");			
			Thread.sleep(interval);
			
			path = path.replace("sub-dir1/", "");
			Log.i("observe test", "4.1 move to [" + path + "]");		
			f.renameTo(new File(path));
			
			// delete a sub file..
			f.delete();
			Log.i("observe test", "5. delete [" + path + "]");
			Thread.sleep(interval);
			
			// create sub sub dir ..
			path = this.baseDir + File.separator + "sub-dir1" +File.separator + "sub-dir2" + File.separator + "sub-dir3";
			f = new File(path);
			f.mkdirs();
			//f.createNewFile();			
			Log.i("observe test", "6. mkdirs [" + path + "]");			
			Thread.sleep(interval);
			
			list();
			
			// file crate under new dir..
			path += File.separator + "test-file-sub-sub-sub";
			f = new File(path);
			f.createNewFile();
			Log.i("observe test", "7. createNewFile [" + path + "]");
			Thread.sleep(interval);
			
			// delete for test dir..
			path = f.getParent();
			f = new File(path);
			delete(f);
			Log.i("observe test", "8. delete [" + path + "]");
			Thread.sleep(interval);
				
			list();
			
			path = this.baseDir + File.separator + "sub-dir1" +File.separator + "sub-dir2" + File.separator + "sub-dir3";
			f = new File(path);
			f.mkdirs();
			f = new File(path + File.separator + "sub-file");
			f.createNewFile();
			Log.i("observe test", "10. create [" + path + "]");
			Thread.sleep(interval);
			
			f = new File(this.baseDir + File.separator + "sub-dir1");
			delete(f);
			Log.i("observe test", "11. delete [" + path + "]");
			
			Log.i("observe test", "dir observation ends..[" + baseDir + "]");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	private void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	private void list(){
		String[] list = new File(this.baseDir).list();
		
		if (list == null)
			return;
		
		for(String name : list){
			Log.i("observe test", " - " + name);
		}
		
	}
}
