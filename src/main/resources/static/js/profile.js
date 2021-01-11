(function($) {
    // SETUP AJAX
    $.ajaxSetup({
        headers:
            {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')}
    });

    // FOLLOW BTN SUBMIT A FOLLOW REQUEST AJAX
    $( "#addFriend" ).click(function() {
        let formData = {"friendId" : userId}
        let url = "/users/follow";
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url,
            data: JSON.stringify(formData),
            dataType: 'json'
        });
        $('#addFriend').text("Following");
        $('#addFriend').prop("disabled", true);
    });

    const getMoreSuggestions = ()=>{
        $.ajax({'url': '/users/'+userId+'/matches'}).done(function (recipes) {
            $('#suggestionsTab').html(recipes);
            $('[data-toggle="tooltip"]').tooltip();
        });
    }

    // SUGGESTIONS AJAX
    $( "#suggestions" ).click(function() {
          getMoreSuggestions();
        $.post( '/users/'+userId+'/matches/update', function( data ) {
            userMatchCount = data;
        });
        $("#notification").html("");
    });

    // PANTRY AJAX
    let pantryInventory;
    let inventoryIngredients;
    const makePantryAjaxCall = ()=>{
        pantryInventory = [];
        inventoryIngredients = [];
        $.ajax({'url': '/api/users/'+userId+"/pantry"}).done(function (response) {
            response.pantryIngredients.forEach(function(item) {
                inventoryIngredients.push(item);
                pantryInventory.push(item.ingredient.name.toLowerCase());
            });
        });
    };

    // CREATE HTML FOR PANTRY TEMPLATE
    const renderPantry = ()=>{
        if( inventoryIngredients.length > 0 ){
            $("#ingredientsTable").removeClass("d-none");
        }else{
            if(!$("#ingredientsTable").hasClass("d-none")){
                $("#ingredientsTable").addClass("d-none")
            }
        }
        // SORT INGREDIENTS BY NAME
        inventoryIngredients.sort((a, b) => (a.ingredient.name > b.ingredient.name) ? 1 : -1);
        let pantryHtml = "";
        // ADD ROWS TO PANTRY TABLE
        inventoryIngredients.forEach((item)=>{
            pantryHtml += '<tr id="'+item.id+'">'+
                '<td class="green-text" type="button" data-toggle="modal" data-target="#ingredientModal">' + item.ingredient.name + '</td>'+
                '<td class="green-text"  data-amount-float="'+item.amount+'-'+item.unit+'"><span class="rational">' + item.amount + '</span><span> ' + item.unit+ '</span></td>'+
                '<td class="qty">'+
                '<span class="font-weight-bold red-text" type="button" data-toggle="modal" data-target="#deleteIngredientModal" style="font-size: 1.2em!important;">X</span>' +
                // '<span  type="button" data-toggle="modal" data-target="#ingredientModal" ><i class="fas fa-pencil-alt"></i></span>'+
                '</td>'+
                '</tr>'
            ;
        })
        // RENDER TEMPLATE
        $('#ingredientsBody').html(pantryHtml);
    }

    // ONCLICK EVENT FOR USER PANTRY
    $( "#userPantry" ).click(function() {
        // GET PANTRY INGREDIENTS
        makePantryAjaxCall();
        // RENDER INGREDIENTS
        setTimeout(function() {
            renderPantry();
            convertAllRationals();
        }, 500);
    });

    // AUTO-SEARCH INGREDIENTS FOR PANTRY
    $(document).on('keyup',"#ingredientSearch",function(){
        let searchResults = $('#searchResults');
        let searchInput = this.value.trim();
        /* dont display any suggestions if empty string */
        if(this.value === "") {
            searchResults.empty();
            /* if input string is already in pantry notify user */
        }else if(pantryInventory.includes(searchInput.toLowerCase())){
            searchResults.empty();
            searchResults.append(
                '<button id="ingredientInput" type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal" disabled>'+searchInput.toLowerCase()+' is in pantry</button>'
            );
        }
        /* create suggestions buttons that user can select */
        else{
            searchResults.empty();
            searchResults.append(
                '<button id="ingredientInput" type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal">'+searchInput.toLowerCase()+'</button>'
            );

            /* search existing ingredients list to auto suggest ingredients and if not in list allow user to select own input */
            ingredientsList.filter(function(ingredient){
                if(ingredient.toLowerCase() === searchInput.toLowerCase()){
                    $("#ingredientInput").addClass("d-none");
                }
                if (ingredient.toLowerCase().startsWith(searchInput.toLowerCase())) {
                    searchResults.append(
                        '<button type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal">'+ingredient+'</button>'
                    );
                }
            });
            searchResults.append("</div>");
        }
    });

    // USER PANTRY ADD NEW PANTRY INPUT CLICK
    $(document).on('click',"#showIngredientForm", function () {
        let input = $("#ingredientSearch");
        if(input.hasClass("d-none")){
            $(this).prop('value', 'close');
            input.removeClass("d-none");
        }
        else{
            $(this).prop('value', 'Add New Ingredient');
            $("#searchResults").empty();
            $("#ingredientSearch").val("")
            input.addClass("d-none");
        }
    })

    // INGREDIENT MODAL ON SHOW DYNAMIC INFO
    $('#ingredientModal').on('show.bs.modal', function (event) {
        let openModal = $(this);
        let button = $(event.relatedTarget); // Button that triggered the modal
        let tagType = button[0].tagName;
        let clickedIngredient; // Ingredient name to be used
        /* if tag type is span change ingredient form to an update form */
        if(tagType === "TD"){
            /* get data for update form from the tr element */
            let pantryIngId = button.parent()[0].getAttribute("id");
            let tableRow = button.parent()[0];
            let amountUnit = tableRow.firstChild.nextSibling.getAttribute("data-amount-float").split("-");
            let unit = amountUnit[1].split(" ")[0].toLowerCase();
            clickedIngredient = tableRow.firstChild.innerText;
            /* set form values for update */
            openModal.find('select').val(unit); // set unit select value
            openModal.find('.modal-title').text("Update " + clickedIngredient); // place ingredient name on title
            openModal.find('.modal-body input').val(clickedIngredient);
            count.val(parseFloat(amountUnit[0]));// set amount value
            $("#typeSubmit").html(
                '<input name="id" type="hidden" value="'+pantryIngId+'"/>'+
                '<button id="updateIngredient" type="submit" class="btn btn-green">Update</button>'
            );// set edit submit button
        }
        /* else ingredient form is a new ingredient form */
        else{
            clickedIngredient = button[0].innerHTML;
            $("#typeSubmit").html('<button id="addIngredient" type="submit" class="btn btn-green">Save</button>');
            openModal.find('.modal-title').text("Add "+ clickedIngredient +" to pantry");
            openModal.find('.modal-body input').val(clickedIngredient);
            count.val(1);
        }

    })

    // Plus Minus Count for Ingredient Form modal
    let count = $('#ingredientForm').find('input.count');
    $(document).on('click','#formQty .plus',function(){
        if (count.val() == 0) {
            count.val(0);
        }
        count.val(parseFloat(count.val()) + 1);
    });
    $(document).on('click','#formQty .minus',function(){
        count.val(parseFloat(count.val()) - 1);
        if (count.val() == 0) {
            count.val(1);
        }
    });

    // ON INGREDIENT MODAL SUBMIT POST REQUEST ADD INGREDIENT TO PANTRY
    $(document).on('click','#addIngredient',function(){
        $('#ingredientModal').modal('toggle');
        let form = $('#ingredientForm');
        let url = "/pantry/ingredient/new";
        let formData = JSON.stringify({
            "name" : form.find('input#ingredientName').val(),
            "amount": form.find('input.count').val(),
            "unit" : form.find('select  option:selected').text()
        });
        // CREATE POST REQUEST FOR INGREDIENT
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url,
            data: formData,
            dataType: "json",
        });
        // GET PANTRY INGREDIENTS AFTER CREATE
        setTimeout(function() {
            makePantryAjaxCall();
        }, 1000);
        // RENDER THE PANTRY INGREDIENTS
        setTimeout(function() {
            $("#searchResults").empty();
            $("#ingredientSearch").val("")
            renderPantry();
            getMoreSuggestions();
            convertAllRationals();
        }, 1500);
        // RENDER NOTIFICATION IF NEW SUGGESTIONS FROM NEW INGREDIENTS
        setTimeout(function () {
            updateNotification()
        },2000);
        return false;
    })

    // UPDATE INGREDIENT
    $(document).on('click','#updateIngredient',function(){
        $('#ingredientModal').modal('toggle');
        let form = $('#ingredientForm');
        let url = "/pantry/ingredient/edit";
        let formData = JSON.stringify({
            "id" : form.find('input[name=id]').val(),
            "amount": form.find('input.count').val(),
            "unit" : form.find('select  option:selected').text()
        });
        // MAKE UPDATE POST REQUEST FOR INGREDIENT
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: url,
            data: formData,
            dataType: "json",
        });
        // GET PANTRY INGREDIENTS AFTER UPDATE
        setTimeout(function() {
            makePantryAjaxCall();
        }, 1000);
        // RENDER THE PANTRY INGREDIENTS
        setTimeout(function() {
            renderPantry();
            convertAllRationals();

        }, 1500);
        return false;
    });

    // INGREDIENT MODAL ON SHOW DYNAMIC INFO
    $('#deleteIngredientModal').on('show.bs.modal', function (event) {
        let openModal = $(this);
        let button = $(event.relatedTarget); // Button that triggered the modal
        let clickedIngredient; // Ingredient name to be used
        /* get data for update form from the tr element */
        let pantryIngId = button.parent().parent()[0].getAttribute("id");
        let tableRow = button.parent().parent()[0];
        clickedIngredient = tableRow.firstChild.innerText;
        /* set form values for update */
        openModal.find('.modal-title').text("Remove " + clickedIngredient); // place ingredient name on title
        openModal.find('.modal-body input').val(pantryIngId);
    })

    // ON DELETE INGREDIENT MODAL SUBMIT REMOVE INGREDIENT FROM PANTRY
    $("#deleteIngredient").submit(function(){
        $('#deleteIngredientModal').modal('toggle');
        let form = $(this);
        let ingredientId = form.find('input[name=id]').val(); // GETS INGREDIENT ID
        let url = '/api/pantry-ingredients/'+ingredientId+'/delete';
        // MAKE DELETE REQUEST FOR INGREDIENT
        $.ajax({
            type: "POST",
            url: url,
            contentType: "application/json",
            dataType: 'json'
        });
        // AFTER DELETE GET PANTRY INGREDIENTS
        setTimeout(function () {
            makePantryAjaxCall();
        }, 1000);
        // RENDER THE PANTRY INGREDIENTS
        setTimeout(function () {
            renderPantry();
            convertAllRationals();
        }, 1500);
        return false;
    })

    $('[data-toggle="tooltip"]').tooltip();

    const verifyViewport = ()=>{
        let viewport = $(window).width();

        if ( viewport <= 759 ) {
            if($(".nav-tabs").children().length<5){
                $(".nav-tabs").children().each(function () {
                    $(this).removeClass("col-4")
                    $(this).addClass("col-3");
                })
            }
        }
        else {
            $(".nav-tabs").children().each(function () {
                $(this).addClass("col-4")
                $(this).removeClass("col-3");
            })
        };
    }
    $(window).ready(function() {
        verifyViewport();
    });
    $(window).resize(function(){
        verifyViewport();
    });

    $("#draftTab").click(function () {
        if($("#draftRecipes").hasClass("d-none")){
            $("#draftRecipes").removeClass("d-none");
            $("#publishedRecipes").addClass("d-none");
        }

    });
    $("#publishedTab").click(function () {
        if($("#publishedRecipes").hasClass("d-none")){
            $("#draftRecipes").addClass("d-none");
            $("#publishedRecipes").removeClass("d-none");
        }
    });

    $(".search-btn").click(function(){
        $(".input-box").toggleClass("active").focus;
        $(this).toggleClass("animate");
        $(".input-box").val("");
    });

    // NOTIFICATION
    let userMatchCount = parseInt($("#notification").data('internalid'));
    const updateNotification = ()=>{
        // the actual notification count
        let notificationCount = parseInt($("#notification").text());

        // the users current matching recipes
        let currentMatches = $(".suggestions-card").length;

        // if the user doesnt have notifcations update count
        if(isNaN(notificationCount)){
            notificationCount = currentMatches - userMatchCount;
        }else{
            let tempCount = currentMatches - userMatchCount;
            if(tempCount > notificationCount){
                notificationCount = tempCount;
            }
        }
        // render the count to html notfication
        $("#notification").html(notificationCount);
    }
})(jQuery);
