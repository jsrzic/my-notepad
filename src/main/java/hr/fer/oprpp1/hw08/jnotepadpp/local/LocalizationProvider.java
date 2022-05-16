package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton class which implements the actual fetching of translations from resource bundles.
 * @author Josip
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {
	/**
	 * Currently selected localization.
	 */
	private String language;
	
	/**
	 * Object which holds translations for current language.
	 */
	private ResourceBundle bundle;
	
	/**
	 * The only instance of this class.
	 */
	private static final LocalizationProvider instance = new LocalizationProvider();
	
	/**
	 * Creates new {@link LocalizationProvider} with default language set to English.
	 */
	private LocalizationProvider() {
		this.language = "en";
		this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.local.prijevodi", Locale.forLanguageTag(language));
	}
	
	/**
	 * Returns the only instance of {@link LocalizationProvider}.
	 * @return the only instance of {@link LocalizationProvider}
	 */
	public static LocalizationProvider getInstance() {
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(String key) {
		return bundle.getString(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * Sets current language to the given one and notifies all listeners that the language has been changed.
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
		this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.local.prijevodi", Locale.forLanguageTag(language));
		fire();
	}
	

}
