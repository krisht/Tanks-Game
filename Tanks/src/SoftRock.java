
public class SoftRock extends Tank {

	public boolean explodable = true;
	public boolean isDead=false;
	
	public SoftRock(){
		setColor(null);
	}
	
	public void act(){
		//SoftRocks don't do anything
	}
	
	public boolean getExplodable(){
		return explodable; 
	}
	
}
