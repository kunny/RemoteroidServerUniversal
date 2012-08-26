package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.RequestFactory;
import org.secmem.remoteroid.lib.request.Response;

public class WelcomeDialog extends Dialog {

	protected Object result;
	protected Shell shlWelcome;
	private Text txtEmail;
	private Text txtPassword;
	private Canvas canvas;
	private Button btnSkipLogin;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WelcomeDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlWelcome.open();
		shlWelcome.layout();
		Display display = getParent().getDisplay();
		while (!shlWelcome.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlWelcome = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlWelcome.setSize(369, 390);
		shlWelcome.setText("Welcome");
		result = null; // Assume not logged in at first
		
		Rectangle parent = getParent().getBounds();
		shlWelcome.setBounds((int)((double)(parent.width)/2+parent.x-184), 
				(int)((double)(parent.height)/2+parent.y-182), 
				369, 364);
		
		txtEmail = new Text(shlWelcome, SWT.BORDER);
		txtEmail.setMessage("E-mail address");
		txtEmail.setBounds(96, 195, 177, 22);
		txtEmail.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				char c = e.character;
				if(c==SWT.CR || c==SWT.LF){
					processLogin();
				}
			}
		});
		
		txtPassword = new Text(shlWelcome, SWT.BORDER | SWT.PASSWORD);
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char c = e.character;
				if(c==SWT.CR || c==SWT.LF){
					processLogin();
				}
			}
		});
		txtPassword.setMessage("Password");
		txtPassword.setBounds(96, 223, 177, 22);
		
		final Image img = new Image(Display.getDefault(), "remoteroid.png");	
		
		canvas = new Canvas(shlWelcome, SWT.NO_BACKGROUND);
		canvas.setBounds(111, 39, 150, 150);
		
		Button btnRegisterNewAccount = new Button(shlWelcome, SWT.NONE);
		btnRegisterNewAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				RegisterDialog dlg = new RegisterDialog(shlWelcome, SWT.DEFAULT);
				dlg.open();
			}
		});
		btnRegisterNewAccount.setBounds(77, 266, 219, 28);
		btnRegisterNewAccount.setText("Register new account...");
		
		btnSkipLogin = new Button(shlWelcome, SWT.NONE);
		btnSkipLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				shlWelcome.close();
			}
		});
		btnSkipLogin.setBounds(77, 292, 219, 28);
		btnSkipLogin.setText("Skip login");
		canvas.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(img, 0, 0);
			}
			
		});
		
	}
	
	private void processLogin(){
		// A simple hack for prevent 'Enter' key event on dialog causes calls processLogin() method again
		canvas.setFocus();
		// Process login here
		try {
			final String email = txtEmail.getText();
			final String password = txtPassword.getText();
			
			if(email.length()==0){
				MessageDialog.openError(shlWelcome, "Error", "Enter your E-mail address.");
				return;
			}
			
			if(!Pattern.matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})", email)){
				MessageDialog.openError(shlWelcome, "Error", "Please enter a valid E-mail address.");
				return;
			}
			
			if(password.length()==0){
				MessageDialog.openError(shlWelcome, "Error", "Please enter password.");
				return;
			}
			
			
			new ProgressMonitorDialog(shlWelcome).run(true, true, new IRunnableWithProgress(){

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Login in progres..", IProgressMonitor.UNKNOWN);
					try {
						Account account = new Account();
						account.setEmail(email);
						account.setPassword(password);
						
						Request request = RequestFactory.getRequest(API.Account.LOGIN).attachPayload(account);
						
						final Response response = request.sendRequest();
						
						Display disp = getParent().getDisplay();
						disp.syncExec(new Runnable(){
							public void run(){
								
								if(response.isSucceed()){
									// Set result to Account : User has logged-in.
									result = response.getPayloadAsAccount();
									shlWelcome.close();
								}else{
									switch(response.getErrorCode()){
									case Codes.Error.Account.AUTH_FAILED:
										MessageDialog.openError(shlWelcome, "Login failed", "Failed to authenticate user.");
										break;
										
									case Codes.Error.GENERAL:
										MessageDialog.openError(shlWelcome, "Login failed", "Unexpected error has occoured.");
										break;
									}
									
								}
								
							}
						});
						
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			});
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		
		txtPassword.setSelection(0, txtPassword.getText().length());
		txtPassword.setFocus();
	}
}
