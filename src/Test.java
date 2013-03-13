import com.enlightware.pixpack.DBFSExtension;
import com.laukien.datetime.DateTime;
import com.laukien.exception.ParameterException;

public class Test {

	private static String gUsername;
	private static String gTimestamp;
	private static String gKey;
	private static String gExtension;

	public static void main(String[] args) {
		setInternalFilename("ich:20060818163437483_maosbmsbet.gif");
		
		System.out.println("User: "+gUsername+" Timestamp: "+gTimestamp+" Key: "+gKey+" Extension: "+gExtension);
		System.out.println(removeExtension("file.ext"));
		System.out.println(extractFilename("http://path/file.ext"));
		System.out.println("SELECT " +
				"file.server, file.name, file.timestamp, file.key, file.last, file.count, file.description, file.public, file.adult, file.status, file.ip, " +
				"extension.id AS extension_id, extension.name AS extension_name, extension.mimetype AS extension_mimetype, extension.description AS extension_description, " +
				"folder.id AS folder_id, folder.name AS folder_name, " +
				"account.name AS account_name, account.password AS account_password, account.timestamp AS account_timestamp, account.status AS account_status " +
				"FROM file, extension, folder, account " +
				"WHERE file.timestamp=? AND file.key=? AND " +
				"file.key_extension=extension.id AND " +
				"(file.key_folder=-1 OR file.key_folder=folder.id) AND "+
				"file.key_user=account.id");
	}
	
	public static void setInternalFilename(String pInternal) {
		if(pInternal==null || pInternal.length()<32) throw new ParameterException("DBFS.setInternalFilename: Invalid filename\n(timestamp_key.ext)"+pInternal);
		//to lower case
		pInternal=pInternal.toLowerCase();
		
		int pos;
		
		//is user?
		pos=pInternal.indexOf(':');
		if(pos!=-1) {
			gUsername=pInternal.substring(0,pos);
			pInternal=pInternal.substring(pos+1);
		}
		pos=pInternal.indexOf('_');
		if(pos!=17) throw new ParameterException("DBFS: Invalid length (Timestamp)");
		gTimestamp=pInternal.substring(0,pos);
		gKey=pInternal.substring(pos+1,pos+11);
		
		//Extension
		pos=pInternal.length();
		if(pInternal.charAt(pos-4)!='.') gExtension=null;
		else gExtension=(pInternal.substring(pos-3,pos));
	}

	public static String removeExtension(String pFilename) {
		int pos=pFilename.lastIndexOf('.');
		if(pos==-1) return pFilename;
		return pFilename.substring(0,pos);
	}
	
	public static String extractFilename(String file) {
		int pos=file.lastIndexOf('/');
		if(pos==-1) pos=file.lastIndexOf('\\');
		if(pos==-1) return file;	//no path
		file=file.substring(pos+1);

		return file;
	}

}
