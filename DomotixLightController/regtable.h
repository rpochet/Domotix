/**
 * regtable.h
 *
 * List of registers. Header file.
 *
 * Product name: DomotixLightController (Domotix light controller with temperature and pressure sensor)
 * Author: Pochet Romuald
 * Creation date: 04 Nov 2013
 */
#ifndef _REGTABLE_H
#define _REGTABLE_H

#include "Arduino.h"
#include "register.h"
#include "commonregs.h"
/**
 * Register indexes
 */
DEFINE_REGINDEX_START()
  // First index here = 11
  REGI_SENSOR_DELAY,
  REGI_SENSOR,
  REGI_PULSE_WIDTH,
  REGI_OUTPUT0,
  REGI_OUTPUT1,
  REGI_OUTPUT2,
  REGI_OUTPUT3,
  REGI_OUTPUT4,
  REGI_OUTPUT5,
  REGI_OUTPUT6,
  REGI_OUTPUT7,
  REGI_OUTPUT8,
  REGI_OUTPUT9,
  REGI_OUTPUT10,
  REGI_OUTPUT11,
  REGI_OUTPUT12,
  REGI_OUTPUT13,
  REGI_OUTPUT14,
  REGI_OUTPUT15,
  REGI_OUTPUT16,
  REGI_OUTPUT17,
  REGI_OUTPUT18,
  REGI_OUTPUT19,
  REGI_OUTPUT20,
  REGI_OUTPUT21,
  REGI_OUTPUT22,
  REGI_OUTPUT23,
  REGI_OUTPUT24/*,
  REGI_OUTPUT25,
  REGI_OUTPUT26,
  REGI_OUTPUT27,
  REGI_OUTPUT28,
  REGI_OUTPUT29,
  REGI_OUTPUT30,
  REGI_OUTPUT31*/
DEFINE_REGINDEX_END()

#endif
