package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.net.InetAddress;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.server.net.ClientManager;
import org.secmem.remoteroid.server.ui.view.DeviceScreenCanvas;

public class Main extends ApplicationWindow implements ClientManager.ClientStateListener{
	
	private static ClientManager clientManager;
	private static DeviceScreenCanvas deviceScreenCanvas;	
	
	private Action menu_connection_login;
	private Action menu_connection_logout;
	private Action menu_connection_exit;
	private Text edtEmail;
	private Text edtPassword;
	
	private Composite cmpstLogin;

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
		
		cmpstLogin = new Composite(container, SWT.NONE);
		cmpstLogin.setBackground(SWTResourceManager.getColor(64, 64, 64));
		
		cmpstLogin.setBounds(27, 24, 300, 527);
		Image img = new Image(Display.getDefault(), "welcome.png");
		
		Label lblEmailAddress = new Label(cmpstLogin, SWT.NONE);
		lblEmailAddress.setLocation(35, 197);
		lblEmailAddress.setSize(132, 14);
		lblEmailAddress.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEmailAddress.setText("E-mail address");
		
		Label lblPassword = new Label(cmpstLogin, SWT.NONE);
		lblPassword.setLocation(35, 244);
		lblPassword.setSize(59, 14);
		lblPassword.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPassword.setText("Password");
		
		edtEmail = new Text(cmpstLogin, SWT.BORDER);
		edtEmail.setBounds(35, 218, 228, 19);
		
		edtPassword = new Text(cmpstLogin, SWT.BORDER | SWT.PASSWORD);
		edtPassword.setBounds(35, 264, 225, 19);
		edtPassword.setText("");
		
		Button btnLogin = new Button(cmpstLogin, SWT.NONE);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO process login
			}
		});
		btnLogin.setEnabled(false);
		btnLogin.setBounds(35, 294, 225, 37);
		btnLogin.setText("Login");
		
		Canvas canvas_1 = new Canvas(cmpstLogin, SWT.NONE);
		canvas_1.setBounds(0, 0, 300, 270);
		canvas_1.setBackgroundImage(img);
		
		Label label = new Label(cmpstLogin, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(35, 337, 225, 14);
		
		Button btnRegister = new Button(cmpstLogin, SWT.NONE);
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				RegisterDialog dlg = new RegisterDialog(shell, SWT.DEFAULT);
				dlg.open();
			}
		});
		btnRegister.setBounds(35, 357, 107, 39);
		btnRegister.setText("Register");
		
		Button btnSkipLogin = new Button(cmpstLogin, SWT.NONE);
		btnSkipLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// Set Login composite to invisible
				cmpstLogin.setVisible(false);
				
				// Listen incoming connections
				clientManager.waitClientConnection();
			}
		});
		btnSkipLogin.setBounds(148, 357, 112, 39);
		btnSkipLogin.setText("Skip login");
		
		deviceScreenCanvas = new DeviceScreenCanvas(container, SWT.NONE);
		deviceScreenCanvas.setSize(326, 545);
		deviceScreenCanvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));

		clientManager = new ClientManager(this);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			menu_connection_login = new Action("&Show login dialog\u2026") {
			};
			menu_connection_login.setAccelerator(SWT.COMMAND+'L');
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
			MenuManager connection = new MenuManager("Session");
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
			
			if(clientManager!=null)
				clientManager.disconnect();
			
			Display disp = Display.getCurrent();
			if(disp!=null)
				disp.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Shell shell;
	
	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		shell = newShell;
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

	@Override
	public void onConnected(String ipAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveCommand(CommandPacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveScreen(final byte[] image) {
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run(){
				deviceScreenCanvas.setImage(image);
			}
		});
		
	}

	@Override
	public void onDisconnected() {
		
	}
}
