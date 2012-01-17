/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define("qx.event.type.DomEvent",
{
  extend : qx.event.type.Event,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vType, vDomEvent, vDomTarget, vTarget, vOriginalTarget)
  {
    this.base(arguments, vType);

    this.setDomEvent(vDomEvent);
    this.setDomTarget(vDomTarget);

    this.setTarget(vTarget);
    this.setOriginalTarget(vOriginalTarget);
  },




  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {

    /** {int} The modifier mask for the shift key. */
    SHIFT_MASK : 1,

    /** {int} The modifier mask for the control key. */
    CTRL_MASK  : 2,

    /** {int} The modifier mask for the alt key. */
    ALT_MASK   : 4,

    /** {int} The modifier mask for the meta key (e.g. apple key on Macs). */
    META_MASK  : 8
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    bubbles :
    {
      _fast        : true,
      defaultValue : true,
      noCompute    : true
    },

    propagationStopped :
    {
      _fast        : true,
      defaultValue : false,
      noCompute    : true
    },

    domEvent :
    {
      _fast       : true,
      setOnlyOnce : true,
      noCompute   : true
    },

    domTarget :
    {
      _fast       : true,
      setOnlyOnce : true,
      noCompute   : true
    },


    /**
     * The modifiers. A mask of the pressed modifier keys. This is an OR-combination of
     * {@link #SHIFT_MASK}, {@link #CTRL_MASK}, {@link #ALT_MASK} and {@link #META_MASK}.
     */
    modifiers :
    {
      _cached      : true,
      defaultValue : null
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * property computer
     *
     * @type member
     * @return {var} TODOC
     */
    _computeModifiers : function()
    {
      var mask = 0;
      var evt = this.getDomEvent();
      if (evt.shiftKey) mask |= qx.event.type.DomEvent.SHIFT_MASK;
      if (evt.ctrlKey) mask |= qx.event.type.DomEvent.CTRL_MASK;
      if (evt.altKey) mask |= qx.event.type.DomEvent.ALT_MASK;
      if (evt.metaKey) mask |= qx.event.type.DomEvent.META_MASK;
      return mask;
    },




    /*
    ---------------------------------------------------------------------------
      SPECIAL KEY SUPPORT
    ---------------------------------------------------------------------------
    */

    /**
     * Returns whether the the ctrl key is pressed.
     *
     * @type member
     * @return {Boolean} whether the the ctrl key is pressed.
     */
    isCtrlPressed : function() {
      return this.getDomEvent().ctrlKey;
    },


    /**
     * Returns whether the the shift key is pressed.
     *
     * @type member
     * @return {Boolean} whether the the shift key is pressed.
     */
    isShiftPressed : function() {
      return this.getDomEvent().shiftKey;
    },


    /**
     * Returns whether the the alt key is pressed.
     *
     * @type member
     * @return {Boolean} whether the the alt key is pressed.
     */
    isAltPressed : function() {
      return this.getDomEvent().altKey;
    },


    /**
     * Returns whether the the meta key is pressed.
     *
     * @type member
     * @return {Boolean} whether the the meta key is pressed.
     */
    isMetaPressed : function() {
      return this.getDomEvent().metaKey;
    },


    /**
     * Returns whether the ctrl key or (on the Mac) the command key is pressed.
     *
     * @type member
     * @return {Boolean} <code>true</code> if the command key is pressed on the Mac
     *           or the ctrl key is pressed on another system.
     */
    isCtrlOrCommandPressed : function()
    {
      if( org.eclipse.rwt.Client.getPlatform() === "mac" ) {
        return this.getDomEvent().metaKey;
      } else {
        return this.getDomEvent().ctrlKey;
      }
    },



    /*
    ---------------------------------------------------------------------------
      PREVENT DEFAULT
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @param vValue {var} TODOC
     * @return {var} TODOC
     * @signature function(vValue)
     */
    setDefaultPrevented : function( vValue ) {
      if( !vValue ) {
        throw new Error( "It is not possible to set preventDefault to false if it was true before!" );
      }
      org.eclipse.rwt.EventHandlerUtil.stopDomEvent( this.getDomEvent() );
      this.base(arguments, vValue);
    }

  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeFields("_valueDomEvent", "_valueDomTarget");
  }
});
