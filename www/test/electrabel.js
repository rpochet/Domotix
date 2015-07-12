var moment = require('moment');
var cradle = require('cradle');
var dbEvent = new(cradle.Connection)("192.168.1.4", 5984).database('events');

var rawData = {
	"start" : "2007-01-01T00:00:00.000Z",
	"end" : "2015-02-01T00:00:00.000Z",
	"consumptionDataByPeriodRangeDictionary" : [{
			"Key" : 0,
			"Value" : {
				"consumptionsByPeriod" : [{
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 2317
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 3560
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1167606000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 5877
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 2840
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 4943
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1199142000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 7783
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 3384
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 5004
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1230764400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 8388
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 4182
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 5288
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1262300400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 9470
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 4116
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 4560
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1293836400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 3,
						"value" : 8676
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 3793
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 5414
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1325372400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 11,
						"value" : 9207
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 4419
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 5986
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1356994800000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 12,
						"value" : 10405
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 3657
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 5089
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1388530800000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 11,
						"value" : 8746
					}
				],
				"cycles" : null,
				"periodUnit" : 0,
				"similarFamilyConsumptionsByPeriod" : [{
						"date" : "/Date(1167606000000+0100)/",
						"value" : 14960.6651156997
					}, {
						"date" : "/Date(1199142000000+0100)/",
						"value" : 15110.0617393292
					}, {
						"date" : "/Date(1230764400000+0100)/",
						"value" : 15025.581316139853
					}, {
						"date" : "/Date(1262300400000+0100)/",
						"value" : 15351.9026412986
					}, {
						"date" : "/Date(1293836400000+0100)/",
						"value" : 14442.7956965816
					}, {
						"date" : "/Date(1325372400000+0100)/",
						"value" : 14799.10866679118
					}, {
						"date" : "/Date(1356994800000+0100)/",
						"value" : 14915.54081239789
					}, {
						"date" : "/Date(1388530800000+0100)/",
						"value" : 15313.9832462847
					}, {
						"date" : "/Date(1420066800000+0100)/",
						"value" : 15313.9832462847
					}
				]
			}
		}, {
			"Key" : 1,
			"Value" : {
				"consumptionsByPeriod" : [{
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 156
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 197
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1167606000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 353
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 141
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 178
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1170284400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 319
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 156
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 197
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1172703600000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 353
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 151
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 191
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1175378400000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 342
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 156
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 197
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1177970400000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 353
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 151
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 191
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1180648800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 342
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 196
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 301
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1183240800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 497
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 245
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 427
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1185919200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 672
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 238
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 413
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1188597600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 651
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 245
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 427
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1191189600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 672
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 238
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 413
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1193871600000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 651
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 245
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 427
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1196463600000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 672
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 238
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 415
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1199142000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 653
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 223
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 388
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1201820400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 611
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 238
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 415
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1204326000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 653
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 402
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1207000800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 633
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 238
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 415
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1209592800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 653
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 402
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1212271200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 633
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 239
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 415
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1214863200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 654
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 243
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 424
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1217541600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 667
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 236
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 410
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1220220000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 646
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 243
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 424
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1222812000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 667
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 236
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 410
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1225494000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 646
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 243
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 424
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1228086000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 667
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 403
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1230764400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 634
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 209
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 364
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1233442800000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 573
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 403
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1235862000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 634
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 224
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 390
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1238536800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 614
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 403
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1241128800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 634
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 224
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 390
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1243807200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 614
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 231
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 403
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1246399200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 634
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 237
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 405
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1249077600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 642
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 385
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 454
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1251756000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 839
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 398
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 469
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1254348000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 867
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 385
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 454
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1257030000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 839
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 398
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 469
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1259622000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 867
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 379
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 446
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1262300400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 825
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 342
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 403
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1264978800000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 745
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 379
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 446
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1267398000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 825
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 366
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 432
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1270072800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 798
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 379
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 446
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1272664800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 825
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 366
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 432
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1275343200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 798
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 379
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 446
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1277935200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 825
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 330
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 452
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1280613600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 782
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 311
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 439
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1283292000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 750
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 321
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 453
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1285884000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 774
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 311
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 439
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1288566000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 750
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 321
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 453
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1291158000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 774
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 283
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 400
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1293836400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 683
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 256
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 362
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1296514800000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 618
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 283
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 400
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1298934000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 683
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 274
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 388
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1301608800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 662
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 283
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 400
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1304200800000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 683
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 274
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 388
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1306879200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 662
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 283
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 400
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1309471200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 683
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 283
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 400
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1312149600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 683
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 389
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 291
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1314828000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 680
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 493
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 224
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1317420000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 717
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 487
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 314
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1320102000000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 801
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 524
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 593
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1322694000000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 1117
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 526
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 611
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1325372400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 1137
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 471
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 616
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1328050800000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 1087
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 303
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 497
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1330556400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 0,
						"value" : 800
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 297
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 473
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1333231200000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 770
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 185
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 366
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1335823200000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 551
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 173
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 317
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1338501600000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 490
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 188
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 321
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1341093600000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 509
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 184
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 303
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1343772000000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 2,
						"value" : 487
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 188
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 286
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1346450400000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 474
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 264
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 442
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1349042400000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 706
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 466
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 550
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1351724400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 2,
						"value" : 1016
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 547
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 632
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1354316400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 1179
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 555
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 616
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1356994800000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 1171
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 575
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 774
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1359673200000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 2,
						"value" : 1349
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 562
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 774
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1362092400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 2,
						"value" : 1336
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 299
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 437
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1364767200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 736
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 308
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 451
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1367359200000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 759
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 299
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 437
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1370037600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 736
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 300
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 441
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1372629600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 741
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 139
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 169
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1375308000000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 2,
						"value" : 308
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 152
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 314
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1377986400000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 0,
						"value" : 466
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 258
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 412
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1380578400000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 2,
						"value" : 670
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 454
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 545
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1383260400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 999
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 519
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 616
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1385852400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 1135
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 517
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 607
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1388530800000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 1124
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 427
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 515
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1391209200000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 942
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 312
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 497
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1393628400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 809
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 240
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 418
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1396303200000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 658
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 212
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 389
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1398895200000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 601
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 161
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 294
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1401573600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 455
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 163
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 302
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1404165600000+0200)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 465
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 151
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 309
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1406844000000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 460
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 170
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 311
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1409522400000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 2,
						"value" : 481
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 272
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 440
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1412114400000+0200)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 712
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 404
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 464
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1414796400000+0100)/",
						"isAccurate" : true,
						"readingsCount" : 1,
						"value" : 868
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 628
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 543
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1417388400000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 1171
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 628
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 543
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 1,
						"date" : "/Date(1420066800000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 0,
						"value" : 1171
					}, {
						"consumptionsByMeter" : [{
								"consumptionsByRegister" : [{
										"key" : "Peak001",
										"registerNumber" : "001",
										"registerType" : 2,
										"value" : 20
									}, {
										"key" : "OffPeak002",
										"registerNumber" : "002",
										"registerType" : 3,
										"value" : 18
									}
								],
								"meterId" : "33590483",
								"meterLabel" : "0483"
							}
						],
						"coverage" : 0.03571428571428571,
						"date" : "/Date(1422745200000+0100)/",
						"isAccurate" : false,
						"readingsCount" : 1,
						"value" : 38
					}
				],
				"cycles" : [{
						"cycleIndex" : 0,
						"month" : "/Date(1167606000000+0100)/",
						"officialReadingsDates" : ["/Date(1167606000000+0100)/"]
					}, {
						"cycleIndex" : 1,
						"month" : "/Date(1183240800000+0200)/",
						"officialReadingsDates" : ["/Date(1184709600000+0200)/"]
					}, {
						"cycleIndex" : 2,
						"month" : "/Date(1214863200000+0200)/",
						"officialReadingsDates" : ["/Date(1217455200000+0200)/"]
					}, {
						"cycleIndex" : 3,
						"month" : "/Date(1249077600000+0200)/",
						"officialReadingsDates" : ["/Date(1251669600000+0200)/"]
					}, {
						"cycleIndex" : 4,
						"month" : "/Date(1280613600000+0200)/",
						"officialReadingsDates" : ["/Date(1281045600000+0200)/"]
					}, {
						"cycleIndex" : 5,
						"month" : "/Date(1314828000000+0200)/",
						"officialReadingsDates" : ["/Date(1315951200000+0200)/"]
					}, {
						"cycleIndex" : 6,
						"month" : "/Date(1343772000000+0200)/",
						"officialReadingsDates" : ["/Date(1344204000000+0200)/"]
					}, {
						"cycleIndex" : 7,
						"month" : "/Date(1375308000000+0200)/",
						"officialReadingsDates" : ["/Date(1375740000000+0200)/"]
					}, {
						"cycleIndex" : 8,
						"month" : "/Date(1406844000000+0200)/",
						"officialReadingsDates" : ["/Date(1407276000000+0200)/"]
					}
				],
				"periodUnit" : 1,
				"similarFamilyConsumptionsByPeriod" : [{
						"date" : "/Date(1167606000000+0100)/",
						"value" : 1530.8494366207
					}, {
						"date" : "/Date(1170284400000+0100)/",
						"value" : 1315.6394256964
					}, {
						"date" : "/Date(1172703600000+0100)/",
						"value" : 1354.0054018467
					}, {
						"date" : "/Date(1175378400000+0200)/",
						"value" : 1089.9462619196
					}, {
						"date" : "/Date(1177970400000+0200)/",
						"value" : 1103.5060863592
					}, {
						"date" : "/Date(1180648800000+0200)/",
						"value" : 1019.6336070028
					}, {
						"date" : "/Date(1183240800000+0200)/",
						"value" : 998.7681539433
					}, {
						"date" : "/Date(1185919200000+0200)/",
						"value" : 1034.0998748358
					}, {
						"date" : "/Date(1188597600000+0200)/",
						"value" : 1130.7273282841
					}, {
						"date" : "/Date(1191189600000+0200)/",
						"value" : 1304.206319418
					}, {
						"date" : "/Date(1193871600000+0100)/",
						"value" : 1430.7033295323
					}, {
						"date" : "/Date(1196463600000+0100)/",
						"value" : 1648.5798902408
					}, {
						"date" : "/Date(1199142000000+0100)/",
						"value" : 1528.8386156685535
					}, {
						"date" : "/Date(1201820400000+0100)/",
						"value" : 1366.250980601
					}, {
						"date" : "/Date(1204326000000+0100)/",
						"value" : 1412.3462924425
					}, {
						"date" : "/Date(1207000800000+0200)/",
						"value" : 1219.9078042627
					}, {
						"date" : "/Date(1209592800000+0200)/",
						"value" : 1083.8751769002463
					}, {
						"date" : "/Date(1212271200000+0200)/",
						"value" : 1031.5173128552
					}, {
						"date" : "/Date(1214863200000+0200)/",
						"value" : 1004.8735769419
					}, {
						"date" : "/Date(1217541600000+0200)/",
						"value" : 1030.8113381923
					}, {
						"date" : "/Date(1220220000000+0200)/",
						"value" : 1087.1469761983
					}, {
						"date" : "/Date(1222812000000+0200)/",
						"value" : 1281.2531851066
					}, {
						"date" : "/Date(1225494000000+0100)/",
						"value" : 1426.3261480661
					}, {
						"date" : "/Date(1228086000000+0100)/",
						"value" : 1636.9143320938
					}, {
						"date" : "/Date(1230764400000+0100)/",
						"value" : 1677.710708767
					}, {
						"date" : "/Date(1233442800000+0100)/",
						"value" : 1398.16974075
					}, {
						"date" : "/Date(1235862000000+0100)/",
						"value" : 1376.679793916
					}, {
						"date" : "/Date(1238536800000+0200)/",
						"value" : 1127.881993357
					}, {
						"date" : "/Date(1241128800000+0200)/",
						"value" : 1107.960158847
					}, {
						"date" : "/Date(1243807200000+0200)/",
						"value" : 1011.3130786491536
					}, {
						"date" : "/Date(1246399200000+0200)/",
						"value" : 999.462743033
					}, {
						"date" : "/Date(1249077600000+0200)/",
						"value" : 1033.30605215
					}, {
						"date" : "/Date(1251756000000+0200)/",
						"value" : 1064.052967692
					}, {
						"date" : "/Date(1254348000000+0200)/",
						"value" : 1269.687402799
					}, {
						"date" : "/Date(1257030000000+0100)/",
						"value" : 1343.642814411
					}, {
						"date" : "/Date(1259622000000+0100)/",
						"value" : 1615.7138617687
					}, {
						"date" : "/Date(1262300400000+0100)/",
						"value" : 1682.68311055
					}, {
						"date" : "/Date(1264978800000+0100)/",
						"value" : 1426.9398922128
					}, {
						"date" : "/Date(1267398000000+0100)/",
						"value" : 1379.3349197245
					}, {
						"date" : "/Date(1270072800000+0200)/",
						"value" : 1155.1338128885
					}, {
						"date" : "/Date(1272664800000+0200)/",
						"value" : 1161.554687847
					}, {
						"date" : "/Date(1275343200000+0200)/",
						"value" : 994.647054172
					}, {
						"date" : "/Date(1277935200000+0200)/",
						"value" : 984.052772165
					}, {
						"date" : "/Date(1280613600000+0200)/",
						"value" : 1027.975616179
					}, {
						"date" : "/Date(1283292000000+0200)/",
						"value" : 1086.927340012
					}, {
						"date" : "/Date(1285884000000+0200)/",
						"value" : 1286.6074690301
					}, {
						"date" : "/Date(1288566000000+0100)/",
						"value" : 1422.110704105
					}, {
						"date" : "/Date(1291158000000+0100)/",
						"value" : 1743.9352624127
					}, {
						"date" : "/Date(1293836400000+0100)/",
						"value" : 1574.6245784753
					}, {
						"date" : "/Date(1296514800000+0100)/",
						"value" : 1344.2436476372
					}, {
						"date" : "/Date(1298934000000+0100)/",
						"value" : 1312.4901641677
					}, {
						"date" : "/Date(1301608800000+0200)/",
						"value" : 1064.4858959716
					}, {
						"date" : "/Date(1304200800000+0200)/",
						"value" : 1039.3218513941
					}, {
						"date" : "/Date(1306879200000+0200)/",
						"value" : 987.8544553023
					}, {
						"date" : "/Date(1309471200000+0200)/",
						"value" : 992.6170734444
					}, {
						"date" : "/Date(1312149600000+0200)/",
						"value" : 1025.0854762871
					}, {
						"date" : "/Date(1314828000000+0200)/",
						"value" : 1042.7545909612
					}, {
						"date" : "/Date(1317420000000+0200)/",
						"value" : 1217.850080033
					}, {
						"date" : "/Date(1320102000000+0100)/",
						"value" : 1317.5555860223
					}, {
						"date" : "/Date(1322694000000+0100)/",
						"value" : 1523.9122968854
					}, {
						"date" : "/Date(1325372400000+0100)/",
						"value" : 1510.9103532438
					}, {
						"date" : "/Date(1328050800000+0100)/",
						"value" : 1509.9257838044
					}, {
						"date" : "/Date(1330556400000+0100)/",
						"value" : 1257.3384568035
					}, {
						"date" : "/Date(1333231200000+0200)/",
						"value" : 1207.64510721
					}, {
						"date" : "/Date(1335823200000+0200)/",
						"value" : 1086.098291481
					}, {
						"date" : "/Date(1338501600000+0200)/",
						"value" : 985.963066125
					}, {
						"date" : "/Date(1341093600000+0200)/",
						"value" : 960.4232313138547
					}, {
						"date" : "/Date(1343772000000+0200)/",
						"value" : 973.2324020644493
					}, {
						"date" : "/Date(1346450400000+0200)/",
						"value" : 1039.9484764075034
					}, {
						"date" : "/Date(1349042400000+0200)/",
						"value" : 1258.9947124855944
					}, {
						"date" : "/Date(1351724400000+0100)/",
						"value" : 1399.4414061396137
					}, {
						"date" : "/Date(1354316400000+0100)/",
						"value" : 1609.1873797124656
					}, {
						"date" : "/Date(1356994800000+0100)/",
						"value" : 1620.7571478923455
					}, {
						"date" : "/Date(1359673200000+0100)/",
						"value" : 1411.1563940153335
					}, {
						"date" : "/Date(1362092400000+0100)/",
						"value" : 1485.9847117180975
					}, {
						"date" : "/Date(1364767200000+0200)/",
						"value" : 1176.9344701533748
					}, {
						"date" : "/Date(1367359200000+0200)/",
						"value" : 1148.5238013824724
					}, {
						"date" : "/Date(1370037600000+0200)/",
						"value" : 1004.0205868561465
					}, {
						"date" : "/Date(1372629600000+0200)/",
						"value" : 951.3955125200877
					}, {
						"date" : "/Date(1375308000000+0200)/",
						"value" : 909.8841537362915
					}, {
						"date" : "/Date(1377986400000+0200)/",
						"value" : 1064.283017902044
					}, {
						"date" : "/Date(1380578400000+0200)/",
						"value" : 1216.4549674185007
					}, {
						"date" : "/Date(1383260400000+0100)/",
						"value" : 1405.3985624712805
					}, {
						"date" : "/Date(1385852400000+0100)/",
						"value" : 1520.7474863319155
					}, {
						"date" : "/Date(1388530800000+0100)/",
						"value" : 1577.9355529315
					}, {
						"date" : "/Date(1391209200000+0100)/",
						"value" : 1366.8124666257
					}, {
						"date" : "/Date(1393628400000+0100)/",
						"value" : 1383.3891791765
					}, {
						"date" : "/Date(1396303200000+0200)/",
						"value" : 1199.1910794774
					}, {
						"date" : "/Date(1398895200000+0200)/",
						"value" : 1143.7226774484
					}, {
						"date" : "/Date(1401573600000+0200)/",
						"value" : 1062.0274631598
					}, {
						"date" : "/Date(1404165600000+0200)/",
						"value" : 1064.2584226031
					}, {
						"date" : "/Date(1406844000000+0200)/",
						"value" : 1087.8135299269
					}, {
						"date" : "/Date(1409522400000+0200)/",
						"value" : 1142.0150396253
					}, {
						"date" : "/Date(1412114400000+0200)/",
						"value" : 1279.1284853808
					}, {
						"date" : "/Date(1414796400000+0100)/",
						"value" : 1420.0077555552
					}, {
						"date" : "/Date(1417388400000+0100)/",
						"value" : 1587.6815943741
					}, {
						"date" : "/Date(1420066800000+0100)/",
						"value" : 1577.9355529315
					}, {
						"date" : "/Date(1422745200000+0100)/",
						"value" : 1366.8124666257
					}
				]
			}
		}
	],
	"consumptionDataReadingsCountByYear" : [{
			"Key" : 2007,
			"Value" : 2
		}, {
			"Key" : 2008,
			"Value" : 1
		}, {
			"Key" : 2009,
			"Value" : 1
		}, {
			"Key" : 2010,
			"Value" : 1
		}, {
			"Key" : 2011,
			"Value" : 3
		}, {
			"Key" : 2012,
			"Value" : 11
		}, {
			"Key" : 2013,
			"Value" : 12
		}, {
			"Key" : 2014,
			"Value" : 11
		}, {
			"Key" : 2015,
			"Value" : 1
		}
	],
	"productionDataByPeriodRangeDictionary" : [{
			"Key" : 0,
			"Value" : {
				"consumptionsByPeriod" : [],
				"cycles" : null,
				"periodUnit" : 0,
				"similarFamilyConsumptionsByPeriod" : null
			}
		}, {
			"Key" : 1,
			"Value" : {
				"consumptionsByPeriod" : [],
				"cycles" : [{
						"cycleIndex" : 0,
						"month" : "/Date(1167606000000+0100)/",
						"officialReadingsDates" : ["/Date(1167606000000+0100)/"]
					}, {
						"cycleIndex" : 1,
						"month" : "/Date(1183240800000+0200)/",
						"officialReadingsDates" : ["/Date(1184709600000+0200)/"]
					}, {
						"cycleIndex" : 2,
						"month" : "/Date(1214863200000+0200)/",
						"officialReadingsDates" : ["/Date(1217455200000+0200)/"]
					}, {
						"cycleIndex" : 3,
						"month" : "/Date(1249077600000+0200)/",
						"officialReadingsDates" : ["/Date(1251669600000+0200)/"]
					}, {
						"cycleIndex" : 4,
						"month" : "/Date(1280613600000+0200)/",
						"officialReadingsDates" : ["/Date(1281045600000+0200)/"]
					}, {
						"cycleIndex" : 5,
						"month" : "/Date(1314828000000+0200)/",
						"officialReadingsDates" : ["/Date(1315951200000+0200)/"]
					}, {
						"cycleIndex" : 6,
						"month" : "/Date(1343772000000+0200)/",
						"officialReadingsDates" : ["/Date(1344204000000+0200)/"]
					}, {
						"cycleIndex" : 7,
						"month" : "/Date(1375308000000+0200)/",
						"officialReadingsDates" : ["/Date(1375740000000+0200)/"]
					}, {
						"cycleIndex" : 8,
						"month" : "/Date(1406844000000+0200)/",
						"officialReadingsDates" : ["/Date(1407276000000+0200)/"]
					}
				],
				"periodUnit" : 1,
				"similarFamilyConsumptionsByPeriod" : null
			}
		}
	]
};

