package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.data.WakeupMessage;
import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.server.R;
import org.secmem.remoteroid.server.net.ConnectionManager;
import org.secmem.remoteroid.server.ui.view.DeviceScreenCanvas;

public class Main implements ConnectionManager.ClientStateListener{
	
	private static final Logger log = Logger.getLogger("Main");

	protected Shell shell;
	private DeviceScreenCanvas canvas;
	
	private static ConnectionManager clientManager;
	private Device currentDevice;
	private Account currentAccount;
	private String currentIpAddress;
	
	private MenuItem accountMenuItem;
	private MenuItem deviceMenuItem;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
			clientManager.disconnect();
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
		
		clientManager.waitClientCommandConnection();
		
		// Launch Welcome dialog
		Object loginResult = new WelcomeDialog(shell, SWT.DEFAULT).open();
		if(loginResult!=null){
			currentAccount = (Account)loginResult;
			setAccountSession(currentAccount);
			Object deviceSelectionResult = new DeviceSelectionDialog(shell, SWT.DEFAULT).setUserAccount(currentAccount).open();
			
			if(deviceSelectionResult!=null){
				currentDevice = (Device)deviceSelectionResult;
				sendConnectionRequestToDevice();
			}else{
				// Client did not selected device.
				setDeviceSession(null);
				log.info("User did not selected device.");
			}
		}else{
			// Client did not logged in
			log.info("Not logged-in & Device not selected...");
			setAccountSession(null);
			setDeviceSession(null);
		}
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}
	
	private void sendConnectionRequestToDevice(){
		if(currentIpAddress==null){
			MessageDialog.openError(shell, R.getString("error"), R.getString("invalid_ip_address"));
			return;
		}
		if(currentDevice==null){
			MessageDialog.openError(shell, R.getString("error"), R.getString("no_device_to_connect"));
			return;
		}
		try {
			new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress(){

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(R.getString("fetching_device_list"), IProgressMonitor.UNKNOWN);
					
					WakeupMessage msg = new WakeupMessage();
					msg.setDevice(currentDevice);
					msg.setServerIpAddress(currentIpAddress);
					
					Request request = Request.Builder.setRequest(API.Wakeup.WAKE_UP).setPayload(msg).build();
					
					final Response response = request.sendRequest();
					
					Display disp = Display.getCurrent();
					disp.syncExec(new Runnable(){
						public void run(){
							
							if(response.isSucceed()){
								MessageDialog.openInformation(shell, R.getString("message_sent"),String.format(R.getString("connection_request_message_sent_to"), currentDevice.getNickname()));
							}else{
								switch(response.getErrorCode()){
								case Codes.Error.Account.AUTH_FAILED:
									MessageDialog.openError(shell, R.getString("error"), R.getString("failed_to_authenticate_user"));
									break;
								
								case Codes.Error.GENERAL:
									MessageDialog.openError(shell, R.getString("error"), R.getString("unexpected_error"));
									break;
								}
								
							}
							
						}
					});
				}
				
			});
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		
	}
	
	private void setAccountSession(Account account){
		this.currentAccount = account;
		if(currentAccount!=null){
			accountMenuItem.setText(String.format(R.getString("logged_in_as"), currentAccount.getEmail()));
			deviceMenuItem.setEnabled(true);
		}else{
			accountMenuItem.setText("Log-in...");
		}
	}
	
	private void setDeviceSession(Device device){
		this.currentDevice = device;
		if(currentDevice!=null){
			deviceMenuItem.setText("Disconnect from device ("+currentDevice.getNickname()+")");
		}else{
			deviceMenuItem.setText("Connect to device...");
		}
	}
	
	private void resetSession(){
		setAccountSession(null);
		setDeviceSession(null);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(377, 666);
		Rectangle parent = Display.getDefault().getBounds();
		shell.setBounds((int)((double)(parent.width)/2+parent.x-130), 
				(int)((double)(parent.height)/2+parent.y-333), 
				377, 666);
		try{
			InetAddress ip = InetAddress.getLocalHost();
			shell.setText("Remoteroid - "+ip.getHostAddress());
			currentIpAddress = ip.getHostAddress();
			
			canvas = new DeviceScreenCanvas(shell, SWT.NONE);
			canvas.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					// TODO send mouse event to device
				}
			});
			canvas.setBounds(24, 24, 326, 545);
			
			Button btnBack = new Button(shell, SWT.NONE);
			btnBack.setBounds(24, 587, 94, 28);
			btnBack.setText("Back");
			
			Button btnHome = new Button(shell, SWT.NONE);
			btnHome.setBounds(143, 587, 94, 28);
			btnHome.setText("Home");
			
			Button btnMenu = new Button(shell, SWT.NONE);
			btnMenu.setBounds(256, 587, 94, 28);
			btnMenu.setText("Menu");
			
			shell.setMenuBar(generateMenuBar());
			
			clientManager = new ConnectionManager(this);
		}catch(IOException e){
			e.printStackTrace();
			shell.setText("Remoteroid - No connection");
		}

	}
	

	
	private Menu generateMenuBar(){
		Menu menu = new Menu(shell, SWT.BAR);
		
		MenuItem session = new MenuItem(menu, SWT.CASCADE);
		session.setText("Session");
		
		Menu sessionMenu = new Menu(shell, SWT.DROP_DOWN);
		session.setMenu(sessionMenu);
		
		accountMenuItem = new MenuItem(sessionMenu, SWT.PUSH);
		accountMenuItem.setText("Log-in...");
		accountMenuItem.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				new WelcomeDialog(shell, SWT.DEFAULT).setLooggedinAccount(currentAccount).open();
			}
			
		});
		
		deviceMenuItem = new MenuItem(sessionMenu, SWT.PUSH);
		deviceMenuItem.setText("Connect to device...");
		deviceMenuItem.setEnabled(false);
		deviceMenuItem.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				new DeviceSelectionDialog(shell, SWT.DEFAULT).open();
			}
			
		});
		
		return menu;
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
		Display.getCurrent().syncExec(new Runnable(){
			public void run(){
				canvas.setImage(image);
			}
		});
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
