/**
 * page가 load되고 디바이스가 준비되었을때에 해당하는 이벤트를 등록
 */
function onLoad(){
	
}

// page가 load되고 디바이스가 준비되었을때에 해당하는 이벤트를 등록
document.addEventListener("deviceready", onDeviceReady, false);

/**
 * 디바이스가 준비된 후 작업할 설정에 대한 정의
 */
function onDeviceReady(){
//	navigator.notification.alert("deviceready", null, null, '확인');
	
	// android back button event
//	document.addEventListener("backbutton", onBackKeyDown, false);
	
}

/**
 * 종료 알림창
 */
function onBackKeyDown() {
	if (history.length == 1) {
		navigator.notification.confirm("종료하시겠습니까?", onBackKeyDownMsg, null, "종료, 취소");
	} else {
		history.back();
	}
	/*try {
		history.back();
	} catch (e) {
		navigator.notification.confirm("종료하시겠습니까?", onBackKeyDownMsg, null, "종료, 취소");
	}*/
}

/**
 * 
 * @param button 1 : 종료 , 2 : 취소
 */
function onBackKeyDownMsg(button) {
	if(button == 1) navigator.app.exitApp();
}

/** 
 * 알림창 확인시 정의
 */
function alertDismissed(){

}


/* ************************************************************************************************
 * INFO Network method
 */
/** 
 * 현재 network 상태를 체크 하여 알림
 */
function checkNetworkState(){
	var networkState = navigator.connection.type;
	var states = {};
	states[Connection.UNKNOWN] 	= "UNKNOWN";
	states[Connection.ETHERNET] = "ETHERNET";
	states[Connection.WIFI] 	= "WIFI";
	states[Connection.CELL_2G] 	= "CELL_2G";
	states[Connection.CELL_3G] 	= "CELL_3G";
	states[Connection.CELL_4G] 	= "CELL_4G";
	states[Connection.NONE] 	= "NONE";
	
	navigator.notification.alert("connection_type : " + states[networkState]);
}


/* ************************************************************************************************
 * INFO viewport 조정
 */
$(document).bind("mobileinit", function() {
	$.extend($.mobile, {
		metaViewportContent: 'user-scalable=yes, initial-scale=1, maximum-scale=2, minimum-scale=1, width=device-width'
	});
});



/* ************************************************************************************************
 * INFO back btn 재정의
 */
$.mobile.page.prototype.options.backBtnText = "이전";
//$.mobile.page.prototype.options.backBtnTheme = "a";


/* ************************************************************************************************
 * INFO loading
 */
//$.mobile.showPageLoadingMsg();
$(document).on("click", ".show-page-loading-msg", function() {
    var $this = $( this ),
        theme = $this.jqmData("theme") || $.mobile.loader.prototype.options.theme,
        msgText = $this.jqmData("msgtext") || $.mobile.loader.prototype.options.text,
        textVisible = $this.jqmData("textvisible") || $.mobile.loader.prototype.options.textVisible,
        textonly = !!$this.jqmData("textonly");
        html = $this.jqmData("html") || "";
    $.mobile.loading( 'show', {
            text: msgText,
            textVisible: textVisible,
            theme: theme,
            textonly: textonly,
            html: html
    });
})
.on("click", ".hide-page-loading-msg", function() {
    $.mobile.loading( 'hide' );
});

//$( document ).on( "mobileinit", function() {
//	$.mobile.loader.prototype.options.text = "loading";
//	$.mobile.loader.prototype.options.textVisible = false;
//	$.mobile.loader.prototype.options.theme = "a";
//	$.mobile.loader.prototype.options.html = "";
//});



/* ************************************************************************************************
 * INFO web storage
 */



/* ************************************************************************************************
 * INFO geolocation
 */
/**
 * tag id
 */
var locationTagId = null;
function getLocation(id){
	locationTagId = id;
	if (id != null) {
		var id = document.getElementById(locationTagId); 
		id.innerHTML = "";
		id.style.display = "none";
	}
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(successGetLocation, failGetLocation);
		//navigator.geolocation.watchPosition(successGetLocation, failGetLocation);
	}
	else {
		alert("현재 브라우저는 위치정보 조회를 지원하지 않습니다.");
	}
}

function successGetLocation(position) {
	var msg = "현재 위치 : 위도=" + position.coords.latitude + ", 경도=" + position.coords.longitude;
	if (locationTagId == null) {
		alert(msg);
	} else {
		var id = document.getElementById(locationTagId); 
		id.style.display = "block";
		id.innerHTML = msg;
	}
	$.mobile.loading( 'hide' );
}

function failGetLocation(error) {
	if (locationId == null) {
		alert("에러 : " + error.message);
	}
	else {
		var id = document.getElementById(locationTagId); 
		id.style.display = "block";
		id.innerHTML = "error : " + error.message;
	}
	$.mobile.loading( 'hide' );
}