<%@page import="com.naens.moweb.dao.UserDao"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<div>
	<div class="styles-list">
		<s:iterator status="stylestatus" value='getSideTypes(topic)'>
			<div class="style" data-id="<s:property value='id'/>"><s:property value='name'/></div>
		</s:iterator>
	</div>
	<div class="center-panel">
		<s:iterator status="ptypestatus" value="getPairTypes(topic)">
			<div class="pair-type" data-id="<s:property value='id'/>">
				<div class="folders-panel">
					<div class="folders-list">
						<s:iterator status="folderstatus" value="getFoldersByPairTypeSortedByNumber(top)">
							<div class="folder" data-id="<s:property value='id'/>">
								<div class="folder-title"><s:property value='name'/></div>
								<div class="folder-files-number">(<s:property value='files.size'/> files)</div>
								<div class="delete-folder-button"></div>
								<div class="files-panel">
									<div class="files-list">
										<s:iterator value="files">
                              	       		<div class="file" data-id="<s:property value='id'/>">
                                        		<div class="file-name"><s:property value='name'/></div>
                                        		<div class="file-ih"><div class="delete-file-button"></div></div>
                                    		</div>
                                    	</s:iterator>
									</div>
                                	<div class="add-file-button"></div>
								</div>
							</div>
						</s:iterator>
					</div>
				</div>
				<div class="side-styles">
					<s:iterator status="fstylesstatus" value="sideTypes">
						<div class="side-style" data-id="<s:property value='id'/>"><s:property value="name"/></div>
					</s:iterator>
				</div>
			</div>
		</s:iterator>
		<s:set name="noptFolders" value="getFoldersByPairTypeSortedByNumber(topic)"/>
		<s:if test="#noptFolders.size > 0">
			<div class="pair-type">
				<div class="folders-panel">
					<div class="folders-list nopt-folders-list">
						<s:iterator status="folderstatus" value="noptFolders">
							<div class="folder" data-id="<s:property value='id'/>">
								<div class="folder-title"><s:property value='name'/></div>
								<div class="delete-folder-button"></div>
							</div>
						</s:iterator>
					</div>
				</div>
				<div class="side-styles">
					<s:iterator status="fstylesstatus" value="sideTypes">
						<div class="side-style" data-id="<s:property value='id'/>"><s:property value="name"/></div>
					</s:iterator>
				</div>
			</div>
		</s:if>
		<div class="add-folder-button"></div>
	</div>
</div>