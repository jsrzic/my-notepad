package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.nio.file.Path;

import javax.swing.JTextArea;

/**
 * Interface which defines communication with model of a single document.
 * @author Josip
 *
 */
public interface SingleDocumentModel {
	/**
	 * Returns component used for displaying and managing text.
	 * @return component used for displaying and managing text
	 */
	JTextArea getTextComponent();
	
	/**
	 * Returns file path which corresponds to this document. Can be <code>null</code> if document still hasn't been saved to the disk.
	 * @return file path which corresponds to this document
	 */
	Path getFilePath();
	
	/**
	 * Setter for file path.
	 * @param path new path
	 */
	void setFilePath(Path path);
	
	/**
	 * Returns modification status of this document.
	 * @return modification status of this document
	 */
	boolean isModified();
	
	/**
	 * Setter for modification status.
	 * @param modified new modification status
	 */
	void setModified(boolean modified);
	
	/**
	 * Adds given {@link SingleDocumentListener} to internal collection of listeners.
	 * @param l listener to be added
	 */
	void addSingleDocumentListener(SingleDocumentListener l);
	
	/**
	 * Removes given {@link SingleDocumentListener} from internal collection of listeners.
	 * @param l listener to be removed
	 */
	void removeSingleDocumentListener(SingleDocumentListener l);
}
