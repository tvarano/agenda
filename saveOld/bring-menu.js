
document.getElementById("menu-reveal").onmouseover = revealMenu;
document.getElementById("compressed-nav").onmouseleave = hideMenu;
console.log("running bring-menu"); 

function revealMenu() {
    var menuElems = document.getElementsByClassName("menu-elem");
    for (var el of menuElems) {
        el.classList.add("shown");
        el.classList.remove("collapsed");
    }
    document.getElementById("menu-list").classList.add("shown");
}

function hideMenu() {
    var menuElems = document.getElementsByClassName("menu-elem");
    for (var el of menuElems) {
        el.classList.add("collapsed");
        el.classList.remove("shown");
    }
    document.getElementById("menu-list").classList.remove("shown");
}