$(function() {
    //initialise modals
    var modelOpen = false;
    var needToUpdate = false;
    var lastAlertData;
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
        $(".favOutput").mCustomScrollbar();
    });


    //polling for alerts
    var alertPollSeconds = 20;
    var newsPollSeconds = 35;


    (function pollForAlerts() {
        console.log('polling for alerts');
        var startTime = new Date().getTime();
        var responseTime;
        $.get('alertServlet', function(data, textStatus, xhr) {
                responseTime = new Date().getTime() - startTime;
        
               
                //if there is a valid message
                if (data) {
                    //outputMessage('helper', data);

                    if (data != lastAlertData) {
                        var parsedAlerts = jQuery.parseHTML(data);
                        var favCompanyCard = $(parsedAlerts[0]);


                        // favCompanyCard.find('.card-content').append(parsedAlerts[0]);
                        // parsedAlerts[0].textContent.split('/').join(' / ');
                        // console.log(parsedAlerts[0].textContent.split('/').join(' / '));

                        var favSectorCard = $('<div>');



                        favSectorCard.append(parsedAlerts[1]);
                        favSectorCard.append(parsedAlerts[2]);
                        favSectorCard.append(parsedAlerts[3]);
                        favSectorCard.append(parsedAlerts[4]);
                        favSectorCard.append(parsedAlerts[5]);
                        favSectorCard.append(parsedAlerts[6]);
                        favSectorCard = favSectorCard.contents().unwrap();


                        $('.tableholder1, .tableholder2').fadeOut(1000, function() {
                            console.log("pasting ");
                            console.log();
                             $('.tableholder2').html(favSectorCard);
                            $('.tableholder1').html(favCompanyCard);
                           
                            $(this).text().split('/').join(' / ');
                            $("table tr td").each(function() {

                                $(this).text($(this).text().split('/').join(' / '));
                                $(this).text($(this).text().split('(').join(' ('));

                                if ($(this).text().charAt(0) == '+') {
                                    console.log('bingo');
                                    $(this).addClass('myGreen');
                                }
                                if ($(this).text().charAt(0) == '-') {
                                    console.log('bingo');
                                    $(this).addClass('red-text');
                                }
                            });
                           $('.tableHolder1, .tableHolder2').fadeIn(1500, function() {

                           });

                        });
                        // $('.tableHolder2').fadeOut('1000').html(favSectorCard);



                        // $('.favCard .card-content').html(title);
                        //  $('.favCard .card-content').append(parsedAlerts[0]);
                        // title= '<span class="card-title activator white-text text-darken-4">Favourite Sectors<i class="material-icons right">more_vert</i></span>';


                        console.log(parsedAlerts);
                    } else {
                        console.log('alert favourites are the same so nothing was done');
                    }
                    lastAlertData = data;

                } //if data end
                setTimeout(pollForAlerts, 1000 * alertPollSeconds);
            }) //callback end
            .fail(function() {
                responseTime = new Date().getTime() - startTime;
                console.log('request failed');
                setTimeout(pollForAlerts, 5000);
            })
            .always(function() { //this runs regardless of whether the request was succesful or not
                console.log('alert response retrieved after ' + responseTime / 1000 + ' seconds');

            }); //callback end

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

                    var colourClass = 'myRed';

                    if (parseFloat(json[i][2]) > 50) {
                        colourClass = 'myGreen';
                    }

                    newsCards +=
                        '<div class="card">' +
                        '<div class="card-content text_125rem">' +
                        '<span class=" activator white-text text-darken-4">' + json[i][0] + '</span>' +
                        '</div>' +
                        // '<div class="card-reveal">' +
                        //     '<span class="card-title black-text ">Card Title<i class="material-icons right">close</i></span>' +
                        //     '<p>'+json[i][2]+'</p>' +
                        // '</div>' +
                        '<div class="card-action text_125rem">' +
                        '<a class="purple-text text-accent-1" href="#modal' + i + '">Summary</a>' +
                        '<a class="purple-text text-accent-1" href="' + json[i][1] + '">Go to Article</a>' +
                        '<span class="sentimentText ' + colourClass + '">SMA: ' + json[i][2] + '</span>' +
                        '</div>' +
                        '</div>';

                    modals +=
                        '<!-- Modal Structure -->' +
                        '<div id="modal' + i + '" class="modal" data-article-src="' + json[i][1] + '"> ' +
                        '<div class="modal-content">' +
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
                    console.log('printing');
                    console.log($('.sentimentText').val());
                } else {
                    console.log('tried to update while a modal was open');
                    needToUpdate = true;
                }

                //function that allows every modal with the the class "modal" to open open when something calls its href, and tells them how to act
                (function initialiseModal() {
                    $('.modal').modal({
                        ready: function(modal, trigger) { //when a modal is opened, find out the number its id and select the iframe with the same number
                            var iframeId = 'iframe_' + modal.attr('id').slice(5);
                            $('#' + iframeId).attr("src", modal.attr('data-article-src')); //using the page url I embeded in data-article-src attribute of the modal, and at the src to iframe, thus loading the iframe article

                            modelOpen = true; //tell the world the modal is open

                        },
                        complete: function() {
                            modelOpen = false; //when closed tell the world the modal is closed
                            console.log('closing modal');
                            if (needToUpdate) { //if the news articles tried to update while the modal was open, do the updating 
                                needToUpdate = false;
                                $('.newsOutput .mCSB_container').html(newsCards + modals); //update the headlines and modals
                                console.log('updating on modal close');
                                initialiseModal(); //these new modals will need to be initialised with this very function so they too can initialise future modals, hence why the .modal function is wrapped in this closure. 
                            }

                        }
                    });
                }());

                console.log(parseFloat($('.sentimentText').val()));
                console.log($('.sentimentText').val());
                if (parseFloat($('.sentimentText').val()) < 0.5) {
                    $('.sentimentText').addClass('red-text')
                }
                setTimeout(pollForNews, 1000 * newsPollSeconds);
            })
            .fail(function() {
                responseTime = new Date().getTime() - startTime;
                console.log('request failed');
                setTimeout(pollForNews, 5000);
            })
            .always(function() { //this runs regardless of whether the request was succesful or not
                console.log('news retrieved after ' + responseTime / 1000 + ' seconds');

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
                /*optional stuff to do after success 
                 */

                console.log(data);
                $('.loading').fadeOut('slow', function() {
                    $(this).remove();
                    outputMessage('helper', data);

                });

            });

            var loader = '<div class="spinner">' +
              '<div class="cube1"></div>' +
  '<div class="cube2"></div>' +
'</div>';
            console.log(loader);
            outputMessage('loading', loader);
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

        newMessage.show('slow', function() {});
        $('.output').mCustomScrollbar('scrollTo', 'bottom');

        // $(".output .mCSB_container").stop().animate({ scrollTop: $(".output")[0].scrollHeight }, 1000);



    }
});