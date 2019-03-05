
const rootPath = 'https://tvarano54.github.io/agenda/data/';
const daytypePath = rootPath + "daytypes/";
const rotationPath = rootPath + "rotations/";

// function doGET(path, callback) {

//     var xhr = new XMLHttpRequest();
//     xhr.onreadystatechange = function() {
//         if (xhr.readyState == 4) {
//             // The request is done; did it work?
//             if (xhr.status == 200) {
//                 callback(xhr.responseText);
//             } else {
//                 callback(null);
//             }
//         }
//     };
//     xhr.open("GET", path);
//     xhr.send();
// }

function syncRequest(address) {
    return httpRequest(address, "GET", false);
}

function handleDayType(req) {
    var raw = req.responseText;

    var data = raw.split("\n");
    var i = 0;
    var starts = [];
    while (data[++i] != 'END') {
        starts.push(new Time(data[i]));
    }
    var ends = [];
    while (data[++i] != 'LAB') {
        ends.push(new Time(data[i]));
    }
    var lab = data[++i] == "NULL" ? null : new Time(data[i]);
    return [starts, ends, lab];
}

function handleRotation(req) {
    var raw = req.responseText;

    var data = raw.split("\n");
    var ret = []
    for (slot of data)
        ret.push(parseInt(slot))
    return ret
}

function httpRequest(address, reqType, asyncProc) {
    var req = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    if (asyncProc) { 
      req.onreadystatechange = function() { 
        if (this.readyState == 4) {
          asyncProc(this);
        } 
      };
    } 
    req.open(reqType, address, asyncProc);
    req.send();
    return req;
}



function readDayType(name) {
    var data = handleDayType(syncRequest(daytypePath + name + ".txt"));
    return new DayType(name, data[0], data[1], data[2]);
}

function readRotation(hrefName) {
    return handleRotation(syncRequest(rotationPath + hrefName + ".txt"))
}