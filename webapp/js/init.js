$(function() {
 
  $('.postMessage').click(function(event) {
    outputMessage('user',$('input[name="query"]').val());
    $.post('ai', {query: $('input[name="query"]').val()}, function(data, textStatus, xhr) {
      /*optional stuff to do after success */
      console.log(data);
      outputMessage('helper',data);
    });

  });

  function outputMessage(sender,message) {
    var newMessage = $('<div>', {
      "class": 'col s12'
    }).append(
    $('<div>',{
      "class": 'chatLine '+sender
    }).text(message)).hide();

    $('.output').append(newMessage);
    newMessage.show('slow');
  }
});