let y = $(window).scrollTop();
$(".scroll-btn").click(function() {
    $("html, body").animate({ scrollTop: y + 55 }, 600)
});

window.onscroll = function() {
    scrollFunction()
};

function scrollFunction() {
    if (document.body.scrollTop > 50 || document.documentElement.scrollTop > 50) {
        $("#chowText").addClass("modify-size");
        $("#favoritesContainer").addClass("modify-position");
        $(".scroll-btn").hide(600);
        $("#headerSlide").hide();
    } else {
        $("#chowText").removeClass("modify-size",{duration:600});
        $("#favoritesContainer").removeClass("modify-position",{duration:1200})
        $(".scroll-btn").show(600);
        $("#headerSlide").show();
        $("#showRegistration").addClass("toggle-reg")
    }
}

const target = document.querySelector('#showRegistration');
document.addEventListener('scroll', () => {
    if (window.scrollY >= target.getBoundingClientRect().top) {
        if($("#showRegistration").hasClass("toggle-reg")){
            $("#showRegistration").removeClass("toggle-reg");
            setTimeout(function() {
                $("#registerModal").modal("toggle");
            }, 1000);
        }
    }
})