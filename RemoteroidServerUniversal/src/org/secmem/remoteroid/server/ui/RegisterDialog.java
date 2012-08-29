package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.RequestFactory;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.server.R;

public class RegisterDialog extends Dialog {

	protected Object result;
	protected Shell shlRegister;
	private Text txtEmail;
	private Label lblPassword;
	private Text txtPassword;
	private Text txtVerifyPassword;
	private Label lblStatus;
	private Button btnRegister;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RegisterDialog(Shell parent, int style) {
		super(parent, style);
		setText("Register");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlRegister.open();
		shlRegister.layout();
		
		Display display = getParent().getDisplay();
		
		while (!shlRegister.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	private void refreshDialogState(){
		if(txtEmail.getText().length()==0){
			lblStatus.setText(R.getString("enter_email_address"));
			btnRegister.setEnabled(false);
			return;
		}
		
		if(!Pattern.matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})", txtEmail.getText())){
			lblStatus.setText(R.getString("enter_valid_email_address"));
			btnRegister.setEnabled(false);
			return;
		}
		
		if(txtPassword.getText().length()==0){
			lblStatus.setText(R.getString("enter_password"));
			btnRegister.setEnabled(false);
			return;
		}
		
		if(txtVerifyPassword.getText().length()==0){
			lblStatus.setText(R.getString("verify_password"));
			btnRegister.setEnabled(false);
			return;
		}
		
		if(!txtPassword.getText().equals(txtVerifyPassword.getText())){
			lblStatus.setText(R.getString("password_does_not_matches"));
			btnRegister.setEnabled(false);
			return;
		}
		
		lblStatus.setText("");
		btnRegister.setEnabled(true);
		
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlRegister = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlRegister.setSize(306, 232);
		shlRegister.setText(R.getString("register"));
		
		Rectangle parent = getParent().getBounds();
		shlRegister.setBounds((int)((double)(parent.width)/2+parent.x-153), 
				(int)((double)(parent.height)/2+parent.y-116), 
				306, 232);
		
		txtEmail = new Text(shlRegister, SWT.BORDER);
		txtEmail.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshDialogState();
			}
		});
		txtEmail.setBounds(10, 44, 286, 22);
		
		Label lblEmailAddress = new Label(shlRegister, SWT.NONE);
		lblEmailAddress.setBounds(10, 24, 122, 14);
		lblEmailAddress.setText(R.getString("email_short"));
		
		lblPassword = new Label(shlRegister, SWT.NONE);
		lblPassword.setBounds(10, 69, 87, 14);
		lblPassword.setText(R.getString("password"));
		
		txtPassword = new Text(shlRegister, SWT.BORDER | SWT.PASSWORD);
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshDialogState();
			}
		});
		txtPassword.setBounds(10, 89, 286, 22);
		
		btnRegister = new Button(shlRegister, SWT.NONE);
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				final String email = txtEmail.getText();
				final String pass = txtPassword.getText();
				
				try {
					new ProgressMonitorDialog(shlRegister).run(true, true, new IRunnableWithProgress(){

						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(R.getString("register_in_progress"), IProgressMonitor.UNKNOWN);
							try {
								Account account = new Account();
								account.setEmail(email);
								account.setPassword(pass);
								
								Request request = RequestFactory.getRequest(API.Account.ADD_ACCOUNT).attachPayload(account);
								request.attachPayload(account);
								
								final Response response = request.sendRequest();
								
								Display disp = getParent().getDisplay();
								disp.syncExec(new Runnable(){
									public void run(){
										MessageBox messageBox = new MessageBox(shlRegister, SWT.ICON_INFORMATION | SWT.OK);
										messageBox.setText(R.getString("remoteroid"));
										if(response.isSucceed()){
											messageBox.setMessage(R.getString("account_created"));
											messageBox.open();
											shlRegister.close();
										}else{
											switch(response.getErrorCode()){
											case Codes.Error.Account.DUPLICATE_EMAIL:
												messageBox.setMessage(R.getString("email_duplicated"));
												break;
												
											case Codes.Error.GENERAL:
												messageBox.setMessage(R.getString("failed_to_register"));
												break;
											}
											messageBox.open();
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
				
			}
		});
		btnRegister.setEnabled(false);
		btnRegister.setBounds(202, 167, 94, 28);
		btnRegister.setText(R.getString("register"));
		
		lblStatus = new Label(shlRegister, SWT.NONE);
		lblStatus.setBounds(10, 174, 186, 21);
		lblStatus.setText("");
		
		Label lblVerifyPassword = new Label(shlRegister, SWT.NONE);
		lblVerifyPassword.setBounds(10, 114, 138, 14);
		lblVerifyPassword.setText(R.getString("verify_password"));
		
		txtVerifyPassword = new Text(shlRegister, SWT.BORDER | SWT.PASSWORD);
		txtVerifyPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshDialogState();
			}
		});
		txtVerifyPassword.setBounds(10, 134, 286, 22);

	}
}
