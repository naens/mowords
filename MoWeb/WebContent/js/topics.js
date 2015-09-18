var topics = {};
var topicId = 0;
var addFileFolderId = -1;
var renFolderId = -1;
var renFileId = -1;
var renFileFolderId = -1;
$(document).ready(function() {

	requestTopic ();

	//topic dialog buttons
	$(function() {
		$("#add-topic-dialog").dialog({autoOpen: false});
		$(".add-topic-button").on("click", function() {$("#add-topic-dialog").dialog("open");});
	});
	$("#add-topic-submit").click(function(e) {
		var newTopicName = $("#add-topic-name").val().trim();
		$('#add-topic-name').val('');
		$('#add-topic-dialog').dialog('close');
		if (newTopicName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addTopic(newTopicName);
		}
	});

	$(function() {
		$("#rename-topic-dialog").dialog({autoOpen: false, modal: true});
		$(".topic").dblclick(function() {
			if ($(this).hasClass("current-topic")) {
				$("#rename-topic-dialog").dialog("open");
				var topicName = $(this).text().trim();
				var $nameInput = $("#rename-topic-name");
				$nameInput.val(topicName);
				$nameInput.focus();
				$nameInput[0].setSelectionRange(topicName.length, topicName.length);
			}
		});
	});
	$("#rename-topic-submit").click(function(e) {
		var newTopicName = $("#rename-topic-name").val().trim();
		$('#rename-topic-name').val('');
		$('#rename-topic-dialog').dialog('close');
		if (newTopicName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameTopic(topicId, newTopicName);
		}
	});

	$(".delete-topic-button").click(function(e) {
		var countFolders=$(".folder").length;
		var message = "Topic " + topics[topicId] + " has " + countFolders + 
					(countFolders == 1 ? " child." : " children.");
		console.log(message);
		if (countFolders > 0) {
			alert (message + "\nOnly empty topics can be removed.");
		} else {
			$("#confirm-delete-topic-dialog").html("Confirm Delete Topic " + topics[topicId]);
			$("#confirm-delete-topic-dialog").dialog({ resizable: false, modal: true,
				title: "Modal", height: 250, width: 400, buttons: {
					"Yes": function () {
						$(this).dialog('close');
						deleteTopic (topicId);},
					"No": function () {$(this).dialog('close');}
				}
			});
		}
	});

	//styles
	//sortable: styles
	var stylePosition = -1;
	$(".styles-list").sortable({
		connectWith: ".side-styles",
		helper: 'clone',
		start: function(event, ui) {
			stylePosition=ui.item.index();
		},
		stop: function(event, ui) {
			var id=$(ui.item).data("id");
			var position=ui.item.index();
			var $pairtype = $(ui.item).closest('.pair-type');
			if ($pairtype.hasClass('pair-type')) {	//move to pairtype
				addStyleToPairType($pairtype.data("id"), id, position);
			} else { 								//change style position
				if (stylePosition != position) {
					positionStyle(topicId, stylePosition, position);
				}
			}
		}
	});

	//styles: dialog buttons
	$(function() {
		$("#add-style-dialog").dialog({autoOpen: false});
		$(".add-style-button").on("click", function() {$("#add-style-dialog").dialog("open");});
	});
	$("#add-style-submit").click(function(e) {
		var newStyleName = $("#add-style-name").val().trim();
		$('#add-style-name').val('');
		$('#add-style-dialog').dialog('close');
		if (newStyleName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addStyle(topicId, newStyleName);
		}
	});

	$("#rename-style-submit").click(function(e) {
		var newStyleName = $("#rename-style-name").val().trim();
		$('#rename-style-name').val('');
		$('#rename-style-dialog').dialog('close');
		if (newStyleName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameStyle($(this).data('styleId'), newStyleName);
		}
	});

	$(".delete-style-button").click(function(e) {
		console.log("style delete not yet implemented");
//		var styleId=...;//TODO
		return;
		var countFolders=$(".folder").length;
		var message = "Style " + styleId + " has " + countFolders + 
					(countFolders == 1 ? " child." : " children.");
		console.log(message);
		if (countFolders > 0) {
			alert (message + "\nOnly empty styles can be removed.");
		} else {
			$("#confirm-delete-style-dialog").html("Confirm Delete Style " + styleId);
			$("#confirm-delete-style-dialog").dialog({ resizable: false, modal: true,
				title: "Modal", height: 250, width: 400, buttons: {
					"Yes": function () {
						$(this).dialog('close');
						deleteStyle (styleId);},
					"No": function () {$(this).dialog('close');}
				}
			});
		}
	});

	//folders dialog buttons
	$("#add-folder-submit").click(function(e) {
		var newFolderName = $("#add-folder-name").val().trim();
		$('#add-folder-name').val('');
		$('#add-folder-dialog').dialog('close');
		if (newFolderName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addFolder(topicId, newFolderName);
		}
	});

	$("#rename-folder-submit").click(function(e) {
		var newFolderName = $("#rename-folder-name").val().trim();
		$('#rename-folder-name').val('');
		$('#rename-folder-dialog').dialog('close');
		if (newFolderName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameFolder(renFolderId, newFolderName);
		}
	});

	//file dialog submit buttons
	$("#add-file-submit").click(function(e) {
		var newFileName = $("#add-file-name").val().trim();
		$('#add-file-name').val('');
		$('#add-file-dialog').dialog('close');
		if (newFileName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			addFile(addFileFolderId, newFileName);
		}
	});

	$("#rename-file-submit").click(function(e) {
		var newFileName = $("#rename-file-name").val().trim();
		$('#rename-file-name').val('');
		$('#rename-file-dialog').dialog('close');
		if (newFileName === '') {
			alert("Please fill name!");
			e.preventDefault();
		} else {
			renameFile(renFileFolderId, renFileId, newFileName);
		}
	});
});

