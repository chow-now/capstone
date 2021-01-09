(function($) {
   // REGISTER/LOGIN MODAL JS
    $.ajax({'url': '/register-form'}).done(function (form) {
        $('#regForm').html(form);
    });

    $( ".register-btn" ).click(function() {
        $.ajax({'url': '/register-form'}).done(function (form) {
            $('#regForm').html(form);
        });
    });

    $(".login-signup-toggle").click(function () {
        $("#loginModal").modal("toggle");
        $("#registerModal").modal("toggle");
    })

    // NAVBAR JS

    // used to shrink nav bar removing paddings and adding black background
    $('.navTrigger').click(function () {
        $(this).toggleClass('active');
        $("#mainListDiv").toggleClass("show_list");
        $("#mainListDiv").fadeIn();
        if($('body').css('overflow') == 'hidden'){
            $('body').css('overflow','auto');
        }else{
            $('body').css('overflow','hidden');
        }

    });
    // Hide Header on on scroll down
    let didScroll;
    let lastScrollTop = 0;
    let delta = 5;
    let navbarHeight = $('.main-nav').outerHeight();

    $(window).scroll(function() {
        didScroll = true;
        if (didScroll) {
            hasScrolled();
            didScroll = false;
        }
    });

    const hasScrolled = () => {
        let st = $(this).scrollTop();
        // Add bg to nav if past top else remove
        if(st > 50){
            $('.main-nav,.navTrigger').addClass('affix');
            $('.main-nav').css("position", "fixed");
            $('main').css("margin-top", "-25px");
        }else{
            $('.main-nav,.navTrigger').removeClass('affix');
            $('.main-nav').css("position", "relative");
            $('main').css("margin-top", "0");

        }
        // Make sure they scroll more than delta
        if(Math.abs(lastScrollTop - st) <= delta) return;
        // If they scrolled down and are past the navbar, add class .nav-up.
        if (st > lastScrollTop && st > navbarHeight){
            // Scroll Down
            $('header').hide();
        } else {
            // Scroll Up
            if(st + $(window).height() < $(document).height()) {
                $('header').show("slow");
                $('.main-nav, .nav-trigger').removeClass(".nav-padding");

            }
        }
        lastScrollTop = st;
    }

    console.log(window.location.href);

})(jQuery);