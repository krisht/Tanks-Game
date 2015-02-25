import info.gridworld.actor.Bug;
import info.gridworld.grid.Location;


public class Tank extends Bug{

	public boolean isDead=false;
	public boolean explodable = true; 
	
	public Tank(){
		
		setColor(null);
		
		this.setDirection(Location.EAST);
	}


	public void shoot(){
		
		if( getGrid().isValid(this.getLocation().getAdjacentLocation(this.getDirection())) && getGrid().get(this.getLocation().getAdjacentLocation(this.getDirection())) ==null){
			
			TankMissile missile = new TankMissile();
			
			missile.setDirection(this.getDirection());
			
			missile.putSelfInGrid(getGrid(), this.getLocation().getAdjacentLocation(this.getDirection()));
		}
	}
	
	public void dropBomb() {
		
		if(getGrid().isValid(this.getLocation().getAdjacentLocation(this.getDirection()+Location.HALF_CIRCLE)) 
				&& getGrid().get(this.getLocation().getAdjacentLocation(this.getDirection()+Location.HALF_CIRCLE)) ==null){
			
			TankBomb bomb = new TankBomb();
			
			bomb.putSelfInGrid(getGrid(), this.getLocation().getAdjacentLocation(this.getDirection()+Location.HALF_CIRCLE) );
		}

	}
	
	public void act(){
		//Tanks act differently from Actors
	}
	
	public boolean getExplodable(){
		return explodable; 
	}
	
}