function _foldertitle_click ($folder_title) {
	var ptFolderId = {};
	var clickTime = 0;
	var $filesPanelShown = '';
	var $filesPanelHidden = '';
	var firstClick = false;
	$folder_title.click(function(){
		firstClick = clickTime == 0;
		if (firstClick) {
			clickTime = new Date().getTime();
		}
		setTimeout(function(){$filesPanelShown = '';$filesPanelHidden = ''; clickTime = 0;}, 500);
		var $ft = $(this);
		setTimeout(function(){
			var $folder = $ft.parents('.folder');
			var $foldersList = $folder.parents('.folders-list');
			if ($foldersList.hasClass('noclick')) {
				$foldersList.removeClass('noclick');
			} else if (firstClick) {
				var $filesPanel = $folder.children(".files-panel");
				if ($filesPanel.length) {
					$foldersList.children().each(function() {
						var $fp = $(this).children(".files-panel");
						if ($fp.is(":visible")) {
							$fp.slideUp("slow", "swing");
							$filesPanelHidden=$fp;
						}
					});
	
					if ($filesPanel.is(":visible")) {
						$filesPanel.slideUp("slow", "swing");
						$filesPanel.removeClass("shownFilesPanel");
						$filesPanelHidden = $filesPanel;
					} else {
						$filesPanel.slideDown("slow", "swing");
						$filesPanel.addClass("shownFilesPanel");
						var pairTypeId = $folder.parents('.pair-type').data('id');
						ptFolderId [pairTypeId] = $folder.data('id');
						$filesPanelShown = $filesPanel;
					}
				}
			}
		}, 80);
	});

	$folder_title.dblclick(function() {
		var dTime = new Date().getTime();
		var delay = dTime - clickTime;
		clickTime = 0;
		if (delay < 500) {
			if ($filesPanelShown != '') {
				$filesPanelShown.hide().stop(true, true);
			}
			if ($filesPanelHidden != '') {
				$filesPanelHidden.stop(true, true);
				$filesPanelHidden.slideDown(0);
			}
		}
		$("#rename-folder-dialog").dialog("open");
		var folderName = $(this).text().trim();
		var $nameInput = $("#rename-folder-name");
		$nameInput.val(folderName);
		$nameInput.focus();
		$nameInput[0].setSelectionRange(folderName.length, folderName.length);
		renFolderId = $(this).parents('.folder').data('id');
	});
	
}

