package hr.fer.oprpp1.hw08.jnotepadpp.local;

/**
 * Class which serves as a middle man between localizable components and {@link ILocalizationListener}.
 * All components subscribe to this class, which in turn subscribes to {@link ILocalizationListener}.
 * 
 * @author Josip
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {
	/**
	 * Connection status.
	 */
	private boolean connected;
	
	/**
	 * Instance of {@link ILocalizationProvider} used for obtaining translations for given keys.
	 */
	private ILocalizationProvider lp;
	
	/**
	 * Current localization.
	 */
	private String currentLanguage;
	
	/**
	 * Listener which listens for language changes in the {@link ILocalizationProvider} and informs all subscribed components.
	 */
	private final ILocalizationListener listener;
	
	/**
	 * Creates new {@link LocalizationProviderBridge}.
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 */
	public LocalizationProviderBridge(ILocalizationProvider lp) {
		this.connected = false;
		this.lp = lp;
		this.currentLanguage = lp.getLanguage();
		this.listener = new ILocalizationListener() {
			@Override
			public void localizationChanged() {
				LocalizationProviderBridge.this.currentLanguage = lp.getLanguage();
				fire();
			}
		};
		lp.addLocalizationListener(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(String key) {
		return lp.getString(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguage() {
		return currentLanguage;
	}
	
	/**
	 * Opens connection between this bridge and {@link ILocalizationProvider}.
	 */
	public void connect() {
		if(connected) return;
		
		connected = true;
		
		if(!currentLanguage.equals(lp.getLanguage())) {
			currentLanguage = lp.getLanguage();
			fire();
		}
		
		lp.addLocalizationListener(listener);
	}
	
	/**
	 * Closes connection between this bridge and {@link ILocalizationProvider}.
	 */
	public void disconnect() {
		if(!connected) return;
		
		connected = false;
		currentLanguage = lp.getLanguage();
		
		lp.removeLocalizationListener(listener);
	}
	
}
