package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.net.InetAddress;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.secmem.remoteroid.server.net.CommandReceiverThread;
import org.secmem.remoteroid.server.net.ScreenReceiver;
import org.secmem.remoteroid.server.net.ScreenReceiver.ImageReceiveListener;
import org.secmem.remoteroid.server.ui.view.DeviceScreenCanvas;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Link;

public class Main extends ApplicationWindow {
	
	private ScreenReceiver screenReceiver;
	private CommandReceiverThread cmdReceiver;
	private static DeviceScreenCanvas canvas;
	
	private static ImageReceiveListener listener = new ImageReceiveListener(){

		@Override
		public void onClientConnected(String clientIpAddress) {
			
			
		}

		@Override
		public void onReceiveImageData(final byte[] image) {
			
			Display disp = Display.getDefault();
			disp.syncExec(new Runnable(){
				public void run(){
					canvas.setImage(image);
					canvas.redraw();
				}
			});
			
			
		}

		@Override
		public void onInterrupt() {
			
			
		}
		
	};
	private Action menu_connection_login;
	private Action menu_connection_logout;
	private Action menu_connection_exit;
	private Text edtEmail;
	private Text edtPassword;

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
		container.setBackground(SWTResourceManager.getColor(40, 40, 40));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(64, 64, 64));
		
		composite.setBounds(27, 24, 300, 527);
		Image img = new Image(Display.getDefault(), "welcome.png");
		
		Label lblEmailAddress = new Label(composite, SWT.NONE);
		lblEmailAddress.setLocation(35, 197);
		lblEmailAddress.setSize(132, 14);
		lblEmailAddress.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEmailAddress.setText("E-mail address");
		
		Label lblPassword = new Label(composite, SWT.NONE);
		lblPassword.setLocation(35, 244);
		lblPassword.setSize(59, 14);
		lblPassword.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPassword.setText("Password");
		
		edtEmail = new Text(composite, SWT.BORDER);
		edtEmail.setBounds(35, 218, 228, 19);
		
		edtPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		edtPassword.setBounds(35, 264, 225, 19);
		edtPassword.setText("");
		
		Button btnLogin = new Button(composite, SWT.NONE);
		btnLogin.setEnabled(false);
		btnLogin.setBounds(35, 294, 225, 37);
		btnLogin.setText("Login");
		
		Canvas canvas_1 = new Canvas(composite, SWT.NONE);
		canvas_1.setBounds(0, 0, 300, 270);
		canvas_1.setBackgroundImage(img);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(35, 337, 225, 14);
		
		Button btnRegister = new Button(composite, SWT.NONE);
		btnRegister.setBounds(35, 357, 107, 39);
		btnRegister.setText("Register");
		
		Button btnSkipLogin = new Button(composite, SWT.NONE);
		btnSkipLogin.setBounds(148, 357, 112, 39);
		btnSkipLogin.setText("Skip login");
		
		
		
		canvas = new DeviceScreenCanvas(container, SWT.NONE);
		canvas.setSize(326, 545);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));

		
		

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			menu_connection_login = new Action("Login") {
			};
		}
		{
			menu_connection_logout = new Action("Logout") {
			};
		}
		{
			menu_connection_exit = new Action("Exit") {
			};
		}
		
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		{
			MenuManager connection = new MenuManager("Connection");
			menuManager.add(connection);
			connection.add(menu_connection_login);
			connection.add(menu_connection_logout);
			connection.add(new Separator());
			connection.add(menu_connection_exit);
		}
		return menuManager;
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
		
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(355, 631);
	}
}
