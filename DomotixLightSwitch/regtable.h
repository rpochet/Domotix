/**
 * regtable.h
 *
 * List of registers. Header file.
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 12 Nov 2013
 */
#ifndef _REGTABLE_H
#define _REGTABLE_H

#include "Arduino.h"
#include "register.h"
#include "commonregs.h"

const int REGI_TOUCH_START_IDX = 13;

/**
 * Register indexes
 */
DEFINE_REGINDEX_START()
  // First index here = 11
  REGI_SENSOR_DELAY,
  REGI_SENSOR,
  REGI_TOUCH_0,
  REGI_TOUCH_1,
  REGI_TOUCH_2,
  REGI_TOUCH_3,
  REGI_TOUCH_4,
  REGI_TOUCH_5,
  REGI_TOUCH_6,
  REGI_TOUCH_7
DEFINE_REGINDEX_END()

#endif
