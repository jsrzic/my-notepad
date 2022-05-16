package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.JMenu;

/**
 * Implementation of localizable JMenu.
 * @author Josip
 *
 */
public class LJMenu extends JMenu {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Key which maps to the translation of JMenu name.
	 */
	private String nameKey;
	
	/**
	 * Instance of {@link ILocalizationProvider} used for obtaining translations for given keys.
	 */
	private ILocalizationProvider lp;
	
	/**
	 * Creates new {@link LJMenu}.
	 * @param nameKey key which maps to the translation of JMenu name
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 */
	public LJMenu(String nameKey, ILocalizationProvider lp) {
		this.nameKey = nameKey;
		this.lp = lp;
		
		updateName();
		lp.addLocalizationListener(new ILocalizationListener() {
			
			@Override
			public void localizationChanged() {
				updateName();
			}
		});
	}
	
	/**
	 * Updates name of the JMenu item to match the translation in the current language.
	 */
	private void updateName() {
		String translation = lp.getString(nameKey);
		setText(translation);
	}
}
