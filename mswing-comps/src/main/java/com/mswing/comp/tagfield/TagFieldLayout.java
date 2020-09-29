package com.mswing.comp.tagfield;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author timabell & RThu
 * 
 * <br>Originally this layout is called a WrapLayout created by timabell.
 * Original : https://github.com/timabell/WrapLayout
 * 
 * This version is modified to use in TagField.
 * 
 * FlowLayout subclass that fully supports wrapping of components.
 */
public class TagFieldLayout extends FlowLayout {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>WrapLayout</code> with a left alignment and a default
	 * 5-unit horizontal and vertical gap.
	 */
	public TagFieldLayout() {
		super();
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified alignment and a
	 * default 5-unit horizontal and vertical gap. The value of the alignment
	 * argument must be one of <code>WrapLayout</code>, <code>WrapLayout</code>, or
	 * <code>WrapLayout</code>.
	 * 
	 * @param align the alignment value
	 */
	public TagFieldLayout(int align) {
		super(align);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment and the
	 * indicated horizontal and vertical gaps.
	 * <p>
	 * The value of the alignment argument must be one of <code>WrapLayout</code>,
	 * <code>WrapLayout</code>, or <code>WrapLayout</code>.
	 * 
	 * @param align the alignment value
	 * @param hgap  the horizontal gap between components
	 * @param vgap  the vertical gap between components
	 */
	public TagFieldLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	/**
	 * Returns the preferred dimensions for this layout given the <i>visible</i>
	 * components in the specified target container.
	 * 
	 * @param target the component which needs to be laid out
	 * @return the preferred dimensions to lay out the subcomponents of the
	 *         specified container
	 */
	@Override
	public Dimension preferredLayoutSize(Container target) {
		return layoutSize(target, true);
	}

	/**
	 * Returns the minimum dimensions needed to layout the <i>visible</i> components
	 * contained in the specified target container.
	 * 
	 * @param target the component which needs to be laid out
	 * @return the minimum dimensions to lay out the subcomponents of the specified
	 *         container
	 */
	@Override
	public Dimension minimumLayoutSize(Container target) {
		Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1);
		return minimum;
	}
	
	@Override
	public void layoutContainer(Container target) {
		recalculateTextComponentSize(target);
		super.layoutContainer(target);
	}

	/**
	 * Returns the minimum or preferred dimension needed to layout the target
	 * container.
	 *
	 * @param target    target to get layout size for
	 * @param preferred should preferred size be calculated
	 * @return the dimension to layout the target container
	 */
	private Dimension layoutSize(Container target, boolean preferred) {
		synchronized (target.getTreeLock()) {
			// Each row must fit with the width allocated to the containter.
			// When the container width = 0, the preferred width of the container
			// has not yet been calculated so lets ask for the maximum.

			int targetWidth = target.getSize().width;
			Container container = target;

			while (container.getSize().width == 0 && container.getParent() != null) {
				container = container.getParent();
			}

			targetWidth = container.getSize().width;

			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;

			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;

			// Fit components into the allowed width

			int nmembers = target.getComponentCount();
			
			Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;
			
			target.doLayout();
			
			if(nmembers > 1) {
				for (int i = 0; i < nmembers; i++) {
					Component m = target.getComponent(i);

					if (m.isVisible()) { 
						Dimension d = m.getPreferredSize();
						
						if (rowWidth + d.width > maxWidth) {
							if(dim.height == 0) {
								dim.height = rowHeight;
							}
							
							addRow(dim, d.width, d.height);
							rowWidth = 0;
							rowHeight = 0;
						}

						// Add a horizontal gap for all components after the first

						if (rowWidth != 0) {
							rowWidth += hgap;
						}
						
						rowWidth += d.width;
						rowHeight = Math.max(rowHeight, d.height);
					}
				}
				
				if(dim.height <= 0) {
					dim.height = rowHeight;
				}
			}
			else {
				rowWidth = target.getComponent(0).getMinimumSize().width;
				rowHeight = target.getComponent(0).getMinimumSize().height;
				addRow(dim, rowWidth, rowHeight);
			}
			
			dim.width += horizontalInsetsAndGap;
			dim.height += insets.top + insets.bottom + vgap * 2;

			// When using a scroll pane or the DecoratedLookAndFeel we need to
			// make sure the preferred size is less than the size of the
			// target containter so shrinking the container size works
			// correctly. Removing the horizontal gap is an easy way to do this.

			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

			if (scrollPane != null && target.isValid()) {
				dim.width -= (hgap + 1);
			}

			return dim;
		}
	}

	/*
	 * A new row has been completed. Use the dimensions of this row to update the
	 * preferred size for the container.
	 *
	 * @param dim update the width and height when appropriate
	 * 
	 * @param rowWidth the width of the row to add
	 * 
	 * @param rowHeight the height of the row to add
	 */
	private void addRow(Dimension dim, int rowWidth, int rowHeight) {
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0) {
			dim.height += getVgap();
		}
		
		dim.height += rowHeight;
	}
	
	private void recalculateTextComponentSize(Container target) {
		int width = 0;
		for(int i = 0; i < target.getComponents().length; i++) {
			if(!(target.getComponent(i) instanceof JTextField)) {
				width += target.getComponent(i).getPreferredSize().getWidth() + getHgap();
				
				width = width > target.getWidth() ? 0 : width;
				
				if(width == 0) {
					i--;
				}
			}
		}
		
		for(Component c : target.getComponents()) {
			if(c instanceof JTextField) {
				Insets insets = target.getInsets();
				
				if(target.getWidth() - width < 50) {
					c.setPreferredSize(new Dimension(target.getWidth() - insets.left - insets.right - (getHgap() * 2), c.getPreferredSize().height));
				}
				else {
					c.setPreferredSize(new Dimension(target.getWidth() - insets.left - insets.right - width - (getHgap() * 2), c.getPreferredSize().height));
				}
				
				break;
			}
		}
	}
}
