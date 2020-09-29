package com.mswing.comp.tagfield;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * @author RThu
 *
 */

public class TagField extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private TagComponent addedTagComponent;
	private TagComponent removedTagComponent;
	
	private final JTextField txtInput = new JTextField();
	
	private TagFieldEventType tagFieldEventType;
	
	private int maxTags = 10;
	
	private JPopupMenu popupMenu;
	
	private String selectedTag;
	
	private List<String> expectedTags;
	private List<JMenuItem> popupMenuItems;
	
	private boolean editable = true;
	
	public TagField() {
		setBackground(Color.WHITE);
		setLayout(new TagFieldLayout(TagFieldLayout.LEFT, 3, 3));
		setBorder(UIManager.getBorder("TextField.border"));
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				requestFocus();
			}
		});
		
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				txtInput.requestFocus();
			}
		});
		
		popupMenu = new JPopupMenu("Expected Tags");
		
		txtInput.setBorder(null);
		txtInput.requestFocus();
		//txtInput.setSize(new Dimension(50, 30));
		//txtInput.setPreferredSize(new Dimension(50, 30));
		//txtInput.setMinimumSize(new Dimension(50, 30));
		
		txtInput.addActionListener((e) -> {
			if(txtInput.getText().isEmpty()) {
				return;
			}
			
			if(!popupMenu.isVisible()) {
				selectedTag = null;
				addTag(new TagComponent(txtInput.getText().trim(), TagField.this));
				clearText();
			}
			else {
				MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
				SwingUtilities.invokeLater(() -> {
					((JMenuItem) path[path.length - 1]).dispatchEvent(
							new MouseEvent((JMenuItem) path[path.length - 1], MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 0, 0, 1, false, MouseEvent.BUTTON1));
				});
			}
		});
		
		txtInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if(txtInput.getText().length() == 0) {
						removeTag((TagComponent) getComponent(getTagCount() - 1));
					}
				}
			}
		});
		
		txtInput.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(txtInput.getText().isEmpty()) {
					popupMenu.setVisible(false);
					return;
				}
				
				collectExpectedTags();
				
				List<String> result = filterExpectedTags();
				
				if(result == null || result.isEmpty()) {
					popupMenu.setVisible(false);
					return;
				}
				
				clearPopupMenu();
				buildPopupMenuItems(result);
				getReadyPopupMenu();
				
				popupMenu.setPopupSize(txtInput.getWidth() < 100 ? 100 : txtInput.getWidth(), popupMenu.getComponentCount() * 30);
				popupMenu.show(txtInput, 0, txtInput.getHeight());
				selectFirstMenuFromPopupMenu();
				
				txtInput.requestFocusInWindow();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				collectExpectedTags();
				
				List<String> result = filterExpectedTags();
				
				if(result == null || result.isEmpty()) {
					popupMenu.setVisible(false);
					return;
				}
				
				clearPopupMenu();
				buildPopupMenuItems(result);
				getReadyPopupMenu();
				
				popupMenu.setPopupSize(txtInput.getWidth() < 100 ? 100 : txtInput.getWidth(), popupMenu.getComponentCount() * 30);
				popupMenu.show(txtInput, 0, txtInput.getHeight());
				selectFirstMenuFromPopupMenu();
				
				txtInput.requestFocusInWindow();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// Do nothing
			}
		});
		
		add(txtInput);
	}
	
	public void setEnabled(boolean enabled) {
		if(enabled ^ editable) {
			setEditable(enabled);
		}
		
		super.setEnabled(enabled);
	}
	
	public void addTags(List<String> tags) {
		for(String t : tags) {
			TagComponent tc = new TagComponent(t, this);
			tc.setRemovable(editable);
			add(tc, getTagCount());
		}
		
		repaint();
		revalidate();
	}
	
	protected void addTag(TagComponent tagComponent) {
		if(getTagCount() == maxTags) {
			return;
		}
		
		tagFieldEventType = TagFieldEventType.TAG_ADDED;
		
		addedTagComponent = tagComponent;
		
		add(tagComponent, getTagCount());
		repaint();
		revalidate();
		
		fireEvent();
	}
	
	protected void removeTag(TagComponent tagComponent) {
		tagFieldEventType = TagFieldEventType.TAG_REMOVED;
		
		removedTagComponent = tagComponent;
		
		remove(tagComponent);
		repaint();
		revalidate();
		
		fireEvent();
	}
	
	public void clearTags() {
		for(Component c : getComponents()) {
			if(c instanceof TagComponent) {
				remove(c);
			}
		}
		
		repaint();
		revalidate();
	}
	
	public TagComponent getTagAt(int index) {
		if(index >= getTagCount() && index < 0) {
			return null;
		}
		
		return (TagComponent) getComponent(index);
	}
	
	public void setMaxTags(int maxTags) {
		this.maxTags = maxTags;
	}
	
	public int getMaxTags() {
		return maxTags;
	}
	
	public int getTagCount() {
		return getComponentCount() - 1;
	}
	
	public void clearText() {
		txtInput.setText("");
	}
	
	protected String getFilterText() {
		return txtInput.getText().length() == 0 ? "" : txtInput.getText().substring(0, 1);
	}
	
	public List<String> getTagValues() {
		if(getComponentCount() == 1) {
			return null;
		}
		
		List<String> result = new ArrayList<>();
		
		for(Component c : getComponents()) {
			if(c instanceof TagComponent) {
				result.add(((TagComponent) c).getText());
			}
		}
		
		return result;
	}
	
	public void addTagFieldListener(TagFieldListener listener) {
		listenerList.add(TagFieldListener.class, listener);
	}
	
	public void removeTagFieldListener(TagFieldListener listener) {
		listenerList.remove(TagFieldListener.class, listener);
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
		txtInput.setVisible(editable);
		for(Component c : getComponents()) {
			if(c instanceof TagComponent) {
				((TagComponent) c).setRemovable(editable);
			}
		}
		
		repaint();
		revalidate();
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void refresh() {
		repaint();
		revalidate();
	}
	
	private void fireEvent() {
		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i < listeners.length; i += 2) {
			if(listeners[i] == TagFieldListener.class) {
				if(tagFieldEventType == TagFieldEventType.TAG_ADDED) {
					((TagFieldListener) listeners[i + 1]).tagAdded(new TagFieldEvent(TagField.this, addedTagComponent, null, selectedTag));
				}
				else if(tagFieldEventType == TagFieldEventType.TAG_REMOVED) {
					((TagFieldListener) listeners[i + 1]).tagRemoved(new TagFieldEvent(TagField.this, null, removedTagComponent, null));
				}
				else {
					expectedTags = ((TagFieldListener) listeners[i + 1]).collectingExpectedTags(new TagFieldEvent(TagField.this, null, null, null));
				}
			}
		}
	}
	
	private void collectExpectedTags() {
		if(expectedTags == null || expectedTags.isEmpty() || !txtInput.getText().substring(0, 1).equalsIgnoreCase(expectedTags.get(0).toString().substring(0, 1))) {
			tagFieldEventType = TagFieldEventType.COLLECTING_EXPECTED_TAGS;
			fireEvent();
		}
	}
	
	private List<String> filterExpectedTags() {
		if(expectedTags == null || expectedTags.isEmpty()) {
			return null;
		}
		
		return expectedTags.stream()
			.filter(t -> t.toString().toLowerCase().startsWith(txtInput.getText().toLowerCase().trim()))
			.limit(5)
			.collect(Collectors.toList());
	}
	
	private void buildPopupMenuItems(List<String> filteredExpectedTags) {
		popupMenuItems = filteredExpectedTags.stream()
				.map(t -> buildMenuItem(t))
				.collect(Collectors.toList());
	}
	
	private void clearPopupMenu() {
		if(popupMenuItems == null) {
			return;
		}
		
		for(JMenuItem m : popupMenuItems) {
			popupMenu.remove(m);
		}
	}
	
	private void getReadyPopupMenu() {
		for(JMenuItem m : popupMenuItems) {
			popupMenu.add(m);
		}
	}
	
	private JMenuItem buildMenuItem(String tag) {
		JMenuItem menuItem = new JMenuItem(tag);
		menuItem.addActionListener((e) -> {
			selectedTag = tag;
			addTag(new TagComponent(tag, TagField.this));
			clearText();
		});
		
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				selectedTag = tag;
				addTag(new TagComponent(tag, TagField.this));
				clearText();
			}
		});
		
		return menuItem;
	}
	
	private void selectFirstMenuFromPopupMenu() {
		SwingUtilities.invokeLater(() -> {
			popupMenu.dispatchEvent(new KeyEvent(popupMenu, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN, '\0'));
		});
	}

}
