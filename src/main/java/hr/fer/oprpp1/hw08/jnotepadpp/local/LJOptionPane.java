package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Class which offers localized version 
 * of the {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, javax.swing.Icon, Object[], Object)} method.
 * 
 * @author Josip
 *
 */
public class LJOptionPane {
	
	/**
	 * Localized version of the {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, javax.swing.Icon, Object[], Object)} method.
	 * @param parent parent component
	 * @param messageKey key which maps to the translation of dialog message
	 * @param titleKey key which maps to the translation of dialog title
	 * @param optionType check out {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, javax.swing.Icon, Object[], Object)}
	 * @param messageType check out {@link JOptionPane#showOptionDialog(Component, Object, String, int, int, javax.swing.Icon, Object[], Object)}
	 * @param optionsKeys keys which map to translations of dialog options
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 * @param args format arguments if the string is to be formatted, else <code>null</code>
	 * @return index of chosen dialog option (>= 0)
	 */
	public static int showDialog(Component parent, String messageKey, String titleKey, int optionType, int messageType, String[] optionsKeys, ILocalizationProvider lp, Object[] args) {
		String messageTranslation = args == null ? lp.getString(messageKey) : String.format(lp.getString(messageKey), args);
		String titleTranslation = lp.getString(titleKey);
		Object[] optionsTranslations = new Object[optionsKeys.length];
		for(int i = 0; i < optionsKeys.length; i++) {
			optionsTranslations[i] = lp.getString(optionsKeys[i]);
		}
		
		return JOptionPane.showOptionDialog(parent, messageTranslation, titleTranslation, optionType, messageType, null, optionsTranslations, optionsTranslations[0]);
	}
}
