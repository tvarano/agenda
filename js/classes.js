
class Comparable {
    constructor() {
    }

    compareTo(other) {
        console.log("unimplemented compareTo");
        return 0;
    }

    gt(other) {
        return this.compareTo(other) > 0;
    }

    gte(other) {
        return this.compareTo(other) >= 0;
    }

    lt(other) {
        return this.compareTo(other) < 0;
    }
    
    lte(other) {
        return this.compareTo(other) <= 0;
    }

    eq(other) {
        return this.compareTo(other) == 0;
    }

}


class ClassPeriod extends Comparable {
    constructor(slot, start, end, name) {
        super()
        this.slot = slot
        this.start = start
        this.end = end
        this.name = name
    }

    getName() {
        switch (this.slot) {
            case 9 : return "Lunch"
            case 10 : return "Pascack Period"
            case 11 : return "No School"
            case 12 : return "Pascack Study Period"
            default : {
                if (this.name == null)
                    return "Period " + this.slot
                return this.name
            }
        }
    }

    get duration() {
        return this.end.compareTo(this.start)
    }

    compareTo(other) {
        return this.slot - other.slot;
    }

    timeLeft() {
        var now = new Date();
        return this.end.totalMins - (now.getHours() * 60 + now.getMinutes())
    }
}

class Time extends Comparable {
    constructor(a, b) {
        super();
        if (typeof a == 'string' || a instanceof String) {
            // using string constructor
            this.hour = parseInt(a.substring(0, a.indexOf(',')), 10)
            this.min = parseInt(a.substring(a.indexOf(',') + 1), 10)
        } else if (b === undefined) {
            //using totalmins constructor
            this.hour = Math.floor(a / 60);
            this.min = a % 60;
        } else {
            //using standard
            this.hour = a;
            this.min = b;
        }
    }

    timeUntil (other) {
        if (other.gte(this)) {
            return other.totalMins - this.totalMins;
        }
        const timeToMidnight = 1440 - this.totalMins;
        return new Time(timeToMidnight + other.totalMins);
    }

    get totalMins() {
        return this.hour * 60 + this.min;
    }

    compareTo(other) {
        return this.totalMins - other.totalMins;
    }

    toString() {
        var hrStr = this.hour > 12 ? this.hour % 12 : this.hour
        var minStr = this.min < 10 ? "0" + this.min : this.min
        return hrStr + ":" + minStr
    }
}

class DayType {
    constructor(name, starts, ends, lab) {
        this.name = name; this.starts = starts; this.ends = ends; this.lab = lab;
    }
}

function getTodayRotation() {
    switch (new Date().getDay()) {
        case 0, 6:
            return getRotation("No School")
        case 1:
            return getRotation("R1")
        case 2:
            return getRotation("Odd Block")
        case 3:
            return getRotation("Even Block")
        case 4:
            return getRotation("R4")
        case 5:
            return getRotation("R3")
        default:
            return getRotation("No School")
        }
}

class RotationBundle {
    constructor (name, daytypeName, hrefName) {
        this.name = name;
        this.daytypeName = daytypeName;
        this.hrefName = (hrefName === null || hrefName === undefined) ? name.toLowerCase().replace(" ", "_") : hrefName;
        this.timeArr = []
    }
    retrieveDayType() {
        this.daytype = readDayType(this.daytypeName);
    }

    getTimes() {
        if (this.timeArr.length == 0) 
            this.makeTimes();
        return this.timeArr
    }

    makeTimes() {
        console.log("make times.")
        if (this.daytype ==  null) {
            this.retrieveDayType()
        }
        // this.timeArr = []
        var slotArr = readRotation(this.hrefName)
        for (var i = 0; i < this.daytype.starts.length; i++){
            this.timeArr.push(new ClassPeriod(slotArr[i], this.daytype.starts[i], this.daytype.ends[i]))
        }
    }

    get labTime() {
        if (this.daytype == null) {
            this.retrieveDayType();
        }
        return this.daytype.lab;
    }

    get start() {
        return this.timeArr[0].start
    }

    get end() {
        return this.timeArr[this.timeArr.length - 1].end
    }
}

function getRotation(name) {
    for (arr of rotationsWithCategories) {
        for (var i = 1; i < arr.length; i++) {
            if (arr[i] instanceof RotationBundle && arr[i].name == name)
                return arr[i];
        }
    }
    return null;
}