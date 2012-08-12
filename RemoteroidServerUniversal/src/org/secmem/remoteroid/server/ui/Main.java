package org.secmem.remoteroid.server.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.secmem.remoteroid.server.net.ScreenReceiver;
import org.secmem.remoteroid.server.net.ScreenReceiver.ImageReceiveListener;

public class Main extends ApplicationWindow{
	
	private ScreenReceiver receiver;
	private static Canvas canvas;
	
	private static ImageReceiveListener listener = new ImageReceiveListener(){

		@Override
		public void onClientConnected(String clientIpAddress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReceiveImageData(byte[] image) {
			ImageData data = new ImageData(new ByteArrayInputStream(image));
			final Image img = new Image(Display.getDefault(), data);
			System.out.print("Img="+img.getImageData().data);
			Display disp = Display.getDefault();
			disp.syncExec(new Runnable(){
				public void run(){
					canvas.setBackgroundImage(img);
					canvas.redraw();
				}
			});
			
			
		}

		@Override
		public void onInterrupt() {
			// TODO Auto-generated method stub
			
		}
		
	};

	/**
	 * Create the application window.
	 */
	public Main() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		canvas = new Canvas(container, SWT.NONE);
		canvas.setBounds(10, 10, 415, 499);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Main window = new Main();
			Display.setAppName("Remoteroid");
			window.setBlockOnOpen(true);
			
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		try{
			InetAddress ip = InetAddress.getLocalHost();
			newShell.setText("Remoteroid - "+ip.getHostAddress());
		}catch(IOException e){
			e.printStackTrace();
			newShell.setText("Remoteroid - No connection");
		}
		
		
		receiver = new ScreenReceiver();
		receiver.startReceivingImage(listener);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(435, 594);
	}

}
