package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Implementation of model representing a single document.
 * @author Josip
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {
	/**
	 * File path which corresponds to this document.
	 */
	private Path path;
	
	/**
	 * Component used for displaying and managing text.
	 */
	private JTextArea textComponent;
	
	/**
	 * Modification status of this document.
	 */
	private boolean isModified;
	
	/**
	 * Collection of listeners subscribed to this {@link DefaultSingleDocumentModel}.
	 */
	private List<SingleDocumentListener> listeners;
	
	/**
	 * Creates a new {@link DefaultSingleDocumentModel}.
	 * @param path file path which corresponds to this document.
	 * @param text initial text of the text GUI component
	 */
	public DefaultSingleDocumentModel(Path path, String text) {
		this.path = path;
		this.textComponent = new JTextArea(text);
		this.isModified = false;
		this.listeners = new ArrayList<SingleDocumentListener>();
		this.textComponent.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				setModified(true);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				setModified(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				setModified(true);
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JTextArea getTextComponent() {
		return this.textComponent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFilePath() {
		return this.path;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if path is <code>null</code>
	 */
	@Override
	public void setFilePath(Path path) {
		if(path == null) throw new NullPointerException("Path can not be null");
		
		this.path = path;
		for (SingleDocumentListener listener : listeners) {
			listener.documentFilePathUpdated(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isModified() {
		return this.isModified;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModified(boolean modified) {
		this.isModified = modified;
		for (SingleDocumentListener listener : listeners) {
			listener.documentModifyStatusUpdated(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		this.listeners.add(l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		this.listeners.remove(l);
	}

}
