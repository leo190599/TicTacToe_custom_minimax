import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Game extends Canvas implements Runnable,MouseListener{

	public final int width=300,height=300;
	private boolean isRunning=false;
	private BufferStrategy bs;
	private Thread t;
	private Graphics g;
	private int[][] cells=new int[3][3];
	private BufferedImage enemy;
	private BufferedImage player;
	private final int p=1,e=-1;
	private int curPlayer=e;
	private boolean mousePressed=false;
	private int mx,my;
	
	public Game()
	{
		this.setPreferredSize(new Dimension(width,height));
		try {
			player = ImageIO.read(getClass().getResource("/player.png"));
			enemy = ImageIO.read(getClass().getResource("/enemy.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addMouseListener(this);
		restart();
	}
	
	public void restart()
	{
		for(int xx=0;xx<cells.length;xx++)
		{
			for(int yy=0;yy<cells.length;yy++)
			{
				cells[xx][yy]=0;
			}
		}
	}
	
	public void start()
	{
		isRunning=true;
		t=new Thread(this);
		t.start();
	}
	
	private void stop()
	{
		isRunning=false;
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkCells(int startX,int startY,int stepX,int stepY,int playerToCheck,int interactions)
	{
		for(int i=0;i<interactions;i++)
		{
			if(cells[startX][startY]!=playerToCheck)
			{
				return false;
			}
			startX+=stepX;
			startY+=stepY;
		}
		return true;
	}
	
	private boolean checkPlayerVictory()
	{
		for(int i=0;i<cells.length;i++)
		{
			if(checkCells(0,i,1,0,p,3))
				return true;
		}
		for(int i=0;i<cells.length;i++)
		{
			if(checkCells(i,0,0,1,p,3))
				return true;
		}
		if(checkCells(0,0,1,1,p,3)||checkCells(2,0,-1,1,p,3))
		{
			return true;
		}
		return false;
	}
	
	private boolean checkEnemyVictory()
	{
		for(int i=0;i<cells.length;i++)
		{
			if(checkCells(0,i,1,0,e,3))
				return true;
		}
		for(int i=0;i<cells.length;i++)
		{
			if(checkCells(i,0,0,1,e,3))
				return true;
		}
		if(checkCells(0,0,1,1,e,3)||checkCells(2,0,-1,1,e,3))
		{
			return true;
		}
		return false;
	}
	
	private int checkVictory()
	{
		if(checkPlayerVictory())
		{
			return p;
		}
		if(checkEnemyVictory())
		{
			return e;
		}
		for(int xx=0;xx<cells.length;xx++)
		{
			for(int yy=0;yy<cells[0].length;yy++)
			{
				if(cells[xx][yy]==0)
				{
					return -2;
				}
			}
		}
		return 0;
	}
	
	private void tick()
	{
		switch(checkVictory())
		{
			case p:
				System.out.println("Player Ganhou");
				System.exit(1);
				break;
			case e:
				System.out.println("Oponente Ganhou");
				System.exit(1);
				break;
			case 0:
				System.out.println("Empate");
				System.exit(1);
				break;
		}
		if(mousePressed)
		{
			mousePressed=false;
			if(curPlayer==p)
			{
				if(cells[mx][my]==0)
				{
					cells[mx][my]=p;
					curPlayer=e;
				}
			}
		}
		else if(curPlayer==e)
		{
			Node cellToPlay=new Node(0,0,0,-100);
			
			for(int c=0;c<cells.length*cells[0].length;c++)
			{
				if(cells[c%3][c/3]==0)
				{
					cellToPlay=getBestMoviment(c%3,c/3,0,e);
					break;
				}	
			}
			cells[cellToPlay.x][cellToPlay.y]=e;
			curPlayer=p;
		}
	}
	
	private Node getBestMoviment(int x, int y, int deph,int player)
	{	
		if(checkVictory()==p)
		{
			return new Node(x,y,deph,deph-10);	
		}
		else if(checkVictory()==e)
		{
			if(deph==1)
			{
				return new Node(x,y,deph,-10);
			}
			else
			{
				return new Node(x,y,deph,10-deph);
			}
		}
		else if(checkVictory()==0)
		{
			return new Node(x,y,deph,0);
		}
		List<Node> nodes=new ArrayList<Node>();
		for(int xx=0;xx<cells.length;xx++)
		{
			for(int yy=0;yy<cells[0].length;yy++)
			{
				if(cells[xx][yy]==0)
				{
					if(player==p)
					{
						cells[xx][yy]=p;
						nodes.add(getBestMoviment(xx,yy,deph+1,e));
					}
					else
					{
						cells[xx][yy]=e;
						nodes.add(getBestMoviment(xx,yy,deph+1,p));
					}
					cells[xx][yy]=0;
				}
			}
		}
		
		Node finalNode=nodes.get(0);
		for(int i=0;i<nodes.size();i++)
		{	
			if(player==p)
			{
				if(nodes.get(i).score>finalNode.score)
				{
					finalNode=nodes.get(i);
				}
			}
			else
			{
				if(nodes.get(i).score<finalNode.score)
				{
					finalNode=nodes.get(i);
				}
			}
		}
		return finalNode;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	private Node getBestMoviment(int x, int y, int deph,int player)
	{
		if(checkVictory()==p)
			return new Node(x,y,deph,10-deph);
		else if(checkVictory()==e)
			return new Node(x,y,deph,deph-10);
		else if(checkVictory()==0)
			return new Node(x,y,deph,0);
		
		List<Node> nodes=new ArrayList<Node>();
		
		for(int xx=0;xx<cells.length;xx++)
		{
			for(int yy=0;yy<cells[0].length;yy++)
			{
				if(cells[xx][yy]==0)
				{
					if(player==e)
					{
						cells[xx][yy]=e;
						nodes.add(getBestMoviment(xx,yy,deph+1,p));
					}
					else
					{
						cells[xx][yy]=p;
						nodes.add(getBestMoviment(xx,yy,deph+1,e));
					}
					cells[xx][yy]=0;
				}
			}
		}
		Node finalNode=nodes.get(0);
		for(int i=0;i<nodes.size();i++)
		{
			if(nodes.get(i).score>finalNode.score && player==p)
			{
				finalNode=nodes.get(i);
			}
			else if(nodes.get(i).score<finalNode.score)
			{
				finalNode=nodes.get(i);
			}
		}
		return finalNode;
	}
	
	*/
	private void render()
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		for(int xx=0;xx<cells.length;xx++)
		{
			for(int yy=0;yy<cells[0].length;yy++)
			{
				g.drawRect(xx*100, yy*100, 100, 100);
				if(cells[xx][yy]==p)
				{
					g.drawImage(player, xx*100+25, yy*100+25,50,50, null);
				}
				else if(cells[xx][yy]==e)
				{
					g.drawImage(enemy, xx*100+25, yy*100+25,50,50, null);
				}
			}
		}
		bs.show();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.requestFocus();
		createBufferStrategy(3);
		bs=this.getBufferStrategy();
		g=bs.getDrawGraphics();
		int fps=60;
		final double ns=1000000000/fps;
		double lastTime=System.nanoTime();
		double curTime=System.nanoTime();
		double lastTimeFps=System.currentTimeMillis();
		double curTimeFps=System.currentTimeMillis();
		double deltaTimeFps=curTimeFps-lastTimeFps;
		double deltaTime=curTime-lastTime;
		int fpsCounter=0;
		while(isRunning)
		{
			deltaTime+=curTime-lastTime;
			lastTime=curTime;
			if(deltaTime>=ns)
			{
				deltaTime=0;
				tick();
				render();
				fpsCounter+=1;
			}
			deltaTimeFps+=curTimeFps-lastTimeFps;
			lastTimeFps=curTimeFps;
			curTimeFps=System.currentTimeMillis();
			if(deltaTimeFps>=1000)
			{
				//System.out.println(fpsCounter);
				fpsCounter=0;
				deltaTimeFps=0;
			}
			curTime=System.nanoTime();
		}
		stop();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		mx=e.getX()/100;
		my=e.getY()/100;
		mousePressed=true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
