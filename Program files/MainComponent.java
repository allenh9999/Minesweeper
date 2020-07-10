// This project was made by Allen Ho <https://github.com/allenh9999>, Vania Rohmetra, and Allen Purchis
// This is the main component in the minesweeper program

// Importing all of the important goodies
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import java.awt.Image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Random;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Queue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;

public class MainComponent extends JComponent {

	// the location of where the JComponent will be stored
	private static final long serialVersionUID = 1L;
	
	// the screen in which the game is in
	enum Screen {title, themes, nextSweep, instructions, game, endGame};
	private Screen screen;
	
	// the location of all the mines in the map
	private int mines[] = {0,0,0,0,0,0,0,0,0,0};
	
	// for the number of mines around all of the blocks
	private ArrayList<Integer> minesAround = new ArrayList<Integer>();
	
	private String mineImages[] = new String[100];
	
	// the number of points in a game
	private int points = 0;
	
	// whether or not the game is in flagged mode
	private int flagged = 0;
	
	// the number of blocks left in the program
	private int blocksLeft = 100;
	
	// the number of mines left in the program
	private int minesLeft = 10;
	
	// the current theme color
	private String themeColor = "";
	
	// if the game is playing right now
	private int playing = 0;
	
	// if the game is stopped
	private int stopped = 0;
	
	// the current time in the game
	int time = 0;
	
	// The timer used to keep time in track
	Timer timer = new Timer(true);
	
	// The timer used to make the animations run
	Timer animations = new Timer(true);
	
	// Used to determine whether to set the animations timer
	private boolean setAnimationsTimer = true;
	
	// Threads the random generator with a new seed
	private Random generator = new Random(System.currentTimeMillis());
	
	// initializes all of the stuff
	public MainComponent() {
		screen = Screen.title;
	}
	
	// paints all of the graphics
	@Override
	public void paint(Graphics g) {
		// Draw the correct screen
		switch(screen) {
		// draws the title screen
		case title:
			drawTitle(g);
			break;
		case instructions:
			drawInstructions(g);
			break;
		case game:
			drawGame(g);
			break;
		case nextSweep:
			drawNextSweep(g);
			break;
		case themes:
			drawThemes(g);
			break;
		case endGame:
			drawEndGame(g);
			break;
		default:
			break;
		}
		// if the screen is title or endGame, animate
		if(setAnimationsTimer && (screen == Screen.title || screen == Screen.endGame)) {
			animations = new Timer();
			animations.schedule(new RunAnimations(this), 0, 100);
			setAnimationsTimer = false;
		} else if (screen != Screen.title && screen != Screen.endGame) {
			animations.cancel();
			setAnimationsTimer = true;
		}
	}
	
	// draws a multiple line text
	// From https://programming.guide/java/drawing-multiline-strings-with-graphics.html
	private void drawCenteredString(Graphics2D g2, String text, int x, int y, int width) {
		String temp = "";
		FontMetrics fontmetrics = g2.getFontMetrics();
		for(String s : text.split(" ")) {
			if(fontmetrics.stringWidth(temp + ' ' + s) > width || s.equals("\n")) {
				g2.drawString(temp, x + (width - fontmetrics.stringWidth(temp)) / 2,
						y += fontmetrics.getHeight() - 3);
				temp = s;
			}
			else if(temp == "") temp = s;
			else temp = temp + ' ' + s;
		}
		g2.drawString(temp, x + (width - fontmetrics.stringWidth(temp)) / 2,
				y += fontmetrics.getHeight());
	}
	
	// draws the title screen
	private void drawTitle(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draws the background first
		Image toDraw = new ImageIcon("src/Background3.png").getImage();
		g2.drawImage(toDraw, 0, 0, 320, 450, null);
		// draws the main title
		toDraw = new ImageIcon("src/Title.png").getImage();
		g2.drawImage(toDraw, 0, 0, 316, 55, null);
		// draws the cool minesweeper gif
		toDraw = new ImageIcon("src/TitleGif.gif").getImage();
		g2.drawImage(toDraw, 40, 90, 230, 235, null);
		// draws the little play button
		toDraw = new ImageIcon("src/PlayButton.png").getImage();
		g2.drawImage(toDraw, 100, 365, 125, 75, null);
		// g2.drawImage(img, x, y, width, height, observer)
	}
	
