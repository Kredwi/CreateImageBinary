package ru.kredwi.binarytoimg;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	private String path = "";

	public Window() {
		super("Binary to IMG");
		int height = Integer.parseInt((String)Main.getProperties().getOrDefault("screen_height", "500"));
		int width = Integer.parseInt((String)Main.getProperties().getOrDefault("screen_width", "400"));
		super.setBounds(0, 0, height,width);
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		super.setLayout(new BorderLayout());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JTextArea area = new JTextArea();
		
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setAutoscrolls(true);
		area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		
		JScrollPane scrollPane = new JScrollPane(area);
		
		JPanel controlPanel = new JPanel();
		
		JButton save = new JButton();
		JButton selectPath = new JButton();
		
		Field[] charsetsFields = StandardCharsets.class.getDeclaredFields();
		
		JComboBox<String> comboList = new JComboBox<>(Arrays.stream(charsetsFields)
				.map(s -> s.getName())
				.toArray(String[]::new));
		
		comboList.setSelectedIndex(-1);
		
		JLabel labelH = new JLabel("Height: ");
		JTextField h = new JTextField();
		h.setText("128");
		JLabel labelW = new JLabel("Width: ");
		JTextField w = new JTextField();
		w.setText("128");
		JLabel labelDotSize = new JLabel("Dot Size: ");
		JTextField dotSize = new JTextField();
		dotSize.setText("5");
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		
		save.setText("Create!");
		selectPath.setText("Select Path!");
		selectPath.setToolTipText("None(");
		
		save.addActionListener(s -> {
			
			if (area.getText().length() < 1) {
				JOptionPane.showMessageDialog(null, "Please, write your text in text area", "IllegalArgumentException", JOptionPane.ERROR_MESSAGE);
				throw new IllegalArgumentException("Please, write your text in text area");
			}
			
			
			int outputImageH = Integer.parseInt(h.getText());
			int outputImageW = Integer.parseInt(w.getText());
			int outputDotSize = Integer.parseInt(dotSize.getText());
			try {
				if (outputImageH > 1_000_000 | outputImageW > 1_000_000) {
					JOptionPane.showMessageDialog(null, "Output image size is out of bounds","IndexOutOfBoundsException", JOptionPane.ERROR_MESSAGE);
					throw new IndexOutOfBoundsException("Output image size is out of bounds");
				}
				Main.setHeight(outputImageH);
				Main.setWidth(outputImageW);
				Main.setDotSize(outputDotSize);
				Main.main(new String[] {area.getText(), path});	
			} catch (IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),"IndexOutOfBoundsException", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		});
		
		selectPath.addActionListener(s -> {
			int fileChooserStatus = fileChooser.showOpenDialog(null);
			if (fileChooserStatus == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				this.path = selectedFile.getPath();
				selectPath.setToolTipText(selectedFile.getPath());
				this.revalidate();
			}
		});
		
		comboList.addActionListener(s -> {
			try {
				String e = (String) comboList.getSelectedItem();
				Field field = StandardCharsets.class.getField(e);
				
				Main.setCharset((Charset) field.get(null));
				
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		// buttons
		controlPanel.add(save);
		controlPanel.add(selectPath);
		
		controlPanel.add(comboList);
		
		// settings
		controlPanel.add(labelH);
		controlPanel.add(h);
		controlPanel.add(labelW);
		controlPanel.add(w);
		controlPanel.add(labelDotSize);
		controlPanel.add(dotSize);
		
		
		super.add(scrollPane, BorderLayout.CENTER);
		super.add(controlPanel, BorderLayout.SOUTH);
	}
}
