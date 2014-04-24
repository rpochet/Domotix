exports.actions = function(req, res, ss){

  // Easily debug incoming requests here
  console.log(req);

  return {
  
    // Square a number and return the result
    square: function(num){
      res(num * num);
    },

    sendAlert: function(){
      ss.publish.all('systemAlert',
        'The server is about to be shut down'
      );
    }

  }
}
