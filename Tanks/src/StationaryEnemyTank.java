import java.awt.Color;

import info.gridworld.grid.Location;

public class StationaryEnemyTank extends Tank{

	private MyTank tank;

	public StationaryEnemyTank(MyTank tank){

		setColor(Color.RED);

		this.tank = tank; 
	}

	public void act(){

		if(tank.getGrid() != null){

			Location possibleLocation = this.getLocation().getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation())); 

			if(this.getGrid().isValid(possibleLocation) 
					&& (getGrid().get(possibleLocation) ==null 
					|| getGrid().get(possibleLocation) instanceof MyTank))
				this.setDirection(this.getLocation().getDirectionToward(tank.getLocation()));

			if(decideToShoot() && Math.random()<.50)
				this.shoot();
		}
		
	}

	public boolean decideToShoot(){

		Location loc= this.getLocation().getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation()));

		while(getGrid().isValid(loc)){

			if(getGrid().get(loc) instanceof MyTank)
				return true;

			if(!(getGrid().get(loc) instanceof MyTank) && getGrid().get(loc) !=null)
				return false;

			if(getGrid().get(loc) == null)
				loc = loc.getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation())); 

		}

		return Math.random()<.50;
	}

}
