/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class TimerExecTask_Test extends TestCase {

  private TimerExecScheduler scheduler;
  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = spy( new Display() );
    scheduler = spy( new TimerExecScheduler( display ) );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreation() {
    Runnable runnable = mock( Runnable.class );

    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    assertEquals( runnable, task.getRunnable() );
  }

  public void testCreation_activatesServerPush() {
    Runnable runnable = mock( Runnable.class );

    new TimerExecTask( scheduler, runnable );

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  public void testRun_removesIselfFromScheduler() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    verify( scheduler ).removeTask( same( task ) );
  }

  public void testRun_addsRunnableToQueue() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    verify( display ).asyncExec( same( runnable ) );
  }

  public void testRun_doesNotAddRunnableWhenDisplayDisposed() {
    // Ensure that runnables that were added via timerExec are *not* executed on session shutdown
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );
    display.dispose();

    task.run();

    verify( display, times( 0 ) ).asyncExec( any( Runnable.class ) );
  }

  public void testRun_deactivatesServerPush() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  public void testCancel_deactivatesServerPush() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.cancel();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

}