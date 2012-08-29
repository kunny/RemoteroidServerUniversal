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
import org.secmem.remoteroid.server.R;
import org.eclipse.swt.widgets.Label;

public class WelcomeDialog extends Dialog {

	protected Object result;
	protected Shell shlWelcome;
	private Text txtEmail;
	private Text txtPassword;
	private Canvas canvas;
	private Button btnRegisterNewAccount;
	private Button btnSkipLogin;
	private Button btnLogin;
	private Label lblLoggedInAs;
	
	private Account currentAccount;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WelcomeDialog(Shell parent, int style) {
		super(parent, style);
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
		shlWelcome.setSize(369, 387);
		shlWelcome.setText(R.getString("welcome"));
		result = null; // Assume not logged in at first
		
		Rectangle parent = getParent().getBounds();
		shlWelcome.setBounds((int)((double)(parent.width)/2+parent.x-184), 
				(int)((double)(parent.height)/2+parent.y-163), 
				369, 387);
		
		txtEmail = new Text(shlWelcome, SWT.BORDER);
		txtEmail.setMessage(R.getString("email"));
		txtEmail.setBounds(98, 195, 177, 22);
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
		txtPassword.setMessage(R.getString("password"));
		txtPassword.setBounds(98, 223, 177, 22);
		
		final Image img = new Image(Display.getDefault(), "res/remoteroid.png");	
		
		canvas = new Canvas(shlWelcome, SWT.NO_BACKGROUND);
		canvas.setBounds(111, 39, 150, 150);
		
		btnRegisterNewAccount = new Button(shlWelcome, SWT.NONE);
		btnRegisterNewAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(currentAccount==null){
					RegisterDialog dlg = new RegisterDialog(shlWelcome, SWT.DEFAULT);
					dlg.open();
				}else{
					// If user have logged-in, Just close dialog
					shlWelcome.close();
				}
			}
		});
		btnRegisterNewAccount.setBounds(77, 290, 219, 28);
		btnRegisterNewAccount.setText(R.getString("register_new_account"));
		
		btnSkipLogin = new Button(shlWelcome, SWT.NONE);
		btnSkipLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				shlWelcome.close();
			}
		});
		btnSkipLogin.setBounds(77, 316, 219, 28);
		btnSkipLogin.setText(R.getString("skip_login"));
		
		btnLogin = new Button(shlWelcome, SWT.NONE);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				processLogin();
			}
		});
		btnLogin.setBounds(77, 256, 219, 28);
		btnLogin.setText(R.getString("login"));
		
		lblLoggedInAs = new Label(shlWelcome, SWT.NONE);
		lblLoggedInAs.setAlignment(SWT.CENTER);
		lblLoggedInAs.setBounds(25, 214, 317, 22);
		lblLoggedInAs.setText("Logged in as : test@test.com");
		lblLoggedInAs.setVisible(false);
		
		canvas.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(img, 0, 0);
			}
			
		});
		
		// Represent current login information if logged-in
		if(currentAccount!=null){
			lblLoggedInAs.setText(String.format(R.getString("logged_in_as"), currentAccount.getEmail()));
			lblLoggedInAs.setVisible(true);
			txtEmail.setVisible(false);
			txtPassword.setVisible(false);
			btnSkipLogin.setVisible(false);
			btnLogin.setVisible(false);
			btnRegisterNewAccount.setText(R.getString("close"));
		}
		
	}
	
	public WelcomeDialog setLooggedinAccount(Account account){
		this.currentAccount = account;
		return this;
	}
	
	private void processLogin(){
		// A simple hack for prevent 'Enter' key event on dialog causes calls processLogin() method again
		canvas.setFocus();
		// Process login here
		try {
			final String email = txtEmail.getText();
			final String password = txtPassword.getText();
			
			if(email.length()==0){
				MessageDialog.openError(shlWelcome, R.getString("error"), R.getString("enter_email_address"));
				return;
			}
			
			if(!Pattern.matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})", email)){
				MessageDialog.openError(shlWelcome, R.getString("error"), R.getString("enter_valid_email_address"));
				return;
			}
			
			if(password.length()==0){
				MessageDialog.openError(shlWelcome, R.getString("error"), R.getString("enter_password"));
				return;
			}
			
			
			new ProgressMonitorDialog(shlWelcome).run(true, true, new IRunnableWithProgress(){

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(R.getString("login_in_progress"), IProgressMonitor.UNKNOWN);
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
									result = (Account)response.getPayloadAsAccount();
									shlWelcome.close();
								}else{
									switch(response.getErrorCode()){
									case Codes.Error.Account.AUTH_FAILED:
										MessageDialog.openError(shlWelcome, R.getString("login_failed"), R.getString("failed_to_authenticate_user"));
										break;
										
									case Codes.Error.GENERAL:
										MessageDialog.openError(shlWelcome, R.getString("login_failed"), R.getString("unexpected_error"));
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
		if(shlWelcome!=null && !shlWelcome.isDisposed()){
			txtPassword.setSelection(0, txtPassword.getText().length());
			txtPassword.setFocus();
		}
	}
}
