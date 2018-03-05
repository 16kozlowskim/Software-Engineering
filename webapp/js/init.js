$(function() {
    //initialise modals
    var modelOpen = false;
    var needToUpdate = false;
    $('.modal').modal({
        ready: function(modal, trigger) {
            var iframeId = 'iframe_' + modal.attr('id').slice(5);
            $('#' + iframeId).attr("src", modal.attr('data-article-src'));

        }
    });
    //initialise scrollbars
    $(window).on("load", function() {
        console.log('loaded');
        $(".output").mCustomScrollbar();
        $(".newsOutput").mCustomScrollbar();
    });


    //polling for alerts
    var alertPollSeconds = 64;
    var newsPollSeconds = 30;


    (function pollForAlerts() {
        $.get('alertServlet', function(data, textStatus, xhr) {
            console.log(data);
            //if there is a valid message
            if (data) {
                outputMessage('helper', data);
            }
            setTimeout(pollForAlerts, 1000 * alertPollSeconds);
        });

    }());

    //polling for news and stuff 

    (function pollForNews() {
        console.log('sending request for news');
        var startTime = new Date().getTime();
        var responseTime;
        $.getJSON('newsServlet', { param1: 'value1' }, function(json, textStatus) {
                /*optional stuff to do after success */
                responseTime = new Date().getTime() - startTime;

                console.log(json);
                var newsCards = '';
                var modals = '';
                for (var i = 0; i < json.length; i++) {

                    newsCards +=
                        '<div class="card">' +
                        '<div class="card-content">' +
                        '<span class=" activator white-text text-darken-4">' + json[i][0] + '<i class="material-icons right">more_vert</i></span>' +
                        '</div>' +
                        // '<div class="card-reveal">' +
                        //     '<span class="card-title black-text ">Card Title<i class="material-icons right">close</i></span>' +
                        //     '<p>'+json[i][2]+'</p>' +
                        // '</div>' +
                        '<div class="card-action">' +
                        '<a class="purple-text text-accent-1" href="#modal' + i + '">Summary</a>' +
                        '<a class="purple-text text-accent-1" href="' + json[i][1] + '">Go to Article</a>' +
                        '</div>' +
                        '</div>';

                    modals +=
                        '<!-- Modal Structure -->' +
                        '<div id="modal' + i + '" class="modal" data-article-src="' + json[i][1] + '"> ' +
                        '<div class="modal-content">' +
                        '<h4>Modal Header</h4>' +
                        //      '<p>'+json[i][2]+'</p>' + summary is being scrapped
                        '<div class="box"><iframe id="iframe_' + i + '"" src="" width = "95%" height = "80%"  sandbox="allow-forms allow-scripts" ></iframe></div>' +
                        '</div>' +
                        '<div class="modal-footer">' +
                        '<a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Read More!</a>' +
                        '</div>' +
                        '</div>';
                }
                if (!modelOpen) {
                    console.log('updated news');
                    $('.newsOutput .mCSB_container').hide().html(newsCards + modals).fadeIn('slow');
                } else {
                  console.log('tried to update while a modal was open');
                  needToUpdate = true;
                }

                //function that allows every modal with the the class "modal" to open open when something calls its href, and tells them how to act
               (function initialiseModal() {
                $('.modal').modal({
                    ready: function(modal, trigger) {   //when a modal is opened, find out the number its id and select the iframe with the same number
                        var iframeId = 'iframe_' + modal.attr('id').slice(5);
                        $('#' + iframeId).attr("src", modal.attr('data-article-src'));  //using the page url I embeded in data-article-src attribute of the modal, and at the src to iframe, thus loading the iframe article

                        modelOpen = true; //tell the world the modal is open

                    },complete: function() {
                      modelOpen = false;    //when closed tell the world the modal is closed
                      console.log('closing modal');
                      if(needToUpdate) {    //if the news articles tried to update while the modal was open, do the updating 
                      needToUpdate = false;  
                      $('.newsOutput .mCSB_container').html(newsCards + modals); //update the headlines and modals
                      console.log('updating on modal close');
                      initialiseModal();    //these new modals will need to be initialised with this very function so they too can initialise future modals, hence why the .modal function is wrapped in this closure. 
                      }
                    
                  }
                });
              }());



            })
            .fail(function() {
                responseTime = new Date().getTime() - startTime;
                console.log('request failed');
            })
            .always(function() { //this runs regardless of whether the request was succesful or not
                console.log('news retrieved after ' + responseTime / 1000 + ' seconds');
                setTimeout(pollForNews, 1000 * newsPollSeconds);
            });
          

    }());

    //when enter is pressed on the input
    $(".queryForm").submit(function(event) {
        event.preventDefault();
        console.log("form submit intercepted");
        var inputText = $('input[name="query"]').val();
        if (inputText.length > 0) {
            $('input[name="query"]').val("");
            outputMessage('user', inputText);
            $.post('ai', { query: inputText }, function(data, textStatus, xhr) {
                /*optional stuff to do after success */
                console.log(data);
                outputMessage('helper', data);
            });
            //stop it doing the default behaviour

        } else {
            console.log("empty input");
        }
    });

    //when the button is clicked
    $('.postMessage').click(function(event) {

        console.log("button clicked");
        // var inputText = $('input[name="query"]').val();
        // console.log(inputText.length);
        // if (inputText.length > 0) {
        //     $('input[name="query"]').val("");
        //     outputMessage('user', inputText);
        //     $.post('ai', { query: inputText }, function(data, textStatus, xhr) {
        //         /*optional stuff to do after success */
        //         console.log(data);
        //         outputMessage('helper', data);
        //     });
        // } else {
        //     console.log("empty input");
        // }
    });

    function outputMessage(sender, message) {
        console.log('outputting message');
        var newMessage = $('<div>', {
            "class": 'col s12'
        }).append(
            $('<div>', {
                "class": 'chatLine ' + sender
            }).html(message)).hide();

        $('.output .mCSB_container').append(newMessage);
        newMessage.show('slow');

        $(".output").stop().animate({ scrollTop: $(".output")[0].scrollHeight }, 1000);

    }
});