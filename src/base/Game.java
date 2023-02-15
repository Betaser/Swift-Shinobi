package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import base.input.Input;
import console.Console;
import console.ConsolePanel;
import math.Polygon;
import math.Sizer;
import math.Vec;
import state.CollisionSimState;
import state.State;
import state.SurroundsPointSimState;
import state.TestLevelState;
import state.levelDesignState.LevelDesignState;

public class Game {
	
	private Dimension size;
	
	private static Game game;
	
	public TreeMap<Double, BufferedImage> imageLayers;
	public CollisionSimState collisionSimState;
	public SurroundsPointSimState surroundsPointSimState;
	public TestLevelState testLevelState;
	public LevelDesignState levelDesignState;
	public ConsolePanel genConsolePanel;
	public State state;
	public JFrame window;
	public JLayeredPane pane;
	public Input input;
	public GamePanel gamePanel;
	
	int fps;
	
	public class GamePanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		private ArrayList<BufferedImage> toDraw;
		
		boolean startedRunning;
		
		public GamePanel() {
			toDraw = new ArrayList<>();
			setPreferredSize(size);
			setBackground(Color.WHITE);
			setDoubleBuffered(true);
		}
		
		@Override
		public void repaint() {
			if (!startedRunning)
				return;
			super.repaint();
			synchronized (toDraw) {
				toDraw.clear();
				toDraw.addAll(imageLayers.values());
				imageLayers.clear();
			}
		}
		
		//	Wildly asynchronous
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, size.width, size.height);
			
			synchronized (toDraw) {
				for (BufferedImage layer : toDraw)
					g.drawImage(layer, 0, 0, size.width, size.height, null);
			}
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.PLAIN, 14));
			g.drawString("FPS: " + fps, 0, 12);
		}
	}
	
	public Game(String programName, int width, int height, int targetFPS) {
		game = this;
		this.fps = targetFPS;
		size = new Dimension(width, height);
		imageLayers = new TreeMap<>((d1, d2) -> -Double.compare(d1, d2));
		
		window = new JFrame(programName);
		window.setSize(size);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		gamePanel = new GamePanel();
		
		pane = new JLayeredPane();
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setPreferredSize(new Dimension(width, height));

		window.add(gamePanel);
		window.pack();
		gamePanel.add(pane);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		input = new Input();
		gamePanel.addKeyListener(input);
		gamePanel.addMouseListener(input);
		gamePanel.addMouseWheelListener(input);
		gamePanel.addMouseMotionListener(input);
		gamePanel.setFocusTraversalKeysEnabled(false);
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();
		
		Sizer.DEF.center = new Vec(getWidth() / 2, getHeight() / 2);
		
		Console genConsole = new Console();
		genConsolePanel = new ConsolePanel(genConsole);
		genConsole.init(input.cursor, genConsolePanel);
		genConsolePanel.initBounds();
		
		collisionSimState = new CollisionSimState();
		testLevelState = new TestLevelState();
		levelDesignState = new LevelDesignState();
		surroundsPointSimState = new SurroundsPointSimState();
		
		collisionSimState.init();
		testLevelState.init();
		levelDesignState.init();
		surroundsPointSimState.init();
		
		state = levelDesignState;
	}
	
	public BufferedImage createLayer() {
		return createLayer(size.width, size.height);
	}
	
	public BufferedImage createLayer(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
	}
	
	public void fillPolygon(Graphics g, Polygon polygon) {
		int[] xPoints = new int[polygon.vertices.size()];
		int[] yPoints = new int[polygon.vertices.size()];
		for (int i = 0; i < polygon.vertices.size(); i++) {
			Vec vertex = polygon.vertices.get(i);
			xPoints[i] = vertex.getXInt();
			yPoints[i] = getHeight() - vertex.getYInt();
		}
		g.fillPolygon(xPoints, yPoints, polygon.vertices.size());
	}
	
	//	No cam, use sizer
	public void drawLine(Graphics g, int x1, int y1, int x2, int y2, Sizer sizer) {
		List<Vec> pts = List.of(new Vec(x1, y1), new Vec(x2, y2));
		sizer.resize(pts);
		g.drawLine(
			pts.get(0).getXInt(), 
			getHeight() - pts.get(0).getYInt(), 
			pts.get(1).getXInt(), 
			getHeight() - pts.get(1).getYInt()
		);
	}
	
	public Graphics getGraphics(double depth) {
		if (imageLayers.containsKey(depth))
			return imageLayers.get(depth).getGraphics();
		BufferedImage layer = createLayer();
		imageLayers.put(depth, layer);
		return layer.getGraphics();
	}
	
	public void draw(Consumer<Graphics> drawCall, double depth) {
		if (imageLayers.containsKey(depth)) {
			Graphics g = imageLayers.get(depth).getGraphics();
			drawCall.accept(g);
			return;
		}
		BufferedImage layer = createLayer();
		Graphics g = layer.getGraphics();
		drawCall.accept(g);
		imageLayers.put(depth, layer);
	}
	
	public void fillRect(Color color, Rectangle rect, double depth) {
		draw(g -> {
			g.setColor(color);
			g.fillRect(rect.x, rect.y, rect.width, getHeight() - rect.y - rect.height);
		}, depth);
	}
	
	public void drawRect(Color color, Rectangle rect, double depth) {
		draw(g -> {
			g.setColor(color);
			g.drawRect(rect.x, rect.y, rect.width, getHeight() - rect.y - rect.height);
		}, depth);
	}
	
	public void draw(BufferedImage image, double depth) {
		draw(image, 0, 0, size.width, size.height, depth);
	}
	
	private void draw(BufferedImage image, double x, double y, double width, double height, double depth) {
		if (imageLayers.containsKey(depth)) {
			Graphics g = imageLayers.get(depth).getGraphics();
			g.drawImage(image, (int) x, (int) (getHeight() - y - height), (int) width, (int) height, null);
			return;
		}
		BufferedImage layer = createLayer();
		Graphics g = layer.getGraphics();
		g.drawImage(image, (int) x, (int) (getHeight() - y - height), (int) width, (int) height, null);
		imageLayers.put(depth, layer);
	}
	
	public void run() {
		double drawInterval;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int renderCount = 0;
		gamePanel.startedRunning = true;
		
		while (true) {
			drawInterval = 1e9 / fps;
			now = System.nanoTime();
			delta += (now - lastTime) / drawInterval;
			timer += now - lastTime;
			lastTime = now;
			
			if (delta >= 1) {
				input.tick();
				state.tick();
				state.render();
				gamePanel.repaint();
				renderCount++;
				delta--;
			}
			
			if (timer >= 1e9) {
				fps = renderCount;
				renderCount = 0;
				timer = 0;
			}
		}
	}
	
	public int getWidth() {
		return size.width;
	}
	
	public int getHeight() {
		return size.height;
	}
	
	public static Game get() {
		return game;
	}
	
	public Console console() {
		return genConsolePanel.console;
	}
	
}
