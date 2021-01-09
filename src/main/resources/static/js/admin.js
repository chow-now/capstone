$(document).ready(function() {
    $('table.display').DataTable();
} );
$('[data-toggle="tooltip"]').tooltip();

const verifyViewport = ()=>{
    let viewport = $(window).width();

    if ( viewport <= 759 ) {
        $("#myTable2").addClass("table-responsive");
    }
    else {
        $("#myTable2").removeClass("table-responsive");
    }
}
$(window).ready(function() {
    verifyViewport();
});
$(window).resize(function(){
    verifyViewport();
});
