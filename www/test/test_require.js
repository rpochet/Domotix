var fs = require("fs");
var name = "/home/pi/panStamp/domotix/test/zmq2";

// checks if module is available to load
var isModuleAvailableSync = function(moduleName)
{
	var found = false;
    var dirSeparator = require("path").sep

    console.log(module.paths);

    // scan each module.paths. If there exists
    // node_modules/moduleName then
    // return true. Otherwise return false.
    module.paths.forEach(function(nodeModulesPath)
    {
        if(fs.existsSync(nodeModulesPath + dirSeparator + moduleName) === true)
        {
            found = true;
			console.log(moduleName + " exists in " + nodeModulesPath);
            return false; // break forEach
        }
    });

	if(!found) 
	{
    	console.error(name + " is not found");
	}
}

isModuleAvailableSync(name);

try {
    console.log(require.resolve(name));
} catch(e) {
    console.error(name + " is not found");
    process.exit(e.code);
}