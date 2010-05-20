/*
 * Copyright (c) 2009 Tamas Korodi <kotyo@zamba.fm>
 * Copyright (c) 2010 Levente Farkas <lfarkas@lfarkas.org>
 *
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.swt.overlay;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.gstreamer.BusSyncReply;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Message;
import org.gstreamer.Structure;
import org.gstreamer.event.BusSyncHandler;

import com.sun.jna.Platform;

public class VideoComponent extends Canvas implements BusSyncHandler {
	private final Element videosink;
	
	public VideoComponent(final Composite parent, int style) {
		super(parent, style | SWT.EMBEDDED);

		// TODO: replace directdrawsink with dshowvideosink if dshowvideosink become more stable:
		// http://forja.rediris.es/forum/forum.php?thread_id=5255&forum_id=1624
		videosink = ElementFactory.make(Platform.isLinux() ? "xvimagesink" : "directdrawsink", "OverlayVideoComponent");
		videosink.set("sync", false);
		videosink.set("async", false);
		SWTOverlay.wrap(videosink).setWindowID(this);		
	}
	
	/**
	 * Set to keep aspect ratio
	 * 
	 * @param keepAspect
	 */
	public void setKeepAspect(boolean keepAspect) {
		videosink.set("force-aspect-ratio", keepAspect);
	}

	/**
	 * Retrieves the Gstreamer element, representing the video component
	 * 
	 * @return element
	 */
	public Element getElement() {
		return videosink;
	}

	/**
	 * Implements the BusSyncHandler interface
	 * @param message
	 * @return
	 */
    public BusSyncReply syncMessage(Message message) {
        Structure s = message.getStructure();
        if (s == null || !s.hasName("prepare-xwindow-id"))
            return BusSyncReply.PASS;
        SWTOverlay.wrap(videosink).setWindowID(this);
        return BusSyncReply.DROP;
    }
}