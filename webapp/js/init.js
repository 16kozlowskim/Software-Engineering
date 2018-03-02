$(function() {


//polling for alerts
var alertPollSeconds = 5;
var newsPollSeconds = 5;


(function pollForAlerts() {
    $.get('alertServlet', function(data, textStatus, xhr) {
    console.log(data);
    //if there is a valid message
        if(data) {
            outputMessage('helper',data);
    } 
    setTimeout(pollForAlerts, 1000*alertPollSeconds);
  });
      
}());

//polling for news and stuff 

(function pollForNews() {
    $.get('newsServlet', function(data, textStatus, xhr) {
    /*optional stuff to do after success */
    console.log(data);
    setTimeout(pollForNews, 1000*newsPollSeconds);
  });
      
}());

//when enter is pressed on the input
$( ".queryForm" ).submit(function( event ) {
  console.log( "form submit intercepted" );
  var inputText = $('input[name="query"]').val();
  if(inputText.length > 0){
  $('input[name="query"]').val("");
      outputMessage('user',inputText);
    $.post('ai', {query: inputText}, function(data, textStatus, xhr) {
      /*optional stuff to do after success */
      console.log(data);
      outputMessage('helper',data);
    });
    //stop it doing the default behaviour
  event.preventDefault();
} else {
  console.log("empty input");
}
});
 
 //when the button is clicked
  $('.postMessage').click(function(event) {
    console.log("button clicked");
    var inputText = $('input[name="query"]').val();
    console.log(inputText.length);
    if(inputText.length > 0){
    $('input[name="query"]').val("");
    outputMessage('user',inputText);
    $.post('ai', {query: inputText}, function(data, textStatus, xhr) {
      /*optional stuff to do after success */
      console.log(data);
      outputMessage('helper',data);
    });
  } else {
    console.log("empty input");
  }
  });

  function outputMessage(sender,message) {
    var newMessage = $('<div>', {
      "class": 'col s12'
    }).append(
    $('<div>',{
      "class": 'chatLine '+sender
    }).html(message)).hide();

    $('.output').append(newMessage);
    newMessage.show('slow');
  }
});