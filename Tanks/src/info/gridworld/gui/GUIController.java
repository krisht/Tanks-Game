/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Julie Zelenski
 * @author Cay Horstmann
 */

package info.gridworld.gui;

import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import info.gridworld.world.World;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The GUIController controls the behavior in a WorldFrame. <br />
 * This code is not tested on the AP CS A and AB exams. It contains GUI
 * implementation details that are not intended to be understood by AP CS
 * students.
 */

public class GUIController<T>
{
	public static final int INDEFINITE = 0, FIXED_STEPS = 1, PROMPT_STEPS = 2;

	private static final int MIN_DELAY_MSECS = 10, MAX_DELAY_MSECS = 1000;
	private static final int INITIAL_DELAY = MIN_DELAY_MSECS
			+ (MAX_DELAY_MSECS - MIN_DELAY_MSECS) / 2;
	public boolean restarter = false;
	private Timer timer;
	private JButton stepButton, runButton, stopButton,restartButton, scoreButton;
	private JComponent controlPanel;
	private GridPanel display;
	private WorldFrame<T> parentFrame;
	private int numStepsToRun, numStepsSoFar;
	private ResourceBundle resources;
	@SuppressWarnings("unused")
	private DisplayMap displayMap;
	private boolean running;
	@SuppressWarnings("rawtypes")
	private Set<Class> occupantClasses;

