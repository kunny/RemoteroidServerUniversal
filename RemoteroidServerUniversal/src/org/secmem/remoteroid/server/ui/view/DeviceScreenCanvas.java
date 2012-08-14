package org.secmem.remoteroid.server.ui.view;

import java.io.ByteArrayInputStream;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DeviceScreenCanvas extends Canvas{

	private boolean isUpdatingImage = false;
	private Image frame;
	
	public DeviceScreenCanvas(Composite parent, int style) {
		super(parent, style);
	}
	
	public void startUpdatingImage(){
		if(frame==null)
			throw new IllegalStateException("No image to display.");
		
		if(!isUpdatingImage){
			addPaintListener(new PaintListener(){
				@Override
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(frame, 0, 0);
				}	
			});
		}			
	}
	
	public void setImage(byte[] imageData){
		ImageData data = new ImageData(new ByteArrayInputStream(imageData));
		if(frame!=null){
			frame.dispose();
		}
		frame = new Image(Display.getCurrent(), data);
	}
	
	public void setScreenSize(int width, int height){
		Rectangle rect = this.getBounds();
		rect.width = width;
		rect.height = height;
		setBounds(rect);
	}
	
}