	// determines whether the resume stuff shows up
	private boolean showResumeInstructions = true;
	
	// draws the instructions screen
	private void drawInstructions(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draws the background first
		Image toDraw = new ImageIcon("src/Background5.png").getImage();
		g2.drawImage(toDraw,  0,  0,  320,  450, null);
		// Draws the frame that holds the instructions inside
		toDraw = new ImageIcon("src/ForInstructions.png").getImage();
		g2.drawImage(toDraw, 5, 65, 306, 306, null);
		// draw the text available
		g2.setFont(new Font("Palatino", Font.PLAIN, 13));
		g2.setColor(new Color(255, 255, 255));
		drawCenteredString(g2, "There are a hundred blocks in a minefield. "
				+ "Each minefield will have 10 mines which you can identify "
				+ "by right clicking. The blocks that show a number when you "
				+ "click on them show the number of mines that are next to it,"
				+ " and those also add to your points. You lose the game when "
				+ "you click on a mine. If you click on all the blocks that "
				+ "are not mines, then you can move on to the next minefield. "
				+ "Try to get as many points as you can and beat your high "
				+ "score! Also, try to see how fast you can play!", 50, 105, 225);
		// draw the title (instructions)
		toDraw = new ImageIcon("src/Instructions.png").getImage();
		g2.drawImage(toDraw, 0, 0, 316, 65, null);
		// draw the play button
		toDraw = new ImageIcon("src/PlayButton.png").getImage();
		g2.drawImage(toDraw, 110, 375, 100, 65, null);
		// if the resume game instructions has been triggered
		if(showResumeInstructions) {
			// draw the black background to the text
			g2.setColor(new Color(0, 0, 0, 173));
			g2.fillRoundRect(60, 125, 200, 100, 5, 5);
			// draw the text available
			g2.setColor(new Color(255, 255, 255));
			g2.setFont(new Font("Arial", Font.PLAIN, 14));
			drawCenteredString(g2, "You have a game saved. Do "
					+ "you want to resume the game?",
					60, 125, 200);
			// draw the resume button
			toDraw = new ImageIcon("src/Resume.png").getImage();
			g2.drawImage(toDraw, 75, 185, 80, 30, null);
			// draw the start over button
			toDraw = new ImageIcon("src/StartOver.png").getImage();
			g2.drawImage(toDraw, 165, 185, 80, 30, null);
		}
	}
	
	// determines whether the next screen sprites show up
	private boolean showNextScreenGame = false;
	
	// determines what words show up in the next screen background
	private String nextScreenBackgroundTxt = "Hello. \n This is where you display the mine hit and next mine stuff";
	
