package miscObjects;

public class Tuple4<A, B, C, D> {

	protected A a;
	protected B b;
	protected C c;
	protected D d;
	
	public Tuple4(A a, B b, C c, D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Tuple4<A, B, C, D> clone() {
		return new Tuple4<>(a, b, c, d);
	}
	
	public String toString() {
		return "(" + a + ", " + b + ", " + c + ", " + d + ")";
	}
	
}
