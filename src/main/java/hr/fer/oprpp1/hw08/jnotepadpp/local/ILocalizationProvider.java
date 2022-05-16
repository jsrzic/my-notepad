package hr.fer.oprpp1.hw08.jnotepadpp.local;

/**
 * Interface for obtaining translations for given keys.
 * Also allows registration and deregistration of listeners which fire whenever the language of this provider changes.
 * @author Josip
 *
 */
public interface ILocalizationProvider {
	/**
	 * Takes in a key and gives back the localization.
	 * @param key some localization key
	 * @return translation/localization which is stored for the given key
	 */
	String getString(String key);
	
	/**
	 * Returns current language.
	 * @return current language
	 */
	String getLanguage();
	
	/**
	 * Adds listener which listens for change of language.
	 * @param listener listener to be added
	 */
	void addLocalizationListener(ILocalizationListener listener);
	
	/**
	 * Removes given listener from collection of listeners.
	 * @param listener listener to be removed
	 */
	void removeLocalizationListener(ILocalizationListener listener);
}
