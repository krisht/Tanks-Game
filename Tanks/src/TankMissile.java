import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;


public class TankMissile extends Tank{

	public boolean explodable = true; 

	public void act(){

		if (canMove())
			move();
		else{
			
			if(getGrid().isValid((this.getLocation()).getAdjacentLocation(this.getDirection()))){

				Actor actor = getGrid().get((this.getLocation()).getAdjacentLocation(this.getDirection()));

				if( actor instanceof Tank && !(actor instanceof HardRock) && !(actor instanceof SoftRock))
					if(actor instanceof TankBomb)
						((TankBomb) actor).setBombCount(((TankBomb) actor).getExplodeNumber());
					else if(actor instanceof PitHole){

						((PitHole)actor).increaseHitCount();

						if(((PitHole)actor).getHitCount()==3)
							((PitHole)actor).removeSelfFromGrid();

						this.removeSelfFromGrid();
					}
					else{

						if(actor instanceof MyTank)

							if(((MyTank)actor).lives>0)
								((MyTank)actor).lives--;
							else ((MyTank)actor).setDeath(true);

						else actor.removeSelfFromGrid();

						this.removeSelfFromGrid();
					}				
				else this.removeSelfFromGrid(); 
			}
			else this.removeSelfFromGrid();
			
			SoundEffect.bulletExplosion.playOnce();
		}
	}


	public void move(){
		
		Grid<Actor> gr = getGrid();
		
		if (gr == null)
			return;
		
		Location loc = getLocation();
		
		Location next = loc.getAdjacentLocation(getDirection());
		
		if (gr.isValid(next))
			moveTo(next);
		else removeSelfFromGrid();
	}

	public boolean canMove(){
		
		Grid<Actor> gr = getGrid();
		
		if (gr == null)
			return false;
		
		Location loc = getLocation();
		
		Location next = loc.getAdjacentLocation(getDirection());
		
		if (!gr.isValid(next))
			return false;
		
		Actor neighbor = gr.get(next);
	
		return (neighbor == null); 
	}


	public boolean getExplodable(){
		return explodable; 
	}

}
