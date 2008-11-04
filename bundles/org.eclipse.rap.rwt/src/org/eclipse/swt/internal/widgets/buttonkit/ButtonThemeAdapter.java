/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Button;


public final class ButtonThemeAdapter extends ControlThemeAdapter {

  private static final Point CHECK_SIZE = new Point( 13, 13 );
  private static final int CHECK_SPACING = 4;

  protected void configureMatcher( final WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addStyle( "TOGGLE", SWT.TOGGLE );
    matcher.addStyle( "CHECK", SWT.CHECK );
    matcher.addStyle( "RADIO", SWT.RADIO );
  }

  public Rectangle getPadding( final Button button ) {
    Rectangle result = getCssBoxDimensions( "Button", "padding", button );
    // TODO [rst] Additional padding for PUSH and TOGGLE buttons, remove when
    //            CSS theming is in place
    if( ( button.getStyle() & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      result.x += 1;
      result.y += 1;
      result.width += 2;
      result.height += 2;
    }
    return result;
  }

  public int getSpacing( final Button button ) {
    return getCssDimension( "Button", "spacing", button );
  }

  public Point getCheckSize() {
    return CHECK_SIZE;
  }

  public int getCheckSpacing() {
    return CHECK_SPACING;
  }
}
