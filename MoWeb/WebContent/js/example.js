$(document).ready(function(){

	var cb=0;
	var here=0;

//	var beforeH=20;
//	var afterH=20;
//	var widthH=40;
//	var distanceH=30;
	var beforeH=120;
	var afterH=120;
	var widthH=160;
	var distanceH=140;
	var screenWidth=400;
	var widthsH=[];
	var leftsH=[];
	var totalWidthH;

//	var beforeSel=10;
//	var afterSel=8;
//	var widthSel=60;
//	var distanceSel=80;

//	var beforeB=20;
//	var afterB=20;
//	var distanceB=28;
//	var widthsB=[];
//	var leftsB=[];
//	var totalWidthB;

	var i=0;

	var isplaying=false;

//	var topics=["Japanese", "Types"];
	var topics=["Japanese", "Types", "Hindi", "Greek", "Arabic"];

	var buttonsPaneMaxWidth=240;

	var itemHeight= 20;

//	var currentLeftB=beforeB;
	totalWidthH=beforeH+(topics.length-1)*(widthH+distanceH)+widthH+afterH;
	if (totalWidthH < screenWidth) {
		alert("totalWidthH=" + totalWidthH + ", screenWidth=" + screenWidth);
		var toAdd=(screenWidth-totalWidthH)/2;
		beforeH += toAdd;
		afterH += toAdd;
	}
	var currentLeftH=beforeH;
	$.each(topics, function(key,value) {
//		alert(key+": "+value);
		$("#ct").append('<div class="house" id="h' + key + '">' + value + '</div>' );
		$("#h"+key).css("left", currentLeftH);
		$("#h"+key).css("height", 80 + (parseInt(key)%3)*40);
		$("#h"+key).css("width", widthH);
		widthsH.push(widthH);
		leftsH.push(currentLeftH);
		currentLeftH+=widthH+distanceH;

		$("#ls").append('<div class="topicitem" id="i' + key + '">' + value + '</div>' );
		$("#i"+key).css("height", itemHeight);

//		$("#btns").append('<div class="hbtn" id="b' + key + '">' + value + '</div>' );
//		$("#b"+key).css("left", currentLeftB);
//		var widthB=$("#b"+key).outerWidth(true);
//		widthsB.push(widthB);
//		leftsB.push(currentLeftB);
//		currentLeftB+=widthB+distanceB;
	});
	totalWidthH=currentLeftH-distanceH+afterH;
	$("#ct").css("width", totalWidthH);
	$("#g").css("width", totalWidthH);
	$("#w").css("width", screenWidth);

	$(".topicitem").click(function(event) {
		here=parseInt(event.target.id.substring(1));
		if (here != cb && !isplaying) {
			var fromNumber=cb;
			var toNumber=here;
			++ i;
	
			//List
			var fromTop=4+fromNumber*itemHeight;
			var toTop=4+toNumber*itemHeight;
			$.keyframe.define([{
			    name: 'animation-list' + i,
			    'from':{top:fromTop+"px"},
			    'to':{top:toTop+"px"},
			}]);
			var step=Math.abs(fromNumber-toNumber);
			var time= step == 1 ? 0.75 : step > 8 ? 4 : Math.abs(fromNumber-toNumber) / 2;
			$("#listpointer").playKeyframe('animation-list' + i + ' ' + time + 's ease forwards', animationComplete);
			isplaying=true;
	
			//Houses
			if (totalWidthH>screenWidth) {
				var fromLeft=getLeftHByNumber(fromNumber);
				var toLeft=getLeftHByNumber(toNumber);
				$.keyframe.define([{
				    name: 'animation-house' + i,
				    'from':{left:fromLeft+"px"},
				    'to':{left:toLeft+"px"},
				}]);
				$("#ct").playKeyframe('animation-house' + i + ' ' + time + 's ease forwards', animationComplete);
				isplaying=true;
				$("#status").text("fromLeft:"+fromLeft+" toLeft:"+toLeft + " time:" + time);
			}
	
			cb=here;
		}
	});

//	$(".hbtn").click(function(event) {
//		here=parseInt(event.target.id.substring(1));
//		doWhenClick();
//	});

//	function doWhenClick() {
//		if (here != cb && !isplaying) {
//			++ i;
//			var fromNumber=cb;
//			var toNumber=here;
//
//			//Houses
//			if (totalWidthH>screenWidth) {
//				var fromLeft=getLeftHByNumber(fromNumber);
//				var toLeft=getLeftHByNumber(toNumber);
//				$.keyframe.define([{
//				    name: 'animation-house' + i,
//				    'from':{left:fromLeft+"px"},
//				    'to':{left:toLeft+"px"},
//	//			    'complete': animationComplete,
//				}]);
//				$("#ct").playKeyframe('animation-house' + i + ' 1s ease forwards', animationComplete);
//				isplaying=true;
//				$("#status").text(here+" clicked! "+cb+"->"+here+"! "+"fromLeft:"+fromLeft+" toLeft:"+toLeft);
//			}
//
//			//selection window
////			var fromSelection=getLeftByNumber(fromNumber, beforeSel, afterSel, widthSel, distanceB, selWindowWidth, topics.length);
////			var toSelection=getLeftByNumber(toNumber, beforeSel, afterSel, widthSel, distanceB, selWindowWidth, topics.length);
////			var fromFromCss=$("#ct").css("left");
////			var state=$("#ct").css("animation-play-state");
////			$("#status").text(here+" clicked! "+cb+"->"+here+"! "+"fromLeft:"+fromFromCss+" toLeft:"+toLeft);
////			$("#status").text(" state:"+state);
////			$.keyframe.define([{
////			    name: 'animation-selwindow' + i,
////			    'from':{left:fromFromCss},
////			    'to':{left:toSelection+"px"},
////			}]);
////			$("#selection").playKeyframe('animation-selwindow' + i + ' 1s linear forwards');
//
//			//selection
////			var fromSelection=beforeSel+...
////			var toSelection=beforeSel+...
////			$.keyframe.define([{
////			    name: 'animation-selwindow' + i,
////			    'from':{left:fromSelection+"px"},
////			    'to':{left:toSelection+"px"},
////			}]);
////			$("#selection").playKeyframe('animation-selection' + i + ' 1s linear forwards');
//			cb=here;
//		}
//	}

	function animationComplete() {
		isplaying=false;
	}

	function getLeftHByNumber(number) {
		var totalLeft=leftsH[number]+widthsH[number]/2;
		var middleScreen=screenWidth/2;
		var result=totalLeft-middleScreen;
		var min=0;
		var max=totalWidthH-screenWidth;
		if (result < min) {
			return -min;
		}
		if (result > max) {
			return -max;
		}
		return -result;
	}
});
