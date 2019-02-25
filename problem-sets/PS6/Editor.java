import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 * @author Thomas Monfre, winter 2018, filled in missing methods
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingID = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

    // move state
	private boolean move = false;

	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	/** 
	 * constructor method, sets up objects and communicators
	 */
	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		// listener methods
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// get sketch's map
		Map<Integer, Shape> map = sketch.getMap();
		
		// loop over everything in that map and draw it
		for (Integer id : map.keySet()) {
			map.get(id).draw(g);
		}
		
		// draw current drawing
		if (curr != null) {
			curr.draw(g);
		}
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		if (mode == Mode.DRAW) {
			
			// update drawFrom values and update shape
			drawFrom = p;
			
			// if drawing ellipse, create new ellipse
			if (shapeType.equals("ellipse")) curr = new Ellipse((int)drawFrom.getX(), (int)drawFrom.getY(), color);
			
			// if drawing rectangle, create new rectangle
			else if (shapeType.equals("rectangle")) curr = new Rectangle((int)drawFrom.getX(), (int)drawFrom.getY(), color);
			
			// if drawing freehand, create new polyline object
			else if (shapeType.equals("freehand")) curr = new Polyline(p, color);
			
			// if drawing segment, create new segment object
			else if (shapeType.equals("segment")) curr = new Segment((int)drawFrom.getX(), (int)drawFrom.getY(), color);
		}
		
		// request to start moving the shape
		else if (mode == Mode.MOVE) {
			// store reference of the ID of the shape that was clicked on
			movingID = sketch.clickedOnShape((int)p.getX(), (int)p.getY());
			
			// so long as that ID is valid, update moveFrom and set boolean move to true (request is performed in handleDrag)
			if (movingID != -1) {
				moveFrom = p;
				move = true;
			}
		}
		
		// request to recolor
		else if (mode == Mode.RECOLOR) {
			// store ID of the shape clicked on
			int id = sketch.clickedOnShape((int)p.getX(), (int)p.getY());
			
			// so long as that ID is valid, send server request to update that shape's color
			if (id != -1) {
				SendRequests.requestRecolor(id, color.getRGB(), comm.getOut());
			}
		}
		
		// request to delete
		else {
			// store ID of the shape clicked on
			int id = sketch.clickedOnShape((int)p.getX(), (int)p.getY());
			
			// so long as that ID is valid, send server request to update that shape's color
			if (id != -1) {
				SendRequests.requestDelete(id, comm.getOut());
			}
		}
		
		// repaint the window
		repaint();
	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {

		// if in draw mode, update local drawing - don't send server request
		if (mode == Mode.DRAW) {
			
			// if ellipse, update moveFrom and call setCorners method
			if (shapeType.equals("ellipse")) {
				Ellipse e = (Ellipse) curr;
				
				// update moveFrom values and call Shape's setCorners method with new moveFrom values
				moveFrom = new Point((int)p.getX(), (int)p.getY());
				e.setCorners((int)drawFrom.getX(), (int)drawFrom.getY(), (int)moveFrom.getX(), (int)moveFrom.getY());
				
			}
			
			// if rectangle, update moveFrom and call setCorners method
			else if (shapeType.equals("rectangle")) {
				Rectangle r = (Rectangle) curr;
				
				// update moveFrom values and call Shape's setCorners method with new moveFrom values
				moveFrom = new Point((int)p.getX(), (int)p.getY());
				r.setCorners((int)drawFrom.getX(), (int)drawFrom.getY(), (int)moveFrom.getX(), (int)moveFrom.getY());
				
			}
			
			// if polyline, add the current point to the Polyline object's list of points
			else if (shapeType.equals("freehand")) {
				Polyline poly = (Polyline) curr;
				poly.addPoint(p);
			}
			
			// if segment, set the end of the segment to the current point
			else if (shapeType.equals("segment")) {
				Segment s = (Segment) curr;
				s.setEnd((int)p.getX(), (int)p.getY());
			}
		}
		
		// if in move mode, send request to server to move the object
		else if (mode == Mode.MOVE && move) {
			
			// make sure clicked on valid shape
			if (movingID != -1) {
				
				// send server request then update moveFrom to current position
				SendRequests.requestMove(movingID, (int)(p.getX() - moveFrom.getX()), (int)(p.getY() - moveFrom.getY()), comm.getOut());
				moveFrom = p;	
			}
		}
		
		// repaint the window
		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {
		
		// if in draw mode, send server request to draw the current shape then clear the local drawing
		if (mode == Mode.DRAW) {
			SendRequests.requestAddShape(curr, comm.getOut());
			
			// clear local drawing (if request goes through, EditorCommunicator object comm will draw the object via the server, meaning local copy no longer needed)
			curr = null;
			drawFrom = null;
			moveFrom = null;
		}
		
		// if in move mode, stop moving
		if (mode == Mode.MOVE) {
			move = false;
		}
		
		// repaint the window
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
