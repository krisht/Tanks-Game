

public class HardRock extends Tank {

	public boolean explodable = false;  
	
	public HardRock(){
		setColor(null);
	}
	
	public void act(){
		//HardRocks don't do anything
	}
	
	public boolean getExplodable(){
		return explodable; 
	}
	
	
}