package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Partial implementation of {@link ILocalizationProvider}.
 * Implements adding and removal of listeners, as well as notifying them.
 * @author Josip
 *
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider{
	
	/**
	 * List of listeners which are subscribed to this object.
	 */
	private List<ILocalizationListener> listeners;
	
	/**
	 * Creates new {@link AbstractLocalizationProvider} with no listeners.
	 */
	public AbstractLocalizationProvider() {
		this.listeners = new ArrayList<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notifies all subscribed listeners that a change in localization has occurred.
	 */
	public void fire() {
		for(ILocalizationListener listener : listeners) {
			listener.localizationChanged();
		}
	}

}
