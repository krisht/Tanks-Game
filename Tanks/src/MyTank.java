import info.gridworld.grid.Location;


public class MyTank extends Tank{

	public int lives = 5; 
	public boolean explodable = true;
	public boolean closeWindow = false;
	protected boolean isDead = false;
	public int ammoCount=30, reloadCount,counter=0;
	public int stepsToReload =50, ammoPerReload=30; 
	
	public MyTank(){
		
		setColor(null);
		
		this.setDirection(Location.EAST);
	}


	public void shoot(){
		
		if(getGrid().get(this.getLocation().getAdjacentLocation(this.getDirection())) ==null && !(ammoCount<=0)){
		
			TankMissile missile = new TankMissile();
			
			missile.setDirection(this.getDirection());
			
			missile.putSelfInGrid(getGrid(), this.getLocation().getAdjacentLocation(this.getDirection()));
			
			ammoCount--; 
		}

	}


	public void act(){
		
		if(ammoCount==0 && reloadCount <stepsToReload){
			reloadCount++;
		}
		
		if(reloadCount == stepsToReload && ammoCount==0){
			ammoCount =ammoPerReload;
			reloadCount=0;
		}
		
	}


	public void dropBomb() {
		
		if(getGrid().get(this.getLocation().
				getAdjacentLocation(this.getDirection()+Location.HALF_CIRCLE)) ==null){
			
			TankBomb bomb = new TankBomb();
			
			bomb.putSelfInGrid(getGrid(), this.getLocation().getAdjacentLocation(this.getDirection()+Location.HALF_CIRCLE) );
		}

	}

	public boolean isDead(){
		return isDead; 
	}

	public void setDeath(boolean b) {
		this.isDead = b; 
	}

}
