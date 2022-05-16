package hr.fer.oprpp1.hw08.jnotepadpp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;

import hr.fer.oprpp1.hw08.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LJMenu;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LJOptionPane;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LocalizableAction;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LocalizationProviderBridge;
import hr.fer.oprpp1.hw08.jnotepadpp.models.DefaultMultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;

/**
 * Implementation of custom Notepad application which supports multiple document tabs, localization and many different kinds of actions.
 * @author Josip
 *
 */
public class JNotepadPP extends JFrame {
	/**
	 * Object which holds information about opened tabs and which tab is currently selected.
	 */
	private MultipleDocumentModel mdm;
	
	/**
	 * Object used for securing that this frame properly connects and disconnects from its {@link LocalizationProviderBridge}.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates JNotepadPP window and initializes GUI components.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				checkModifiedBeforeExit();
			}
			
		});
		setLocation(0, 0);
		setSize(600, 600);

		initGUI();
	}
	
	/**
	 * Initializes all necessary GUI components and sets window title (and listener needed to display correct title).
	 */
	private void initGUI() {
		mdm = new DefaultMultipleDocumentModel();
		JTabbedPane tabbedPane = (JTabbedPane)mdm;
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(tabbedPane.getTabCount() > 0) {
					SingleDocumentModel selected = mdm.getDocument(tabbedPane.getSelectedIndex());
					String title = selected.getFilePath() == null ? "(unnamed)" : selected.getFilePath().toAbsolutePath().toString();
					JNotepadPP.this.setTitle(title + " - JNotepad++");
				}
				else {
					JNotepadPP.this.setTitle("JNotepad++");
				}
			}
		});
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(mdm.getVisualComponent(), BorderLayout.CENTER);
		setTitle("(unnamed) - JNotepad++");
		
		createMenus();
		createToolbar(centerPanel);
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		createStatusBar();
	}
	
	/**
	 * Action which opens a new empty document.
	 */
	private Action newDocumentAction = 
			new LocalizableAction("newDocumentActionName", KeyStroke.getKeyStroke("control N"), KeyEvent.VK_N, "newDocumentActionDescription", flp) {

				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						mdm.createNewDocument();
					} catch(IllegalStateException ex) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"readFileErrorMessage",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
						
						return;
					}
					
				}
				
			};
	
	
	/**
	 * Action which opens existing document from file system.
	 */
	private Action openDocumentAction = 
			new LocalizableAction("openDocumentActionName", KeyStroke.getKeyStroke("control O"), KeyEvent.VK_O, "openDocumentActionDescription", flp) {

				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Open file");
					if(fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
						return;
					}
					File file = fc.getSelectedFile();
					Path filePath = file.toPath();
					if(!Files.isReadable(filePath)) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"noSuchFileErrorMessage",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
						
						return;
					}
					
					try {
						mdm.loadDocument(filePath);
					} catch(IllegalStateException ex) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"readFileErrorMessage",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
						return;
					}
					
				}
				
			};
	
	/**
	 * Action which saves an existing file. If file had not been saved, delegates work to {@link saveAsDocumentAction}.
	 */
	private Action saveDocumentAction = 
			new LocalizableAction("saveDocumentActionName", KeyStroke.getKeyStroke("control S"), KeyEvent.VK_S, "saveDocumentActionDescription", flp) {

				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					if(mdm.getCurrentDocument().getFilePath() == null) {
						saveAsDocumentAction.actionPerformed(e);
						return;
					}
					
					try {
						mdm.saveDocument(mdm.getCurrentDocument(), null);
					} catch(IllegalStateException ex) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"saveFileErrorMessage",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
						
						return;
					}
					
				}
				
			};
	
	/**
	 * Saves file to the file system.
	 */
	private Action saveAsDocumentAction = 
			new LocalizableAction("saveAsDocumentActionName", KeyStroke.getKeyStroke("control alt S"), KeyEvent.VK_A, "saveAsDocumentActionDescription", flp) {
					
				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Save file as");
					if(fc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
						return;
					}
					File file = fc.getSelectedFile();
					Path filePath = file.toPath();
					
					try {
						mdm.saveDocument(mdm.getCurrentDocument(), filePath);
						JNotepadPP.this.setTitle(file.getAbsolutePath().toString() + " - JNotepad++");
					} catch(IllegalStateException ex) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"saveFileErrorMessage",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
						return;
					} catch(IllegalArgumentException ex) {
						LJOptionPane.showDialog(
								JNotepadPP.this,
								"fileAlreadyOpenedError",
								"errorTitle",
								JOptionPane.OK_OPTION,
								JOptionPane.ERROR_MESSAGE,
								new String[]{"okOption"},
								flp,
								null);
					
						return;
					}
					
				}
				
			};
	
	
	/**
	 * Closes tab which is currently selected.
	 */
	private Action closeDocumentAction = 
			new LocalizableAction("closeDocumentActionName", KeyStroke.getKeyStroke("control W"), KeyEvent.VK_W, "closeDocumentActionDescription", flp) {
				
				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					mdm.closeDocument(mdm.getCurrentDocument());
				}
				
			};
	
	/**
	 * Cuts selected text to clipboard.
	 */
	private Action cutTextAction =
			new LocalizableAction("cutTextActionName", KeyStroke.getKeyStroke("control X"), KeyEvent.VK_X, "cutTextActionDescription", flp){

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new DefaultEditorKit.CutAction().actionPerformed(e);
				}
			};
	
	/**
	 * Copies selected text to clipboard.
	 */
	private Action copyTextAction =
			new LocalizableAction("copyTextActionName", KeyStroke.getKeyStroke("control C"), KeyEvent.VK_C, "copyTextActionDescription", flp){

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new DefaultEditorKit.CopyAction().actionPerformed(e);
				}
			};
	
	/**
	 * Pastes text from clipboard to editor.
	 */
	private Action pasteTextAction =
			new LocalizableAction("pasteTextActionName", KeyStroke.getKeyStroke("control V"), KeyEvent.VK_V, "pasteTextActionDescription", flp){

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new DefaultEditorKit.PasteAction().actionPerformed(e);
				}
			};
	
	/**
	 * Displays some statistics about text in the current tab.
	 */
	private Action getStatisticsAction = 
			new LocalizableAction("getStatisticsActionName", KeyStroke.getKeyStroke("control I"), KeyEvent.VK_I, "getStatisticsActionDescription", flp) {

				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					String text = mdm.getCurrentDocument().getTextComponent().getText();
					int linesCount = countLines(text);
					int charCount = text.length();
					int nonBlankCharCount = text.replaceAll("\\s+", "").length();
										
					LJOptionPane.showDialog(
							JNotepadPP.this,
							"statsTemplate",
							"statsTitle",
							JOptionPane.OK_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							new String[]{"okOption"},
							flp,
							new Object[] {charCount, nonBlankCharCount, linesCount});
				}
				
			};
	
	/**
	 * Checks that all tabs are saved and quits the application.
	 */
	private Action exitAction = 
			new LocalizableAction("exitActionName", KeyStroke.getKeyStroke("control Q"), KeyEvent.VK_Q, "exitActionDescription", flp){

				private static final long serialVersionUID = 1L;
		
				@Override
				public void actionPerformed(ActionEvent e) {
					checkModifiedBeforeExit();
				}
				
			};
			
	/**
	 * Changes letters from selected text to uppercase.
	 */
	private Action toUppercaseAction =
			new LocalizableAction("toUppercaseActionName", KeyStroke.getKeyStroke("control U"), KeyEvent.VK_U, "toUppercaseActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextArea editor = mdm.getCurrentDocument().getTextComponent();
					Document doc = editor.getDocument();
					int len = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
					int offset = Math.min(editor.getCaret().getDot(),editor.getCaret().getMark());

					try {
						String text = doc.getText(offset, len);
						text = changeCase(text, Character::toUpperCase);
						doc.remove(offset, len);
						doc.insertString(offset, text, null);
					} catch(BadLocationException ex) {
						ex.printStackTrace();
					}
				}
				
			};
	
	/**
	 * Changes letters from selected text to lowercase.
	 */
	private Action toLowercaseAction =
			new LocalizableAction("toLowercaseActionName", KeyStroke.getKeyStroke("control L"), KeyEvent.VK_L, "toLowercaseActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextArea editor = mdm.getCurrentDocument().getTextComponent();
					Document doc = editor.getDocument();
					int len = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
					int offset = Math.min(editor.getCaret().getDot(),editor.getCaret().getMark());

					try {
						String text = doc.getText(offset, len);
						text = changeCase(text, Character::toLowerCase);
						doc.remove(offset, len);
						doc.insertString(offset, text, null);
					} catch(BadLocationException ex) {
						ex.printStackTrace();
					}
				}
				
			};
	
	/**
	 * Inverts case for each letter in the selected text.
	 */
	private Action invertCaseAction =
			new LocalizableAction("invertCaseActionName", KeyStroke.getKeyStroke("control T"), KeyEvent.VK_T, "invertCaseActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextArea editor = mdm.getCurrentDocument().getTextComponent();
					Document doc = editor.getDocument();
					int len = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
					int offset = Math.min(editor.getCaret().getDot(),editor.getCaret().getMark());

					try {
						String text = doc.getText(offset, len);
						text = invertCase(text);
						doc.remove(offset, len);
						doc.insertString(offset, text, null);
					} catch(BadLocationException ex) {
						ex.printStackTrace();
					}
				}
				
			};
	
	/**
	 * Sorts text in selected lines in ascending order.
	 */
	private Action sortTextAscendingAction =
			new LocalizableAction("sortTextAscendingActionName", KeyStroke.getKeyStroke("control K"), KeyEvent.VK_K, "sortTextAscendingActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Comparator<Object> comparator = Collator.getInstance(new Locale(flp.getLanguage()));
					sortSelectedLines(comparator);
				}
				
			};
	
	/**
	 * Sorts text in selected lines in descending order.
	 */
	private Action sortTextDescendingAction =
			new LocalizableAction("sortTextDescendingActionName", KeyStroke.getKeyStroke("control J"), KeyEvent.VK_J, "sortTextDescendingActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Comparator<Object> comparator = Collator.getInstance(new Locale(flp.getLanguage())).reversed();
					sortSelectedLines(comparator);
				}
				
			};
			
	/**
	 * Removes duplicate lines from selected text, keeping only the first occurrence.
	 */
	private Action removeDuplicateLinesAction =
			new LocalizableAction("removeDuplicateLinesActionName", KeyStroke.getKeyStroke("control D"), KeyEvent.VK_D, "removeDuplicateLinesActionDescription", flp) {
			
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextArea editor = mdm.getCurrentDocument().getTextComponent();
					Document doc = editor.getDocument();
					Element root = doc.getDefaultRootElement();
					Set<String> linesSet = new HashSet<>();
					Stack<Integer> removalOffsetStack = new Stack<>();
					Stack<Integer> removalLenStack = new Stack<>();
					
					
					try {
						int startLine = editor.getLineOfOffset(Math.min(editor.getCaret().getDot(), editor.getCaret().getMark()));
						int endLine = editor.getLineOfOffset(Math.max(editor.getCaret().getDot(), editor.getCaret().getMark()));
						
						for(int i = startLine; i <= endLine; i++) {
							Element element = root.getElement(i);
							int offset = element.getStartOffset();
							int len = element.getEndOffset() - element.getStartOffset();
							if(element.getEndOffset() != doc.getLength()) {
								len--;
							}
							String text = doc.getText(offset, len);
							if(!linesSet.add(text.strip())) {
								removalOffsetStack.push(offset);
								removalLenStack.push(len);
							}
						}
						
						while(!removalLenStack.isEmpty()) {
							doc.remove(removalOffsetStack.pop(), removalLenStack.pop());
						}
						
					} catch(BadLocationException ex) {
						ex.printStackTrace();
					}
				}
				
			};
	
	/**
	 * Helper function used by sortTextAscendingAction and sortTextDescendingAction. 
	 * It does the actual action, while sortTextAscendingAction and sortTextDescendingAction only provide this function the 
	 * appropriate comparator (based on localization settings).
	 * @param comparator comparator which is used for character comparison
	 */
	private void sortSelectedLines(Comparator<Object> comparator) {
		JTextArea editor = mdm.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();
		
		try {
			int startLine = editor.getLineOfOffset(Math.min(editor.getCaret().getDot(), editor.getCaret().getMark()));
			int endLine = editor.getLineOfOffset(Math.max(editor.getCaret().getDot(), editor.getCaret().getMark()));
			
			for(int i = startLine; i <= endLine; i++) {
				int len = editor.getLineEndOffset(i) - editor.getLineStartOffset(i);
				if(editor.getLineEndOffset(i) != doc.getLength()) {
					len--;
				}
				len = len < 0 ? 0 : len;
				int offset = editor.getLineStartOffset(i);
				String text = doc.getText(offset, len);
				String[] chunks = text.split("");
				Arrays.asList(chunks).sort(comparator);
				text = String.join("", chunks);
				doc.remove(offset, len);
				doc.insertString(offset, text, null);
			}
			
		} catch(BadLocationException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Helper function used by invertCaseAction.
	 * Takes in some text and returns new String with inverted character case for each character.
	 * @param text some input String
	 * @return same String but with inverted character cases
	 */
	private String invertCase(String text) {
		char[] chars = text.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if(Character.isLowerCase(c)) {
				chars[i] = Character.toUpperCase(c);
			} else if(Character.isUpperCase(c)) {
				chars[i] = Character.toLowerCase(c);
			}
		}
		return new String(chars);
	}
	
	/**
	 * Helper function used by toUppercaseAction and toLowercaseAction.
	 * Applies change function on each character and returns the resulting text.
	 * @param text some input text
	 * @param change transformation function
	 * @return transformed text
	 */
	private String changeCase(String text, Function<Character, Character> change) {
		char[] chars = text.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i];
			chars[i] = change.apply(c);
		}
		return new String(chars);
	}
	
	/**
	 * Helper function used by getStatisticsAction.
	 * Counts number of newline characters in given text.
	 * @param text some input text
	 * @return number of newline characters
	 */
	private int countLines(String text) {
		if(text.equals("")) return 0;
		
		int count = 1;
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '\n') {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Helper function used by exitAction.
	 * Loops through opened documents and for each checks whether it is saved.
	 * If not, offers user 3 choices: save the document, discard changes or cancel the exit action.
	 * If end of loop is reached, closes window by calling dispose().
	 */
	private void checkModifiedBeforeExit() {
		for(int i = 0; i < mdm.getNumberOfDocuments(); i++) {
			if(mdm.getDocument(i).isModified()) {
				int response = LJOptionPane.showDialog(
						JNotepadPP.this,
						"docEditedNotSavedTemplate",
						"warningUnsaved",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE,
						new String[]{"saveOption", "discardOption", "cancelOption"},
						flp,
						new Object[] {mdm.getDocument(i).getFilePath() == null ? "(unnamed)" :  mdm.getDocument(i).getFilePath().toAbsolutePath()});
	
				
				if(response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
					return;
				}
				
				else if(response == JOptionPane.NO_OPTION) {
					continue;
				}
				
				else {
					((JTabbedPane)mdm).setSelectedIndex(i);
					saveDocumentAction.actionPerformed(null);
				}
			}
		}
		
		dispose();
	}
	
	/**
	 * Creates menu bar with all its items.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new LJMenu("fileMenu", flp);
		menuBar.add(fileMenu);
		
		fileMenu.add(new JMenuItem(newDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));
		
		JMenu editMenu = new LJMenu("editMenu", flp);
		menuBar.add(editMenu);
		
		editMenu.add(new JMenuItem(cutTextAction));
		editMenu.add(new JMenuItem(copyTextAction));
		editMenu.add(new JMenuItem(pasteTextAction));
		
		JMenu viewMenu = new LJMenu("viewMenu", flp);
		menuBar.add(viewMenu);
		
		viewMenu.add(new JMenuItem(getStatisticsAction));
		
		JMenu langMenu = new LJMenu("languagesMenu", flp);
		menuBar.add(langMenu);
		
		langMenu.add(new JMenuItem("hr"));
		langMenu.add(new JMenuItem("en"));
		langMenu.add(new JMenuItem("de"));
		
		for(int i = 0; i < 3; i++) {
			JMenuItem langItem = (JMenuItem) langMenu.getMenuComponent(i);
			langItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					LocalizationProvider.getInstance().setLanguage(langItem.getText());
				}
				
			});
		}
		
		JMenu toolsMenu = new LJMenu("toolsMenu", flp);
		menuBar.add(toolsMenu);
		
		JMenu changeCaseSubmenu = new LJMenu("changeCaseSubmenu", flp);
		toolsMenu.add(changeCaseSubmenu);
		
		changeCaseSubmenu.add(new JMenuItem(toUppercaseAction));
		changeCaseSubmenu.add(new JMenuItem(toLowercaseAction));
		changeCaseSubmenu.add(new JMenuItem(invertCaseAction));
		
		JMenu sortSubmenu = new LJMenu("sortSubmenu", flp);
		toolsMenu.add(sortSubmenu);
		
		sortSubmenu.add(new JMenuItem(sortTextAscendingAction));
		sortSubmenu.add(new JMenuItem(sortTextDescendingAction));
		
		toolsMenu.add(new JMenuItem(removeDuplicateLinesAction));
		
		for(int i = 0; i < toolsMenu.getItemCount(); i++) {
			((JMenuItem) toolsMenu.getMenuComponent(i)).setEnabled(false);
		}
		
		CaretListener cl = new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				updateMenuItemsEnabled(toolsMenu);
			}	
		};
		
		mdm.getCurrentDocument().getTextComponent().addCaretListener(cl);
		
		mdm.addMultipleDocumentListener(new MultipleDocumentListener() {
			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}
			
			@Override
			public void documentAdded(SingleDocumentModel model) {
			}
			
			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				previousModel.getTextComponent().removeCaretListener(cl);
				updateMenuItemsEnabled(toolsMenu);
				currentModel.getTextComponent().addCaretListener(cl);
			}
		});
		
		this.setJMenuBar(menuBar);
	}
	
	/**
	 * Loops through given menu and toggles enabled status for each item, depending on whether any text is selected.
	 * @param menu menu to loop through
	 */
	private void updateMenuItemsEnabled(JMenu menu) {
		for(int i = 0; i < menu.getItemCount(); i++) {
			JMenuItem caseItem = (JMenuItem) menu.getMenuComponent(i);
			String selection = mdm.getCurrentDocument().getTextComponent().getSelectedText();
			
			boolean isSelected = selection != null;
			
			if(isSelected) {
				if(!caseItem.isEnabled()) caseItem.setEnabled(true);
			} else {
				if(caseItem.isEnabled()) caseItem.setEnabled(false);
			}
		}
	}
	
	/**
	 * Creates floatable toolbar with all its items.
	 * @param centerPanel
	 */
	private void createToolbar(JPanel centerPanel) {
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.setFloatable(true);
		
		toolBar.add(new JButton(newDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(cutTextAction));
		toolBar.add(new JButton(copyTextAction));
		toolBar.add(new JButton(pasteTextAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(getStatisticsAction));
		toolBar.add(new JButton(exitAction));
		
		centerPanel.add(toolBar, BorderLayout.PAGE_START);
	}
	
	/**
	 * Creates status bar which shows length of text, position of caret, length of selection and current time.
	 */
	private void createStatusBar() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		JTextField bar1 = new JTextField("length:0");
		JTextField bar2 = new JTextField("Ln:1 Col:1 Sel:0");
		JTextField bar3 = new JTextField(sdf.format(new Date()));
		
		Timer t = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				bar3.setText(sdf.format(new Date()));
			}
			
		});
		t.start();
		
		bar1.setEditable(false);
		bar2.setEditable(false);
		bar3.setEditable(false);
		bar3.setHorizontalAlignment(JTextField.RIGHT);
		JPanel statusBar = new JPanel(new GridLayout(0, 3));
		statusBar.add(bar1);
		statusBar.add(bar2);
		statusBar.add(bar3);
		statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		this.getContentPane().add(statusBar, BorderLayout.PAGE_END);
				
		CaretListener cl = new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				updateStatusBar(bar1, bar2);
			}
			
		};
		
		mdm.getCurrentDocument().getTextComponent().addCaretListener(cl);
		
		mdm.addMultipleDocumentListener(new MultipleDocumentListener() {
			
			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}
			
			@Override
			public void documentAdded(SingleDocumentModel model) {
			}
			
			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				previousModel.getTextComponent().removeCaretListener(cl);
				updateStatusBar(bar1, bar2);
				currentModel.getTextComponent().addCaretListener(cl);
			}
		});
	}
	
	/**
	 * Helper function used by status bar to update its content.
	 * @param bar1 part of status bar which shows length of text
	 * @param bar2 part of status bar which shows position of caret and length of selection
	 */
	private void updateStatusBar(JTextField bar1, JTextField bar2) {
		JTextArea textArea = mdm.getCurrentDocument().getTextComponent();
		int caretPosition = textArea.getCaretPosition();
		int ln = 1;
		int col = 1;
		int sel = textArea.getSelectedText() == null ? 0 : textArea.getSelectedText().length();
		try {
			ln = textArea.getLineOfOffset(caretPosition);
			col = caretPosition - textArea.getLineStartOffset(ln) + 1;
		} catch (BadLocationException e) {}
		
		ln++;
		
		bar1.setText("length:" + textArea.getText().length());
		bar2.setText(String.format("Ln:%d Col:%d Sel:%d", ln, col, sel));
	}
	
	/**
	 * Starts the application by creating the window.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JNotepadPP().setVisible(true);
			}
		});
	}

}
