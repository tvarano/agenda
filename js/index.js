
// 0, in class
// 1, in between classes
// 2, out of school
// 3, unknown / error
var status = 3;

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
var firstLoad = true
var dayDefined = new Date().getDay()
console.log(dayDefined)

function setCurrentRotation(name) {
    rotation = getRotation(name)
    updateUI(true, currentClass())
}

function resetTodayRotation() {
    rotation = getTodayRotation()
    updateUI(true, currentClass())
}


function updateUI(statusChanged, current) {
    if (statusChanged) {
        if (status == 0) {
            //in class
            document.getElementById("status").innerHTML = "You are in..."
            document.getElementById("for").innerHTML = "for..."
        } else if (status == 1) {
            // in between
            document.getElementById("status").innerHTML = "You are between classes."
            document.getElementById("for").innerHTML = "is next in..."
        } else {
            // out of school.
            document.getElementById("status").innerHTML = ""
            document.getElementById("for").innerHTML = ""
            document.getElementById("current-class").innerHTML = "School is not in session"
            document.getElementById("time-left").innerHTML = ""
        }
    }
    // set current class
    if (status == 0) {
        // in class
        document.getElementById("current-class").innerHTML = current.getName()
        var timeLeft = now.timeUntil(current.end)
        var timeLeftStr = timeLeft >= 60 ? Math.floor(timeLeft / 60) + " hr " + timeLeft % 60 + " min" : timeLeft + " min"
        document.getElementById("time-left").innerHTML = timeLeftStr
    } else if (status == 1) {
        // in between
    }

    // set rotation info
    // NOTE only happens when rotation is changed. might want to fix so it's not constantly updating everything
    document.getElementById("current-rotation").innerHTML = "Current Rotation: "+rotation.name
    var listElems = ""
    var timeElems = ""
    
    for (period of rotation.getTimes()) {
        var name = period.getName()
        var activePeriod = period.slot == current.slot
        var classActive = activePeriod ? " active" : ""
        listElems += "<li class=\"class-info" + classActive + "\">" + period.getName() + "</li>"
        timeElems += "<li class=\"class-time" + classActive + "\">" + period.start + " - " + period.end + "</li>"
    }
    document.getElementById("sched-slots").innerHTML = listElems
    document.getElementById("sched-times").innerHTML = timeElems
    document.getElementById("lab-indicator").innerHTML = rotation.labTime === null ? "No Lab" : "Lab Switch: " + rotation.labTime
}

function currentClass() {
    // var now = get
    for (temp of rotation.getTimes()) {
        if (now.gt(temp.start) && now.compareTo(temp.start) < temp.duration)
            return temp
    }
    return null;
}

function setStatus(ccl) {
    if (rotation.start.gt(now) || rotation.end.lt(now))
        status = 2
    else if (ccl === null) {
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
    updateUI(oldStatus != status, ccl)
    if (firstLoad) {
        toggleLoading()
        loadSetRotation()
        firstLoad = false
    }
    if (new Date().getDay() != dayDefined) {
        dayDefined = today.getDay()
        resetTodayRotation()
    }
}

function getNow() {
    var today = new Date()
    // return new Time(8, 30)
    return new Time(today.getHours(), today.getMinutes())
}

function toggleLoading() {
    console.log(document.getElementsByTagName("section"))
    document.getElementById("loading-indicator").classList.toggle('hidden')
    for (temp of document.getElementsByTagName("section")) {
        temp.classList.toggle("hidden")
    }
}

function loadSetRotation() {
    var fullHtml = ""
    for (cat of rotationsWithCategories) {
        fullHtml += "<li class=\"category\">" + cat[0]
        fullHtml += "<ul>"
        for (var i = 1; i < cat.length; i++)
            fullHtml += "<li class=\"rotation-selector\"><button onclick=\"setCurrentRotation(\'" + cat[i].name + "\')\">" + cat[i].name + "</button></li>"
        fullHtml += "</ul></li>"
    }
    document.getElementById("rotation-list").innerHTML = fullHtml
}

setInterval(updateTime, 2000)