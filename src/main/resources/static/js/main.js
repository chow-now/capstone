(function($) {
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
})(jQuery);