	// draws the game screen
	private void drawGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draws the background first
		Image toDraw = new ImageIcon("src/Background2.png").getImage();
		g2.drawImage(toDraw, 0, 0, 320, 450, null);
		// draws all of the field squares
		for(int i = 0; i < 100; ++i) {
			toDraw = new ImageIcon(mineImages[i]).getImage();
			g2.drawImage(toDraw, 6 + 30 * (i / 10), 65 + 30 * (i % 10), 
					30, 30, null);
		}
		// draws the game logo
		toDraw = new ImageIcon("src/Title.png").getImage();
		g2.drawImage(toDraw, 5, 15, 210, 35, null);
		// draw the points text
		g2.setFont(new Font("Lucida Sans", Font.PLAIN, 14));
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Points: " + points, 
				306 - g2.getFontMetrics().stringWidth("Points: " + points), 390);
		// Draws the mines text
		g2.setFont(new Font("Ludica Sans", Font.PLAIN, 14));
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Mines: " + minesLeft, 
				306 - g2.getFontMetrics().stringWidth("Mines: " + minesLeft), 45);
		// Draws the home button
		toDraw = new ImageIcon("src/HomeButton.png").getImage();
		g2.drawImage(toDraw, 25, 400, 85, 35, null);
		// Draws the time text
		g2.setFont(new Font("Ludica Sans", Font.PLAIN, 13));
		g2.setColor(new Color(255, 255, 255));
		g2.drawString("Time: " + time, 
				306 - g2.getFontMetrics().stringWidth("Time: " + time), 30);
		// Draws the theme button
		toDraw = new ImageIcon("src/Theme.png").getImage();
		g2.drawImage(toDraw, 210, 400, 100, 40, null);
		// Draws the next screen text
		if(showNextScreenGame) {
			// Draws the background rectangle for the text
			g2.setColor(new Color(0, 0, 0, 173));
			g2.fillRoundRect(60, 125, 200, 100, 5, 5);
			// Add the text to the background
			g2.setColor(new Color(255, 255, 255));
			drawCenteredString(g2, nextScreenBackgroundTxt, 60, 125, 200);
			// Draws the next screen button
			toDraw = new ImageIcon("src/GotItButton.png").getImage();
			g2.drawImage(toDraw, 125, 185, 70, 35, null);
		}
		// g2.drawImage(img, x, y, width, height, observer)
	}
	
	// draws the next sweep screen
	private void drawNextSweep(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draw the background first
		Image toDraw = new ImageIcon("src/Background7.png").getImage();
		g2.drawImage(toDraw, 0, 0, 320, 450, null);
		// Draw the congratulations label for the screen
		toDraw = new ImageIcon("src/NextMineScreen.png").getImage();
		g2.drawImage(toDraw, 10, 10, 300, 300, null);
		// Draws the button to continue onto the next minefield
		toDraw = new ImageIcon("src/MinefieldButton.png").getImage();
		g2.drawImage(toDraw, 100, 330, 130, 70, null);
	}
	
	// draws the themes screen
	private void drawThemes(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draws the background first
		Image toDraw = new ImageIcon("src/Background4.png").getImage();
		g2.drawImage(toDraw, 0, 0, 320, 450, null);
		// draws the overlaying background to the themes
		toDraw = new ImageIcon("src/ForThemes.png").getImage();
		g2.drawImage(toDraw, -20, 85, 180, 230, null);
		// draws the basic picture
		toDraw = new ImageIcon("src/0.png").getImage();
		g2.drawImage(toDraw, 15, 105, 40, 40, null);
		// draws the blue picture
		toDraw = new ImageIcon("src/Blue0.png").getImage();
		g2.drawImage(toDraw, 15, 155, 40, 40, null);
		// draws the red picture
		toDraw = new ImageIcon("src/Red0.png").getImage();
		g2.drawImage(toDraw, 15, 205, 40, 40, null);
		// draws the yellow picture
		toDraw = new ImageIcon("src/Yellow0.png").getImage();
		g2.drawImage(toDraw, 15, 255, 40, 40, null);
		// draws the currently arrow
		toDraw = new ImageIcon("src/Current.png").getImage();
		if(themeColor.equals("")) {
			g2.drawImage(toDraw, 150, 110, 170, 35, null);
		} else if (themeColor.equals("Blue")) {
			g2.drawImage(toDraw, 150, 160, 170, 35, null);
		} else if (themeColor.equals("Red")) {
			g2.drawImage(toDraw, 150, 210, 170, 35, null);
		} else {
			g2.drawImage(toDraw, 150, 260, 170, 35, null);
		}
		// draws the basic text
		toDraw = new ImageIcon("src/Basic.png").getImage();
		g2.drawImage(toDraw, 55, 105, 80, 40, null);
		// draws the blue text
		toDraw = new ImageIcon("src/Blue.png").getImage();
		g2.drawImage(toDraw, 55, 155, 70, 40, null);
		// draws the red text
		toDraw = new ImageIcon("src/Red.png").getImage();
		g2.drawImage(toDraw, 55, 205, 70, 40, null);
		// draws the yellow text
		toDraw = new ImageIcon("src/Yellow.png").getImage();
		g2.drawImage(toDraw, 55, 260, 90, 34, null);
		// Draw the title screen
		toDraw = new ImageIcon("src/ColorsTitle.png").getImage();
		g2.drawImage(toDraw, 35, 5, 250, 70, null);
		// Draws the back button
		toDraw = new ImageIcon("src/BackButton.png").getImage();
		g2.drawImage(toDraw, 105, 350, 100, 50, null);
	}
	
	// determines what words show up in the end game text
	private String endGameTxt = "You landed on a mine! \n You ended with 420 points! \n Play again and beat your score!";
	
	// draws the end game screen
	private void drawEndGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// draws the background first
		Image toDraw = new ImageIcon("src/Background6.png").getImage();
		g2.drawImage(toDraw, 0, 0, 320, 450, null);
		// draws the title for the screen
		toDraw = new ImageIcon("src/GameOver.gif").getImage();
		g2.drawImage(toDraw, 65, 0, 190, 150, null);
		// draws the end game text
		g2.setColor(new Color(255, 255, 255));
		g2.setFont(new Font("Comic", Font.PLAIN, 14));
		drawCenteredString(g2, endGameTxt, 35, 150, 240);
		// draws the end game button
		toDraw = new ImageIcon("src/PlayAgain.png").getImage();
		g2.drawImage(toDraw, 105, 295, 100, 80, null);
	}
	
	// works if the mouse is left clicked
	public void LeftClick(int x, int y) {
		// For debugging purposes
		System.out.println("Left clicked at position (" + x + "," + y + ")");
		// runs different scripts for different screens
		switch(screen) {
		case title:
			LTitle(x,y);
			break;
		case instructions:
			LInstructions(x,y);
			break;
		case game:
			LGame(x,y);
			break;
		case nextSweep:
			LNextSweep(x,y);
			break;
		case themes:
			LThemes(x,y);
			break;
		case endGame:
			LEndGame(x,y);
			break;
		default:
			break;
		}
		repaint();
	}
	
	// runs if the the mouse is left clicked on the title screen
	private void LTitle(int x, int y) {
		// For the play button
		if(x > 100 && x < 225 && y > 365 && y < 440) {
			screen = Screen.instructions;
			showResumeInstructions = false;
		}
	}
	
	// runs if the mouse is left clicked on the instructions screen
	private void LInstructions(int x, int y) {
		// For the play button
		if(x > 110 && x < 210 && y > 375 && y < 440) {
			if(playing == 0) {
				// sets up the mines and changes all of the blocks to the basic form
				setUpBoard();
				// sets the screen to the game
				screen = Screen.game;
				// resets the points to 0
				points = 0;
				// start the timer
				timer.scheduleAtFixedRate(new TotalTime(this), 0, 1000);
				// start the game
				playing = 1;
			} else {
				showResumeInstructions = true;
			}
		}
		// If the resume instructions screen is showing up
		if(showResumeInstructions) {
			// if the resume button is clicked
			if(x > 75 && x < 155 && y > 185 && y < 215) {
				// set the screen to the game
				screen = Screen.game;
				// see whether to start or stop the timer
				if(flagged == 0 || flagged == 1) stopped = 0;
			}
			// if the start over button is clicked
			if(x > 165 && x < 245 && y > 185 && y < 215) {
				// stop the previous timer
				timer.cancel();
				timer = new Timer(true);
				// reset the time to 0
				time = 0;
				// sets up the mines and changes all of the blocks to the basic form
				setUpBoard();
				// sets the screen to the game
				screen = Screen.game;
				// resets the points to 0
				points = 0;
				// start the timer
				timer.scheduleAtFixedRate(new TotalTime(this), 0, 1000);
				// start the playing sequence
				playing = 1;
			}
		}
	}
	
	// runs if the mouse is left clicked on the game screen
	private void LGame(int x, int y) {
		// animates all of the mines
		if(!showNextScreenGame) {
			if(x > 6 && x < 306 && y > 65 && y < 365) {
				blockLClicked(((x - 6) / 30) * 10 + (y - 65) / 30);
			}
		} else { // if you can advance to the next screen
			if(x > 125 && x < 195 && y > 185 && y < 240) {
				if(flagged == 2) screen = Screen.nextSweep;
				else {
					screen = Screen.endGame;
					File file = new File("src/highScore.txt");
					try {
						if(file.createNewFile()) {
							FileWriter out = new FileWriter("src/highScore.txt");
							try {
								out.write("0");
							} catch (IOException e) {
								e.printStackTrace();
							}
							out.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					int highScore = 0;
					try {
						FileReader in = new FileReader("src/highScore.txt");
						BufferedReader reader = new BufferedReader(in);
						highScore = Integer.parseInt(reader.readLine());
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(highScore < points) {
						try {
							FileWriter out = new FileWriter("src/highScore.txt");
							out.write(String.valueOf(points));
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						endGameTxt = "Congrats! \n "
								+ "You ended with " + points + " points! You"
										+ " got the high score! You got " + points
										+ " points in " + time + " seconds.";
					} else {
						endGameTxt = "Congrats! \n "
								+ "You ended with " + points + " points! The high "
										+ "score is " + highScore + ". I'm sure"
												+ "you can beat the high score next time! "
												+ "You got your score in " + time + " seconds.";
					}
				}
			}
		}
		// If the home button was pressed
		if(x > 25 && x < 110 && y > 400 && y < 435) {
			// sets the screen to the title
			screen = Screen.title;
			// pause the timer
			stopped = 1;
			// For debugging
			System.out.println("Going home");
		}
		// Checks if the theme button was pressed
		if(x > 210 && x < 310 && y > 400 && y < 440) {
			// For debugging
			System.out.println("Going to themes");
			// go to the themes screen
			screen = Screen.themes;
			// stops the timer
			stopped = 1;
		}
	}
	
	// runs if a block was left clicked
	private void blockLClicked(int blockNumber) {
		// this theme is reused over and over again, so I'd rather save it as a variable
		String blankTheme = "src/" + themeColor + "Blank.png";
		// checks to make sure that the block was blank
		if(mineImages[blockNumber].equals(blankTheme) && flagged == 0) {
			// checks if the block is a mine
			if(minesAround.get(blockNumber) == -1) {
				// For debugging
				System.out.println("BOOM!");
				// set the flagged to finished
				flagged = 3;
				// show all the blocks on the page
				for (int i = 0; i < 100; ++i) {
					showBlock(i);
				}
				// Show the text that will lead to the end screen
				showNextScreenGame = true;
				nextScreenBackgroundTxt = "Oh no! \n You have landed on a mine!";
				// end the timer
				timer.cancel();
				// set playing to 0
				playing = 0;
			} else { // If the block clicked is not a mine
				// For debugging
				System.out.println("Clicked on block " + blockNumber);
				// show the block
				showBlock(blockNumber);
				// if the block has a number of 0
				if(minesAround.get(blockNumber) == 0) {
					// Create a queue for the blocks that need to be checked for surroundings
					Queue<Integer> expandBlocks = new LinkedList<Integer>();
					expandBlocks.add(blockNumber);
					// runs until the queue is empty
					while(!expandBlocks.isEmpty()) {
						// gets the first number in the queue
						int block = expandBlocks.poll();
						// show the block specified
						showBlock(block);
						// up
						if(block > 9 && mineImages[block - 10].equals(blankTheme)) {
							if(minesAround.get(block - 10) == 0) {
								expandBlocks.add(block - 10);
							} 
							showBlock(block - 10);
						}
						// right
						if(block % 10 != 9 && mineImages[block + 1].equals(blankTheme)) {
							if(minesAround.get(block + 1) == 0) {
								expandBlocks.add(block + 1);
							}
							showBlock(block + 1);
						}
						// down
						if(block < 90 && mineImages[block + 10].equals(blankTheme)) {
							if(minesAround.get(block + 10) == 0) {
								expandBlocks.add(block + 10);
							}
							showBlock(block + 10);
						}
						// left
						if(block % 10 != 0 && mineImages[block - 1].equals(blankTheme)) {
							if(minesAround.get(block - 1) == 0) {
								expandBlocks.add(block - 1);
							} 
							showBlock(block - 1);
						}
						// upper left
						if(block % 10 != 0 && block > 9 && 
								mineImages[block - 11].equals(blankTheme)) {
							if(minesAround.get(block - 11) == 0) {
								expandBlocks.add(block - 11);
							} 
							showBlock(block - 11);
						}
						// upper right
						if(block % 10 != 9 && block > 9 &&
								mineImages[block - 9].equals(blankTheme)) {
							if(minesAround.get(block - 9) == 0) {
								expandBlocks.add(block - 9);
							}
							showBlock(block - 9);
						}
						// lower left
						if(block % 10 != 0 && block < 90 && 
								mineImages[block + 9].equals(blankTheme)) {
							if(minesAround.get(block + 9) == 0) {
								expandBlocks.add(block + 9);
							}
							showBlock(block + 9);
						}
						// lower right
						if(block % 10 != 9 && block < 90 && 
								mineImages[block + 11].equals(blankTheme)) {
							if(minesAround.get(block + 11) == 0) {
								expandBlocks.add(block + 11);
							}
							showBlock(block + 11);
						}
					}
				}
			}
		} else {
			if(mineImages[blockNumber] != "src/" + themeColor + "Flag.png") {
				System.err.println("Error: block " + blockNumber + " already opened.");
			} else {
				System.err.println("Error: block " + blockNumber + " flagged.");
			}
		}
		// sees if there are any blocks left
		if(blocksLeft == 0) {
			// prints out that the mine is finished
			System.out.println("Finished the mine sweep");
			// Sets flagged to 2 to stop clicking the blocks
			flagged = 2;
			// shows the screen that allows the player to advance to the next screen
			showNextScreenGame = true;
			nextScreenBackgroundTxt = "Congrats! \n You finished this mine sweep!";
			// pause the timer
			stopped = 1;
		}
	}
	
	// runs if the mouse is left clicked on the next sweep screen
	private void LNextSweep(int x, int y) {
		// sees if the mouse is on the next minefield button
		if(x > 100 && x < 230 && y > 330 && y < 400) {
			// set up the board
			setUpBoard();
			// change the screen to the game
			screen = Screen.game;
			// set the timer to normal
			stopped = 0;
		}
	}
	
	// runs if the mouse is left clicked on the themes screen
	private void LThemes(int x, int y) {
		// sees if the basic image has been clicked
		if(x > 15 && x < 55 && y > 105 && y < 145) {
			// For debugging
			System.out.println("Changed theme color to basic");
			// change the color
			changeColorBoard("");
		}
		// sees if the blue image has been clicked
		if(x > 15 && x < 55 && y > 155 && y < 195) {
			// For debugging
			System.out.println("Changed theme color to blue");
			// change the color
			changeColorBoard("Blue");
		}
		// sees if the red image has been clicked
		if(x > 15 && x < 55 && y > 205 && y < 245) {
			// For debugging
			System.out.println("Changed theme color to red");
			// change the color
			changeColorBoard("Red");
		}
		// sees if the yellow image has been clicked
		if(x > 15 && x < 55 && y > 255 && y < 295) {
			// For debugging
			System.out.println("Changed theme color to yellow");
			// change the color
			changeColorBoard("Yellow");
		}
		// sees if the back button has been pressed
		if(x > 105 && x < 205 && y > 350 && y < 400) {
			// For debugging
			System.out.println("Back to game screen");
			// set the screen to the game
			screen = Screen.game;
			// return the timer to normal
			if(flagged == 0) stopped = 0;
		}
	}
	
	// runs if the mouse is left clicked on the end game screen
	private void LEndGame(int x, int y) {
		// sees if the play again button was pressed
		if(x > 105 && x < 205 && y > 295 && y < 375) {
			// sets the screen to the title so the player can play again
			screen = Screen.title;
			// resets the timer
			timer = new Timer(true);
			// For debugging
			System.out.println("Back to the main screen");
		}
	}
	
	// works if the mouse is right clicked
	public void RightClick(int x, int y) {
		System.out.println("Right clicked at position (" + x + "," + y + ")");
		switch(screen) {
		case game:
			RGame(x, y);
			break;
		default:
			break;
		}
		repaint();
	}
	
	// if the game was right clicked on the game screen
	private void RGame(int x, int y) {
		// animates all of the mines
		if(!showNextScreenGame) {
			if(x > 6 && x < 306 && y > 65 && y < 365) {
				blockRClicked(((x - 6) / 30) * 10 + (y - 65) / 30);
			}
		}
	}
	
	// runs if a block was right clicked
	private void blockRClicked(int blockNumber) {
		if(mineImages[blockNumber].equals("src/" + themeColor + "Blank.png")
				&& minesLeft > 0) {
			// set the blocks clicked in flag mode
			mineImages[blockNumber] = "src/" + themeColor + "Flag.png";
			// For debugging
			System.out.println("Flagged block " + blockNumber);
			// Subtracts one from the mine count
			--minesLeft;
		} else if (mineImages[blockNumber].equals("src/" + themeColor + "Flag.png")) {
			mineImages[blockNumber] = "src/" + themeColor + "Blank.png";
			// For debugging
			System.out.println("Unflagged block " + blockNumber);
			// Adds one to the mine count
			++minesLeft;
		} else {
			// Error code because you cannot flag/unflag
			System.err.println("Error: Block already revealed or flag limit reached");
		}
	}
	
	// works if the mouse exits the application
	public void ExitApp(int x, int y) {
		System.out.println("Exited the frame at position (" + x + "," + y + ")");
	}
	
	// works if the mouse enters the application
	public void EnterApp(int x, int y) {
		System.out.println("Entered the frame at position (" + x + "," + y + ")");
	}
	
	// sets up all the positions of all the mines and also changes all of the blocks to the basic form
	private void setUpBoard() {
		// hides the sprite for the next screen
		showNextScreenGame = false;
		// one loop for every single mine
		for(int i = 0; i < 10; ++i) {
			// set the mine to a random number
			mines[i] = generator.nextInt(100);
			// check to make sure that the mine is not one of the corners
			while(mines[i] == 0 || mines[i] == 9 || mines[i] == 90 || mines[i] == 99) {
				mines[i] = generator.nextInt(100);
			}
			// check to make sure that the mine is not previously used
			for(int j = 0; j < i; ++j) {
				// checks if the mine is not the same as another mine
				if(mines[i] == mines[j]) {
					// if the mine is the same as another mine
					// re-check all of the mines
					j = 0;
					// sets the mine to another random number
					mines[i] = generator.nextInt(100);
					// check to make sure that the mine is not one of the corners
					while(mines[i] == 0 || mines[i] == 9 || mines[i] == 90 || mines[i] == 99) {
						mines[i] = generator.nextInt(100);
					}
				}
			}
		}
		// set the array for the number of mines in each position
		minesAround = new ArrayList<Integer>();
		for(int j = 0; j < 100; ++j) {
			minesAround.add(numberOfMines(j));
		}
		// set the mine positions as all -1
		for(int j = 0; j < 10; ++j) {
			minesAround.set(mines[j], -1);
		}
		// for the programmer to see where all the mines are
		// mainly for debugging
		System.out.print("mine position:");
		for(int i : mines) {
			System.out.print(i + " ");
		}
		System.out.println();
		// also change all of the pictures back to the uncovered photo
		for(int i = 0; i < 100; ++i) {
			mineImages[i] = "src/" + themeColor + "Blank.png";
		}
		// sets the blocksLeft equal to 90
		blocksLeft = 90;
		// sets the minesLeft equal to 10
		minesLeft = 10;
		// set flagged to 0
		flagged = 0;
		// set the timer to normal
		stopped = 0;
	}
	
	// Checks the number of mines around a specified location
	private int numberOfMines(int blockNumber) {
		// initialize the return value
		int minesAroundBlock = 0;
		// upper left
		int tempCheck = blockNumber - 11;
		if(blockNumber % 10 != 0 && checkMine(tempCheck)) ++minesAroundBlock;
		// up
		tempCheck = blockNumber - 10;
		if(checkMine(tempCheck)) ++minesAroundBlock;
		// upper right
		tempCheck = blockNumber - 9;
		if(blockNumber % 10 != 9 && checkMine(tempCheck)) ++minesAroundBlock;
		// right
		tempCheck = blockNumber + 1;
		if(blockNumber % 10 != 9 && checkMine(tempCheck)) ++minesAroundBlock;
		// bottom right
		tempCheck = blockNumber + 11;
		if(blockNumber % 10 != 9 && checkMine(tempCheck)) ++minesAroundBlock;
		// bottom
		tempCheck = blockNumber + 10;
		if(checkMine(tempCheck)) ++minesAroundBlock;
		// bottom left
		tempCheck = blockNumber + 9;
		if(blockNumber % 10 != 0 && checkMine(tempCheck)) ++minesAroundBlock;
		// left
		tempCheck = blockNumber - 1;
		if(blockNumber % 10 != 0 && checkMine(tempCheck)) ++minesAroundBlock;
		// return how many mines there are around the block
		return minesAroundBlock;
	}
	
	// checks if there is a mine at the location specified
	private boolean checkMine(int blockNumber) {
		for(int i : mines) {
			if(i == blockNumber) return true;
		}
		return false;
	}
	
	/** Show the block specified
	 * BlockNumber is the block number the player wants to reveal
 	 * Does not repaint
 	 * */
	private void showBlock(int blockNumber) {
		// If the player is not on the end screen
		if(flagged == 0 && minesAround.get(blockNumber) >= 0 &&
				mineImages[blockNumber].equals("src/" + themeColor + "Blank.png")) {
			mineImages[blockNumber] = "src/" + themeColor + minesAround.get(blockNumber) + ".png";
			points += minesAround.get(blockNumber);
			--blocksLeft;
		}
		// if the player clicked on a mine
		else if (flagged == 3) {
			if(minesAround.get(blockNumber) >= 0) {
				mineImages[blockNumber] = "src/" + themeColor + minesAround.get(blockNumber) + ".png";
			} else {
				mineImages[blockNumber] = "src/" + themeColor + "Bomb.png";
			}
		}
	}
	
	/**Change all of the colors of the board
	 * @param newTheme the theme color of the board you want to change it to
	 */
	private void changeColorBoard(String newTheme) {
		// change all of the blocks to the new theme
		for(int i = 0; i < 100; ++i) {
			mineImages[i] = "src/" + newTheme + mineImages[i].substring(themeColor.length() + 4);
		}
		// change the new theme to the new theme
		themeColor = newTheme;
	}
	
	// A class used to keep track of the total game time in the program
	class TotalTime extends TimerTask {
		// hopefully this does not destroy my program
		// To make this class not static
		private MainComponent maincomponent;
		
		// Initializes the maincomponent with the actual main component used
		TotalTime(MainComponent maincomponent) {
			this.maincomponent = maincomponent;
			maincomponent.time = 0;
		}
		
		// adds one to the time and refreshes the page
		@Override
		public void run() {
			if(maincomponent.stopped == 0) ++maincomponent.time;
			maincomponent.repaint();
		}
	}
	
	// A class used to make the animations run
	class RunAnimations extends TimerTask {
		// To make this class not static
		private MainComponent maincomponent;
		
		// Initializes the maincomponent with the actual main component used
		RunAnimations(MainComponent maincomponent) {
			this.maincomponent = maincomponent;
		}
		
		// refreshes the animations
		@Override
		public void run() {
			maincomponent.repaint();
		}
	}
}
