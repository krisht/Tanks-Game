import info.gridworld.actor.Actor;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class TankBomb extends Tank {

	public boolean explodable = true;
	private int explodeNumber = 5;
	private int bombCount;
	public boolean isDead = false;

	public TankBomb() {

		setColor(Color.YELLOW);

		bombCount = 0;
	}

	public void act() {

		if (this.getColor().equals(Color.YELLOW))
			this.setColor(Color.RED);
		else
			setColor(Color.YELLOW);

		if (bombCount == explodeNumber) {
			processActors(getActors(this.getLocation()));
			this.removeSelfFromGrid();
			SoundEffect.bombExplosion.playOnce();
		} else
			bombCount++;
	}

	public void setBombCount(int bombCount) {
		this.bombCount = bombCount;
	}

	public int getExplodeNumber() {
		return explodeNumber;
	}

	public void processActors(ArrayList<Actor> list) {

		for (Actor actor : list)

			if (((Tank) actor).getExplodable())

				if (actor instanceof MyTank)

					if (((MyTank) actor).lives > 0)
						((MyTank) actor).lives--;

					else
						((MyTank) actor).setDeath(true);

				else
					actor.removeSelfFromGrid();

	}

	public ArrayList<Actor> getActors(Location loc) {
		return getGrid().getNeighbors(loc);
	}

	public boolean getExplodable() {
		return explodable;
	}

}
