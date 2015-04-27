import java.util.Vector;

public class Posn{
	public int x;
	public int y;
	public Posn parent = null;
	public Vector[Posn] visits;
	
	public Posn(){
		x = 0;
		y = 0;
	}
	public Posn(int xval, int yval){
		x = xval;
		y = yval;
	}
	public Posn(int xval, int yval, Posn prent, Vector[Posn] places){
		x = xval;
		y = yval;
		parent = prent;
		visits = places;
	}
	public Posn(int xval, int yval, Posn prent){
		x = xval;
		y = yval;
		parent = prent;
		visits = new Vector();
	}
}