// This project was made by Allen Ho <https://github.com/allenh9999>, Vania Rohmetra, and Allen Purchis

// Importing all the important stuff
import javax.swing.JFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// This is the main frame class that handles all of the mouse listening stuff
public class MainFrame extends JFrame implements MouseListener {
	
	// the location of where the JFrame will be stored
	private static final long serialVersionUID = 1L;
	
	// This is the main JComponent that will be running
	MainComponent component;
	
	// Initializes the JFrame and goes to the main menu
	public MainFrame() {
		addMouseListener(this);
		// creates the new component that contains all of the actual graphics
		component = new MainComponent();
		this.add(component);
		this.repaint();
		//this.remove(testlabel);
	}
	
	// What happens when the mouse is clicked
	@Override
	public void mouseClicked(MouseEvent e) { }
	
	// What happens when the mouse enters the page
	@Override
	public void mouseEntered(MouseEvent e) {
		component.EnterApp(e.getX(), e.getY() - 25);
	}
	
	// What happens when the mouse exits the page
	@Override
	public void mouseExited(MouseEvent e) {
		component.ExitApp(e.getX(), e.getY() - 25);
	}
	
	// What happens when the mouse is pressed
	@Override
	public void mousePressed(MouseEvent e) {
		// checks whether the mouse was left clicked or right clicked
		if(e.getButton() == MouseEvent.BUTTON1) {
			component.LeftClick(e.getX(), e.getY() - 25);
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			component.RightClick(e.getX(), e.getY() - 25);
		}
	}
	
	// What happens when the mouse is released
	@Override
	public void mouseReleased(MouseEvent e) { }
}
