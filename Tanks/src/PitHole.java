
public class PitHole extends Tank{

	private int hitCount =0;
	public boolean explodable = false;  

	public PitHole(){
		setColor(null);
	}

	public void act(){
		if(hitCount == 3)
			this.removeSelfFromGrid();
	}

	public boolean getExplodable(){
		return explodable; 
	}
	public void increaseHitCount(){
		hitCount++; 
	}

	public int getHitCount(){
		return hitCount; 
	}

}
