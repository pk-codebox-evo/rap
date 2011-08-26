/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.scrolledcompositekit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public final class ScrolledCompositeLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.custom.ScrolledComposite";
  private static final String SET_CONTENT = "setContent";

  private static final Integer ZERO = new Integer( 0 );

  // Request parameter names
  private static final String PARAM_H_BAR_SELECTION = "horizontalBar.selection";
  private static final String PARAM_V_BAR_SELECTION = "verticalBar.selection";

  // Property names for preserve value mechanism
  static final String PROP_BOUNDS = "clientArea";
  static final String PROP_HAS_H_SCROLL_BAR = "hasHScrollBar";
  static final String PROP_HAS_V_SCROLL_BAR = "hasVScrollBar";
  private static final String PROP_H_BAR_SELECTION = "hBarSelection";
  private static final String PROP_V_BAR_SELECTION = "vBarSelection";
  private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";
  static final String PROP_CONTENT = "content";

  public void preserveValues( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.preserve( PROP_BOUNDS, composite.getBounds() );
    adapter.preserve( PROP_HAS_H_SCROLL_BAR, Boolean.valueOf( hasHScrollBar( composite ) ) );
    adapter.preserve( PROP_HAS_V_SCROLL_BAR, Boolean.valueOf( hasVScrollBar( composite ) ) );
    adapter.preserve( PROP_H_BAR_SELECTION, getBarSelection( composite.getHorizontalBar() ) );
    adapter.preserve( PROP_V_BAR_SELECTION, getBarSelection( composite.getVerticalBar() ) );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasSelectionListener( composite ) ) );
    adapter.preserve( PROP_SHOW_FOCUSED_CONTROL,
                      Boolean.valueOf( composite.getShowFocusedControl() ) );
    adapter.preserve( PROP_CONTENT, composite.getContent() );
    WidgetLCAUtil.preserveCustomVariant( composite );
  }

  public void readData( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    Point origin = composite.getOrigin();
    String value = WidgetLCAUtil.readPropertyValue( widget, PARAM_H_BAR_SELECTION );
    ScrollBar hScroll = composite.getHorizontalBar();
    if( value != null && hScroll != null ) {
      origin.x = NumberFormatUtil.parseInt( value );
      processSelection( hScroll );
    }
    value = WidgetLCAUtil.readPropertyValue( widget, PARAM_V_BAR_SELECTION );
    ScrollBar vScroll = composite.getVerticalBar();
    if( value != null && vScroll != null ) {
      origin.y = NumberFormatUtil.parseInt( value );
      processSelection( vScroll );
    }
    composite.setOrigin( origin );
    ControlLCAUtil.processMouseEvents( composite );
    ControlLCAUtil.processKeyEvents( composite );
    ControlLCAUtil.processMenuDetect( composite );
    WidgetLCAUtil.processHelp( composite );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ScrolledComposite scrolledComposite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( scrolledComposite );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( scrolledComposite );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.writeChanges( composite );
    writeContent( composite );
    writeClipBounds( composite );
    // TODO [rh] initial positioning of the client-side scroll bar does not work
    writeBarSelection( composite );
    // [if] Order is important: writeScrollBars after writeBarSelection
    writeScrollBars( composite );
    writeSelectionListener( composite );
    writeShowFocusedControl( composite );
    WidgetLCAUtil.writeCustomVariant( composite );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.dispose();
  }

  ///////////////////////////////////
  // Helping methods to write changes

  private static void writeContent( ScrolledComposite composite ) throws IOException {
    Control content = composite.getContent();
    if( WidgetLCAUtil.hasChanged( composite, PROP_CONTENT, content, null ) ) {
      final JSWriter writer = JSWriter.getWriterFor( composite );
      final Object[] args = new Object[] { content };
      if( content != null ) {
        // defer call since content is rendered after composite
        WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( content );
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            writer.call( SET_CONTENT, args );
          }
        } );
      } else {
        writer.call( SET_CONTENT, args );
      }
    }
  }

  private static void writeScrollBars( ScrolledComposite composite ) throws IOException {
    boolean hasHBar = hasHScrollBar( composite );
    boolean hasVBar = hasVScrollBar( composite );
    boolean hasHChanged = WidgetLCAUtil.hasChanged( composite,
                                                    PROP_HAS_H_SCROLL_BAR,
                                                    Boolean.valueOf( hasHBar ),
                                                    Boolean.TRUE );
    boolean hasVChanged = WidgetLCAUtil.hasChanged( composite,
                                                    PROP_HAS_V_SCROLL_BAR,
                                                    Boolean.valueOf( hasVBar ),
                                                    Boolean.TRUE );
    if( hasHChanged || hasVChanged ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "scrollBarsVisible", new boolean[]{ hasHBar, hasVBar } );
    }
  }

  private static void writeBarSelection( ScrolledComposite composite ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( composite );
    Integer hBarSelection = getBarSelection( composite.getHorizontalBar() );
    if( hBarSelection != null ) {
      writer.set( PROP_H_BAR_SELECTION, "hBarSelection", hBarSelection, ZERO );
    }
    Integer vBarSelection = getBarSelection( composite.getVerticalBar() );
    if( vBarSelection != null ) {
      writer.set( PROP_V_BAR_SELECTION, "vBarSelection", vBarSelection, ZERO );
    }
  }

  private static void writeClipBounds( ScrolledComposite composite ) throws IOException {
    Rectangle bounds = composite.getBounds();
    if( WidgetLCAUtil.hasChanged( composite, PROP_BOUNDS, bounds, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "clipWidth", bounds.width );
      writer.set( "clipHeight", bounds.height );
    }
  }

  private static void writeSelectionListener( ScrolledComposite composite ) throws IOException {
    boolean hasListener = hasSelectionListener( composite );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( composite, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  private static void writeShowFocusedControl( ScrolledComposite composite ) throws IOException {
    Boolean newValue = Boolean.valueOf( composite.getShowFocusedControl() );
    String prop = PROP_SHOW_FOCUSED_CONTROL;
    if( WidgetLCAUtil.hasChanged( composite, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "showFocusedControl", newValue );
    }
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain scroll bar properties

  private static Integer getBarSelection( ScrollBar scrollBar ) {
    Integer result;
    if( scrollBar != null ) {
      result = new Integer( scrollBar.getSelection() );
    } else {
      result = null;
    }
    return result;
  }

  private static boolean hasSelectionListener( ScrolledComposite composite ) {
    boolean result = false;
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || SelectionEvent.hasListener( horizontalBar );
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      result = result || SelectionEvent.hasListener( verticalBar );
    }
    return result;
  }

  private static boolean hasHScrollBar( ScrolledComposite composite ) {
    ScrollBar horizontalBar = composite.getHorizontalBar();
    return horizontalBar != null && horizontalBar.getVisible();
  }

  private static boolean hasVScrollBar( ScrolledComposite composite ) {
    ScrollBar verticalBar = composite.getVerticalBar();
    return verticalBar != null && verticalBar.getVisible();
  }

  private static void processSelection( ScrollBar scrollBar ) {
    SelectionEvent evt = new SelectionEvent( scrollBar, null, SelectionEvent.WIDGET_SELECTED );
    evt.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    evt.processEvent();
  }
}