function _folderdelete_click($deleteFolderButton) {
	$deleteFolderButton.click(function() {
		var $folder = $(this).parents('.folder');
		var folderId = $folder.data('id');
		var folderName = $folder.children('.folder-title').text();
		var countFiles=$(".shownFilesPanel").find('.file').length;
		var message = "Folder " + folderName + " has " + countFiles + 
					(countFiles == 1 ? " file." : " files.");
		console.log(message);
//TODO	if (countFiles > 0) {
//			alert (message + "\nOnly empty folders can be removed.");
//		} else {
			$("#confirm-delete-folder-dialog").html("Confirm Delete Folder " + folderName);
			$("#confirm-delete-folder-dialog").dialog({ resizable: false, modal: true,
				title: "Modal", height: 250, width: 400, buttons: {
					"Yes": function () {
						$(this).dialog('close');
						deleteFolder (folderId);
					},
//						deleteFolder (ptFolderId [pairTypeId]);},
					"No": function () {$(this).dialog('close');}
				}
			});
//		}
	});
}

function _foldertitle_sortable() {
	var folderPosition = -1;
	var isSourceNopt = false;
	var $pairtypeSource = "";
	$(".folders-list").sortable({
		start: function(event, ui) {
			$(this).addClass('noclick');
			folderPosition=ui.item.index();
			$pairtypeSource = $(ui.item).parents('.pair-type');
			isSourceNopt = $(ui.item).parents('.pair-type').data('id') == undefined;
			console.log("isNopt="+isSourceNopt);
		},
		stop: function(event, ui) {
			console.log("folderlist sorttable stop");
			var id=$(ui.item).data("id");
			var position=ui.item.index();
			var $pairtype = $(ui.item).parents('.pair-type');
			var pairtypeId = $pairtype.data('id');
			if (isSourceNopt && pairtypeId != undefined) {
				setPairTypeForFolder(pairtypeId, id, position);
				if (!$pairtypeSource.find('.folder').length) {
					$pairtypeSource.remove();
				}
			} else if (folderPosition != position) {
				positionFolder(topicId, pairtypeId, folderPosition, position);
			}
		}
	});

	$(".nopt-folders-list").sortable({
		connectWith: ".folders-list"
	});
}

function _file_rename_dbclick($files) {
	$(function() {
		$("#rename-file-dialog").dialog({autoOpen: false, modal: true});
		$files.dblclick(function() {
			$("#rename-file-dialog").dialog("open");
			var fileName = $(this).text().trim();
			var $nameInput = $("#rename-file-name");
			renFileId = $(this).data('id');
			renFileFolderId = $(this).parents('.folder').data('id');
			$nameInput.val(fileName);
			$nameInput.focus();
			$nameInput[0].setSelectionRange(fileName.length, fileName.length);
		});
	});
	
}

function _file_delete_click ($files) {
	$files.find(".delete-file-button").click(function(e) {
		var $file = $(this).parents('.file');
		var fileId = $file.data('id');
		var fileName = $file.children('.file-name').text();
		$("#confirm-delete-file-dialog").html("Confirm Delete File " + fileName);
		$("#confirm-delete-file-dialog").dialog({ resizable: false, modal: true,
			title: "Modal", height: 250, width: 400, buttons: {
				"Yes": function () {
					$(this).dialog('close');
					deleteFile (fileId);
				},
				"No": function () {$(this).dialog('close');}
			}
		});
	});
}

