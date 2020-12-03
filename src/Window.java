import javax.swing.JFrame;

public class Window extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JFrame windowGame;
	private static Game g;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		windowGame=new Window();
		windowGame.setAlwaysOnTop(true);
		g=new Game();
		windowGame.add(g);
		windowGame.pack();
		windowGame.setResizable(false);
		windowGame.setLocationRelativeTo(null);
		windowGame.setVisible(true);
		windowGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		g.start();
	}

	public Window()
	{
		super("TicTacToe");
	}
}
