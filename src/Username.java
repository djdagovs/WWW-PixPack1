import java.util.regex.Pattern;

public class Username {
	private static final Pattern REGEXP_USERNAME=Pattern.compile("[A-Za-z0-9\\.\\-\\_\\@]{3,128}");

	public static void main(String[] args) {
		System.out.println(isValidUsername("test-"));
	}
	
	private static boolean isValidUsername(String lName) {
		return REGEXP_USERNAME.matcher(lName).matches();
	}

}
