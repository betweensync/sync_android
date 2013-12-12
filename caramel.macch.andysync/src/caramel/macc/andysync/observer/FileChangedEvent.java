package caramel.macc.andysync.observer;

import android.os.FileObserver;

public class FileChangedEvent {
	public static final int CREATE = FileObserver.CREATE;
	public static final int DELETE = FileObserver.DELETE;
	public static final int DELETE_SELF = FileObserver.DELETE_SELF;
	public static final int MODIFY = FileObserver.MODIFY;
	public static final int MOVED_FROM = FileObserver.MOVED_FROM;
	public static final int MOVED_TO = FileObserver.MOVED_TO;
	public static final int MOVE_SELF = FileObserver.MOVE_SELF;

	public static final int ACCESS = FileObserver.ACCESS;
	public static final int ATTRIB = FileObserver.ATTRIB;
	public static final int CLOSE_NOWRITE = FileObserver.CLOSE_NOWRITE;
	public static final int CLOSE_WRITE = FileObserver.CLOSE_WRITE;
	public static final int OPEN = FileObserver.OPEN;
	
	private int type;
	private String path;
	private String typeStr;
	
	public FileChangedEvent(int type, String path){
		this.type = type;
		this.path = path;
		
		this.convertTypeToString();
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getTypeString(){
		return this.typeStr;
	}
	
	private void convertTypeToString(){
		switch(this.type){
		case CREATE:
			typeStr = "CREATE";
			break;
		case DELETE:
			typeStr = "DELETE";
			break;
		case DELETE_SELF:
			typeStr = "DELETE_SELF";
			break;
		case MODIFY:
			typeStr = "MODIFY";
			break;
		case MOVED_FROM:
			typeStr = "MOVED_FROM";
			break;
		case MOVED_TO:
			typeStr = "MOVED_TO";
			break;
		case MOVE_SELF:
			typeStr = "MOVE_SELF";
			break;
		case ACCESS:
			typeStr = "ACCESS";
			break;
		case ATTRIB:
			typeStr = "ATTRIB";
			break;
		case CLOSE_NOWRITE:
			typeStr = "CLOSE_NOWRITE";
			break;
		case CLOSE_WRITE:
			typeStr = "CLOSE_WRITE";
			break;
		case OPEN:
			typeStr = "OPEN";
			break;
		}
	}
	
	@Override
	public String toString() {
		return "FileChangedEvent [type=" + typeStr + ", path=" + path + "]";
	}
}