	/**
	 * Creates a new controller tied to the specified display and gui
	 * frame.
	 * @param parent the frame for the world window
	 * @param disp the panel that displays the grid
	 * @param displayMap the map for occupant displays
	 * @param res the resource bundle for message display
	 */
	@SuppressWarnings("rawtypes")
	public GUIController(WorldFrame<T> parent, GridPanel disp,
			DisplayMap displayMap, ResourceBundle res)
	{
		resources = res;
		display = disp;
		parentFrame = parent;
		this.displayMap = displayMap;
		makeControls();

		occupantClasses = new TreeSet<Class>(new Comparator<Class>()
				{
			public int compare(Class a, Class b)
			{
				return a.getName().compareTo(b.getName());
			}
				});

		World<T> world = parentFrame.getWorld();
		Grid<T> gr = world.getGrid();
		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));
		for (String name : world.getOccupantClasses())
			try
		{
				occupantClasses.add(Class.forName(name));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		timer = new Timer(100, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				step();
			}
		});

		display.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				Grid<T> gr = parentFrame.getWorld().getGrid();
				Location loc = display.locationForPoint(evt.getPoint());
				if (loc != null && gr.isValid(loc) && !isRunning())
				{
					display.setCurrentLocation(loc);
					locationClicked();
				}
			}
		});
		stop();
	}

	/**
	 * Advances the world one step.
	 */
	public void step()
	{
		parentFrame.getWorld().step();
		parentFrame.repaint();
		if (++numStepsSoFar == numStepsToRun)
			stop();
		Grid<T> gr = parentFrame.getWorld().getGrid();

		for (Location loc : gr.getOccupiedLocations())
			addOccupant(gr.get(loc));
	}

	@SuppressWarnings("rawtypes")
	private void addOccupant(T occupant)
	{
		Class cl = occupant.getClass();
		do
		{
			if ((cl.getModifiers() & Modifier.ABSTRACT) == 0)
				occupantClasses.add(cl);
			cl = cl.getSuperclass();
		}
		while (cl != Object.class);
	}

	/**
	 * Starts a timer to repeatedly carry out steps at the speed currently
	 * indicated by the speed slider up Depending on the run option, it will
	 * either carry out steps for some fixed number or indefinitely
	 * until stopped.
	 */
	public void run()
	{
		display.setToolTipsEnabled(false); // hide tool tips while running
		parentFrame.setRunMenuItemsEnabled(false);
		stopButton.setEnabled(true);
		stepButton.setEnabled(false);
		runButton.setEnabled(false);
		scoreButton.setEnabled(true);
		numStepsSoFar = 0;
		timer.start();
		running = true;
	}
	
	public void restart(){
		restartButton.setEnabled(true);
		if(isRunning()){
			stopButton.setEnabled(true);
			runButton.setEnabled(false);
			stepButton.setEnabled(false);
			scoreButton.setEnabled(true);
		}
		else{
			stopButton.setEnabled(false);
			runButton.setEnabled(true);
			stepButton.setEnabled(false);
			scoreButton.setEnabled(true);
		}
		this.restarter = true;
	}
	
	
	
	/**
	 * Stops any existing timer currently carrying out steps.
	 */
	public void stop()
	{
		display.setToolTipsEnabled(false);
		parentFrame.setRunMenuItemsEnabled(false);
		timer.stop();
		stopButton.setEnabled(false);
		runButton.setEnabled(true);
		stepButton.setEnabled(true);
		scoreButton.setEnabled(true);
		running = false;
	}
	
	public void showScoreBoard() throws IOException{
		stop();
		
		int counter =0; 
		
		ArrayList<String []> list = new ArrayList<String [] >();
		
		BufferedReader reader = new BufferedReader(new FileReader("ScoreRecord.tnk"));
		
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
            	if (parentFrame.isVisible()){
                        frame.dispose();
                    }
            	else if (!parentFrame.isVisible()){
            		System.exit(0); 
            	}
            }
        });
        
        frame.setContentPane(panel);
        
        frame.pack();
        
        frame.setVisible(true);		
	}

	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Builds the panel with the various controls (buttons and
	 * slider).
	 */
	private void makeControls()
	{
		controlPanel = new JPanel();
		stepButton = new JButton(resources.getString("button.gui.step"));
		runButton = new JButton(resources.getString("button.gui.run"));
		stopButton = new JButton(resources.getString("button.gui.stop"));
		restartButton = new JButton("Restart game");
		scoreButton = new JButton("Score Board"); 
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBorder(BorderFactory.createEtchedBorder());

		Dimension spacer = new Dimension(5, stepButton.getPreferredSize().height + 10);

		controlPanel.add(Box.createRigidArea(spacer));		
		controlPanel.add(runButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(stopButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(restartButton);
		controlPanel.add(Box.createRigidArea(spacer));
		controlPanel.add(scoreButton); 
		runButton.setEnabled(false);
		stepButton.setEnabled(false);
		stopButton.setEnabled(false);
		
		JSlider speedSlider = new JSlider(MIN_DELAY_MSECS, MAX_DELAY_MSECS,
				INITIAL_DELAY);
		speedSlider.setInverted(true);
		speedSlider.setPreferredSize(new Dimension(100, speedSlider
				.getPreferredSize().height));
		speedSlider.setMaximumSize(speedSlider.getPreferredSize());

		// remove control PAGE_UP, PAGE_DOWN from slider--they should be used
		// for zoom
		InputMap map = speedSlider.getInputMap();
		while (map != null)
		{
			map.remove(KeyStroke.getKeyStroke("control PAGE_UP"));
			map.remove(KeyStroke.getKeyStroke("control PAGE_DOWN"));
			map = map.getParent();
		}

		stepButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				step();
			}
		});
		runButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				run();
			}
		});
		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stop();
			}
		});
		speedSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				timer.setDelay(((JSlider) evt.getSource()).getValue());
			}
		});
		
		restartButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				restart();
			}
				
		});
		
		scoreButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				try {
					showScoreBoard();
				} catch (IOException e1) {System.out.println("File not found!");}
				
			}
			
		});
		
	}
	

	/**
	 * Returns the panel containing the controls.
	 * @return the control panel
	 */
	public JComponent controlPanel()
	{
		return controlPanel;
	}

	/**
	 * Callback on mousePressed when editing a grid.
	 */
	private void locationClicked(){
		
	}

	/**
	 * Edits the contents of the current location, by displaying the constructor
	 * or method menu.
	 */
	public void editLocation(){
		
	}

	/**
	 * Edits the contents of the current location, by displaying the constructor
	 * or method menu.
	 */
	public void deleteLocation()
	{
		World<T> world = parentFrame.getWorld();
		Location loc = display.getCurrentLocation();
		if (loc != null)
		{
			world.remove(loc);
			parentFrame.repaint();
		}
	}
}
