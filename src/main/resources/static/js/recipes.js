(function ($) {

    // SETUP AJAX
    $.ajaxSetup({
        headers:
            {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')}
    });

    let recipeId = parseInt(document.getElementById('recipe-id').innerText);


    function readURL(input) {
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function (e) {
                $('#image_preview').show().attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]);
        }
    }

    $("#recipePicture").on('change', function () {
        $("#imageUpload").show();
        readURL(this);
        $('#imageUpload').on('click', function (e) {
            e.preventDefault();

            console.log("upload clicked");
            let data = new FormData($("#uploadImageForm")[0]);
            console.log(data);
            $.ajax({
                url: "/recipes/" + recipeId + "/upload",
                method: "POST",
                enctype: 'multipart/form-data',
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                success: function () {

                    location.reload();
                },
                error: function () {

                }
            });
        });
    });

    let imageId = document.getElementById('image-id');
    console.log(imageId)

    let imageUrl = document.getElementById('image-url');
    console.log(imageUrl)


    // $("#deleteImage" + imageId).on('click', function () {
    //     console.log($("#deleteImage" + imageId).val());
    //     $.ajax({
    //         url: "/recipes/image/" + imageId + "/delete",
    //         method: "POST",
    //         data: {imageUrl},
    //         dataType: "text",
    //
    //         success: function () {
    //             location.reload();
    //         },
    //         error: function () {
    //             location.reload();
    //         }
    //     });
    // });

    $(".delete-image").on('click', function () {
        console.log(this);
        $.post("/recipes/image/" + this.id + "/delete", function (data) {
            console.log(data);
        })
        $(this).parent().hide();

    });


    // // PUBLISH RECIPE
    $("#recipePublish").on('click', function () {
        $.post("/recipes/" + recipeId + "/publish", function (data) {
            console.log(data);
        });
    });


        // RECIPE AJAX
        let recipeInventory;
        let inventoryIngredients;
        const makeRecipeAjaxCall = () => {
            recipeInventory = [];
            inventoryIngredients = [];
            $.ajax({'url': '/api/recipes/' + recipeId}).done(function (response) {
                console.log(response);
                response.recipeIngredients.forEach(function (item) {
                    inventoryIngredients.push(item);
                    recipeInventory.push(item.ingredient.name.toLowerCase());
                });
            });
        };

        // CREATE HTML FOR RECIPE TEMPLATE
        const renderRecipe = () => {
            // SORT INGREDIENTS BY NAME
            inventoryIngredients.sort((a, b) => (a.ingredient.name > b.ingredient.name) ? 1 : -1);
            let recipeHtml = "";
            // ADD ROWS TO PANTRY TABLE
            inventoryIngredients.forEach((item) => {
                recipeHtml += '<tr>' +
                    '<td><span class="green-text">' + item.ingredient.name + '</span></td>' +
                    '<td class="green-text" data-amount-float="' + item.amount + '-' + item.unit + '"><span class="rational">' + item.amount + '</span><span> ' + item.unit + '</span></td>' +
                    '<td  id="' + item.id + '" class="qty">' +
                    '<span  type="button" data-toggle="modal" data-target="#deleteIngredientModal" style="font-size: 0.8em!important;"><i class="far fa-trash-alt"></i></span>&nbsp&nbsp' +
                    '<span  type="button" data-toggle="modal" data-target="#ingredientModal" ><i class="fas fa-pencil-alt"></i></span>' +
                    '</td>' +
                    '</tr>'
                ;
            })
            // RENDER TEMPLATE
            $('#ingredientsBody').html(recipeHtml);
        }

        // ONCLICK EVENT FOR USER RECIPE
        $("#userRecipe").click(function () {
            // GET RECIPE INGREDIENTS
            makeRecipeAjaxCall();
            // RENDER INGREDIENTS
            setTimeout(function () {
                renderRecipe();
                convertAllRationals();
            }, 500);
        });

        // AUTO-SEARCH INGREDIENTS FOR PANTRY
        $(document).on('keyup', "#ingredientSearch", function () {
            let searchResults = $('#searchResults');
            let searchInput = this.value.trim();
            /* dont display any suggestions if empty string */
            if (this.value === "") {
                searchResults.empty();
                /* if input string is already in pantry notify user */
            } else if (recipeInventory.includes(searchInput.toLowerCase())) {
                searchResults.empty();
                searchResults.append(
                    '<button id="ingredientInput" type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal" disabled>' + searchInput.toLowerCase() + ' is in recipe</button>'
                );
            }
            /* create suggestions buttons that user can select */
            else {
                searchResults.empty();
                searchResults.append(
                    '<button id="ingredientInput" type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal">' + searchInput.toLowerCase() + '</button>'
                );

                /* search existing ingredients list to auto suggest ingredients and if not in list allow user to select own input */
                ingredientsList.filter(function (ingredient) {
                    if (ingredient.toLowerCase() === searchInput.toLowerCase()) {
                        $("#ingredientInput").addClass("d-none");
                    }
                    if (ingredient.toLowerCase().startsWith(searchInput.toLowerCase())) {
                        searchResults.append(
                            '<button type="button" class="btn btn-yellow rounded-pill m-2" data-toggle="modal" data-target="#ingredientModal">' + ingredient + '</button>'
                        );
                    }
                });
                searchResults.append("</div>");
            }
        });

        // USER RECIPE ADD NEW RECIPE INPUT CLICK
        $(document).on('click', "#showIngredientForm", function () {
            let input = $("#ingredientSearch");
            if (input.hasClass("d-none")) {
                $(this).prop('value', 'close');
                input.removeClass("d-none");
            } else {
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
            if (tagType === "SPAN") {
                /* get data for update form from the tr element */
                let recipeIngId = button.parent()[0].getAttribute("id");
                let tableRow = button.parent().parent()[0];
                let amountUnit = tableRow.firstChild.nextSibling.getAttribute("data-amount-float").split("-");
                clickedIngredient = tableRow.firstChild.innerText;
                /* set form values for update */
                openModal.find('select').val(amountUnit[1].split(" ")[0].toLowerCase()); // set unit select value
                openModal.find('.modal-title').text("Update " + clickedIngredient); // place ingredient name on title
                openModal.find('.modal-body input').val(clickedIngredient);
                count.val(parseFloat(amountUnit[0]));// set amount value
                $("#typeSubmit").html(
                    '<input name="id" type="hidden" value="' + recipeIngId + '"/>' +
                    '<button id="updateIngredient" type="submit" class="btn btn-green">Update</button>'
                );// set edit submit button
            }
            /* else ingredient form is a new ingredient form */
            else {
                clickedIngredient = button[0].innerHTML;
                $("#typeSubmit").html('<button id="addIngredient" type="submit" class="btn btn-green">Save</button>');
                openModal.find('.modal-title').text("Add " + clickedIngredient + " to recipe");
                openModal.find('.modal-body input').val(clickedIngredient);
                count.val(1);
            }

        })

        // Plus Minus Count for Ingredient Form modal
        let count = $('#ingredientForm').find('input.count');
        $(document).on('click', '#formQty .plus', function () {
            if (count.val() == 0) {
                count.val(0);
            }
            count.val(parseFloat(count.val()) + 1);
        });
        $(document).on('click', '#formQty .minus', function () {
            count.val(parseFloat(count.val()) - 1);
            if (count.val() == 0) {
                count.val(1);
            }
        });

        // ON INGREDIENT MODAL SUBMIT POST REQUEST ADD INGREDIENT TO PANTRY
        $(document).on('click', '#addIngredient', function () {
            $('#ingredientModal').modal('toggle');
            let form = $('#ingredientForm');
            let url = "/recipe/" + recipeId + "/ingredient/new";
            let formData = JSON.stringify({
                "name": form.find('input#ingredientName').val(),
                "amount": form.find('input.count').val(),
                "unit": form.find('select  option:selected').text()
            });
            // CREATE POST REQUEST FOR INGREDIENT
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: url,
                data: formData,
                dataType: "json",
            });
            // GET RECIPE INGREDIENTS AFTER CREATE
            setTimeout(function () {
                makeRecipeAjaxCall();
            }, 1000);
            // RENDER THE RECIPE INGREDIENTS
            setTimeout(function () {
                $("#searchResults").empty();
                $("#ingredientSearch").val("")
                renderRecipe();
                convertAllRationals();
            }, 1500);
            return false;
        })

        // UPDATE INGREDIENT
        $(document).on('click', '#updateIngredient', function () {
            $('#ingredientModal').modal('toggle');
            let form = $('#ingredientForm');
            let url = "/recipe/ingredient/edit";
            let formData = JSON.stringify({
                "id": form.find('input[name=id]').val(),
                "amount": form.find('input.count').val(),
                "unit": form.find('select  option:selected').text()
            });
            // MAKE UPDATE POST REQUEST FOR INGREDIENT
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: url,
                data: formData,
                dataType: "json",
            });
            // GET RECIPE INGREDIENTS AFTER UPDATE
            setTimeout(function () {
                makeRecipeAjaxCall();
            }, 1000);
            // RENDER THE RECIPE INGREDIENTS
            setTimeout(function () {
                renderRecipe();
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
            let recipeIngId = button.parent()[0].getAttribute("id");
            let tableRow = button.parent().parent()[0];
            clickedIngredient = tableRow.firstChild.innerText;
            /* set form values for update */
            openModal.find('.modal-title').text("Remove " + clickedIngredient); // place ingredient name on title
            openModal.find('.modal-body input').val(recipeIngId);
        })

        // ON DELETE INGREDIENT MODAL SUBMIT REMOVE INGREDIENT FROM RECIPE
        $("#deleteIngredient").submit(function () {
            $('#deleteIngredientModal').modal('toggle');
            let form = $(this);
            let ingredientId = form.find('input[name=id]').val(); // GETS INGREDIENT ID
            let url = '/api/recipe-ingredients/' + ingredientId + '/delete';
            // MAKE DELETE REQUEST FOR INGREDIENT
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                dataType: 'json'
            });
            // AFTER DELETE GET RECIPE INGREDIENTS
            setTimeout(function () {
                makeRecipeAjaxCall();
            }, 1000);
            // RENDER THE RECIPE INGREDIENTS
            setTimeout(function () {
                renderRecipe();
                convertAllRationals();
            }, 1500);
            return false;
        })

    })(jQuery);
