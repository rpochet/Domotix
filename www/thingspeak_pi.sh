#!/bin/bash

# Get various system parameters

# 1. CPU Temperature in 1/1000's degree C
d1=$( cat /sys/devices/virtual/thermal/thermal_zone0/temp )
cputemp=field3=$( echo $d1 | sed 's/\([0-9]\{2\}\)\([0-9]\{3\}\)/\1.\2/' )

# 2. Uptime in Seconds
up=$( cat /proc/uptime ) ;# Get uptime values in seconds
uptime=field4=${up%%\.*} ;# Remove all except the integer part of the first value

# 3. Memory
mem=$( free | sed -n "s/^Mem: *[0-9]* *\([0-9]*\) *\([0-9]*\) .*$/field5=\1\&field6=\2/p" )

# 4. Disk
disk=$( df -k / | tail -1 | awk '{print "&field7="substr($5, 0, length($5))}' )

# Add any more values here..


# Push to server
wget -q "https://api.thingspeak.com/update?api_key=CX3JT3EGVNMNWDLX&$cputemp&$uptime&$mem&$disk"