function initTopic () {

	if (!$(".topics-panel").hasClass(".topic-list")){

		$('.topic').each(function() {
			topics[$(this).data("id")] = $(this).data('name');
			//console.log("topics: " + $(this).data("id") + "->" + $(this).data('name'));
		});
	
		//topics
		//sortable: topics
		var topicPosition = -1;
		$(".topic-list").sortable({
			start: function(event, ui) {
				topicPosition=ui.item.index();
				$(this).addClass('noclick');
			},
			stop: function(event, ui) {
				var position=ui.item.index();
				if (topicPosition != position) {
					positionTopic(topicPosition, position);
				}
			}
		});
	
		$(".topic").click(function() {
			var $topicsList = $(this).closest(".topic-list");
			if ($topicsList.hasClass('noclick')) {
				$topicsList.removeClass('noclick');
			} else if (!$(this).hasClass("current-topic")) {
				var id = $(this).data("id");
				requestTopic (id);
				console.log('current topic [click]: ' + topicId);
				topicId = id;
			}
		});

	}

	//style rename function
	$(function() {
		$("#rename-style-dialog").dialog({autoOpen: false, modal: true});
		$(".style").dblclick(function() {
			console.log("dbc!");
			$("#rename-style-submit").data('styleId', $(this).data('id'));
			$("#rename-style-dialog").dialog("open");
			var styleName = $(this).text().trim();
			var $nameInput = $("#rename-style-name");
			$nameInput.val(styleName);
			$nameInput.focus();
			$nameInput[0].setSelectionRange(styleName.length, styleName.length);
		});
	});

	//folders
	//sortable: folders
	_foldertitle_sortable();

	_foldertitle_click($(".folder-title"));

	//folders: dialog buttons
	$(function() {
		$("#add-folder-dialog").dialog({autoOpen: false});
		$(".add-folder-button").on("click", function() {$("#add-folder-dialog").dialog("open");});
	});

	$(function() {
		$("#rename-folder-dialog").dialog({autoOpen: false, modal: true});
	});

	_folderdelete_click($(".delete-folder-button"));

	//pairtypes
	//sortable: pairtype styles
	var inside = false;
	var sideTypePosition = -1;
	var doDelete = false;
	$(".side-styles").sortable({
		start: function(event, ui) {
			sideTypePosition=ui.item.index();
		},
		over: function(e, ui) {inside = true;},
		out: function(e, ui) {inside = false;},
		beforeStop: function (event, ui) {
			doDelete = !inside;
			if (!inside) {
				var pairTypeId=$(ui.item).closest('.pair-type').data("id");
				var styleId=$(ui.item).data("id");
				deleteStyleFromPairType(pairTypeId, styleId, sideTypePosition);
			} else {
				var pairTypeId=$(ui.item).closest('.pair-type').data("id");
				var position=ui.item.index();
				if (sideTypePosition != position) {
					positionStyleInPairType(pairTypeId, sideTypePosition, position);
				}
			}
		},
		stop: function(event, ui) {
			if (doDelete) {
				$(ui.item).remove();
				$(ui.placeholder).remove();
			}
		}
	});

	//files
	//sortable: files
	var $oldPairTypeId="";
	var $oldFolderId="";
	var filePosition = -1;
	$(".files-list").sortable({
		start: function(event, ui) {
			filePosition=ui.item.index();
			$oldPairTypeId=$(ui.item).parents(".pair-type").data("id");
			$oldFolderId=$(ui.item).parents(".folder").data("id");
			console.log('$oldPairTypeId='+$oldPairTypeId);
			$(ui.item).find('.delete-file-button').remove();
		},
		stop: function(event, ui) {
			var folderId = $(ui.item).closest(".folder").data("id");
			$("<div/>", {"class": "delete-file-button"}).appendTo($(ui.item).find('.file-ih'));
			var position=ui.item.index();
			if (filePosition != position) {
				positionFile (folderId, filePosition, position);
			}
		}
	});
	$(".folder").droppable({
		accept: function($draggable) {
			var $newPairTypeId=$(this).parents(".pair-type").data("id");
			var $newFolderId=$(this).data("id");
			return  $newPairTypeId == $oldPairTypeId &&
			 $newFolderId != $oldFolderId && $draggable.hasClass("file");
		},
		hoverClass: "folder-title-hover",
		drop: function(event, ui) {
			var $file=$(ui.draggable);
			var $newFolder=$(event.target).closest(".folder");
			moveFileToFolder($file.data('id'), $newFolder.data('id'));
		}
	});

	//files: dialog buttons
	$(function() {
		$("#add-file-dialog").dialog({autoOpen: false});
		$(".add-file-button").on("click", function() {
			$("#add-file-dialog").dialog("open");
			addFileFolderId = $(this).parents('.folder').data('id');
		});
	});

	_file_rename_dbclick($(".file"));
	_file_delete_click($(".file"));
}

//database request functions (post)
function requestTopic (id) {
	console.log('request topic');
	$.post("topic.html", {'topicId': id}, function (data, status, xhr) {
		$(".topics-panel").html($(data).children(".topics-panel").html());
		$(".center-panel").html($(data).children(".center-panel").html());
		$(".styles-list").html($(data).children(".styles-list").html());
		$(".current-topic").removeClass("current-topic");
		$('.topic').each(function() {
			if (id == $(this).data("id")) {
				$(this).addClass("current-topic");
			}
		});
		topicId = $(data).children(".topic-id").html()
		console.log('found topicId: ' + topicId);
		initTopic();
	});
}