var i = 1;
//for(i = 0; i < rawData.consumptionDataByPeriodRangeDictionary.length; i++) {
  consumptionsByPeriods = rawData.consumptionDataByPeriodRangeDictionary[i].Value.consumptionsByPeriod;
  console.log("----------------------------------------------------------------");
  if(consumptionsByPeriods.Key == 0) {
    console.log("By year: " + consumptionsByPeriods.length);
  } else {
    console.log("By month: " + consumptionsByPeriods.length);
  }
  for(j = 0; j < consumptionsByPeriods.length; j++) {
    consumptionsByPeriod = consumptionsByPeriods[j];
    dateTime = new Date(parseInt(consumptionsByPeriod.date.substring(6, consumptionsByPeriod.date.indexOf('+'))) + 3600000).toISOString();
    description = "isAccurate: " + consumptionsByPeriod.isAccurate + ", readingsCount: " + consumptionsByPeriod.readingsCount;
    for(k = 0; k < consumptionsByPeriod.consumptionsByMeter.length; k++) {
      consumptionsByMeter = consumptionsByPeriod.consumptionsByMeter[k];
      for(l = 0; l < consumptionsByMeter.consumptionsByRegister.length; l++) {
        consumptionsByRegister = consumptionsByMeter.consumptionsByRegister[l];
        if(consumptionsByRegister.registerType == 2) {
          subtype = "HP";
        } else if(consumptionsByRegister.registerType == 3) {
          subtype = "HC";
        } else {
          subtype = "X";
        }
        event = {
          "type": "ELEC",
          "subtype": subtype,
          "dateTime": dateTime,
          "value": consumptionsByRegister.value,
          "description": description
        }
                    
        dbEvent.save(event, function (err, res) {
          console.log(err);
          console.log(res);
        });
      }
    }
  }
//}