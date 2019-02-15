
class Comparable {
    constructor() {
        console.log("comparable constructed");
    }

    compareTo(other) {
        console.log("unimplemented compareTo");
        return 0;
    }

    gt(other) {
        return compareTo(other) > 0;
    }

    gte(other) {
        return compareTo(other) >= 0;
    }

    lt(other) {
        return compareTo(other) < 0;
    }
    
    lte(other) {
        return compareTo(other) <= 0;
    }

    eq(other) {
        return compareTo(other) == 0;
    }

}