//topic update functions (topic-servlet: add, rename, delete, position)
function addTopic (topicName) {
	console.log ("add topic: " + topicName);
	$.post("topic-servlet", {'method': "add", 'topicName': topicName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "added") {
			$( "<div/>", {
				"class": "topic", 
				"data-id": data.topicId,
				"data-name": topicName,
				text: topicName
			}).appendTo('.topic-list');
		} else if (state=="alreadyexists") {
			alert ("topic already exists: " + topicName);
		}
	}, "json");
}

function renameTopic (topicId, topicName) {
	console.log ("rename topic: " + topicId + " to " + topicName);
	$.post("topic-servlet", {'method': "rename", 'topicId': topicId, 'topicName': topicName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "renamed") {
			$(".topic[data-id='" + topicId + "']").text(topicName);
		} else if (state=="alreadyexists") {
			alert ("topic already exists: " + topicName);
		} else if (state=="notfound") {
			alert ("topic not found: " + topicName);
			location.reload();
		}
	});
}

function deleteTopic (topicId) {
	console.log ("delete topic: " + topicId);
	$.post("topic-servlet", {'method': "delete", 'topicId': topicId}, function(data) {
		var state = data.state;
		console.log(state);
	});
}

function positionTopic (startPosition, endPosition) {
	console.log ("position topic from " + startPosition + " to " + endPosition);
	$.post("topic-servlet", {'method': "position", 'startPosition': startPosition, 'endPosition': endPosition}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "positioned") {
			alert("Something occured in function 'positionTopic'");
			location.reload();
		}
	});
}

//style update functions (styles-servlet: add, rename, delete, position)
function addStyle (topicId, styleName) {
	console.log ("add style: " + styleName + " to topic " + topicId);
	$.post("styles-servlet", {'method': "add", 'topicId': topicId, 'styleName': styleName }, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "added") {
			$( "<div/>", {
				"class": "style", 
				"data-id": data.styleId,
				text: styleName
			}).appendTo('.styles-list');
		} else if (state=="alreadyexists") {
			alert ("style already exists: " + styleName);
		}
	});
}

function renameStyle (styleId, styleName) {
	console.log ("rename style: " + styleId + " to " + styleName);
	$.post("styles-servlet", {'method': "rename", 'styleId': styleId, 'styleName': styleName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "renamed") {
			$(".style[data-id='" + styleId + "']").text(styleName);
		} else if (state=="alreadyexists") {
			alert ("style already exists: " + topicName);
		} else if (state=="notfound") {
			alert ("style not found: " + topicName);
			location.reload();
		}
	});
}

function deleteStyle (styleId) {
	console.log ("delete style: " + styleId);
	$.post("styles-servlet", {'method': "delete", 'styleId': styleId}, function(data) {
		var state = data.state;
		console.log(state);
	});
}

function positionStyle (topicId, startPosition, endPosition) {
	console.log ("position style in topic " + topicId + ": " + startPosition + " to " + endPosition);
	$.post("styles-servlet", {'method': "position", 'topicId': topicId,
		'startPosition' : startPosition, 'endPosition' : endPosition}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "positioned") {
			alert("Something occured in function 'positionStyle'");
			location.reload();
		}
	});
}

//folder update functions (folders-servlet: add, rename, delete, position)
function addFolder (topicId, folderName) {
	console.log ("add folder: " + folderName + " to topic " + topicId);
	$.post("folders-servlet", {'method': "add", 'topicId': topicId, 'folderName': folderName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "added") {
			var $folderList = $('.nopt-folders-list');
			if(!$folderList.length) {
				//create new empty pairtype
				var $pairtype = $("<div/>", {"class": "pair-type"});
				$pairtype.insertBefore(".add-folder-button");
				var $folderPanel = $("<div/>", {"class": "folders-panel"});
				$folderPanel.appendTo($pairtype);
				$folderList = $("<div/>", {"class": "folders-list"});
				$folderList.addClass('nopt-folders-list');
				$folderList.appendTo($folderPanel);
				_foldertitle_sortable();
//				$folderList.sortable({
//					connectWith: ".folders-list"
//				});
			}
			var $folder = $("<div/>", {"class": "folder","data-id": data.folderId});
			$folder.appendTo($folderList);
			var $folderTitle = $("<div/>", {"class": "folder-title", text: folderName});
			$folderTitle.appendTo($folder);
			_foldertitle_click($folderTitle);
			var $deleteFolderButton = $("<div/>", {"class": "delete-folder-button"});
			_folderdelete_click($deleteFolderButton);
			$deleteFolderButton.appendTo($folder);
		} else if (state=="alreadyexists") {
			alert ("folder already exists: " + folderName);
		}
	});
}

