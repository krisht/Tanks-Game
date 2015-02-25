import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DoubleTankWorld extends ActorWorld{

	final MyTank tankA=new MyTank();

	final MyTank tankB = new MyTank();

	public DoubleTankWorld app= this;

	public DoubleTankWorld(Grid<Actor> grid) {

		super(grid);

		setMessage("Tank A(Lives: "+ tankA.lives +"\t"+"Ammo: "+tankA.ammoCount+")"+"\n"+"Tank B(Lives: "+ tankB.lives +"\t"+"Ammo: "+tankB.ammoCount+")");

		show();
		
		this.getFrame().setSize(550,695);

		this.getFrame().setTitle("Tanks!");

		Image icon = Toolkit.getDefaultToolkit().getImage("TankWorldIcon.gif");

		this.getFrame().setIconImage(icon);

		tankA.setColor(Color.BLACK);

		tankB.setColor(Color.RED);

		SoundEffect.tanksTheme.playCont();

	}

	public boolean locationClicked(Location loc){
		return false;
	}

	public ArrayList<Actor> getTankActors(){

		ArrayList<Location> locs = getGrid().getOccupiedLocations();

		ArrayList<Actor> actors = new ArrayList<Actor>();

		for(Location loc : locs)
			if(this.getGrid().get(loc) instanceof MyTank || this.getGrid().get(loc) instanceof MovingEnemyTank || this.getGrid().get(loc) instanceof StationaryEnemyTank)
				actors.add(this.getGrid().get(loc)); 

		return actors; 
	}

	public void step(){

		if(!this.getFrame().control.restarter){

			if(tankA.ammoCount ==0)
				setMessage("Tank A(Lives: "+tankA.lives + "\t"+ "ReloadCount: "+ tankA.reloadCount+")");
			else setMessage("Tank A(Lives: "+ tankA.lives +"\t"+"Ammo: "+tankA.ammoCount+")");

			if(tankB.ammoCount ==0)
				setMessage(this.getMessage()+"\n"+"Tank B(Lives: "+tankB.lives + "\t"+ "ReloadCount: "+ tankB.reloadCount+")");
			else setMessage(this.getMessage()+ "\n"+ "Tank B(Lives: "+ tankB.lives +"\t"+"Ammo: "+tankB.ammoCount+")");

			if(tankA.isDead() && tankB.isDead()){
				SoundEffect.tanksTheme.stop();
				showLostScreen();
				this.getFrame().returnController().stop();
				SoundEffect.gameLost.playOnce();
			}
			else if(tankA.isDead() && !tankB.isDead()){
				SoundEffect.tanksTheme.stop();
				showWinScreen(tankB);
				this.getFrame().returnController().stop();
				SoundEffect.gameWon.playOnce();
			}
			else if(!tankA.isDead() && tankB.isDead()){
				SoundEffect.tanksTheme.stop();
				showWinScreen(tankA);
				this.getFrame().returnController().stop();
				SoundEffect.gameWon.playOnce();
			}
			else{
				super.step();
			}

		}
		else{
			this.getFrame().control.restarter=false;
			restartCommands();
		}

	}

	public void restartCommands(){

		int confirm = JOptionPane.showConfirmDialog(null, "Do you want to restart?", "Warning", JOptionPane.YES_NO_OPTION);

		if(confirm == JOptionPane.YES_OPTION){

			this.getFrame().dispose();

			this.getFrame().control.stop();

			SoundEffect.tanksTheme.stop();

			try{
				System.setProperty(" info.gridworld.gui.selection", "hide");

				BufferedReader in = new BufferedReader(new FileReader("TwoPlayerTankWorld.txt"));

				String str; 

				ArrayList<String[]> list = new ArrayList<String[]>();

				while(( str = in.readLine()) != null)
					list.add(str.split("	")); 	

				DoubleTankWorld app = new DoubleTankWorld(new BoundedGrid<Actor>(list.size(),list.get(0).length));

				for(int x=0; x<list.size(); x++)			
					for(int y=0 ;y<list.get(0).length; y++){

						if(list.get(x)[y].equals("R"))
							app.add(new Location(x,y), new HardRock()); 

						if(list.get(x)[y].equals("&"))
							app.add(new Location(x,y), new SoftRock()); 

						if(list.get(x)[y].equals("P"))
							app.add(new Location(x,y), new PitHole());

						if(list.get(x)[y].equals("A")){
							app.add(new Location(x,y), app.tankA);
						}

						if(list.get(x)[y].equals("B")){
							app.add(new Location(x,y), app.tankB);
						}
					}

				app.initActors(); 

				in.close();

				app.getFrame().returnController().run();

			}
			catch(NullPointerException | IOException e){
				System.out.println("File not found!");
			}
		}
		else if(confirm == JOptionPane.NO_OPTION)
			super.step();

	}

	public void showLostScreen(){

		clearScreen();

		File textFile = new File("LostScreen.txt"); 

		screenFiller(textFile);
	}

	public void clearScreen(){

		ArrayList<Location> list = this.getGrid().getOccupiedLocations();

		for(Location loc : list)
			this.getGrid().get(loc).removeSelfFromGrid();
	}

	public void showWinScreen(MyTank tank) {

		clearScreen(); 
		File textFile=new File("WinScreen.txt");

		if(tank.equals(tankA)){
			textFile= new File("TankAWinScreen.txt");
		} 
		else if(tank.equals(tankB)){
			textFile = new File("TankBWinScreen.txt");
		} 

		screenFiller(textFile); 
	}

	public void screenFiller(File file){

		BufferedReader in;

		try {

			in = new BufferedReader(new FileReader(file));

			String str; 

			ArrayList<String[]> list = new ArrayList<String[]>();

			while(( str = in.readLine()) != null)
				list.add(str.split("	")); 

			for(int x=0; x<list.size(); x++)
				for(int y=0 ;y<list.get(0).length; y++){

					if(list.get(x)[y].equals("R"))
						app.add(new Location(x,y), new HardRock()); 

					if(list.get(x)[y].equals("&"))
						app.add(new Location(x,y), new SoftRock()); 


					if(list.get(x)[y].equals("P"))
						app.add(new Location(x,y), new PitHole());
				}

		} catch (IOException e1) {System.out.println("Null pointer error!");}

	}

	public boolean keyPressed(String description, Location loc){

		if(tankA.getGrid() != null && this.getFrame().control.isRunning()){

			if(description.equals("P")){
				this.getFrame().control.stop();
			}

			if(description.equals("A")){

				Location oldLoc=tankA.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()-1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tankA.setDirection(Location.WEST);
					tankA.moveTo(newLoc);
				}

			}

			if(description.equals("D")){

				Location oldLoc=tankA.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()+1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tankA.setDirection(Location.EAST);
					tankA.moveTo(newLoc);
				}
			}

			if(description.equals("W")){

				Location oldLoc=tankA.getLocation();

				Location newLoc=new Location(oldLoc.getRow()-1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tankA.setDirection(Location.NORTH);
					tankA.moveTo(newLoc);
				}
			}

			if(description.equals("S")){

				Location oldLoc=tankA.getLocation();

				Location newLoc=new Location(oldLoc.getRow()+1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tankA.setDirection(Location.SOUTH);
					tankA.moveTo(newLoc);
				}
			}

			if(description.equals("E"))
				tankA.shoot(); 

			if( description.equals("Q"))
				tankA.dropBomb();
		}
		else{
			if(description.equals("P") && this.getFrame().control.isRunning()==false)
				this.getFrame().control.run();
		}

		if(tankB.getGrid() != null && this.getFrame().control.isRunning()){

			if(description.equals("P")){
				this.getFrame().control.stop();
			}

			if(description.equals("J")){

				Location oldLoc=tankB.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()-1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tankB.setDirection(Location.WEST);
					tankB.moveTo(newLoc);
				}

			}

			if(description.equals("L")){

				Location oldLoc=tankB.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()+1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tankB.setDirection(Location.EAST);
					tankB.moveTo(newLoc);
				}
			}

			if(description.equals("I")){

				Location oldLoc=tankB.getLocation();

				Location newLoc=new Location(oldLoc.getRow()-1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tankB.setDirection(Location.NORTH);
					tankB.moveTo(newLoc);
				}
			}

			if(description.equals("K")){

				Location oldLoc=tankB.getLocation();

				Location newLoc=new Location(oldLoc.getRow()+1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tankB.setDirection(Location.SOUTH);
					tankB.moveTo(newLoc);
				}
			}

			if(description.equals("O"))
				tankB.shoot(); 

			if( description.equals("U"))
				tankB.dropBomb();
		}
		else{
			if(description.equals("P") && this.getFrame().control.isRunning()==false)
				this.getFrame().control.run();
		}

		if(description.equals("ESCAPE")){
			int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Warning", JOptionPane.YES_NO_OPTION);

			if(confirm == JOptionPane.YES_OPTION){
				getFrame().dispose();
				System.exit(0);
			}

		}

		if(description.equals("R")){
			this.getFrame().control.restart();
		}
		return true; 
	}

	public void initActors(){

		ArrayList<Location> locs = this.getGrid().getOccupiedLocations();

		ArrayList<Actor> actors = new ArrayList<Actor>();

		for(Location loc:locs )
			actors.add(this.getGrid().get(loc));

		actors.remove(tankA);

		for(Actor actor: actors)
			if(!(actor instanceof HardRock) && !(actor instanceof SoftRock) && !(actor instanceof PitHole))
				actor.setDirection(actor.getLocation().getDirectionToward(tankA.getLocation()));
	}

}