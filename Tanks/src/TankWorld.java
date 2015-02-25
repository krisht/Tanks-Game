import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TankWorld extends ActorWorld{

	public final int totalLevels = 8; 

	private String playerName;

	final MyTank tank=new MyTank();

	public TankWorld app= this;

	public static int tankLevel=7;

	public TankWorld(Grid<Actor> grid){

		super(grid);
		
		JFrame tempFrame = new JFrame("Briefing"); 
		
		JOptionPane.showMessageDialog(tempFrame, "Cadet, you have been assigned to kill Nazi \nmutants who have created very  powerful \n         tanks in "+app.totalLevels+" places. Go kill them!","Briefing!", 2);
		
		tempFrame.dispose();
		
		setMessage("Lives: "+ tank.lives +"\n"+"Ammo: "+tank.ammoCount);

		show();

		this.getFrame().setSize(550,695);

		this.getFrame().setTitle("Tanks!");

		Image icon = Toolkit.getDefaultToolkit().getImage("TankWorldIcon.gif");

		this.getFrame().setIconImage(icon);

		SoundEffect.tanksTheme.playCont();		
	}

	public boolean locationClicked(Location loc)
	{
		return false;
	}

	public void setName(String name){
		playerName = name;
	}

	public ArrayList<Actor> getTankActors(){
		
		ArrayList<Location> locs = getGrid().getOccupiedLocations();

		ArrayList<Actor> actors = new ArrayList<Actor>();

		for(Location loc : locs)
			if(this.getGrid().get(loc) instanceof MyTank || this.getGrid().get(loc) instanceof MovingEnemyTank || this.getGrid().get(loc) instanceof StationaryEnemyTank)
				actors.add(this.getGrid().get(loc)); 

		return actors; 
	}

	@SuppressWarnings("static-access")
	public void step(){

		if(!this.getFrame().control.restarter){	
			if(tank.ammoCount ==0)
				setMessage("Lives: "+ tank.lives +"\n"+"Reload Count: "+tank.reloadCount);
			else setMessage("Lives: "+ tank.lives +"\n"+"Ammo: "+tank.ammoCount);

			ArrayList<Actor> actors = getTankActors();

			if(tank.isDead() == false){

				if(actors.size()==1){
					if(!(tankLevel==totalLevels)){
						SoundEffect.tanksTheme.stop();
						JOptionPane.showMessageDialog(null, "Good job! Next Level: Level "+(tankLevel+1), "Congrats!", JOptionPane.INFORMATION_MESSAGE);
					}

					if(tankLevel<(totalLevels)){
						tankLevel++; 
						SoundEffect.tanksTheme.playCont();
						try {
							fillNextLevel();
						} catch (IOException e) {System.out.println("Null pointer error!");}
					}
					else{ 
						SoundEffect.tanksTheme.stop();
						showWinScreen();
						this.getFrame().returnController().stop();
						Player player = new Player(this.playerName,true, this.tankLevel, this.tank.lives); 
						recordResults(player); 
						SoundEffect.gameWon.playOnce();
					}

				} 

				if(actors.size()<1 && tank.isDead()){
					SoundEffect.tanksTheme.stop();
					showLostScreen();
					this.getFrame().returnController().stop();
					Player player = new Player(this.playerName,false, this.tankLevel, this.tank.lives); 
					recordResults(player);
					SoundEffect.gameLost.playOnce();
				}

			}
			else{
				SoundEffect.tanksTheme.stop();
				showLostScreen();
				this.getFrame().returnController().stop();
				Player player = new Player(this.playerName,false, this.tankLevel, this.tank.lives); 
				recordResults(player); 
				SoundEffect.gameLost.playOnce();
			}

			super.step(); 	
		}
		else{
			this.getFrame().control.restarter=false;
			restartCommands();

		}
	}

	@SuppressWarnings("static-access")
	public void restartCommands(){

		int confirm = JOptionPane.showConfirmDialog(null, "Do you want to restart?", "Warning", JOptionPane.YES_NO_OPTION);

		if(confirm == JOptionPane.YES_OPTION){

			this.getFrame().dispose();
			this.getFrame().control.stop();
			SoundEffect.tanksTheme.stop();
			this.tankLevel =1;
			try{
				System.setProperty("info.gridworld.gui.selection", "hide");

				BufferedReader in = new BufferedReader(new FileReader("Type"+TankWorld.tankLevel+".txt"));

				String str; 

				ArrayList<String[]> list = new ArrayList<String[]>();

				while(( str = in.readLine()) != null)
					list.add(str.split("	")); 	

				TankWorld app = new TankWorld(new BoundedGrid<Actor>(list.size(),list.get(0).length));

				app.setName(playerName);

				for(int x=0; x<list.size(); x++)			
					for(int y=0 ;y<list.get(0).length; y++){

						if(list.get(x)[y].equals("R"))
							app.add(new Location(x,y), new HardRock()); 

						if(list.get(x)[y].equals("&"))
							app.add(new Location(x,y), new SoftRock()); 

						if(list.get(x)[y].equals("S"))
							app.add(new Location(x,y), new StationaryEnemyTank(app.tank)); 

						if(list.get(x)[y].equals("M"))
							app.add(new Location(x,y), new MovingEnemyTank(app.tank)); 

						if(list.get(x)[y].equals("H"))
							app.add(new Location(x,y), app.tank);

						if(list.get(x)[y].equals("P"))
							app.add(new Location(x,y), new PitHole());
					}

				app.initActors(); 

				in.close();

				app.getFrame().returnController().run();

			}
			catch(NullPointerException | IOException e){
				System.out.println("File not found!");
			}
		}
		else if(confirm == JOptionPane.NO_OPTION){
			super.step();

		}

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

	public void fillNextLevel() throws IOException{

		clearScreen(); 

		File textFile = new File("Type"+tankLevel+".txt"); 

		screenFiller(textFile);

		app.initActors();
	}

	public void showWinScreen() {

		clearScreen();

		File textFile = new File("WinScreen.txt"); 

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

					if(list.get(x)[y].equals("S"))
						app.add(new Location(x,y), new StationaryEnemyTank(app.tank)); 


					if(list.get(x)[y].equals("M"))
						app.add(new Location(x,y), new MovingEnemyTank(app.tank)); 

					if(list.get(x)[y].equals("H"))
						app.add(new Location(x,y), app.tank);

					if(list.get(x)[y].equals("P"))
						app.add(new Location(x,y), new PitHole());
				}

		} catch (IOException e1) {System.out.println("Null pointer error!");}

	}

	public boolean keyPressed(String description, Location loc){

		if(tank.getGrid() != null && this.getFrame().control.isRunning()){

			if(description.equals("P")){
				this.getFrame().control.stop();
			}

			if(description.equals("A")||description.equals("LEFT")){

				Location oldLoc=tank.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()-1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tank.setDirection(Location.WEST);
					tank.moveTo(newLoc);
				}

			}

			if(description.equals("D")||description.equals("RIGHT")){

				Location oldLoc=tank.getLocation();

				Location newLoc=new Location(oldLoc.getRow(),oldLoc.getCol()+1);

				if(getGrid().isValid(newLoc) && getGrid().get(newLoc)==null){
					tank.setDirection(Location.EAST);
					tank.moveTo(newLoc);
				}
			}

			if(description.equals("W")||description.equals("UP")){

				Location oldLoc=tank.getLocation();

				Location newLoc=new Location(oldLoc.getRow()-1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tank.setDirection(Location.NORTH);
					tank.moveTo(newLoc);
				}
			}

			if(description.equals("X")||description.equals("DOWN")){

				Location oldLoc=tank.getLocation();

				Location newLoc=new Location(oldLoc.getRow()+1,oldLoc.getCol());

				if(getGrid().isValid(newLoc)&& getGrid().get(newLoc)==null){
					tank.setDirection(Location.SOUTH);
					tank.moveTo(newLoc);
				}
			}

			if(description.equals("S"))
				tank.shoot(); 

			if( description.equals("SPACE"))
				tank.dropBomb();
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

		if(description.equals("R"))
			this.getFrame().control.restart();

		return true; 
	}

	public void initActors(){

		ArrayList<Location> locs = this.getGrid().getOccupiedLocations();

		ArrayList<Actor> actors = new ArrayList<Actor>();

		for(Location loc:locs )
			actors.add(this.getGrid().get(loc));

		actors.remove(tank);

		for(Actor actor: actors)
			if(!(actor instanceof HardRock) && !(actor instanceof SoftRock) && !(actor instanceof PitHole))
				actor.setDirection(actor.getLocation().getDirectionToward(tank.getLocation()));
	}

	public void recordResults(Player player){

		ArrayList<Player> list = new ArrayList<Player>();

		BufferedReader reader=null; 
		PrintWriter writer=null;
		
		try {
			
			reader = new BufferedReader(new FileReader("ScoreRecord.tnk"));
			
			String str; 
			
			while(( str = reader.readLine()) != null)
				list.add(toPlayer(str));

			reader.close();
			
			list.add(toPlayer(player.toString())); 

			Collections.sort(list);
			
			writer = new PrintWriter("ScoreRecord.tnk");
			
			for(Player string: list)
				writer.println(string);
			
			writer.close();
		
		} catch (IOException e1) { System.out.println("File not found!"); }
		finally{
			try{
				reader.close();
				writer.close(); 
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

	}

	public Player toPlayer(String str){

		String [] arr = str.split("\t");
		
		if(arr[1].equals("Won"))
			return new Player(arr[0], true, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
		else
		if(arr[1].equals("Lost"))	
		return new Player(arr[0], false, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
		
		return null; 

	}

}