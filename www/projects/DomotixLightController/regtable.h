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
 * EEPROM addresses
 */
#define NVOLAT_CONFIG_SENSOR_DELAY   NVOLAT_FIRST_CUSTOM
#define NVOLAT_CONFIG_PULSE_WIDTH    NVOLAT_CONFIG_SENSOR_DELAY + 2
#define NVOLAT_CONFIG_OUTPUTS        NVOLAT_CONFIG_PULSE_WIDTH + 2

/**
 * Register indexes
 */
DEFINE_REGINDEX_START()
  // First index here = 11
  REGI_SENSOR_DELAY,
  REGI_SENSOR,
  REGI_PULSE_WIDTH,
  REGI_OUTPUTS
DEFINE_REGINDEX_END()

#endif
