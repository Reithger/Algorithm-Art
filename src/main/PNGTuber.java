package main;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import visual.composite.HandlePanel;
import visual.frame.WindowFrame;

/**
 * 
 * For simplicity as we are going to export this as a runnable .jar
 * 
 * Have a specifically named folder next to the .jar from which we pull the
 * image inside, make the appropriate alternate versions (faded, brighter, etc.),
 * then display the appropriate image based on current volume level.
 * 
 * Eventually make an interface that lets the user navigate to a picture of their
 * choice and produce a .jar automatically for that image, but for now get the
 * basics working.
 * 
 * Also eventually embed the audio loudness inside the .jar so we don't need the
 * python file nearby.
 * 
 * 
 */

public class PNGTuber {
	
	private static final String IMAGE_PATH_DEFAULT = "./main/assets/skull.png";
	private static final int MODE_QUIET = 0;
	private static final int MODE_SPEAKING = 1;
	private static final int MODE_LOUD = 2;
	private static final int WIDTH = 250;
	private static final int HEIGHT = 250;
	private static final int QUIET_SHAKE = 1;
	private static final int SPEAKING_SHAKE = 10;
	private static final int LOUD_SHAKE = 40;
	
	private static HandlePanel hp;
	private static HashMap<Integer, Image> alt_images;
	private static int currentMode;
	private static int currentImage;
	private static int currentShake;
	private static String imageToUse;
	private static int counter;

	public static void main(String[] args) {
		File f = new File("./pngassets/");
		f.mkdirs();
		verifyPythonFileNear();
		localImageOverride();
		callAudioCheck();
		setupWindow();
		setupAltImages();
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			interpretAudioLevel(getCurrentAudio());
			updateDisplayedImage();
			System.out.println(getCurrentAudio());
			if(counter % 25 == 0 && localImageOverride()) {
				setupAltImages();
			}
			counter += 1;
		}
	}
	
	private static boolean localImageOverride() {
		File dir = new File("./pngassets/");
		boolean noFind = true;
		for(String s : dir.list()) {
			if(s.contains(".png") || s.contains("jpg")) {
				noFind = false;
				String newPath = "./pngassets/" + s;
				boolean out = newPath != imageToUse;
				imageToUse = newPath;
				return out;
			}
		}
		imageToUse = IMAGE_PATH_DEFAULT;
		return noFind;
	}
	
	private static void interpretAudioLevel(double in) {
		if(in < 10) {
			currentMode = MODE_QUIET;
			currentShake = QUIET_SHAKE;
		}
		else if (in < 300){
			currentMode = MODE_SPEAKING;
			currentShake = SPEAKING_SHAKE;
		}
		else {
			currentMode = MODE_LOUD;
			currentShake = LOUD_SHAKE;
		}
	}
	
	private static void updateDisplayedImage() {
		if(currentImage == currentMode) {
			hp.removeAllElements();
			Random rand = new Random();
			hp.addImage("image", 5, "default", rand.nextInt(currentShake) - currentShake / 2, rand.nextInt(currentShake) - currentShake / 2, WIDTH, HEIGHT, false, alt_images.get(currentMode), true);
			return;
		}
		hp.removeAllElements();
		hp.addImage("image", 5, "default", 0, 0, WIDTH, HEIGHT, false, alt_images.get(currentMode), true);
		currentImage = currentMode;
	}
	
	private static void setupWindow() {
		WindowFrame wf = new WindowFrame(WIDTH, HEIGHT);
		wf.setName("PNGTuber Ada Made");
		hp = new HandlePanel(0, 0, WIDTH, HEIGHT);
		wf.addPanel("basic", hp);
		wf.showWindow();
	}
	
	private static void setupAltImages() {
		alt_images = new HashMap<Integer, Image>();
		Image baseImage = hp.retrieveImage(imageToUse);
		setupSpeakingImage(baseImage);
		setupQuietImage(baseImage);
		setupLoudImage(baseImage);
		currentMode = MODE_QUIET;
		currentShake = QUIET_SHAKE;
		updateDisplayedImage();
	}
	
	private static void setupSpeakingImage(Image baseImage) {
		BufferedImage copy = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(baseImage, 0, 0, null);
		gr2.dispose();
		alt_images.put(MODE_SPEAKING, copy);
	}
	
	private static void setupQuietImage(Image baseImage) {
		BufferedImage copy = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(baseImage, 0, 0, new Color(255, 255, 255, 0), null);
		gr2.dispose();
		
		int width = copy.getWidth();
		int height = copy.getHeight();
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Color c = new Color(copy.getRGB(i, j), true);
				int transp = c.getAlpha();
				Color use = new Color(c.getRed() / 2, c.getGreen() / 2, c.getBlue() / 2, transp);
				copy.setRGB(i, j, use.getRGB());
			}
		}
		alt_images.put(MODE_QUIET, copy);
	}
	
	private static void setupLoudImage(Image baseImage) {
		BufferedImage copy = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr2 = copy.createGraphics();
		gr2.drawImage(baseImage, 0, 0, new Color(255, 255, 255, 0), null);
		gr2.dispose();
		
		int width = copy.getWidth();
		int height = copy.getHeight();
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Color c = new Color(copy.getRGB(i, j), true);
				int transp = c.getAlpha();
				Color use = new Color((510 + c.getRed()) / 3, c.getGreen() / 2, c.getBlue() / 2, transp);
				copy.setRGB(i, j, use.getRGB());
			}
		}
		alt_images.put(MODE_LOUD, copy);
	}
	
	private static void verifyPythonFileNear() {
		File f = new File("./pngassets/read_audio.py");
		System.out.println(f.length());
		if(!f.exists()) {
			try {
				ArrayList<String> contents = getTemplatePythonContents();
				f.createNewFile();
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				for(String s : contents) {
					raf.writeBytes(s + "\n");
				}
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static ArrayList<String> getTemplatePythonContents() {
		InputStream is = null;
		Scanner sc;
		try {
			is =PNGTuber.class.getResourceAsStream("assets/read_audio.txt");
			sc = new Scanner(is);
		}
		catch(Exception e) {
			try {
				File f;
				f = new File("/assets/read_audio.txt");
				System.out.println(f.getAbsolutePath());
				sc = new Scanner(f);
			}
			catch(Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		ArrayList<String> out = new ArrayList<String>();
		while(sc.hasNextLine()) {
			out.add(sc.nextLine());
		}
		sc.close();
		return out;
	}
	
	private static void callAudioCheck() {
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				try {
					Runtime.getRuntime().exec("python pngassets/read_audio.py");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Timer time = new Timer();
		time.schedule(tt, 15, 250);
	}
	
	private static double getCurrentAudio() {
		File f = new File("pngassets/audio_level.txt");
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner sc = null;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sc.hasNextLine()) {
			String line = sc.nextLine();
			return Double.parseDouble(line);
		}
		return 0;
	}
	
}
