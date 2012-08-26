package org.secmem.remoteroid.server.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.RequestFactory;
import org.secmem.remoteroid.lib.request.Response;

public class DeviceSelectionDialog extends Dialog {

	protected Object result;
	protected Shell shlSelectDeviceTo;
	private Table tblDevices;
	private Button btnEdit;
	private Button btnDelete;
	private Button btnConnect;
	
	private Account account;
	private ArrayList<Device> deviceList;
	
	public void setUserAccount(Account account){
		this.account = account;
	}

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DeviceSelectionDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlSelectDeviceTo.open();
		shlSelectDeviceTo.layout();
		
		fetchDevices();
		
		Display display = getParent().getDisplay();
		while (!shlSelectDeviceTo.isDisposed()) {
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
		shlSelectDeviceTo = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlSelectDeviceTo.setSize(420, 219);
		shlSelectDeviceTo.setText("Select device to connect");
		
		Rectangle parent = getParent().getBounds();
		shlSelectDeviceTo.setBounds((int)((double)(parent.width)/2+parent.x-210), 
				(int)((double)(parent.height)/2+parent.y-108), 
				420, 219);
		
		tblDevices = new Table(shlSelectDeviceTo, SWT.BORDER | SWT.FULL_SELECTION);
		tblDevices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(tblDevices.getSelectionCount()==1){
					btnEdit.setEnabled(true);
					btnDelete.setEnabled(true);
					btnConnect.setEnabled(true);
				}else{
					btnEdit.setEnabled(false);
					btnDelete.setEnabled(false);
					btnConnect.setEnabled(false);
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(tblDevices.getSelectionCount()==1){
					if(deviceList!=null){
						Device device = deviceList.get(tblDevices.getSelectionIndex());
						result = device;
						shlSelectDeviceTo.close();
					}
				}
			}
		});
		tblDevices.setBounds(20, 22, 271, 140);
		tblDevices.setHeaderVisible(true);
		tblDevices.setLinesVisible(true);
		
		TableColumn tblclmnNickname = new TableColumn(tblDevices, SWT.NONE);
		tblclmnNickname.setWidth(261);
		tblclmnNickname.setText("Device name");
		
		btnEdit = new Button(shlSelectDeviceTo, SWT.NONE);
		btnEdit.setEnabled(false);
		btnEdit.setBounds(308, 22, 94, 28);
		btnEdit.setText("Edit...");
		
		btnDelete = new Button(shlSelectDeviceTo, SWT.NONE);
		btnDelete.setEnabled(false);
		btnDelete.setBounds(308, 56, 94, 28);
		btnDelete.setText("Delete...");
		
		btnConnect = new Button(shlSelectDeviceTo, SWT.NONE);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(tblDevices.getSelectionCount()==1){
					if(deviceList!=null){
						Device device = deviceList.get(tblDevices.getSelectionIndex());
						result = device;
						shlSelectDeviceTo.close();
					}
				}
			}
		});
		btnConnect.setEnabled(false);
		btnConnect.setBounds(308, 144, 94, 28);
		btnConnect.setText("Connect");
		
		Button btnRefresh = new Button(shlSelectDeviceTo, SWT.NONE);
		btnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				fetchDevices();
			}
		});
		btnRefresh.setBounds(308, 90, 94, 28);
		btnRefresh.setText("Refresh");

	}
	
	private void fetchDevices(){
		try {
			new ProgressMonitorDialog(shlSelectDeviceTo).run(true, true, new IRunnableWithProgress(){

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Fetching device list..", IProgressMonitor.UNKNOWN);
					try {
						Request request = RequestFactory.getRequest(API.Device.LIST_DEVICE).attachPayload(account);
						
						final Response response = request.sendRequest();
						
						Display disp = getParent().getDisplay();
						disp.syncExec(new Runnable(){
							public void run(){
								
								if(response.isSucceed()){
									deviceList = response.getPayloadAsDeviceList();
									int itemCnt = deviceList.size();
									
									tblDevices.setItemCount(0);
									tblDevices.clearAll();
									
									for(int i=0; i<itemCnt; ++i){
										Device dev = deviceList.get(i);
										TableItem item = new TableItem(tblDevices, SWT.NULL, i);
										item.setText(0, dev.getNickname());
									}
									
								}else{
									switch(response.getErrorCode()){
									case Codes.Error.Account.AUTH_FAILED:
										MessageDialog.openError(shlSelectDeviceTo, "Failed to fetch device list", "Failed to authenticate user.");
										break;
										
									case Codes.Error.Device.DEVICE_NOT_FOUND:
										MessageDialog.openError(shlSelectDeviceTo, "Failed to fetch device list", "No device found.");
										break;
										
									case Codes.Error.GENERAL:
										MessageDialog.openError(shlSelectDeviceTo, "Failed to fetch device list", "Unexpected error has occoured.");
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
	}
}
