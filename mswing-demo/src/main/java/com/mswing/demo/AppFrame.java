package com.mswing.demo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;
import com.mswing.comp.tabbedpane.TabbedPane;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author RThu
 *
 */

public class AppFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JLabel lbTitle;
	
	private TabbedPane tabbedPane;
	
	static {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public AppFrame() {
		prepareLayout();
		setupComponentActions();
	}
	
	private void prepareLayout() {
		lbTitle = new JLabel("MSwing Components");
		lbTitle.setFont(lbTitle.getFont().deriveFont(20f).deriveFont(Font.BOLD));
		lbTitle.setOpaque(true);
		lbTitle.setBackground(Color.WHITE);
		
		tabbedPane = new TabbedPane();
		tabbedPane.addTab("Lobby", new LobbyPanel(), false);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new MigLayout());
		setSize(800, 550);
		setScreenCenteredLocation();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		
		getContentPane().setBackground(Color.WHITE);
		
		add(lbTitle, "growx, pushx, wrap 20");
		add(tabbedPane, "grow, push");
	}
	
	private void setupComponentActions() {
		// Do nothing
	}
	
	private void setScreenCenteredLocation() {
		Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int x = (rect.width - getWidth()) / 2;
		int y = (rect.height - getHeight()) / 2;
		setLocation(x, y);
	}
 
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			AppFrame frame = new AppFrame();
			frame.setVisible(true);
		});
	}
	
	private class LobbyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private JButton btTagFieldDemo;
		
		private JLabel lbLogo;
		
		public LobbyPanel() {
			lbLogo = new JLabel(new ImageIcon(getToolkit().getImage(getClass().getResource("/img/mswing_logo.png"))));
			lbLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lbLogo.setToolTipText("Go to GitHub");
			lbLogo.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					try {
						Desktop.getDesktop().browse(new URI("https://github.com/RThu6990/mswing"));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			});
			
			btTagFieldDemo = new JButton("TagField Demo");
			
			btTagFieldDemo.addActionListener((e) -> {
				int index = tabbedPane.getIndexOfTitle("TagField Demo");
				if(index > -1) {
					tabbedPane.setSelectedIndex(index);
					return;
				}
				
				tabbedPane.addTab("TagField Demo", new TagFieldSamplePane());
				tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
			});
			
			JPanel centerPane = new JPanel(new MigLayout("al center center"));
			centerPane.add(lbLogo);
			
			JPanel southPane = new JPanel(new MigLayout("al center center"));
			southPane.add(btTagFieldDemo);
			
			setLayout(new MigLayout());
			add(centerPane, "dock center");
			add(southPane, "dock south");
		}
		
	}

}
