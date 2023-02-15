package miscObjects;

public class Tuple5<A, B, C, D, E> {

	protected final A a;
	protected final B b;
	protected final C c;
	protected final D d;
	protected final E e;
	
	public Tuple5(A a, B b, C c, D d, E e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}
	
	public Tuple5<A, B, C, D, E> clone() {
		return new Tuple5<>(a, b, c, d, e);
	}
	
	public String toString() {
		return "(" + a + ", " + b + ", " + c + ", " + d + ", " + e + ")";
	}
	
}
