import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import input.CustomEventReceiver;
import misc.Canvas;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class Art {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int VERSION_SHAPE_CIRCLE = 0;
	public final static int VERSION_SHAPE_QUARTER_CIRCLE = 1;
	
	public final static int VERSION_INIT_VAPOR = 0;
	public final static int VERSION_INIT_NONB = 1;
	public final static int VERSION_INIT_NONB_CIRCLE = 2;
	public final static int VERSION_INIT_NONB_DIAGONAL = 3;
	public final static int VERSION_INIT_NONB_QUARTER_CIRCLE = 4;
	
	public final static int VERSION_FUNC_CIRCLE_RHYTHM = 0;
	public final static int VERSION_FUNC_CRT = 1;
	public final static int VERSION_FUNC_QUARTER_CIRCLE_RHYTHM = 2;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static WindowFrame fra;

//---  Operations   ---------------------------------------------------------------------------
	
	public static void main(String[] args) {
		run();
	}

	public static void run() {
		//preset1(500, 500, 600, 1, 10, 20, 30, 1, 1); nonb crt
		
		preset1(500, 500, 150, 1, 5, 10, 15, 0, 0, 1);
		
		//preset1(32, 32, 150, 1, 5, 10, 15);
		//preset3(32, 32, 210, 1, 10, 10, 10);
		//preset2(500, 500, 150, 1, 5, 10, 15);
	}
	
//---  Getter Functions   ---------------------------------------------------------------------
	
	public WindowFrame getFrame() {
		return fra;
	}
	
//---  Preset Run Conditions   ----------------------------------------------------------------
	
	private static void preset1(int wid, int hei, int maxPeriod, int zoomScale, int rChange, int gChange, int bChange, int verShape, int verInit, int verPatt) {
		int x = wid / 2 / zoomScale;
		int y = hei / 2 / zoomScale;
		int maxSize = wid / 2 / zoomScale;
		int minSize = wid / 4 / zoomScale;
		Canvas ca = new Canvas(wid, hei) {

			@Override
			public void initialize() {
				super.initialize();
				switch(verInit) {
				case 0: initializeVaporwave(x, y, maxSize, this);
					break;
				case 1: initializeNonb(x, y, maxSize, this);
					break;
				case 2: initializeNonbCircle(x, y, maxSize, this);
					break;
				case 3 : initializeNonbDiagonals(x, y, maxSize, this);
					break;
				case 4 : initializeNonbCornerRadius(x, y, maxSize, this);
					break;
				}
			}
			
			int counter = 0;
			
			@Override
			public void draw(Graphics g2, int xD, int yD, int maxWi, int maxHe) {
				super.draw(g2, xD, yD, maxWi, maxHe);
				switch(verPatt) {
				case -1:
					break;
				case 0:
					drawRhythmCirclePulse(x, y, minSize, maxSize, maxPeriod, counter, rChange, gChange, bChange, this);
					break;
				case 1:
					drawRhythmicCRTPulse(x, y, maxSize, hei / 10, maxPeriod, counter, rChange, gChange, bChange, this);
					break;
				case 2:
					drawRhythmQuarterCirclePulse(0, 0, minSize, maxSize, maxPeriod, counter, rChange, gChange, bChange, this);
					break;
				}
				counter = (counter + 1) % maxPeriod;
			}
		};
			
		ElementPanel eP = new ElementPanel(0, 0, wid, hei);
		eP.setEventReceiver(new CustomEventReceiver(){
			
			/*
			@Override
			public void mouseMoveEvent(int event, int x, int y) {
				eP.getParentFrame().getFrame().setLocation(x, y);
			}
			*/
			
			boolean dragged = false;
			int lastX;
			int lastY;
			
			@Override
			public void clickPressEvent(int code, int x2, int y2, int type) {
				lastX = x2;
				lastY = y2;
				dragged = true;
			}
			
			@Override
			public void clickReleaseEvent(int code, int x2, int y2, int type) {
				dragged = false;
			}
			
			@Override
			public void dragEvent(int code, int x2, int y2, int type) {
				if(dragged) {
					Point p = eP.getParentFrame().getFrame().getLocationOnScreen();
					eP.getParentFrame().getFrame().setLocation((int)(p.getX() + (x2 - lastX)), (int)(p.getY() + (y2 - lastY)));
				}
			}
			
		});
		eP.addCanvas("ca", 5, false, 0, 0, wid, hei, ca, 5);
		fra = new WindowFrame(wid, hei);
		//fra.getFrame().
		
		switch(verShape) {
			case 0:
				establishDiscFrame(fra, wid, hei);
				break;
			case 1:
				establishQuarterCircleFrame(fra, wid, hei);
				break;
		}
		
		fra.setName("Ada's Algorithm Art - Test");
		fra.addPanel("ca", eP);
	}
	
//---  Window Shape Functions   ---------------------------------------------------------------
	
	private static void establishDiscFrame(WindowFrame in, int wid, int hei) {
		fra.getFrame().setVisible(false);
		fra.getFrame().dispose();
		fra.getFrame().setUndecorated(true);
		fra.getFrame().setShape(new Ellipse2D.Double(0, 0, wid, hei));		//Can you make a quarter circle frame?
		fra.getFrame().setSize(wid, hei);
		//fra.getFrame().setOpacity(.25f);
		fra.getFrame().setVisible(true);
	}
	
	private static void establishQuarterCircleFrame(WindowFrame in, int wid, int hei) {
		fra.getFrame().setVisible(false);
		fra.getFrame().dispose();
		fra.getFrame().setUndecorated(true);
		Path2D shape = new Path2D.Double();
		double mathIsWeird = .551915024494;
		shape.moveTo(0, 0);
		shape.lineTo(wid, 0);
		//shape.curveTo(mathIsWeird * wid, hei, wid, wid * mathIsWeird, 0, hei);		//bezier points are backwards, cool inlets
		shape.curveTo(wid, hei * mathIsWeird, wid * mathIsWeird, hei, 0, hei);
		shape.lineTo(0, 0);
		fra.getFrame().setShape(shape);		//Can you make a quarter circle frame?
		fra.getFrame().setSize(wid, hei);
		//fra.getFrame().setOpacity(.25f);
		fra.getFrame().setVisible(true);
	}
	
//---  Initial Image Generation   -------------------------------------------------------------
	
	private static void initializeVaporwave(int x, int y, int maxSize, Canvas ref) {
		for(int i = x - maxSize; i < x + maxSize; i++) {
			for(int j = y - maxSize; j < y + maxSize; j++) {
				double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
				if(dist >= maxSize) {
					ref.setPixelColor(i, j, new Color(0, 0, 0, 0));
				}
			}
		}
	}
	
	private static void initializeNonb(int x, int y, int maxSize, Canvas ref) {
		for(int i = x - maxSize; i < x + maxSize; i++) {
			for(int j = y - maxSize; j < y + maxSize; j++) {
				switch((int)(j / (maxSize / 2.0))) {
					case 0:
						ref.setPixelColor(i, j, new Color(255, 244, 51));
						break;
					case 1:
						ref.setPixelColor(i, j, new Color(255, 255, 255));
						break;
					case 2:
						ref.setPixelColor(i, j, new Color(155, 89, 208));
						break;
					case 3:
						ref.setPixelColor(i, j, new Color(0, 0, 0));
						break;
				}
			}
		}
	}
	
	private static void initializeNonbCircle(int x, int y, int maxSize, Canvas ref) {
		for(int i = x - maxSize; i < x + maxSize; i++) {
			for(int j = y - maxSize; j < y + maxSize; j++) {
				double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
				switch((int)(dist / (maxSize / 4))) {
					case 0:
						ref.setPixelColor(i, j, new Color(255, 244, 51));
						break;
					case 1:
						ref.setPixelColor(i, j, new Color(255, 255, 255));
						break;
					case 2:
						ref.setPixelColor(i, j, new Color(155, 89, 208));
						break;
					case 3:
						ref.setPixelColor(i, j, new Color(0, 0, 0));
						break;
				}
			}
		}
	}
	
	private static void initializeNonbCornerRadius(int x, int y, int maxSize, Canvas ref) {
		for(int i = x - maxSize; i < x + maxSize; i++) {
			for(int j = y - maxSize; j < y + maxSize; j++) {
				double dist = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
				switch((int)(dist / (maxSize / 2))) {
					case 0:
						ref.setPixelColor(i, j, new Color(255, 244, 51));
						break;
					case 1:
						ref.setPixelColor(i, j, new Color(255, 255, 255));
						break;
					case 2:
						ref.setPixelColor(i, j, new Color(155, 89, 208));
						break;
					case 3:
						ref.setPixelColor(i, j, new Color(0, 0, 0));
						break;
				}
			}
		}
	}
	
	private static void initializeNonbDiagonals(int x, int y, int maxSize, Canvas ref) {
		for(int i = x - maxSize; i < x + maxSize; i++) {
			for(int j = y - maxSize; j < y + maxSize; j++) {
				int val = j < maxSize ? 0 : 2;
				val += (i - j < maxSize) ? 0 : 1;
				switch(val) {
					case 0:
						ref.setPixelColor(i, j, new Color(255, 244, 51));
						break;
					case 1:
						ref.setPixelColor(i, j, new Color(255, 255, 255));
						break;
					case 2:
						ref.setPixelColor(i, j, new Color(155, 89, 208));
						break;
					case 3:
						ref.setPixelColor(i, j, new Color(0, 0, 0));
						break;
				}
			}
		}
	}
	
//---  Drawing Patterns   ---------------------------------------------------------------------
	
	private static void drawRhythmCirclePulse(int x, int y, int minSize, int maxSize, int maxPeriod, int counter, int rChange, int gChange, int bChange, Canvas ref) {
		double prop = Math.sin((double)(counter) / maxPeriod * (2 * Math.PI));
		int distMax = (int)(maxSize * prop);
		int distMin = (int)(minSize * prop);
		for(int i = x - distMax; i < x + distMax; i++) {
			for(int j = y - distMax; j < y + distMax; j++) {
				double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
				if(dist <= distMax && dist >= distMin) {
					Color c = ref.getPixelColor(i, j);
					int r = c.getRed() + (int)(rChange * prop);
					int g = c.getGreen() + (int)(gChange * prop);
					int b = c.getBlue() + (int)(bChange / prop);
					ref.setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
				}
			}
		}
	}
	
	private static void drawRhythmQuarterCirclePulse(int x, int y, int minSize, int maxSize, int maxPeriod, int counter, int rChange, int gChange, int bChange, Canvas ref) {
		double prop = Math.sin((double)(counter + maxPeriod / 4) / maxPeriod * (2 * Math.PI)) / 2 + .5;
		int distMax = (int)(maxSize * 2 * prop);
		int distMin = (int)(minSize * 2 * prop);
		for(int i = 0; i < maxSize * 2; i++) {
			for(int j = 0; j < maxSize * 2; j++) {
				double dist = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
				if(dist <= distMax && dist >= distMin) {
					Color c = ref.getPixelColor(i, j);
					int r = c.getRed() + (int)(rChange * prop);
					int g = c.getGreen() + (int)(gChange * prop);
					int b = c.getBlue() + (int)(bChange / prop);
					ref.setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
				}
			}
		}
	}
	
	private static int establish = 1;
	
	private static void drawRhythmicCRTPulse(int x, int y, int maxSize, int thick, int maxPeriod, int counter, int rChange, int gChange, int bChange, Canvas ref) {
		int maxEnt = 6;
		for(int i = 0; i < maxSize * 2; i++) {
			int curr = (counter + maxPeriod / (2 * maxEnt / 3)) / (maxPeriod / maxEnt);
			establish = establish > curr ? establish : curr;
			for(int k = 0; k < maxEnt && k < establish; k++) {
				double prop = (counter + maxPeriod / maxEnt * (maxEnt - k)) / (double)maxPeriod;
				prop %= 1.0;
				for(int j = (int)(prop * (maxSize * 2 + thick * 2)) - thick; j < (int)(prop * (maxSize * 2 + thick * 2) - thick / 2); j++) {
					if(j < 0 || j >= maxSize * 2) {
						continue;
					}
					Color c = ref.getPixelColor(i, j);
					int r = c.getRed() + (int)(rChange);
					int g = c.getGreen() + (int)(gChange);
					int b = c.getBlue() + (int)(bChange);
					ref.setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
				}
			}
		}
	}
	
//---  Whatever   -----------------------------------------------------------------------------
	
	/*
	private void preset2(int wid, int hei, int maxPeriod, int zoomScale, int rChange, int gChange, int bChange) {
		int x = wid / 2 / zoomScale;
		int y = hei / 2 / zoomScale;
		int maxSize = wid / zoomScale;
		int minSize = wid / 2 / zoomScale;
		
		CanvasPanel ca = new CanvasPanel(0, 0, wid, hei, zoomScale) {

			int counter = 0;
			
			@Override
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				double prop = Math.sin((double)(counter) / maxPeriod * (2 * Math.PI));
				int distMax = (int)(maxSize * prop);
				int distMin = (int)(minSize * prop);
				for(int i = x - distMax; i < x + distMax; i++) {
					for(int j = y - distMax; j < y + distMax; j++) {
						if(i < 0 || i >= wid || j < 0 || j >= hei) {
							continue;
						}
						double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
						if(dist <= distMax && dist >= distMin) {
							Color c = getPixelColor(i, j);
							int r = c.getRed() + (int)(rChange * prop);
							int g = c.getGreen() + (int)(gChange * prop);
							int b = c.getBlue() + (int)(bChange / prop);
							setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
						}
					}
				}
				counter = (counter + 1) % maxPeriod;
			}
		};
		WindowFrame fra = new WindowFrame(wid, hei);
		fra.addPanel("ca", ca);
	}
	
	private void preset3(int wid, int hei, int maxPeriod, int zoomScale, int rChange, int gChange, int bChange) {
		int x = wid / 2 / zoomScale;
		int y = hei / 2 / zoomScale;
		int maxSize = wid / 2 / zoomScale;
		int minSize = wid / 4 / zoomScale;
		CanvasPanel ca = new CanvasPanel(0, 0, wid, hei, zoomScale) {

			@Override
			public void initialize() {
				super.initialize();
				for(int i = x - maxSize; i < x + maxSize; i++) {
					for(int j = y - maxSize; j < y + maxSize; j++) {
						double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
						if(dist >= maxSize) {
							setPixelColor(i, j, new Color(0, 0, 0, 0));
						}
						else {
							setPixelColor(i, j, Color.white);
						}
					}
				}
			}
			
			int counter = 0;
			@Override
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				double prop = Math.sin((double)(counter) / maxPeriod * (2 * Math.PI));
				int distMax = (int)Math.abs((maxSize * prop));
				int distMin = (int)Math.abs((minSize * prop));
				prop = Math.abs(prop);
				for(int i = x - distMax; i < x + distMax; i++) {
					for(int j = y - distMax; j < y + distMax; j++) {
						double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
						if(dist <= distMax && dist >= distMin) {
							Color c = getPixelColor(i, j);
							int r = c.getRed() + (int)(rChange * prop);
							int g = c.getGreen() + (int)(gChange * prop);
							int b = c.getBlue() + (int)(bChange * prop);
							setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
						}
					}
				}
				counter = (counter + 1) % maxPeriod;
			}
		
			boolean dragged = false;
			int lastX;
			int lastY;
			
			@Override
			public void clickPressEvent(int code, int x2, int y2) {
				lastX = x2;
				lastY = y2;
				dragged = true;
			}
			
			@Override
			public void clickReleaseEvent(int code, int x2, int y2) {
				dragged = false;
			}
			
			@Override
			public void dragEvent(int code, int x2, int y2) {
				if(dragged) {
					Point p = getParentFrame().getFrame().getLocationOnScreen();
					getParentFrame().getFrame().setLocation((int)(p.getX() + (x2 - lastX)), (int)(p.getY() + (y2 - lastY)));
				}
			}
			
		};
		WindowFrame fra = new WindowFrame(wid, hei);
		//fra.getFrame().
		fra.getFrame().setVisible(false);
		fra.getFrame().dispose();
		fra.getFrame().setUndecorated(true);
		fra.getFrame().setShape(new Ellipse2D.Double(0, 0, wid, hei));
		fra.getFrame().setSize(wid, hei);
		//fra.getFrame().setOpacity(.25f);
		fra.getFrame().setVisible(true);
		
		fra.setName("Ada's Algorithm Art");
		fra.addPanel("ca", ca);
	}
	*/
}
