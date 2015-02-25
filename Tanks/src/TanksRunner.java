import info.gridworld.actor.Actor;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Location;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class TanksRunner {
	
	private File score = new File("ScoreRecord.tnk"); 

	public ImageIcon createImageIcon(String path, String description){

		java.net.URL imgURL = getClass().getResource(path);

		if (imgURL != null)
			return new ImageIcon(imgURL, description);
		else return null;

	}

	public static void showScoreBoard() throws IOException{

		int counter =0; 

		ArrayList<String []> list = new ArrayList<String [] >();

		TanksRunner runner = new TanksRunner();
		
		BufferedReader reader = new BufferedReader(new FileReader(runner.score));

		String str;

		while(( str = reader.readLine()) != null){
			counter++;
			str = counter+ "	"+str;
			list.add(str.split("	"));
		}

		reader.close();

		String [] columnNames = {"Rank", "Name", "Status", "Final Level", "Lives Left"}; 

		String [][] data = new String[list.size()][list.get(0).length];

		for(int x=0; x<list.size(); x++){
			for(int y=0; y<list.get(0).length; y++){
				data[x][y] = list.get(x)[y];
			}
		}

		JTable table = new JTable(data, columnNames);

		table.setPreferredScrollableViewportSize(new Dimension(500, 160));

		table.setFillsViewportHeight(true);

		final JFrame frame = new JFrame("Scoreboard!");

		Image icon = Toolkit.getDefaultToolkit().getImage("TankWorldIcon.gif");

		frame.setIconImage(icon);

		JScrollPane scrollPane = new JScrollPane(table);

		JPanel panel = new JPanel(new GridLayout(1,0));

		panel.add(scrollPane);

		panel.setOpaque(true);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				System.exit(0);
			}
		});

		frame.setContentPane(panel);

		frame.pack();

		frame.setVisible(true);		
	}



	public static void main(String args []){

		TanksRunner runner = new TanksRunner(); 

		String [] choices = {"One Player", "Two Player","Score Board",  "Exit"}; 

		JFrame frame = new JFrame("Input Dialog");

		ImageIcon icon = runner.createImageIcon("TankWorldIcon.gif", "");

		String choice =	(String)JOptionPane.showInputDialog(frame, "What mode would you like to play in?", "Tanks!", JOptionPane.QUESTION_MESSAGE, icon, choices, choices[0]);

		try{

			if(choice.equals("Exit")){
				frame.dispose();
				System.exit(0);
			}
			else if(choice.equals("Two Player")){

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
			else if(choice.equals("One Player")){

				String playerName = "Player";
				
				playerName = (String) JOptionPane.showInputDialog(frame, "Enter Cadet's Name(Your name)");
				
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
			else if(choice.equals("Score Board")){
				SoundEffect.tanksTheme.playCont();
				
				showScoreBoard();
			}

		}
		catch(NullPointerException | IOException e){
			frame.dispose();
			System.exit(0); 
		}
	}
}