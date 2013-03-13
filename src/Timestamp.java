
public class Timestamp {
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		int timestamp=Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(0,11));
		System.out.println(timestamp);
	}
}
