serialport = require("serialport");

serialPort = new serialport.SerialPort("/dev/ttyAMA0", {
    baudrate: 38400,
    parser: serialport.parsers.readline("\r\n")
});

serialPort.on("open", function() {
    console.log("Port " + this.path + " opened");
    
	//serialPort.write("+++");

    /*setTimeout(function() {
        console.log("Sending 0201000001020b\r");
        serialPort.write("0201000001020b\r");
    }, 5000.0);*/
});

serialPort.on("data", function(data) {
    console.log("Received: " + data);
});

serialPort.on("close", function() {
    console.log("Port closed");
});

serialPort.on("error", function(err) {
    console.log("Error " + err);
});

/*setTimeout(function() {
    serialPort.close();
}, 50000.0);*/

