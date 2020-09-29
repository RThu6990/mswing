package com.mswing.comp.tabbedpane;

import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

/**
 * 
 * @author RThu
 *
 */
public class TabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	
	private int closingTabIndex = -1;
	
	private Component closingTabComponent = null;

	public TabbedPane() {
		super();
		setFocusable(false);
	}

	/* Override Addtab in order to add the close Button everytime */
	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		int count = this.getTabCount() - 1;
		setTabComponentAt(count, new TabTitle(component, title, icon));
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		addTab(title, icon, component, null);
	}

	@Override
	public void addTab(String title, Component component) {
		addTab(title, null, component);
	}
	
	public void addTab(String title, Icon icon, Component component, String tip, boolean withCloseButton) {
		if(withCloseButton) {
			addTab(title, icon, component, tip);
		}
		else {
			super.addTab(title, icon, component, tip);
		}
	}
	
	public void addTab(String title, Icon icon, Component component, boolean withCloseButton) {
		if(withCloseButton) {
			addTab(title, icon, component);
		}
		else {
			super.addTab(title, icon, component);
		}
	}
	
	public void addTab(String title, Component component, boolean withCloseButton) {
		if(withCloseButton) {
			addTab(title, component);
		}
		else {
			super.addTab(title, component);
		}
	}

	public int getIndexOfTitle(String title) {
		if(getTabCount() > 0) {
			int index = 0;
			do {
				if (title.equals(getTitleAt(index))) {
					return index;
				}
			} while (++index < getTabCount());
		}
		
		return -1;
	}
	
	@Override
	public void removeTabAt(int index) {
		fireEvent();
		super.removeTabAt(index);
	}

	public void addTabListener(TabListener listener) {
		listenerList.add(TabListener.class, listener);
	}

	public void removeTabListener(TabListener listener) {
		listenerList.remove(TabListener.class, listener);
	}

	private void fireEvent() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2)
			if (listeners[i] == TabListener.class)
				((TabListener) listeners[i + 1]).tabClosing(new TabClosingEvent(this, closingTabIndex, closingTabComponent));
	}

	public class TabTitle extends JPanel {
		private static final long serialVersionUID = 1L;

		public TabTitle(final Component tab, String title, Icon icon) {
			setOpaque(false);
			setLayout(new BorderLayout(5, 0));

			JLabel jLabel = new JLabel(title);
			jLabel.setIcon(icon);

			add(jLabel, BorderLayout.CENTER);
			add(new CloseButton(tab), BorderLayout.EAST);
		}
	}

	public class CloseButton extends JPanel {
		private static final long serialVersionUID = 1L;

		private float backgroundAlpha = 0f;
		
		private Color backgroundColor = Color.GRAY;

		public CloseButton(Component tabComponent) {
			setOpaque(false);
			
			setPreferredSize(new Dimension(20, 20));

			addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					if (contains(e.getPoint())) {
						backgroundAlpha = 1f;
						backgroundColor = Color.GRAY;
						repaint();
					} else {
						backgroundAlpha = 0f;
						repaint();
					}

					((JTabbedPane) getParent().getParent().getParent()).repaint();
					((JTabbedPane) getParent().getParent().getParent()).revalidate();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					backgroundAlpha = 1f;
					backgroundColor = Color.GRAY.darker();
					repaint();

					((JTabbedPane) getParent().getParent().getParent()).repaint();
					((JTabbedPane) getParent().getParent().getParent()).revalidate();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					backgroundAlpha = 0;
					backgroundColor = Color.GRAY;
					repaint();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					backgroundAlpha = 1f;
					backgroundColor = Color.GRAY;
					repaint();
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getSource() instanceof CloseButton) {
						CloseButton clickedButton = (CloseButton) e.getSource();
						JTabbedPane tabbedPane = (JTabbedPane) clickedButton.getParent().getParent().getParent();
						closingTabComponent = tabComponent;
						closingTabIndex = tabbedPane.indexOfComponent(tabComponent);
						removeTabAt(closingTabIndex);
					}
				}
			});
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Composite composite = g2d.getComposite();
			AffineTransform transform = g2d.getTransform();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, backgroundAlpha));
			g2d.setColor(backgroundColor);
			g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
			g2d.setColor(Color.WHITE);
			g2d.setStroke(new BasicStroke(2f));
			g2d.draw(new Line2D.Double(7, 7, getWidth() - 8, getHeight() - 8));
			g2d.draw(new Line2D.Double(7, getHeight() - 8, getWidth() - 8, 7));
			g2d.setComposite(composite);
			g2d.setTransform(transform);
		}

	}
}