function renameFolder (folderId, folderName) {
	console.log ("rename folder: " + folderId + " to " + folderName);
	$.post("folders-servlet", {'method': "rename", 'folderId': folderId, 'folderName': folderName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "renamed") {
			$(".folder[data-id='" + folderId + "']").find('.folder-title').text(folderName);
		} else if (state=="alreadyexists") {
			alert ("folder already exists: " + folderName);
		} else if (state=="notfound") {
			alert ("folder not found: " + folderName);
			location.reload();
		}
	});
}

function deleteFolder (folderId) {
	console.log ("delete folder: " + folderId);
	$.post("folders-servlet", {'method': "delete", 'folderId': folderId}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "deleted") {
			var $folder = $(".folder[data-id='" + folderId + "']");
			var $folderList = $folder.parents('.folders-list');
			var $pairtype = $folderList.parents('.pair-type');
			$folder.remove();
			if (!$folderList.find('.folder').length && !$pairtype.find('.side-style').length) {
				$pairtype.remove();
			}
//			$folder.parent().remove($folder);
			console.log("delete:ok");
		} else if (state=="notfound") {
			alert ("folder not found: " + folderId);
			location.reload();
		}
	});
}

function positionFolder (topicId, pairtypeId, startPosition, endPosition) {
	console.log ("position folder in partype: " + pairtypeId + "; " + startPosition + " to " + endPosition);
	$.post("folders-servlet", {'method': "position", 'topicId': topicId, 'pairtypeId': pairtypeId, 'startPosition': startPosition, 'endPosition': endPosition}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "positioned") {
			alert("Something occured in function 'positionFolder'");
			location.reload();
		}
	});
}

//pairtype update functions (pairtype-servlet: addStyle, moveFolder, setForFolder, position, delete)
function addStyleToPairType (pairTypeId, styleId, position) {
	console.log ("add style: " + styleId + " to pairtype " + pairTypeId + ", position: " + position);
	$.post("pairtype-servlet", {'method': "add", 'pairTypeId': pairTypeId, 'styleId': styleId, 'position': position}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "inserted") {
			alert("Something occured in function 'addStyleToPairType'");
			location.reload();
		}
	});
}

function setPairTypeForFolder (pairTypeId, folderId, position) {
	console.log("set pair type: " + pairTypeId + " for folder " + folderId + ", position: " + position);
	$.post("pairtype-servlet", {'method': "setForFolder", 'pairTypeId': pairTypeId, 'folderId': folderId, 'position': position}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "inserted") {
			alert("Something occured in function 'setPairTypeForFolder'");
			location.reload();
		}
	});
}

function deleteStyleFromPairType (pairTypeId, styleId, position) {
	console.log ("delete style: " + styleId + " from paretype " + pairTypeId + ", position: " + position);
	$.post("pairtype-servlet", {'method': "delete", 'pairTypeId': pairTypeId, 'styleId': styleId, 'position': position}, function(data) {
		var state = data.state;
		console.log(state);
	});
}

function positionStyleInPairType (pairTypeId, startPosition, endPosition) {
	console.log ("position style: " + startPosition + " to " + endPosition + " in pairtype " + pairTypeId);
	$.post("pairtype-servlet", {'method': "position", 'pairTypeId': pairTypeId, 'startPosition': startPosition, 'endPosition': endPosition}, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "positioned") {
			alert("Something occured in function 'positionStyleInPairType'");
			location.reload();
		}
	});
}

