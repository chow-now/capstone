/**
 * Converts numbers to fractions:
 * - 1.25 to 1 1/4
 * - 2 to 2
 */
const numberToFraction = (amount) => {
    // This is a whole number and doesn't need modification.
    if ( parseFloat( amount ) === parseInt( amount ) ) {
        return Math.floor(amount);
    }
    // Next 12 lines are cribbed from https://stackoverflow.com/a/23575406.
    let gcd = function(a, b) {
        if (b < 0.0000001) {
            return a;
        }
        return gcd(b, Math.floor(a % b));
    };
    let len = amount.toString().length - 2;
    let denominator = Math.pow(10, len);
    let numerator = amount * denominator;
    let divisor = gcd(numerator, denominator);
    numerator /= divisor;
    denominator /= divisor;
    let base = 0;
    // In a scenario like 3/2, convert to 1 1/2
    // by pulling out the base number and reducing the numerator.
    if ( numerator > denominator ) {
        base = Math.floor( numerator / denominator );
        numerator -= base * denominator;
    }
    amount = Math.floor(numerator) + '/' + Math.floor(denominator);
    if ( base ) {
        amount = base + ' ' + amount;
    }
    return amount;
};
const convertAllRationals =()=>{
    let els = document.getElementsByClassName("rational");
    [].forEach.call(els, function (el) {
        let rational = el.innerText;
        el.innerText = numberToFraction(rational);
    });
}

convertAllRationals();

