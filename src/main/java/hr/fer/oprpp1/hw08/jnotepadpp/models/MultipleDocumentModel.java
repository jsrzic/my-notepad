package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * Interface providing methods for communication with a model which consists of multiple {@link SingleDocumentModel} objects.
 * The model also tracks the document which is currently selected.
 * @author Josip
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel>{
	/**
	 * Returns GUI representation of this {@link MultipleDocumentModel}, usually a {@link JTabbedPane}.
	 * @return GUI representation of this {@link MultipleDocumentModel}, usually a {@link JTabbedPane}
	 */
	JComponent getVisualComponent();
	
	/**
	 * Creates a new empty document and adds it to the model.
	 * Also creates a new GUI tab and switches to it.
	 * @return the added document
	 */
	SingleDocumentModel createNewDocument();
	
	/**
	 * Returns currently selected document.
	 * @return currently selected document
	 */
	SingleDocumentModel getCurrentDocument();
	
	/**
	 * Loads document from given path on the disk and adds it to the model.
	 * Also creates a new GUI tab and switches to it.
	 * If a document with the same path is already opened in the editor, it switches to that tab instead.
	 * @param path path of the document
	 * @return the added document
	 */
	SingleDocumentModel loadDocument(Path path);
	
	/**
	 * If newPath is <code>null</code>, saves the document to the path associated with the document.
	 * Otherwise, saves the document to the path given by newPath and updates the path of the document to newPath.
	 * @param model document to be saved
	 * @param newPath newPath of the document, can be <code>null</code>
	 */
	void saveDocument(SingleDocumentModel model, Path newPath);
	
	/**
	 * Closes given tab and removes the associated document from the model.
	 * If only one document is opened, does nothing.
	 * @param model the document to be closed
	 */
	void closeDocument(SingleDocumentModel model);
	
	/**
	 * Adds given {@link MultipleDocumentListener} to internal collection of listeners.
	 * @param l listener to be added
	 */
	void addMultipleDocumentListener(MultipleDocumentListener l);
	
	/**
	 * Removes given listener from internal collection of listeners.
	 * @param l listener to be removed
	 */
	void removeMultipleDocumentListener(MultipleDocumentListener l);
	
	/**
	 * Returns number of {@link SingleDocumentListener} contained in this model.
	 * @return number of {@link SingleDocumentListener} contained in this model
	 */
	int getNumberOfDocuments();
	
	/**
	 * Returns document at given index from this model.
	 * @param index index of the wanted document
	 * @return document at given index from this model
	 */
	SingleDocumentModel getDocument(int index);
	
	/**
	 * Returns document which has the given path from this model. If there is no document with given path, returns <code>null</code>.
	 * @param path path we are searching for
	 * @return document with given path if it exists in this model, otherwise <code>null</code>
	 */
	SingleDocumentModel findForPath(Path path);
	
	/**
	 * Returns index of given document from this model. If given document is not present in this model, returns -1.
	 * @param doc document whose index we are searching for
	 * @return index of given document if it exists in this model, otherwise -1
	 */
	int getIndexOfDocument(SingleDocumentModel doc); //-1 if not present
}
