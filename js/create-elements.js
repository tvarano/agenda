// import runMenu from 'bring-menu';

var nav = document.getElementById("compressed-nav");
if (nav != null) {
    nav.innerHTML = 
    '<a href="index.html" class="noselect" id="menu-reveal"><img src="resources/favicons/favicon-310.png"></a>' + 
    '<ul id="menu-list">' +
        '<a href="index.html" class="menu-elem"><li id="index.html-nav">Home</li></a>' +
        '<a href="webapp.html" class="menu-elem"><li id="webapp.html-nav">Web App</li></a>' + 
        '<a href="download.html" class="menu-elem"><li id="download.html-nav">Download</li></a>' + 
        '<a href="about.html" class="menu-elem"><li id="about.html-nav">About</li></a>' + 
        '<a href="https://github.com/tvarano/agenda" target=\"_blank\" class="menu-elem"><li>Source</li></a>' +
    '</ul>' 
}

var page = window.location.pathname.split("/").pop();

document.getElementById(page + "-nav").classList.add("active");
setTimeout(function() {
    for (elem of document.getElementsByClassName("menu-elem"))
        elem.classList.add("collapsed")
}, 500)
