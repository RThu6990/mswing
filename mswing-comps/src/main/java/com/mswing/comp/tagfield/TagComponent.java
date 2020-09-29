package com.mswing.comp.tagfield;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 
 * @author RThu
 *
 */

public class TagComponent extends JPanel {
	private static final long serialVersionUID = 1L;

	private JLabel lbText;
	
	private CloseButton closeButton;

	public TagComponent(String text, TagField parent) {
		lbText = new JLabel(text, SwingConstants.CENTER);
		
		closeButton = new CloseButton();
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				parent.removeTag(TagComponent.this);
			}
		});

		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(lbText, BorderLayout.CENTER);
		add(closeButton, BorderLayout.EAST);
	}
	
	public String getText() {
		return lbText.getText();
	}
	
	public void setRemovable(boolean removable) {
		closeButton.setVisible(removable);
	}
	
	public void refresh() {
		repaint();
		revalidate();
	}
	
	public class CloseButton extends JPanel {
		private static final long serialVersionUID = 1L;

		private float backgroundAlpha = 1f;
		
		private Color backgroundColor = Color.GRAY;

		public CloseButton() {
			setOpaque(false);
			
			setPreferredSize(new Dimension(18, 18));

			addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					if (contains(e.getPoint())) {
						backgroundColor = Color.RED;
						repaint();
					}
					else {
						backgroundColor = Color.GRAY;
						repaint();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					backgroundColor = Color.RED.darker();
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					backgroundColor = Color.GRAY;
					repaint();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					backgroundColor = Color.RED;
					repaint();
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// Do nothing for now
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
			g2d.setStroke(new BasicStroke(1.5f));
			g2d.draw(new Line2D.Double(6, 6, getWidth() - 7, getHeight() - 7));
			g2d.draw(new Line2D.Double(6, getHeight() - 7, getWidth() - 7, 6));
			g2d.setComposite(composite);
			g2d.setTransform(transform);
		}

	}

}
