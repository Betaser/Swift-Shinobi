package miscObjects;

public class Mut<T> {
	
	public T val;
	
	public Mut() {}
	
	public Mut(T val) {
		this.val = val;
	}
	
	public String toString() {
		return "boxed[ " + val + " ]";
	}

}
