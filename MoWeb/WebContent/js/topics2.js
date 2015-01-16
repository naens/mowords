$(document).ready(function(){

	/**
	 * @memberOf topics.js
	 */
	var here=0;

	/**
	 * @memberOf topics.js
	 * @constant
	 */
	var beforeH=40;

	/**
	 * @memberOf topics.js
	 * @constant
	 */
	var afterH=40;

	/**
	 * @memberOf topics.js
	 * @constant
	 */
	var distanceH=120;

	//!!!units from css in px!
	var widthH=parseInt($(".house").css("width"));				//div.house.width
	var screenWidth=parseInt($("#w").css("width"));				//div#w.width
	var itemHeight=parseInt($(".topicitem").css("height"));		//div.topicitem.height

	var topicsnumber=$('#ct').children('.house').length;
	var totalWidthH=beforeH + (topicsnumber-1) * (widthH+distanceH) + widthH+afterH;

	/**
	 * @memberOf topics.js
	 */
	var cb=0;
	if (typeof window.content.localStorage["cb"] === 'undefined') {
		cb=0;
	} else {
		cb=window.content.localStorage["cb"];
		if (topicsnumber>0 && cb>topicsnumber-1 || cb < 0) {
			cb = topicsnumber-1;
		}
	}
	console.log("cb="+cb);

	if (topicsnumber == 0 || totalWidthH < screenWidth) {
//		alert("totalWidthH=" + totalWidthH + ", screenWidth=" + screenWidth);
		var toAdd=(screenWidth-totalWidthH)/2;
		beforeH += toAdd;
		afterH += toAdd;
		totalWidthH = screenWidth;
	}

//init topics[], topic.left
	/**
	 * @memberOf topics.js
	 */
	var topics=[];

	$('#ct').children('.house').each(function () {
		var x=parseInt(this.id.substring(1));
		topics[x]=$(this);
//		topics[x]=$(this).data('name').trim();
		$(this).css("left", beforeH + x*(distanceH+widthH));
		console.log("topics["+x+"]="+topics[x].data('name').trim());
	});

	/**
	 * @memberOf topics.js
	 */
	//TODO: test 'topic' object
	var topic = topicsnumber > 0 ? {name:topics[cb].data('name'), element:topics[cb]} : 'undefined';
	$("#ct").css("width", totalWidthH);
	$("#g").css("width", totalWidthH);
	$("#w").css("width", screenWidth);

	$("#listpointer").css("top", 2+cb*itemHeight+"px");
	$("#ct").css("left", getLeftHByNumber(cb)+"px");

//topics animation
	var i=0;
	var isplaying=false;
	$(".topicitem").click(function(event) {
		here=parseInt(event.target.id.substring(1));
		if (here != cb && !isplaying) {
			var fromNumber=cb;
			var toNumber=here;
			++ i;

			//List
			var fromTop=2+fromNumber*itemHeight;
			var toTop=2+toNumber*itemHeight;
			$.keyframe.define([{
				name: 'animation-list' + i,
				'from':{top:fromTop+"px"},
				'to':{top:toTop+"px"},
			}]);
			var step=Math.abs(fromNumber-toNumber);
			var time= step == 1 ? 0.75 : step > 8 ? 4 : Math.abs(fromNumber-toNumber) / 2;
			$("#listpointer").playKeyframe('animation-list' + i + ' ' + time + 's ease forwards', function() {isplaying=false;});
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
				$("#ct").playKeyframe('animation-house' + i + ' ' + time + 's ease forwards', function() {isplaying=false;});
				isplaying=true;
				$("#status").text("fromLeft:"+fromLeft+" toLeft:"+toLeft + " time:" + time);
			}
			cb=here;
			topic = {name:topics[cb].data('name'), element:topics[cb]};
			window.content.localStorage["cb"]=cb;
		}
	});

//topics tool
	/**
	 * @memberOf topics.js
	 */
	function getLeftHByNumber(number) {
		var totalLeft=beforeH+number*(widthH+distanceH)+widthH/2;
		var middleScreen=screenWidth/2;
		var result=totalLeft-middleScreen;
		var max=totalWidthH-screenWidth;
		if (result < 0) {
			return 0;
		}
		if (result > max) {
			return -max;
		}
		return -result;
	}


//folders/styles drag and drop
	$('.ptfolders').sortable({
		stop: function(event, ui) {
			folderId=$(ui.item).data("id");
			if ($(ui.item).hasClass('noptfolder')) {
				var pairTypeId = $(ui.item).parent().parent().data("id");
				console.log("nopt folder:" + folderId + " index:" + ui.item.index() + " to pairtype " + pairTypeId);
				$(ui.item).removeClass('noptfolder');
				//set the position of the folder in pairtype
				setFolderPairType (topic.name, pairTypeId, folderId, ui.item.index(), ui.item);
			} else {
				console.log("folder:" + folderId + " index:" + ui.item.index());
				//change the position of the folder in pairtype
				updateFolderPosition (topic.name, folderId, ui.item.index(), ui.item);
			}
		}
	});
	$(".ptnull").sortable({
		connectWith: '.ptfolders'
	});

	var inside = false;
	var startindex = -1;
	$('.folderstyles').sortable({
		forceHelperSize: true,
		over: function(e, ui) {inside = true;},
		out: function(e, ui) {inside = false;},
		start: function(e, ui) {
			startindex=ui.item.index();
			console.log("start");
		},
		beforeStop: function (event, ui) {
			if (!inside) {
				if (ui.item.hasClass('stylename')) {
					ui.item.remove();
					return;
				}
				var sideType=ui.item.data("id");
				var pairType=ui.item.parent().parent().data("id");
				ui.item.remove();
				console.log("remove sidetype "+sideType+" from pairtype "+pairType+" index:" + startindex);
				pairTypeRemoveSideType (pairType, sideType, startindex);
		   }
		},
		update: function(event, ui) {
			var sideType=ui.item.data("id");
			var pairType=ui.item.parent().parent().data("id");
			if (ui.item.index() < 0 && !inside) {
				console.log("sort sidetype index: " +  ui.item.index() +"\tinside="+inside);
				return;
			}
			if (ui.item.hasClass('stylename')) {
				ui.item.addClass('folderstyle').removeClass('stylename');
				ui.item.css('width', 'auto');
				ui.item.css('height', 'auto');
				$(".folderstyles").sortable("refresh");
				console.log("insert sidetype "+sideType+" in pairtype "+pairType+" index:" + ui.item.index() +"\tinside="+inside);
				pairTypeInsertSideType (pairType, sideType, ui.item.index());
			} else if (ui.item.index() >= 0){
				console.log("sort sidetype "+sideType+" in pairtype "+pairType+" index: "+startindex+"->" + ui.item.index() +"\tinside="+inside);
				pairTypeSortSideType (pairType, sideType, startindex, ui.item.index());
			}
		}
	});

	$('.stylename').draggable({
		revert: "invalid",
		helper: 'clone',
		revertDuration: 750,
		connectToSortable: ".folderstyles"
	});

//topic dialogs
	//add topic
	$(function() {
		$("#addtopicdialog").dialog({autoOpen: false});
		$("#addtopicbutton").on("click", function() {$("#addtopicdialog").dialog("open");});
	});
	$("#addtopicsubmit").click(function(e) {
		var newTopicName = $("#addtopicname").val().trim();
		$('#addtopicname').val('');
		$('#addtopicdialog').dialog('close');
		if (newTopicName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addTopic(newTopicName);
		}
	});

	//rename topic
	$(function() {
		$("#renametopicdialog").dialog({autoOpen: false});
		$("#renametopicbutton").on("click", function() {
			$("#renametopicdialog").dialog("open");
			$("#renametopicname").val(topic.name);
		});
	});
	$("#renametopicsubmit").click(function(e) {
		var newTopicName = $("#renametopicname").val().trim();
		console.log("rename " + topic.name + " to " + newTopicName);
		$('#renametopicname').val('');
		$('#renametopicdialog').dialog('close');
		if (newTopicName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameTopic(topic.name, newTopicName);
		}
	});

	//delete topic
	$("#deletetopicbutton").click(function(e) {
		var countFolders=topic.element.find(".folder").length;
		var message = "Topic " + topic.name + " has " + countFolders + 
					(countFolders == 1 ? " child." : " children.");
		console.log(message);
		if (countFolders > 0) {
			alert (message + "\nOnly empty topics can be removed.");
		} else {
			$("#confirmdeletetopicdialog").html("Confirm Delete Topic " + topic.name);
			$("#confirmdeletetopicdialog").dialog({ resizable: false, modal: true,
				title: "Modal", height: 250, width: 400, buttons: {
					"Yes": function () {
						$(this).dialog('close');
						deleteTopic (topic.name);},
					"No": function () {$(this).dialog('close');}
				}
			});
		}
	});

//folder dialogs
	//add folder
	$(function() {
		$("#addfolderdialog").dialog({autoOpen: false});
		$(".topicaddfolder").on("click", function() {$("#addfolderdialog").dialog("open");});
	});
	$("#addfoldersubmit").click(function(e) {
		var newFolderName = $("#addfoldername").val().trim();
		$('#addfoldername').val('');
		$('#addfolderdialog').dialog('close');
		if (newFolderName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addFolder(topic.name, newFolderName);
		}
	});

	//rename folder
	var folderToRename="";
	$(function() {
		$("#renamefolderdialog").dialog({autoOpen: false});
		$(".renamefolder").on("click", function(e) {
			var parent=$(e.target).parent();
			folderToRename=parent.data("id");
			$("#renamefoldername").val(parent.children(".foldername").text());
			$("#renamefolderdialog").dialog("open");
		});
	});
	$("#renamefoldersubmit").click(function(e) {
		var newFolderName = $("#renamefoldername").val().trim();
		console.log("rename " + folderToRename + " to " + newFolderName);
		$('#renamefoldername').val('');
		$('#renamefolderdialog').dialog('close');
		if (newFolderName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameFolder(topic.name, folderToRename, newFolderName);
		}
	});

	//delete folder
	$(".deletefolder").click(function(e) {
		var parent=$(e.target).parent();
		var fdel=parent.data("id");
		//TODO: check if folder empty
		$("#confirmdeletefolderdialog").html("Confirm Delete Folder");
		$("#confirmdeletefolderdialog").dialog({ resizable: false, modal: true,
			title: "Modal", height: 250, width: 400, buttons: {
				"Yes": function () {
					$(this).dialog('close');
					deleteFolder (topic.name, fdel);},
				"No": function () {$(this).dialog('close');}
			}
		});
	});

//style dialogs
	//add style
	$(function() {
		$("#addstyledialog").dialog({autoOpen: false});
		$(".topicaddstyle").on("click", function() {$("#addstyledialog").dialog("open");});
	});
	$("#addstylesubmit").click(function(e) {
		var newStyleName = $("#addstylename").val().trim();
		$('#addstylename').val('');
		$('#addstyledialog').dialog('close');
		if (newStyleName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addStyle(topic.name, newStyleName);
		}
	});

	//rename style
	var styleToRename="";
	$(function() {
		$("#renamestyledialog").dialog({autoOpen: false});
		$(".renamestyle").on("click", function(e) {
			var parent=$(e.target).parent();
			styleToRename=parent.data("id");
			$("#renamestyledialog").dialog("open");
		});
	});
	$("#renamestylesubmit").click(function(e) {
		var newStyleName = $("#renamestylename").val().trim();
		console.log("rename " + styleToRename + " to " + newStyleName);
		$('#renamestylename').val('');
		$('#renamestyledialog').dialog('close');
		if (newStyleName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameStyle(topic.name, styleToRename, newStyleName);
		}
	});

	//delete style
	$(".deletestyle").click(function(e) {
		var parent=$(e.target).parent();
		var styleToDelete = parent.data("id");
		$("#confirmdeletestyledialog").html("Confirm Delete Style");
		$("#confirmdeletestyledialog").dialog({ resizable: false, modal: true,
			title: "Modal", height: 250, width: 400, buttons: {
				"Yes": function () {
					$(this).dialog('close');
					deleteStyle (topic.name, styleToDelete);},
				"No": function () {$(this).dialog('close');}
			}
		});
	});

	/**
	 * @memberOf topics.js
	 */
//topic methods
	function addTopic (newTopicName) {
		$.post("topic-servlet", {'method': "add", 'topic-name': newTopicName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "added") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("topic already exists: " + newTopicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function renameTopic (topicName, newTopicName) {
		$.post("topic-servlet", {'method': "rename", 'topic-name': topicName, 'rename-to': newTopicName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "renamed") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("topic already exists: " + newTopicName);
			} else if (state=="notfound") {
				alert ("topic not found: " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function deleteTopic (deleteTopicName) {
		console.log("delete " + deleteTopicName);
		$.post("topic-servlet", {'method': "delete", 'topic-name': deleteTopicName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "deleted") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="notfound") {
				alert ("topic not found: " + deleteTopicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
//folder methods
	function addFolder (topicName, newFolderName) {
		$.post("folder-servlet", {'method': "add", 'topic-name': topicName, 'folder-name': newFolderName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "added") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("folder already exists: " + newFolderName + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function renameFolder (topicName, folderId, newFolderName) {
		$.post("folder-servlet", {'method': "rename", 'topic-name': topicName,
														'folder-id': folderId, 'rename-to': newFolderName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "renamed") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("folder already exists: " + newFolderName + " in topic " + topicName);
			} else if (state=="notfound") {
				alert ("folder not found: " + folderName + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function deleteFolder (topicName, deleteFolderId) {
		console.log("delete folder " + deleteFolderId);
		$.post("folder-servlet", {'method': "delete", 'topic-name': topicName, 'folder-id': deleteFolderId}, function(data,status, xhr) {
			var state=data.state;
			if (state == "deleted") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="notfound") {
				alert ("folder not found: " + deleteFolderId + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function updateFolderPosition (topicName, folderId, position, folder) {
		folder.prop('disabled', true);
		$.post("folder-servlet", {'method': "position", 'topic-name': topicName,
			'folder-id': folderId, 'position': position}, function(data,status, xhr) {
			var state=data.state;
			if (state == "ok") {
				console.log("New position: OK");
				folder.prop('disabled', false);
			} else {
				alert ("Problem updating position");
				window.content.localStorage["cb"]=cb;
				location.reload();
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	//set the position of the folder in pairtype
	function setFolderPairType (topicName, pairTypeId, folderId, position, folder) {
		$.post("folder-servlet", {'method': "setpairtype", "pairtype-id": pairTypeId,
			'topic-name': topicName, 'folder-id': folderId, 'position': position}, function(data,status, xhr) {
			var state=data.state;
			if (state == "ok") {
				console.log("New pairtype: OK");
			} else {
				alert ("Problem updating pairtype");
				window.content.localStorage["cb"]=cb;
				location.reload();
			}
		});
	}

//style methods
	/**
	 * @memberOf topics.js
	 */
	function addStyle (topicName, newStyleName) {
		$.post("styles-servlet", {'method': "add", 'topic-name': topicName, 'sidetype-name': newStyleName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "added") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("style already exists: " + newStyleName + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function renameStyle (topicName, styleId, newStyleName) {
		$.post("styles-servlet", {'method': "rename", 'topic-name': topicName,
														'sidetype-id': styleId, 'rename-to': newStyleName}, function(data,status, xhr) {
			var state=data.state;
			if (state == "renamed") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="alreadyexists") {
				alert ("style already exists: " + newStyleName + " in topic " + topicName);
			} else if (state=="notfound") {
				alert ("style not found: " + styleId + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function deleteStyle (topicName, deleteStyleId) {
		console.log("delete " + deleteStyleId);
		$.post("styles-servlet", {'method': "delete", 'topic-name': topicName, 'sidetype-id': deleteStyleId}, function(data,status, xhr) {
			var state=data.state;
			if (state == "deleted") {
				console.log(data);
//				alert (data.message);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="inpairtpe") {
				alert ("style is used in pairtype");
			} else if (state=="notfound") {
				alert ("style not found: " + deleteStyleId + " in topic " + topicName);
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function pairTypeRemoveSideType (pairType, sideType, index) {
		$.post("pairtype-servlet", {'method': "delete", 'pairtype': pairType, 'sidetype': sideType,
			'index': index}, function (data, status, xhr) {
			var state=data.state;
			if (state == "deleted") {
				console.log(data);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="error") {
				alert ("error");
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function pairTypeInsertSideType (pairType, sideType, index) {
		$.post("pairtype-servlet", {'method': "insert", 'pairtype': pairType, 'sidetype': sideType,
			'index': index}, function (data, status, xhr) {
			var state=data.state;
			if (state == "inserted") {
				console.log(data);
				window.content.localStorage["cb"]=cb;
				location.reload();
			} else if (state=="error") {
				alert ("error");
			}
		});
	}

	/**
	 * @memberOf topics.js
	 */
	function pairTypeSortSideType (pairType, sideType, startindex, endindex) {
		$.post("pairtype-servlet", {'method': "sort", 'pairtype': pairType, 'sidetype': sideType,
			'startindex': startindex, 'endindex': endindex}, function (data, status, xhr) {
			var state=data.state;
			if (state == "sorted") {
				console.log(data);
//				window.content.localStorage["cb"]=cb;
//				location.reload();
			} else if (state=="error") {
				alert ("error");
			}
		});
	}
});
