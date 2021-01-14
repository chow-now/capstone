// sets the intial servings amout for the recipe
const defaultServingSize = parseInt($("#servingAmount").text())

// function to set the ingredient amount based on servings size
const changeIngredientAmount = (servings) => {
    $(".rational").each(function (amount) {
        // gets the intial ingredient amount
        let ingAmt = parseFloat($(this).data('internalid'));
        // finds what the ingredient amout would based on an individual serving size
        let singleServingAmount = ingAmt / defaultServingSize;
        // finds new amount based on single servings size * requested serving size
        let newIngAmt = singleServingAmount * servings;
        // renders it to the page
        $(this).html(newIngAmt);
    })
    // converts all new ingredient amounts to franctions
    convertAllRationals();
}

// reduces the amount  of servings
$("#reduceServings").click(function () {
    let servings = parseInt($("#servingAmount").text());
    if(servings > 1){
        let newServing = servings-1;
        $(".serving-size").html(newServing);
        changeIngredientAmount(newServing);
    }else{
        $(".serving-size").html(1)
    }
})

// increases the amount of servings
$("#increaseServings").click(function () {
    let servings = parseInt($("#servingAmount").text());
    let newServing = servings+1;
    $(".serving-size").html(newServing);
    changeIngredientAmount(newServing);

})