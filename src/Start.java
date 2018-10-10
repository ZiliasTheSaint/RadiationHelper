import java.io.File;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import gui.Bang;

/**
 * Start application class, containing the main method! <br>
 * 
 * @author Dan Fulea, 17 Apr. 2011
 */
public class Start {

	private static String filename = "JavaLookAndFeelLoader.laf";

	/**
	 * defaultLookAndFeel void method. <br>
	 * Try setting a default system look and feel!
	 */
	private static void defaultLookAndFeel() {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (final Exception exc) {

		// Trying to let only crossPlatformLookAndFeel due to incompatibility of
		// TEXT FONTS of JTextArea
		// between WINDOWS vista or 7 and WINDOWS XP when migrating from old
		// java (jre) 1.4 to new java 6!!
		JFrame.setDefaultLookAndFeelDecorated(true);// the key to set Java Look
													// and Feel from the start!
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (final Exception exc1) {
			System.err.println("Error defaultLookAndFell " + exc1);
		}
		// }
	}

	/**
	 * Converts ASCII int value to a String.
	 * 
	 * @param i
	 *            the ASCII integer
	 * @return the string representation
	 */
	private static String asciiToStr(int i) {
		char a[] = new char[1];
		a[0] = (char) i;
		return (new String(a)); // char to string
	}

	/**
	 * loadLookAndFeel void method. <br>
	 * Try loading the look and feel!
	 */
	private static void loadLookAndFeel() {
		String fileSeparator = System.getProperty("file.separator");
		String curentDir = System.getProperty("user.dir");
		String filename1 = curentDir + fileSeparator + filename;

		File f = new File(filename1);
		int i = 0;
		String desiredLF = "";
		boolean foundB = false;
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(f);
				while ((i = fr.read()) != -1) {
					String s1 = new String();
					s1 = asciiToStr(i);
					desiredLF = desiredLF + s1;
				}
				fr.close();

				JFrame.setDefaultLookAndFeelDecorated(true);

				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if (desiredLF.equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						foundB = true;
						break;
					}
				}

				if (!foundB) {
					if (desiredLF.equals("System")) {
						foundB = true;
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} else if (desiredLF.equals("Java")) {
						foundB = true;
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					}
				}

				if (!foundB)
					UIManager.setLookAndFeel(desiredLF);

				// empty
				if (desiredLF.equals("")) {
					defaultLookAndFeel();
				}

			} catch (Exception e) {
				defaultLookAndFeel();
			}
		} else {
			defaultLookAndFeel();
		}
	}

	/**
	 * Main method. <br>
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		loadLookAndFeel();
		// new Bang();
		SwingUtilities.invokeLater(

				new Runnable() {
					public void run() {
						// Turn off metal's use of bold fonts
						UIManager.put("swing.boldMetal", Boolean.FALSE);
						new Bang();
					}
				}
		);
	}

}
