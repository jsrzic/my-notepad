package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Model which consists of multiple {@link SingleDocumentModel} objects and tracks information about currently selected document.
 * This class is also a {@link JTabbedPane} which has documents as its children (tabs).
 * @author Josip
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Collection of {@link SingleDocumentModel} documents contained in this model.
	 */
	private List<SingleDocumentModel> documents;
	
	/**
	 * Pointer to the currently selected document.
	 */
	private SingleDocumentModel currentDocument;
	
	/**
	 * Collection of listeners which are subscribed to this model.
	 */
	private List<MultipleDocumentListener> listeners;
	
	/**
	 * Creates new {@link DefaultMultipleDocumentModel}, initializes its fields and adds a listener to follow currently selected document.
	 */
	public DefaultMultipleDocumentModel() {
		this.documents = new ArrayList<SingleDocumentModel>();
		this.listeners = new ArrayList<MultipleDocumentListener>();
		this.currentDocument = createNewDocument();
		this.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				SingleDocumentModel previousDocument = currentDocument;
				currentDocument = getNumberOfDocuments() > 0 ? documents.get(getSelectedIndex()) : null;
				if(previousDocument != currentDocument) {
					notifyCurrentDocumentChanged(previousDocument, currentDocument);
				}
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return documents.iterator();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JComponent getVisualComponent() {
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel createNewDocument() {
		SingleDocumentModel previousDocument = currentDocument;
		SingleDocumentModel newDocument = new DefaultSingleDocumentModel(null, "");
		newDocument.setModified(true);
		documents.add(newDocument);
		currentDocument = newDocument;
		addTabWithIcon("(unnamed)", newDocument);
		notifyDocumentAdded(newDocument);
		notifyCurrentDocumentChanged(previousDocument, newDocument);
		return newDocument;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel getCurrentDocument() {
		return currentDocument;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if path is null
	 * @throws IllegalStateException if an error occurs while reading document file or icon file
	 */
	@Override
	public SingleDocumentModel loadDocument(Path path) {
		if(path == null) throw new NullPointerException("Path can not be null");
		
		SingleDocumentModel previousDocument = currentDocument;
		SingleDocumentModel newDocument = findForPath(path);
		
		if(newDocument == null) {
			String text;
			try {
				text = Files.readString(path);
			} catch (IOException e) {
				throw new IllegalStateException("Internal error while reading file.");
			}
			newDocument = new DefaultSingleDocumentModel(path, text);
			documents.add(newDocument);
			addTabWithIcon(path.getFileName().toString(), newDocument);
			notifyDocumentAdded(newDocument);
		}
		
		currentDocument = newDocument;
		notifyCurrentDocumentChanged(previousDocument, newDocument);
		this.setSelectedIndex(documents.indexOf(newDocument));
		

		return newDocument;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if file of the same path is already opened in the editor
	 * @throws IllegalStateException if an error occurs while writing the document to the disk
	 */
	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {
		if(newPath != null && findForPath(newPath) != null) {
			throw new IllegalArgumentException("File of path " + newPath + " already exists.");
		}
		
		newPath = newPath == null ? model.getFilePath() : newPath;
		
		try {
			Files.writeString(newPath, model.getTextComponent().getText());
		} catch (IOException e) {
			throw new IllegalStateException("Internal error while reading file.");
		}
		model.setModified(false);
		if(model.getFilePath() == null || !model.getFilePath().equals(newPath)) {
			model.setFilePath(newPath);
		}		

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeDocument(SingleDocumentModel model) {
		if(getNumberOfDocuments() == 1) {
			return;
		}
		
		int index = getIndexOfDocument(model);
		SingleDocumentModel previousDocument = currentDocument;
		documents.remove(model);
		this.removeTabAt(index);
				
		if(currentDocument != previousDocument) {
			notifyCurrentDocumentChanged(previousDocument, currentDocument);
		}
		
		notifyDocumentRemoved(model);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.add(l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.remove(l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfDocuments() {
		return documents.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleDocumentModel getDocument(int index) {
		return documents.get(index);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws NullPointerException if path is <code>null</code>
	 */
	@Override
	public SingleDocumentModel findForPath(Path path) {
		if(path == null) throw new NullPointerException("Path can not be null");

		for(SingleDocumentModel doc : documents) {
			if(doc.getFilePath() == null) {
				continue;
			}
			if(doc.getFilePath().equals(path)){
				return doc;
			}
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexOfDocument(SingleDocumentModel doc) {
		return documents.indexOf(doc);
	}
	
	/**
	 * Notifies all listeners that current document has been changed.
	 * @param previousModel previous current document
	 * @param currentModel new current document
	 */
	private void notifyCurrentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
		for (MultipleDocumentListener listener : listeners) {
			listener.currentDocumentChanged(previousModel, currentModel);
		}
	}
	
	/**
	 * Notifies all listeners that new document has been added.
	 * @param model the newly added document
	 */
	private void notifyDocumentAdded(SingleDocumentModel model) {
		for (MultipleDocumentListener listener : listeners) {
			listener.documentAdded(model);
		}
	}
	
	/**
	 * Notifies all listeners that a document has been removed.
	 * @param model the removed document
	 */
	private void notifyDocumentRemoved(SingleDocumentModel model) {
		for (MultipleDocumentListener listener : listeners) {
			listener.documentRemoved(model);
		}
	}
	
	/**
	 * Loads icon from given path.
	 * @param path path of the icon
	 * @return object representation of the icon
	 * @throws IllegalStateException if an error occurs while reading the icon from the disk
	 */
	private ImageIcon createNewIcon(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		if(is==null) {
			throw new IllegalStateException("Internal error while reading file.");
		}
		byte[] bytes;
		try {
			bytes = is.readAllBytes();
		} catch (IOException e) {
			throw new IllegalStateException("Internal error while reading file.");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new ImageIcon(bytes);
	}
	
	/**
	 * Adds tab to this JTabbedPane corresponding to the given document.
	 * Also adds listener for tracking modification status of the document which update the icon. 
	 * @param title name of the tab
	 * @param doc document which belongs to the new tab
	 */
	private void addTabWithIcon(String title, SingleDocumentModel doc) {
		ImageIcon redDisk = createNewIcon("../icons/redDiskSmall.png");
		ImageIcon blueDisk = createNewIcon("../icons/blueDiskSmall.png");
		
		this.addTab(title, doc.isModified() ? redDisk : blueDisk, new JScrollPane(doc.getTextComponent()), doc.getFilePath() == null ? "" : doc.getFilePath().toAbsolutePath().toString());
		this.setSelectedIndex(documents.indexOf(doc));
		doc.addSingleDocumentListener(new SingleDocumentListener() {
			
			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				
				if(model.isModified()) {
					DefaultMultipleDocumentModel.this.setIconAt(documents.indexOf(model), redDisk);
				}
				else {
					DefaultMultipleDocumentModel.this.setIconAt(documents.indexOf(model), blueDisk);
				}
			}
			
			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				DefaultMultipleDocumentModel.this.setToolTipTextAt(documents.indexOf(model), model.getFilePath().toAbsolutePath().toString());
				DefaultMultipleDocumentModel.this.setTitleAt(documents.indexOf(model), model.getFilePath().getFileName().toString());
			}
		});
	}

}
