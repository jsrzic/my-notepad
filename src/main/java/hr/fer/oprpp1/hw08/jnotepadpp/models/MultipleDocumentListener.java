package hr.fer.oprpp1.hw08.jnotepadpp.models;

/**
 * Listener which triggers whenever current document is changed, a document is added or document is removed to given {@link MultipleDocumentModel}.
 * @author Josip
 *
 */
public interface MultipleDocumentListener {
	void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel);
	void documentAdded(SingleDocumentModel model);
	void documentRemoved(SingleDocumentModel model);
}
