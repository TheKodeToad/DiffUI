package me.thekodetoad.diffui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FileUtils;

import io.sigpipe.jbsdiff.Diff;
import io.sigpipe.jbsdiff.InvalidHeaderException;
import io.sigpipe.jbsdiff.Patch;
import me.thekodetoad.diffui.utils.Utils;

public class DiffUI extends JFrame {
	
	private static final long serialVersionUID = -2439042086402836543L;
	
	public static File unmodifiedFile = null;
	public static File modifiedFile = null;
	
	public static File fileToPatch = null;
	public static File patchFile = null;
	
	public DiffUI() {
		super("Apply/Generate Patches");
		DiffUI ui = this;
		Dimension size = new Dimension(600, 220);
		
		setResizable(false);
		setMinimumSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
		
		JTabbedPane tabPanel = new JTabbedPane();
		
		JPanel diffPanel = new JPanel();
		diffPanel.setLayout(null);
		
		JLabel unmodifiedFileLabel = new JLabel("Unmodified File: ");
		unmodifiedFileLabel.setBounds(10, 20, graphics.getFontMetrics(unmodifiedFileLabel.getFont()).stringWidth(unmodifiedFileLabel.getText()), 30);
		diffPanel.add(unmodifiedFileLabel);
		
		JTextField unmodifiedFilePathField = new JTextField("unmodified_file.bin");
		unmodifiedFilePathField.setBounds(unmodifiedFileLabel.getWidth() + 10, unmodifiedFileLabel.getY(), 250, unmodifiedFileLabel.getHeight());
		unmodifiedFilePathField.setDisabledTextColor(Color.BLACK);
		unmodifiedFilePathField.setEnabled(false);
		diffPanel.add(unmodifiedFilePathField);
		
		JButton unmodifiedFilePathBrowseButton = new JButton("Choose File");
		unmodifiedFilePathBrowseButton.setBounds(unmodifiedFilePathField.getX() + unmodifiedFilePathField.getWidth() + 10, unmodifiedFilePathField.getY(), 150, unmodifiedFilePathField.getHeight());
		unmodifiedFilePathBrowseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(Utils.getHomeDirectory());
				fileChooser.setDialogTitle("Choose Unmodified File");
				fileChooser.showOpenDialog(ui);
				if(fileChooser.getSelectedFile() != null) {
					unmodifiedFilePathField.setText(fileChooser.getSelectedFile().getName());
					unmodifiedFile = fileChooser.getSelectedFile();
				}
			}
			
		});
		diffPanel.add(unmodifiedFilePathBrowseButton);
		
		JLabel modifiedFileLabel = new JLabel("Modified File: ");
		modifiedFileLabel.setBounds(28, unmodifiedFileLabel.getY() + unmodifiedFileLabel.getHeight() + 10, graphics.getFontMetrics(modifiedFileLabel.getFont()).stringWidth(modifiedFileLabel.getText()), 30);
		diffPanel.add(modifiedFileLabel);
		
		JTextField modifiedFilePathField = new JTextField("modified_file.bin");
		modifiedFilePathField.setBounds(unmodifiedFileLabel.getWidth() + 10, modifiedFileLabel.getY(), 250, modifiedFileLabel.getHeight());
		modifiedFilePathField.setDisabledTextColor(Color.BLACK);
		modifiedFilePathField.setEnabled(false);
		diffPanel.add(modifiedFilePathField);
		
		JButton modifiedFilePathBrowseButton = new JButton("Choose File");
		modifiedFilePathBrowseButton.setBounds(modifiedFilePathField.getX() + modifiedFilePathField.getWidth() + 10, modifiedFilePathField.getY(), 150, modifiedFilePathField.getHeight());
		modifiedFilePathBrowseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(Utils.getHomeDirectory());
				fileChooser.setDialogTitle("Choose Modified File");
				fileChooser.showOpenDialog(ui);
				if(fileChooser.getSelectedFile() != null) {
					modifiedFilePathField.setText(fileChooser.getSelectedFile().getName());
					modifiedFile = fileChooser.getSelectedFile();
				}
			}
			
		});
		diffPanel.add(modifiedFilePathBrowseButton);
		
		JButton generatePatchButton = new JButton("Generate Patch");
		generatePatchButton.setBounds(10, modifiedFilePathField.getY() + modifiedFilePathField.getHeight() + 20, 150, modifiedFilePathField.getHeight());
		generatePatchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(unmodifiedFile == null) {
					JOptionPane.showMessageDialog(ui, "Please Select Unmodified File", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(modifiedFile == null) {
					JOptionPane.showMessageDialog(ui, "Please Select Modified File", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				JFileChooser fileSaver = new JFileChooser(Utils.getHomeDirectory());
				fileSaver.setDialogTitle("Save Patch");
				fileSaver.showSaveDialog(ui);
				if(fileSaver.getSelectedFile() != null) {
					File diffOutput = fileSaver.getSelectedFile();
					try {
						Diff.diff(FileUtils.readFileToByteArray(unmodifiedFile), FileUtils.readFileToByteArray(modifiedFile), new FileOutputStream(diffOutput));
					}
					catch (CompressorException | InvalidHeaderException | IOException e1) {
						JOptionPane.showMessageDialog(ui, e1.getClass().getSimpleName() + ": " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		diffPanel.add(generatePatchButton);
		
		JPanel patchPanel = new JPanel();
		patchPanel.setLayout(null);
		
		JLabel fileToPatchLabel = new JLabel("File to Patch: ");
		fileToPatchLabel.setBounds(10, 20, graphics.getFontMetrics(fileToPatchLabel.getFont()).stringWidth(fileToPatchLabel.getText()), 30);
		patchPanel.add(fileToPatchLabel);
		
		JTextField fileToPatchPathField = new JTextField("file_to_patch.bin");
		fileToPatchPathField.setBounds(fileToPatchLabel.getWidth() + 10, fileToPatchLabel.getY(), 250, fileToPatchLabel.getHeight());
		fileToPatchPathField.setDisabledTextColor(Color.BLACK);
		fileToPatchPathField.setEnabled(false);
		patchPanel.add(fileToPatchPathField);
		
		JButton fileToPatchPathBrowseButton = new JButton("Choose File");
		fileToPatchPathBrowseButton.setBounds(fileToPatchPathField.getX() + fileToPatchPathField.getWidth() + 10, fileToPatchPathField.getY(), 150, fileToPatchPathField.getHeight());
		fileToPatchPathBrowseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(Utils.getHomeDirectory());
				fileChooser.setDialogTitle("Choose File to Patch");
				fileChooser.showOpenDialog(ui);
				if(fileChooser.getSelectedFile() != null) {
					fileToPatchPathField.setText(fileChooser.getSelectedFile().getName());
					fileToPatch = fileChooser.getSelectedFile();
				}
			}
			
		});
		patchPanel.add(fileToPatchPathBrowseButton);
		
		JLabel patchFileLabel = new JLabel("Patch File: ");
		patchFileLabel.setBounds(28, fileToPatchLabel.getY() + fileToPatchLabel.getHeight() + 10, graphics.getFontMetrics(patchFileLabel.getFont()).stringWidth(patchFileLabel.getText()), 30);
		patchPanel.add(patchFileLabel);
		
		JTextField patchFilePathField = new JTextField("modifications.patch");
		patchFilePathField.setBounds(fileToPatchLabel.getWidth() + 10, patchFileLabel.getY(), 250, patchFileLabel.getHeight());
		patchFilePathField.setDisabledTextColor(Color.BLACK);
		patchFilePathField.setEnabled(false);
		patchPanel.add(patchFilePathField);
		
		JButton patchFilePathBrowseButton = new JButton("Choose File");
		patchFilePathBrowseButton.setBounds(patchFilePathField.getX() + patchFilePathField.getWidth() + 10, patchFilePathField.getY(), 150, patchFilePathField.getHeight());
		patchFilePathBrowseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(Utils.getHomeDirectory());
				fileChooser.setDialogTitle("Choose Patch File");
				fileChooser.showOpenDialog(ui);
				if(fileChooser.getSelectedFile() != null) {
					patchFilePathField.setText(fileChooser.getSelectedFile().getName());
					patchFile = fileChooser.getSelectedFile();
				}
			}
			
		});
		patchPanel.add(patchFilePathBrowseButton);
		
		JButton applyPatchButton = new JButton("Apply Patch");
		applyPatchButton.setBounds(10, patchFilePathField.getY() + patchFilePathField.getHeight() + 20, 150, patchFilePathField.getHeight());
		applyPatchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileToPatch == null) {
					JOptionPane.showMessageDialog(ui, "Please Select File to Patch", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(patchFile == null) {
					JOptionPane.showMessageDialog(ui, "Please Select Patch File", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				JFileChooser fileSaver = new JFileChooser(Utils.getHomeDirectory());
				fileSaver.setDialogTitle("Save Patched File");
				fileSaver.showSaveDialog(ui);
				if(fileSaver.getSelectedFile() != null) {
					File patchOutput = fileSaver.getSelectedFile();
					try {
						Patch.patch(FileUtils.readFileToByteArray(fileToPatch), FileUtils.readFileToByteArray(patchFile), new FileOutputStream(patchOutput));
					}
					catch (CompressorException | InvalidHeaderException | IOException e1) {
						JOptionPane.showMessageDialog(ui, e1.getClass().getSimpleName() + ": " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		patchPanel.add(applyPatchButton);
		
		JPanel aboutPanel = new JPanel();
		aboutPanel.setLayout(null);
		
		JLabel aboutTitle = new JLabel("DiffUI 1.0");
		aboutTitle.setFont(aboutTitle.getFont().deriveFont(Font.BOLD, 24));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutTitle.setBounds(10, 10, aboutPanel.getWidth(), 24);
			}
			
		});
		aboutPanel.add(aboutTitle);
		
		JLabel aboutCredit = new JLabel("Created by TheKodeToad");
		aboutCredit.setFont(aboutTitle.getFont().deriveFont(Font.PLAIN, 20));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutCredit.setBounds(10, 40, aboutPanel.getWidth(), 20);
			}
			
		});
		aboutPanel.add(aboutCredit);
		
		JLabel aboutLibraries = new JLabel("Libraries:");
		aboutLibraries.setFont(aboutTitle.getFont().deriveFont(Font.BOLD, 24));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutLibraries.setBounds(10, 65, aboutPanel.getWidth(), 18);
			}
			
		});
		aboutPanel.add(aboutLibraries);
		
		JLabel aboutJbsdiff = new JLabel("jbsdiff 1.0");
		aboutJbsdiff.setFont(aboutTitle.getFont().deriveFont(Font.PLAIN, 18));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutJbsdiff.setBounds(10, 94, aboutPanel.getWidth(), 18);
			}
			
		});
		aboutPanel.add(aboutJbsdiff);
		
		JLabel aboutCommonsIO = new JLabel("commons-io 2.6");
		aboutCommonsIO.setFont(aboutTitle.getFont().deriveFont(Font.PLAIN, 18));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutCommonsIO.setBounds(10, 117, aboutPanel.getWidth(), 18);
			}
			
		});
		aboutPanel.add(aboutCommonsIO);
		
		JLabel aboutCommonsCompress = new JLabel("commons-compress 1.5");
		aboutCommonsCompress.setFont(aboutTitle.getFont().deriveFont(Font.PLAIN, 18));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutCommonsCompress.setBounds(10, 140, aboutPanel.getWidth(), 18);
			}
			
		});
		aboutPanel.add(aboutCommonsCompress);
		
		JLabel aboutXZ = new JLabel("xz 1.2");
		aboutXZ.setFont(aboutTitle.getFont().deriveFont(Font.PLAIN, 18));
		aboutPanel.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				aboutXZ.setBounds(10, 163, aboutPanel.getWidth(), 18);
			}
			
		});
		aboutPanel.add(aboutXZ);
		
		tabPanel.addTab("Apply", patchPanel);
		tabPanel.addTab("Generate", diffPanel);
		tabPanel.addTab("About", aboutPanel);
		
		tabPanel.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tabPanel.getSelectedIndex() == 2) {
					setMinimumSize(new Dimension(600, 260));
				}
				else {
					setMinimumSize(size);
					setSize(size);
				}
			}
			
		});
		
		add(tabPanel);
	}

	public void start() {
		setVisible(true);
	}
	
}
