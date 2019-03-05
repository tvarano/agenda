
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
    constructor(slot, start, end, name) {
        console.log("creating "+name)
        this.slot = slot
        this.start = start
        this.end = end
        this.name = name
        h.asdf;
    }

    getName() {
        if (this.name == null)
            return "Period " + this.slot
        return this.name
    }

    get duration() {
        return this.end - this.start
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
            console.log(a.substring(0, a.indexOf(',')))
            this.hour = parseInt(a.substring(0, a.indexOf(',')), 10)
            this.min = parseInt(a.substring(a.indexOf(',') + 1), 10)
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
        const timeToMidnight = 1440 - self.totalMins;
        return new Time(timeToMidnight + other.totalMins);
    }

    get totalMins() {
        return this.hour * 60 + this.min;
    }

    compareTo() {
        return this.totalMins - other.totalMins;
    }
}

function getNow() {
    return new Time(new Date().getMinutes, new Date().getMinutes())
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
        this.hrefName = (hrefName === null) ? name.toLowerCase().replace(" ", "_") : hrefName;
        this.timeArr = []
    }
    retrieveDayType() {
        this.daytype = readDayType(this.daytypeName);
    }

    getTimes() {
        console.log("getting times "+this.name)
        if (this.timeArr.length == 0)
            this.makeTimes();
        return this.timeArr
    }

    makeTimes() {
        console.log("make times.")
        if (this.daytype ==  null) {
            this.retrieveDayType()
        }
        this.timeArr = []
        for (var i = 0; i < this.daytype.starts.length; i++){
            console.log("creating time slot "+ (i+1))
            timeArr.push(new ClassPeriod(i+1, this.daytype.starts[i], this.daytype.ends[i]))
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
    console.log(rotationsWithCategories)
    for (arr of rotationsWithCategories) {
        console.log(arr)
        for (var i = 1; i < arr.length; i++) {
            console.log("getrotaion search "+arr[i] + " to "+name)
            if (arr[i] instanceof RotationBundle && arr[i].name == name)
                return arr[i];
        }
    }
    return null;
}