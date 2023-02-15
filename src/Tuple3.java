package miscObjects;

public class Tuple3<A, B, C> {
	
	protected final A a;
	protected final B b;
	protected final C c;
	
	public Tuple3(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public Tuple3<A, B, C> clone() {
		return new Tuple3<>(a, b, c);
	}
	
	public String toString() {
		return "(" + a + ", " + b + ", " + c + ")";
	}

}
