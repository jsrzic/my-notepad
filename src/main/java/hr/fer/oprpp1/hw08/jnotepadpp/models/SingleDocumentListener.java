package hr.fer.oprpp1.hw08.jnotepadpp.models;

/**
 * Listener which is triggered whenever the document is modified or its path is updated.
 * @author Josip
 *
 */
public interface SingleDocumentListener {
	void documentModifyStatusUpdated(SingleDocumentModel model);
	void documentFilePathUpdated(SingleDocumentModel model);
}
