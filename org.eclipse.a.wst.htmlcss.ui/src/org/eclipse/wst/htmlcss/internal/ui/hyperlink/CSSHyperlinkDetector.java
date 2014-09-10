/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.htmlcss.internal.ui.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.htmlcss.internal.ui.CSSClassNameOrIdRegion;
import org.eclipse.wst.htmlcss.internal.ui.DOMHelper;
import org.eclipse.wst.htmlcss.internal.ui.WebResourcesTextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Hyperlink detector for Web resources inside HTML:
 * 
 * <ul>
 * <li>@class : hyperlink for CSS class name inside @class attribute.</li>
 * <li>@id : hyperlink for CSS ID inside @id attribute.</li>
 * </ul>
 *
 */
public class CSSHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if ((region != null) && (textViewer != null)) {

			IStructuredDocumentRegion documentRegion = ContentAssistUtils
					.getStructuredDocumentRegion(textViewer, region.getOffset());
			WebResourcesTextRegion attrValueRegion = DOMHelper.getTextRegion(
					documentRegion, region.getOffset());
			if (attrValueRegion != null) {
				switch (attrValueRegion.getType()) {
				case CSS_CLASS_NAME:
				case CSS_ID:
					// hyperlink is done for @id or @class
					CSSClassNameOrIdRegion classNameRegion = DOMHelper
							.getCSSRegion(attrValueRegion, documentRegion,
									textViewer.getDocument(),
									region.getOffset());
					if (classNameRegion != null) {
						// Try to find CSS class name or CSS ID and build
						// Hyperlink
						IDOMNode node = (IDOMNode) ContentAssistUtils
								.getNodeAt(textViewer, region.getOffset());
						CSSHyperlinkTraverser traverser = new CSSHyperlinkTraverser(
								node, classNameRegion);
						traverser.process();
						return traverser.getHyperlinks();
					}
				default:
					// Do nothing : WTP already implements hyperlink for
					// script/@src and
					// link/@href
				}
			}
		}
		return null;
	}

}
