package com.mswing.demo;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.mswing.buttongroup.ButtonGroup;
import com.mswing.comp.tagfield.TagComponent;
import com.mswing.comp.tagfield.TagField;
import com.mswing.comp.tagfield.TagFieldEvent;
import com.mswing.comp.tagfield.TagFieldListener;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author RThu
 *
 */

public class TagFieldSamplePane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel lbTitle;
	private JLabel lbTagRemaining;
	private JLabel lbInfoTitle;
	private JLabel lbSampleWords;
	
	private TagField tagField;
	
	private JTextArea txtEventLog;
	
	private JButton btClearTags;
	private JButton btSetMaxTags;
	
	private ButtonGroup buttonGroup;
	
	private JRadioButton rbYes;
	private JRadioButton rbNo;

	public TagFieldSamplePane() {
		prepareLayout();
		setupComponentActions();
	}
	
	private void prepareLayout() {
		lbTitle = new JLabel("TagField Component", SwingConstants.CENTER);
		lbTitle.setFont(lbTitle.getFont().deriveFont(20f).deriveFont(Font.BOLD));
		
		tagField = new TagField();
		
		lbTagRemaining = new JLabel("You can add " + tagField.getMaxTags() + " more Tag(s)");
		
		txtEventLog = new JTextArea();
		txtEventLog.setEditable(false);
		
		btClearTags = new JButton("Clear Tags");
		btSetMaxTags = new JButton("Set Maximum Tag Count");
		
		rbYes = new JRadioButton("Yes");
		rbNo = new JRadioButton("No");
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(rbYes);
		buttonGroup.add(rbNo);
		buttonGroup.setSelected(rbYes.getModel(), true);
		
		JPanel buttonPane = new JPanel(new MigLayout("al center center"));
		buttonPane.add(lbTagRemaining);
		buttonPane.add(btSetMaxTags);
		buttonPane.add(new JSeparator(JSeparator.VERTICAL), "grow");
		buttonPane.add(btClearTags);
		buttonPane.add(new JSeparator(JSeparator.VERTICAL), "grow");
		buttonPane.add(new JLabel("Editable : "));
		buttonPane.add(rbYes);
		buttonPane.add(rbNo);
		
		lbInfoTitle = new JLabel("Info : Try typing following words");
		lbInfoTitle.setFont(lbInfoTitle.getFont().deriveFont(15f).deriveFont(Font.BOLD));
		lbInfoTitle.setForeground(Color.decode("#79ABFF"));
		
		lbSampleWords = new JLabel("you, can, get, data, from , the, database, connection, jdbc, so, " + 
				"tag, field, is, awsome, mswing, modern, useful, java, swing, component", JLabel.CENTER);
		lbSampleWords.setForeground(Color.WHITE);
		
		JPanel infoPane = new JPanel(new MigLayout("al center center"));
		infoPane.setBackground(Color.decode("#5A5E66"));
		infoPane.setBorder(BorderFactory.createEtchedBorder());
		infoPane.add(lbInfoTitle, "wrap");
		infoPane.add(lbSampleWords);
		
		JScrollPane eventLogView = new JScrollPane(txtEventLog);
		eventLogView.setBorder(BorderFactory.createTitledBorder("Event Log"));
		
		setLayout(new MigLayout());
		add(lbTitle, "growx, wrap 10");
		add(new JLabel("Tag : "), "split 2, shrinkx");
		add(tagField, "grow, pushx, wrap 5");
		add(buttonPane, "growx, pushx, wrap 5");
		add(infoPane, "grow, pushx, wrap 10");
		add(eventLogView, "shrink, grow, push");
	}
	
	private void setupComponentActions() {
		tagField.addTagFieldListener(new TagFieldListener() {
			
			@Override
			public void tagRemoved(TagFieldEvent event) {
				txtEventLog.append("Tag : " + event.getRemovedTagComponent().getText() + " has been removed!\n");
				updateRemainingTagCount();
			}
			
			@Override
			public void tagAdded(TagFieldEvent event) {
				txtEventLog.append("Tag : " + event.getAddedTagComponent().getText() + " has been added!\n");
				updateRemainingTagCount();
			}
			
			@Override
			public List<String> collectingExpectedTags(TagFieldEvent event) {
				txtEventLog.append("Fetching tag that starts with : " + event.getFilterText().toLowerCase() + "\n");
				
				return getDefaultTextList()
						.stream()
						.filter(s -> s.toLowerCase().startsWith(event.getFilterText().toLowerCase()))
						.collect(Collectors.toList());
			}
		});
		
		btClearTags.addActionListener((e) -> {
			tagField.clearTags();
			tagField.clearText();
			
			lbTagRemaining.setText("You can add " + tagField.getMaxTags() + " more Tag(s)");
		});
		
		btSetMaxTags.addActionListener((e) -> {
			int maxTags = 0;
			
			while(maxTags <= 0) {
				try {
					String value = JOptionPane.showInputDialog(TagFieldSamplePane.this, "Set how many tags can be added to the TagField.");
					
					if(value == null || value.isEmpty()) {
						return;
					}
					
					maxTags = Integer.valueOf(value);
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(TagFieldSamplePane.this, "Maximum tag count value should be numberic.");
					continue;
				}
			}
			
			tagField.setMaxTags(maxTags);
			updateRemainingTagCount();
			
			while(maxTags < tagField.getTagCount()) {
				try {
					Method method = tagField.getClass().getDeclaredMethod("removeTag", TagComponent.class);
					method.setAccessible(true);
					method.invoke(tagField, tagField.getTagAt(tagField.getTagCount() - 1));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		buttonGroup.addSelectionChangedListener((e) -> {
			if(e.getSelectedButton() == rbYes && !tagField.isEditable()) {
				tagField.setEditable(true);
			}
			else {
				if(tagField.isEditable()) {
					tagField.setEditable(false);
				}
			}
		});
	}
	
	private void updateRemainingTagCount() {
		lbTagRemaining.setText("You can add " + ((tagField.getMaxTags() - tagField.getTagCount()) == 0 ? "no" : 
			(tagField.getMaxTags() - tagField.getTagCount())) + " more Tag(s)");
	}
	
	private List<String> getDefaultTextList() {
		return List.of("you", "can", "get", "data", "from" , "the", "database", "connection", "jdbc", "so",
				"tag", "field", "is", "awsome", "mswing", "modern", "useful", "java", "swing", "component");
	}

}