//file update functions (file-servlet: add, rename, delete, position, move)
function addFile (folderId, fileName) {
	console.log ("add file: " + fileName + " to folder " + folderId);
	$.post("file-servlet", {'method': "add", 'folderId': folderId, 'fileName': fileName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "added") {
			var $folder = $(".folder[data-id='" + folderId + "']");
			var $fileList = $folder.find('.files-list');

			var $file = $( "<div/>", {"class": "file", "data-id": data.fileId});
			var $fileName = $("<div/>", {"class": "file-name", text: fileName});
			var $fileDeleteButton = $("<div/>", {"class": "delete-file-button"});
			var $fileIh = $("<div/>", {"class": "file-ih"});

			$fileDeleteButton.appendTo($fileIh);
			$fileName.appendTo($file);
			$fileIh.appendTo($file);
			$file.appendTo($fileList);

			var filesNumber = $folder.find('.file').length;
			$folder.children('.folder-files-number').text("("+filesNumber+" files)");
			
			_file_rename_dbclick($file);
			_file_delete_click($file);
		} else if (state=="alreadyexists") {
			alert ("style already exists: " + styleName);
		}
	});
}

function renameFile (folderId, fileId, fileName) {
	console.log ("rename file: " + fileId + " to " + fileName + " in folder " + folderId);
	$.post("file-servlet", {'method': "rename", 'folderId': folderId, 'fileId': fileId, 'fileName': fileName}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "renamed") {
			$(".file[data-id='" + fileId + "']").find('.file-name').text(fileName);
		} else if (state=="alreadyexists") {
			alert ("file already exists: " + fileName);
		} else if (state=="notfound") {
			alert ("file not found: " + fileName);
			location.reload();
		}
	});
}

function deleteFile (fileId) {
	console.log ("delete file: " + fileId);
	$.post("file-servlet", {'method': "delete", 'fileId': fileId}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "deleted") {
			var $file = $(".file[data-id='" + fileId + "']");
			var $folder = $file.parents('.folder');

			$file.remove();

			var filesNumber = $folder.find('.file').length;
			console.log("files in folder: " + filesNumber);
			$folder.children('.folder-files-number').text("("+filesNumber+" files)");
		} else if (state=="notfound") {
			alert ("file not found: " + fileId);
			location.reload();
		}
	});
}

function positionFile (folderId, startPosition, endPosition) {
	console.log ("position file in folder: " + folderId + ", " + startPosition + " to " + endPosition);
	$.post("file-servlet", {'method': "position", 'folderId': folderId, 'startPosition': startPosition, 'endPosition': endPosition }, function(data) {
		var state = data.state;
		console.log(state);
		if (state != "positioned") {
			alert("Something occured in function 'positionFile'");
			location.reload();
		}
	});
}

function moveFileToFolder (fileId, folderId) {
	console.log ("move file: " + fileId + " to folder " + folderId);
	$.post("file-servlet", {'method': "move", 'fileId': fileId, 'folderId': folderId}, function(data) {
		var state = data.state;
		console.log(state);
		if (state == "moved") {
			//delete from old
			var $oldFile = $(".file[data-id='" + fileId + "']");
			var fileName = $oldFile.find('.file-name').text();
			var $oldFolder = $oldFile.parents('.folder');
			$oldFile.remove();
			var oldFolderFilesNumber = $oldFolder.find('.file').length;
			$oldFolder.children('.folder-files-number').text("("+oldFolderFilesNumber+" files)");

			var $oldFilesPanel = $oldFolder.find(".files-panel");
			$oldFilesPanel.slideUp("slow", "swing");

			//insert in new
			var $folder = $(".folder[data-id='" + folderId + "']");
			var $fileList = $folder.find('.files-list');

			var $file = $( "<div/>", {"class": "file", "data-id": fileId});
			var $fileName = $("<div/>", {"class": "file-name", text: fileName});
			var $fileDeleteButton = $("<div/>", {"class": "delete-file-button"});
			var $fileIh = $("<div/>", {"class": "file-ih"});

			$fileDeleteButton.appendTo($fileIh);
			$fileName.appendTo($file);
			$fileIh.appendTo($file);
			$file.appendTo($fileList);

			var filesNumber = $folder.find('.file').length;
			$folder.children('.folder-files-number').text("("+filesNumber+" files)");
			
			_file_rename_dbclick($file);
			_file_delete_click($file);

			var $newFilesPanel = $folder.find(".files-panel");
			$newFilesPanel.slideDown("slow", "swing");
		} else {
			alert("Something occured in function 'moveFileToFolder'");
			location.reload();
		}
	});
}