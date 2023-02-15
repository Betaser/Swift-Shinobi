package miscObjects;

public class Noted<E extends Enum<?>, V> {

	public E enumVal;
	public V val;
	
	public Noted(E enumVal, V val) {
		this.enumVal = enumVal;
		this.val = val;
	}
	
	public String toString() {
		return "[" + enumVal + "] " + val;
	}
	
}
