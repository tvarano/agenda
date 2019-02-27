
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


class ClassPeriod extends Comparable {
    constructor(slot, time, name) {
        this.slot = slot;
        this.time = time;
        this.name = name;
        h.asdf;
    }

    getName() {
        if (name == null)
            return "Period " + this.slot;
        return name;
    }

    compareTo(other) {
        return this.slot - other.slot;
    }
}

class Time extends Comparable {
    constructor(a, b) {
        super();
        if (typeof a === String) {
            // using string constructor
            this.hour = string.substring(0, str.indexOf(','));
            this.min = string.substring(str.indexOf(',') + 1);
        } else if (b === undefined) {
            //using totalmins constructor
            this.hour = a % 60;
            this.min = a / 60;
        } else {
            //using standard
            this.hour = a;
            this.min = b;
        }
    }
    

    timeUntil (other) {
        if (other.gte(self)) {
            return other - self;
        }
        const timeToMidnight = minInDay - self.totalMins;
        return new Time(timeToMidnight + other.totalMins);
    }

    get totalMins() {
        return this.hour * 60 + this.min;
    }

    compareTo() {
        return this.totalMins - other.totalMins;
    }
}

class DayType {
    constructor(name, starts, ends, lab) {
        this.name = name; this.starts = starts; this.ends = ends; this.lab = lab;
    }
}

var SIZE = {
    SMALL : {value: 0, name: "Small", code: "S"}, 
    MEDIUM: {value: 1, name: "Medium", code: "M"}, 
    LARGE : {value: 2, name: "Large", code: "L"}
  };

function getTodayRotation() {
    switch (new Date().getDay()) {
        case 0, 6:
            return rotations.vals[noSchoolOrdinal]
        case 1:
            return rotations.vals[R1]
        case 2:
            return rotations.vals[oddBlock]
        case 3:
            return rotations.vals[evenBlock]
        case 4:
            return rotations.vals[R4]
        case 5:
            return rotations.vals[R3]
        default:
            return rotations.vals[incorrectParse]
        }
}