package hr.fer.oprpp1.hw08.jnotepadpp.local;


import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * Action without the actionPerformed method. 
 * Initializes action information in the constructor and adds subscription to localization changes.
 * @author Josip
 *
 */
public abstract class LocalizableAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates new {@link LocalizableAction}.
	 * @param nameKey key mapping to translation of action name
	 * @param accelerator command shortcut
	 * @param mnemonic mnemonic for this command
	 * @param descriptionKey key mapping to translation of action description
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 */
	public LocalizableAction(String nameKey, KeyStroke accelerator, int mnemonic, String descriptionKey, ILocalizationProvider lp) {
		setActionInfo(nameKey, accelerator, mnemonic, descriptionKey, lp);
		
		lp.addLocalizationListener(new ILocalizationListener() {
			
			@Override
			public void localizationChanged() {
				setActionInfo(nameKey, accelerator, mnemonic, descriptionKey, lp);
			}

		});
	}
	
	/**
	 * Updates action information.
	 * @param nameKey key mapping to translation of action name
	 * @param accelerator command shortcut
	 * @param mnemonic mnemonic for this command
	 * @param descriptionKey key mapping to translation of action description
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 */
	private void setActionInfo(String nameKey, KeyStroke accelerator, int mnemonic, String descriptionKey,
			ILocalizationProvider lp) {
		String nameTranslation = lp.getString(nameKey);
		String descriptionTranslation = lp.getString(descriptionKey);
		
		putValue(NAME, nameTranslation);
		putValue(ACCELERATOR_KEY, accelerator);
		putValue(MNEMONIC_KEY, mnemonic);
		putValue(SHORT_DESCRIPTION, descriptionTranslation);
	}

	
}
