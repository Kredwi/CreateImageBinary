package ru.kredwi.binarytoimg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private static int WIDTH = 128;
	private static int HEIGHT = 128;
	
	private static int DOT_SIZE = 5;
	
	private static Charset CHARSET = StandardCharsets.US_ASCII;
	
	private static final Properties properties = new Properties();
	
	/**
	 * @author Kredwi
	 * Loading configuration
	 * */
	static {
		try (InputStream propFile = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
			properties.load(propFile);
		} catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	private static Window WINDOW = new Window();
	
	public static void main(String[] args) throws IndexOutOfBoundsException {		
		WINDOW.setVisible(true);
		
		if (args.length < 2) {
			return;
		}
		String fileName = String.format(properties.getProperty("file-name"), String.valueOf(new Date().getTime() / 1000));
		
		File file = args.length < 2 || args[1].isEmpty() ? new File(fileName) : new File(args[1], fileName);
		
		String binary = toBinary(args[0]);
		
		BufferedImage result = getResultImage(binary);
		try {
			ImageIO.write(result, properties.getProperty("file-type"), file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static BufferedImage imageInitilization() {
		BufferedImage image = new BufferedImage(HEIGHT, WIDTH, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		
		graphics.dispose();
		
		return image;
	}
	
	private static String toBinary(String text) {
		StringBuilder builder = new StringBuilder();
		byte[] textBytes = text.getBytes(CHARSET);
		
		for (byte b : textBytes) {
			
			String binaryString = Integer.toBinaryString(b & 0xFF);
			
			String paddedBinaryString = String.format("%8s", binaryString)
					.replace(' ', '0');
			builder.append(paddedBinaryString);
		}

		return builder.toString();
	}
	
	private static BufferedImage getResultImage(String binaryText) throws IndexOutOfBoundsException {
		BufferedImage image = imageInitilization();
		char[] chars = binaryText.toCharArray();
		
		int imageHeight = image.getHeight();
		int imageWidth = image.getWidth();
		
		int getChar = 0;
		
		for (int row = 0; row < imageHeight; row += DOT_SIZE) {
			for (int col = 0; col < imageWidth; col += DOT_SIZE) {
				Color color = Color.WHITE;
				if (getChar < chars.length) {
					color = chars[getChar] == '0' ? Color.WHITE : Color.BLACK;
					getChar++;
				}
				for (int i = 0; i < DOT_SIZE && row + i < imageHeight; i++) {
					for (int j = 0; j < DOT_SIZE && col + j < imageWidth; j++) {
						image.setRGB(col + j, row + i, color.getRGB());
					}
				}
			}
		}
		
		if (getChar < chars.length) {
		JOptionPane.showMessageDialog(null, "Dots out of bounds o_0", "IndexOutOfBoundsException", JOptionPane.ERROR_MESSAGE);
		throw new IndexOutOfBoundsException("Dots out of bounds o_0");
	}
		
		return image;
	}
	
	public static Charset getCharset() {
		return CHARSET;
	}
	public static Properties getProperties() {
		return properties;
	}
	public static void setWidth(int width) {
		WIDTH = width;
	}
	public static void setHeight(int height) {
		HEIGHT = height;
	}
	public static void setDotSize(int size) {
		DOT_SIZE = size;
	}
	public static void setCharset(Charset charset) {
		CHARSET = charset;
	}
}
