import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;


public class MovingEnemyTank extends Tank{

	private MyTank tank; 
	private double distBetween = 2; 

	public MovingEnemyTank(MyTank tank){

		setColor(Color.RED);

		this.tank = tank; 
	}

	public double distanceFrom(Location loc1, Location loc2){

		int x1 = loc1.getRow();
		int y1 = loc1.getCol();
		int x2 = loc2.getRow();
		int y2 = loc2.getCol(); 

		double dist = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));

		return dist; 
	}

	public void act(){

		Location possibleLocation = this.getLocation().getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation())); 

		if(this.getGrid().isValid(possibleLocation) 
				&& getGrid().get(possibleLocation) ==null 
				&& distanceFrom(this.getLocation(), tank.getLocation())>distBetween 
				&& decideToShoot()){

			this.setDirection(this.getLocation().getDirectionToward(tank.getLocation()));

			this.moveTo(possibleLocation);
		}
		else{

			Location oldLocation = this.getLocation();

			ArrayList<Location> locs = this.getGrid().getEmptyAdjacentLocations(this.getLocation());

			Location newLocation = locs.get((int) (Math.random()*locs.size()));

			this.moveTo(newLocation);

			this.setDirection(oldLocation.getDirectionToward(newLocation));
		}

		if(decideToShoot() && Math.random()<.30)
			this.shoot();
	}

	public boolean decideToShoot(){

		Location loc = this.getLocation().getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation()));

		while(getGrid().isValid(loc)){

			if(getGrid().get(loc) instanceof MyTank)
				return true;

			if(!(getGrid().get(loc) instanceof MyTank) && getGrid().get(loc) !=null)
				return false;

			if(getGrid().get(loc) == null)
				loc = loc.getAdjacentLocation(this.getLocation().getDirectionToward(tank.getLocation())); 

		}

		return Math.random()<.30;

	}

}
