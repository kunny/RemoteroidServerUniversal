package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.net.InetAddress;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main2 {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main2 window = new Main2();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		
		try{
			InetAddress ip = InetAddress.getLocalHost();
			shell.setText("Remoteroid - "+ip.getHostAddress());
		}catch(IOException e){
			e.printStackTrace();
			shell.setText("Remoteroid - No connection");
		}

	}

}
