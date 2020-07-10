// This project was made by Allen Ho <https://github.com/allenh9999>, Vania Rohmetra, and Allen Purchis
// Importing all the important stuff
import javax.swing.JFrame;

// This is the main class that everything runs in
public class Minesweeper {
	public static void main(String[] args) {
		JFrame frame = new MainFrame();
		frame.setSize(320,475);
		frame.setTitle("Minesweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
