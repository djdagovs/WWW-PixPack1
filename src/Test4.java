

public class Test4 {

	public static void main(String[] args) {
		String template="Name.Style";
		int idx=template.indexOf('.');
		System.out.println(template.substring(0,idx));
		System.out.println(template.substring(idx+1));
	}
}
