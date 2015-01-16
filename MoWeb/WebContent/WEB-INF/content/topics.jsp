<%@page import="com.naens.moweb.dao.UserDao"%>
<html>
  <%@taglib uri="/struts-tags" prefix="s"%>
  <%@ page contentType="text/html; charset=UTF-8"%>
	<head>
		<meta charset="utf-8"/>
		<link rel="stylesheet" type="text/css" href="css/moweb.css">
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.css">
		<script src="js/ext/jquery-2.1.1.js"></script>
		<script src="js/ext/jquery-ui.js"></script>
		<script src="js/topics.js"></script>
		<title>Topics</title>
	</head>
	<body>
		<div class="left-panel">
			<div class="topics-panel">
				<h1>Topics</h1>
				<div class="topic-list">
					<s:set name="topic" value="currentTopic"/>
					<s:iterator status="topicstatus1" value="topics">
						<div class="topic"	data-id   = "<s:property value =  'id' />"
											data-name = "<s:property value = 'name'/>"	>
							<s:property value='name'/>
						</div>
					</s:iterator>
				</div>
				<div class="topics-buttons">
					<div class="add-topic-button"></div>
					<div class="delete-topic-button"></div>
				</div>
			</div>
			<div class="styles-panel">
				<h1>Styles</h1>
				<div class="styles-list"></div>
				<div class="styles-buttons">
					<div class="add-style-button"></div>
					<div class="delete-style-button"></div>
				</div>
			</div>
		</div>
		<div class="center-panel"></div>

		<div class="footer"></div>

<!-- 	FORMS -->

		<div id="add-topic-dialog" title="Add new Topic">
			<label>Name:</label>
			<input id="add-topic-name" name="name" type="text">
			<input id="add-topic-submit" type="button" value="Submit">
		</div>
		<div id="rename-topic-dialog" title="Rename Topic">
			<label>Name:</label>
			<input id="rename-topic-name" name="name" type="text">
			<input id="rename-topic-submit" type="button" value="Submit">
		</div>
		<div id="confirm-delete-topic-dialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
		</div>

		<div id="add-style-dialog" title="Add new Topic">
			<label>Name:</label>
			<input id="add-style-name" name="name" type="text">
			<input id="add-style-submit" type="button" value="Submit">
		</div>
		<div id="rename-style-dialog" title="Rename Topic">
			<label>Name:</label>
			<input id="rename-style-name" name="name" type="text">
			<input id="rename-style-submit" type="button" value="Submit">
		</div>
		<div id="confirm-delete-style-dialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
		</div>

		<div id="add-folder-dialog" title="Add new Topic">
			<label>Name:</label>
			<input id="add-folder-name" name="name" type="text">
			<input id="add-folder-submit" type="button" value="Submit">
		</div>
		<div id="rename-folder-dialog" title="Rename Topic">
			<label>Name:</label>
			<input id="rename-folder-name" name="name" type="text">
			<input id="rename-folder-submit" type="button" value="Submit">
		</div>
		<div id="confirm-delete-folder-dialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
		</div>

		<div id="add-file-dialog" title="Add new Topic">
			<label>Name:</label>
			<input id="add-file-name" name="name" type="text">
			<input id="add-file-submit" type="button" value="Submit">
		</div>
		<div id="rename-file-dialog" title="Rename Topic">
			<label>Name:</label>
			<input id="rename-file-name" name="name" type="text">
			<input id="rename-file-submit" type="button" value="Submit">
		</div>
		<div id="confirm-delete-file-dialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
		</div>
	</body>
</html>