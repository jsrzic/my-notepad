package hr.fer.oprpp1.hw08.jnotepadpp.local;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Class derived from LocalizationProviderBridge which registers itself as a WindowListener to a JFrame.
 * When the frame is opened, calls connect and when the frame is closed, calls disconnect.
 * 
 * @author Josip
 *
 */
public class FormLocalizationProvider extends LocalizationProviderBridge{
	/**
	 * Creates new {@link FormLocalizationProvider}.
	 * @param lp instance of {@link ILocalizationProvider} used for obtaining translations for given keys
	 * @param frame window assigned to this {@link FormLocalizationProvider}
	 */
	public FormLocalizationProvider(ILocalizationProvider lp, JFrame frame) {
		super(lp);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
			}
			
		});
	}
	
}
