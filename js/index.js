
// 0, in class
// 1, in between classes
// 2, out of school
// 3, unknown / error
var status = 3;

// var day = getDayTypeTimes("half_day");
// console.log(day);
// console.log("hello...")

var rotationsWithCategories = [
    ["Normal", new RotationBundle("R1", "normal"), new RotationBundle("Odd Block", "block"), new RotationBundle("Even Block", "block"), new RotationBundle("R4", "normal"), new RotationBundle("R3", "normal")], 
    ["Half Day", new RotationBundle("Half Day R1", "half_day"), new RotationBundle("Half Day R2", "half_day"), new RotationBundle("Half Day R4", "half_day"), new RotationBundle("Half Day R3", "half_day")], 
    ["Delayed Opening", new RotationBundle("Delayed R1", "delayed_open", "delay_r1"), new RotationBundle("Delayed R4", "delayed_open", "delay_r4"), new RotationBundle("Delayed R3", "delayed_open", "delay_r3"), new RotationBundle("Delayed Odd Block", "delay_odd", "delay_odd"), new RotationBundle("Delayed Even Block", "delay_even", "delay_even")], 
    ["Testing", new RotationBundle("Testing Day One", "test_day", "test_one"), new RotationBundle("Testing Day Two", "test_day", "test_two"), new RotationBundle("Testing Day Three", "test_three", "test_three")], 
    ["Other", new RotationBundle("R2", "normal"), new RotationBundle("Pep Rally", "block", "flip_even_block"), new RotationBundle("No School", "no_school"), new RotationBundle("Special Rotation", "special", "special")]
];

var rotation = getTodayRotation();
var now = getNow()
var currentEnd = null

function setCurrentRotation(name) {
    rotation = getRotation(name)
    updateUI()
}


function updateUI(fullUpdate) {
    var current = currentClass()
}

function currentClass() {
    console.log("reading times from "+rotation)
    var now = new Time(new Date().getHours() + new Date().getMinutes())
    for (temp of rotation.getTimes()) {
        if (now.gt(temp.start) && now.compareTo(temp.start) < temp.duration)
            return temp
    }
    return null;
}

function setStatus(ccl) {
    if (rotation.start.gt(now) || rotation.end.lt(now))
     status = 2
    else if (ccl == null) {
        status = 1
    } else {
        status = 0
    }
}

// main loop
function updateTime() {
    console.log("UPDATE TIME")
    now = getNow()
    ccl = currentClass()
    var oldStatus = status
    setStatus(ccl)
    updateUI(oldStatus != status)
}

function toggleLoading() {
    document.getElementById("loading-indicator").classList.toggle('hidden')
    for (temp of document.getElementsByClassName("section"))
        temp.classList.toggle("hidden")
}

setInterval(updateTime, 1000)
toggleLoading()