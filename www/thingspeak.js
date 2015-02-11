var c = require('coffee-script'),
    ts = require('server/plugins/thingspeaksub'),
    log4js = require('log4js');
log4js.configure('config/log4js_configuration.json', {});
var logger = log4js.getLogger(__filename.split('/').pop(-1).split('.')[0]);


