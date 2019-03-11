const icsURL = "https://calendar.google.com/calendar/ical/8368c5a91jog3s32oc6k22f4e8%40group.calendar.google.com/public/basic.ics"

google.load("gdata", "2.x");

function init() {
  // init the Google data JS client library with an error handler
  google.gdata.client.init(handleGDError);
  // load the code.google.com calendar
  loadMyCalendar();
}
/**
 * Loads the Google Event Calendar
 */
function loadMyCalendar() {
  loadCalendarByAddress('MY_ADDRESS@gmail.com'); /* address here */
}

/**
 * Adds a leading zero to a single-digit number.  Used for displaying dates.
 */
function padNumber(num) {
  if (num <= 9) {
    return "0" + num;
  }
  return num;
}

/**
 * Determines the full calendarUrl based upon the calendarAddress
 * argument and calls loadCalendar with the calendarUrl value.
 *
 * @param {string} calendarAddress is the email-style address for the calendar
 */ 
function loadCalendarByAddress(calendarAddress) {
  var calendarUrl = 'https://www.google.com/calendar/feeds/' +
                    calendarAddress + 
                    '/public/full';
  loadCalendar(calendarUrl);
}

function loadCalendar(calendarUrl) {
  var service = new 
      google.gdata.calendar.CalendarService('gdata-js-client-samples-simple');
  var query = new google.gdata.calendar.CalendarEventQuery(calendarUrl);
  query.setOrderBy('starttime');
  query.setSortOrder('ascending');
  query.setFutureEvents(true);
  query.setSingleEvents(true);
  query.setMaxResults(100);
  service.getEventsFeed(query, listEvents, handleGDError);
}

    /**
 * Callback function for the Google data JS client library to call when an error
 * occurs during the retrieval of the feed. Details available depend partly
 * on the web browser, but this shows a few basic examples. In the case of
 * a privileged environment using ClientLogin authentication, there may also
 * be an e.type attribute in some cases.
 *
 * @param {Error} e is an instance of an Error 
 */
function handleGDError(e) {
  document.getElementById('jsSourceFinal').setAttribute('style', 
      'display:none');
  if (e instanceof Error) {
    /* alert with the error line number, file and message */
    alert('Error at line ' + e.lineNumber +
          ' in ' + e.fileName + '\n' +
          'Message: ' + e.message);
    /* if available, output HTTP error code and status text */
    if (e.cause) {
      var status = e.cause.status;
      var statusText = e.cause.statusText;
      alert('Root cause: HTTP error ' + status + ' with status text of: ' + 
            statusText);
    }
  } else {
    alert(e.toString());
  }
}

/**
 * Callback function for the Google data JS client library to call with a feed 
 * of events retrieved.
 *
 * Creates an unordered list of events in a human-readable form.  This list of
 * events is added into a div called 'events'.  The title for the calendar is
 * placed in a div called 'calendarTitle'
 *
 * @param {json} feedRoot is the root of the feed, containing all entries 
 */ 
function listEvents(feedRoot) {
  var entries = feedRoot.feed.getEntries();
  var eventDiv = document.getElementById('events');
  if (eventDiv.childNodes.length > 0) {
    eventDiv.removeChild(eventDiv.childNodes[0]);
                                        }	  

  var ul = document.createElement('ul');
  /* set the calendarTitle div with the name of the calendar */
  /*document.getElementById('calendarTitle').innerHTML = 
    "Calendar: " + feedRoot.feed.title.$t + "<br/><br/>";*/
  /* loop through each event in the feed */
 var len = entries.length;
 for (var i = 0; i < len; i++) {
    var entry = entries[i];
    /* contenuto e titolo sono invertiti */
    var cont = entry.getTitle().getText();
    var title = entry.getContent().getText();  /* get description notes */
   /* only events containing WORD_1 &/or WORD_2 & not containing '?' */
       if(cont.indexOf('?') == -1 && (cont.indexOf('WORD_1') > -1 || cont.indexOf('WORD_2') > -1)){
    var whereIs = entry.getLocations()[0].getValueString();
    var startDateTime = null;
    var startJSDate = null;
    var times = entry.getTimes();
    if (times.length > 0) {
      startDateTime = times[0].getStartTime();
      startJSDate = startDateTime.getDate();
    }
    var entryLinkHref = null;
    if (entry.getHtmlLink() != null) {
      entryLinkHref = entry.getHtmlLink().getHref();
    }
    var day = padNumber(startJSDate.getDate());
    var month = padNumber(startJSDate.getMonth() + 1);
    var dateString = day + "/" + month + "/" + startJSDate.getFullYear();
    if (title.indexOf(' - ') > -1) {
      cont = title.substring(0, title.indexOf(' - ')+3) + cont + " @ " + whereIs;
      title = title.substring(title.indexOf(' - ')+3);
    } else cont = "h_:_ - " + cont + " @ " + whereIs;
    var li = document.createElement('li');

    /* if we have a link to the event, create an 'a' element */
    if (entryLinkHref != null) {
      entryLink = document.createElement('a');
      entryLink.setAttribute('href', entryLinkHref);
      li.appendChild(document.createTextNode(dateString + ' - '));
   entryLink.appendChild(document.createTextNode(title));
      li.appendChild(entryLink);
      
    } else {
      li.appendChild(document.createTextNode(dateString + ' - ' + title));
    }	    

    var p = document.createElement("p");
    
    var lo = document.createElement('lo');
    lo.appendChild(document.createTextNode(cont));

	li.style.fontSize = "25px";
	lo.style.fontSize = "15px";


    /* append the list item onto the unordered list */
    ul.appendChild(li);
    ul.appendChild(lo);
    ul.appendChild(p);
    
  }
    eventDiv.appendChild(ul);
    }
}

loadCalendar(icsURL);
google.setOnLoadCallback(init);
