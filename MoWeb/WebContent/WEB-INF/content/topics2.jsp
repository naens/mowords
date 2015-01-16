<%@page import="com.naens.moweb.dao.UserDao"%>
<html>
  <%@taglib uri="/struts-tags" prefix="s"%>
  <%@ page contentType="text/html; charset=UTF-8"%>
	<head>
		<meta charset="utf-8"/>
		<link rel="stylesheet" type="text/css" href="css/moweb2.css">
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.css">
		<script src="js/ext/jquery-2.1.1.js"></script>
		<script src="js/ext/jquery.keyframes.js"></script>
		<script src="js/ext/jquery-ui.js"></script>
		<script src="js/ext/prefixfree.min.js"></script>
		<script src="js/topics2.js"></script>
		<title>Topics</title>
	</head>
	<body>
		<h1>Topics</h1>
		<div id="w">
			<div id="ct">
				<div id="g"></div>
				<s:iterator status="topicstatus1" value="topics">
					<div class="house" data-id="<s:property value='id'/>" data-name="<s:property value='name'/>"  id='h<s:property value="%{#topicstatus1.index}" />'>

						<div class="topictitle"><s:property value='name'/></div>
						<div class="topicaddfolder">add new folder</div>

						<s:set name="topic" value="top"/>
						<s:iterator status="ptypestatus" value="getPairTypes(#topic)">
							<div class="pairtype" data-id="<s:property value='id'/>">
								<div class="ptfolders">
									<s:iterator status="folderstatus" value="getFoldersByPairTypeSortedByNumber(#topic, top)">
										<div class="folder" data-id="<s:property value='id'/>">
											<div class="foldername"><s:property value='name'/></div>
											<div class="renamefolder">rename</div>
											<div class="deletefolder">x</div>
										</div>
									</s:iterator>
								</div>
								<div class="folderstyles">
									<s:iterator status="fstylesstatus" value="sideTypes">
										<div class="folderstyle" data-id="<s:property value='id'/>"><s:property value="name"/></div>
									</s:iterator>
								</div>
							</div>
						</s:iterator>
						<div class="pairtype">
							<div class="ptfolders ptnull">
								<s:iterator status="folderstatus" value="getFoldersByPairTypeSortedByNumber(#topic, null)">
									<s:set name="has_folders" value="true"/>
									<div class="folder noptfolder" data-id="<s:property value='id'/>">
										<div class="foldername"><s:property value='name'/></div>
										<div class="renamefolder">rename</div>
										<div class="deletefolder">x</div>
									</div>
								</s:iterator>
							</div>
							<s:if test="%{#has_folders}">
								<div class="folderstyles"></div>
							</s:if>
						</div>

						<div class="styles">
							<s:iterator status="stylestatus" value='getSideTypes(#topic)'>
								<div class="style" data-id="<s:property value='id'/>">
									<div class="stylename" data-id="<s:property value='id'/>"><s:property value='name'/></div>
									<div class="renamestyle">rename</div>
									<div class="deletestyle">delete</div>
								</div>
							</s:iterator>
						</div>
						<div class="topicaddstyle">add new style</div>

					</div>
				</s:iterator>
			</div>
			<div id=listpointer></div>
			<div id=ls>
				<s:iterator status="topicstatus2" value="topics">
					<div class="topicitem" id='i<s:property value="%{#topicstatus2.index}" />'> <s:property value='name'/></div>
				</s:iterator>
			</div>
		</div>

		<div id="status"></div>

		<div id="buttonpane">
			<div id="addtopicdialog" title="Add new Topic">
				<label>Name:</label>
				<input id="addtopicname" name="name" type="text">
				<input id="addtopicsubmit" type="button" value="Submit">
			</div>
			<button id="addtopicbutton" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
				<span class="ui-button-text">Add new Topic!</span>
			</button><br/><br/>
	
			<div id="renametopicdialog" title="Rename Topic">
				<label>Name:</label>
				<input id="renametopicname" name="name" type="text">
				<input id="renametopicsubmit" type="button" value="Submit">
			</div>
			<button id="renametopicbutton" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
				<span class="ui-button-text">Rename Topic!</span>
			</button><br/><br/>
	
			<div id="confirmdeletetopicdialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
			</div>
			<button id="deletetopicbutton" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
				<span class="ui-button-text">Delete Topic!</span>
			</button>
	
			<div id="addfolderdialog" title="Add new Folder">
				<label>Name:</label>
				<input id="addfoldername" name="name" type="text">
				<input id="addfoldersubmit" type="button" value="Submit">
			</div>
	
			<div id="renamefolderdialog" title="Rename Folder">
				<label>Name:</label>
				<input id="renamefoldername" name="name" type="text">
				<input id="renamefoldersubmit" type="button" value="Submit">
			</div>
	
			<div id="confirmdeletefolderdialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
			</div>
	
			<div id="addstyledialog" title="Add new Style">
				<label>Name:</label>
				<input id="addstylename" name="name" type="text">
				<input id="addstylesubmit" type="button" value="Submit">
			</div>
	
			<div id="renamestyledialog" title="Rename Style">
				<label>Name:</label>
				<input id="renamestylename" name="name" type="text">
				<input id="renamestylesubmit" type="button" value="Submit">
			</div>
	
			<div id="confirmdeletestyledialog" class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons ui-draggable ui-resizable">
			</div>
		</div>
	</body>
</html>