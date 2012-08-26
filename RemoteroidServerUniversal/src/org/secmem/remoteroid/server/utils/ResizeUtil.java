package org.secmem.remoteroid.server.utils;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

public class ResizeUtil {
	public static Image resize(Image original, int width, int height){
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(original, 0, 0, 
		original.getBounds().width, original.getBounds().height, 
		0, 0, width, height);
		gc.dispose();
		original.dispose(); // don't forget about me!
		return scaled;
	}
	
}
