#!/bin/bash

cpuTemp0=$(cat /sys/class/thermal/thermal_zone0/temp)
cpuTemp1=$(($cpuTemp0/1000))
cpuTemp2=$(($cpuTemp0/100))
cpuTempM=$(($cpuTemp2 % $cpuTemp1))
cpuFreq=`cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq | sed 's/.\{3\}$//'`Mhz

echo CPU temp"="$cpuTemp1"."$cpuTempM"'C"
echo GPU $(/opt/vc/bin/vcgencmd measure_temp)
echo CPU frequency=$cpuFreq

#free -h -s 1

#top

#df -Bm 
