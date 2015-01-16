package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.dao.FileDao;
import com.naens.moweb.dao.FolderDao;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFile;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.service.FileService;

@WebServlet(value = "/file-servlet")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -6582132157445939196L;

	private FolderDao folderDao = new FolderDao();

	private FileDao fileDao = new FileDao();

	private FileService fileService = new FileService();;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getParameter("method");
		User user = (User) req.getSession().getAttribute("user");
		if (user == null) {
			resp.sendRedirect("");
			return;
		}

		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter printout = resp.getWriter();

		try {
			//file update protected voids (files-servlet: add, rename, delete, position, move)
			if (method.equals("add")) {
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				String fileName = req.getParameter("fileName");
				printout.print(addFile(folderId, fileName));
			} else if (method.equals("rename")) {
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				Long fileId = Long.parseLong(req.getParameter("fileId"));
				String fileName = req.getParameter("fileName");
				printout.print(renameFile(folderId, fileId, fileName));
			} else if (method.equals("delete")) {
				Long fileId = Long.parseLong(req.getParameter("fileId"));
				printout.print(deleteFile(fileId));
			} else if (method.equals("position")) {
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				int startPosition = Integer.parseInt(req.getParameter("startPosition"));
				int endPosition = Integer.parseInt(req.getParameter("endPosition"));
				printout.print(positionFile(folderId, startPosition, endPosition));
			} else if (method.equals("move")) {
				Long fileId = Long.parseLong(req.getParameter("fileId"));
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				printout.print(moveFileToFolder(fileId, folderId));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		printout.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("FileServlet");
		System.out.println("FileServlet");
	}

	private JSONObject addFile (Long folderId, String fileName) throws JSONException {
		JSONObject json = new JSONObject();
		WordFolder folder = folderDao.getById(folderId);
		System.out.println("FileServlet: add file: " + fileName + " to folder " + folder.getName());
		WordFile file = fileService .getByName(folder, fileName);
		if (file == null) {
			file = new WordFile();
			file.setName(fileName);
			int orderNumber = fileService.countFilesInFolder(folder);
			file.setOrderNumber(orderNumber);
			file.setFolder(folder);
			file.setAuthor(folder.getTopic().getOwner());
			fileDao.persist(file);
			json.put("state", "added");
			json.put("fileId", file.getId());
		} else {
			json.put("state", "alreadyexists");
		}

		return json;
	}

	private JSONObject renameFile (Long folderId, Long fileId, String fileName) throws JSONException {
		System.out.println("FileServlet.renameFile: " + fileId + " to " + fileName + " in folder " + folderId);
		JSONObject json = new JSONObject();
		WordFile file = fileDao.getById(fileId);
		WordFolder folder = folderDao.getById(folderId);
		if (file != null) {
			WordFile file2 = fileService .getByName(folder, fileName);
			if (file2 == null) {
				file.setName(fileName);
				fileDao.merge(file);
				json.put("state", "renamed");
			} else {
				json.put("state", "alreadyexists");
			}
		} else {
			json.put("state", "notfound");
		}
		return json;
	}

	private JSONObject deleteFile (Long fileId) throws JSONException {
		JSONObject json = new JSONObject();
		WordFile file = fileDao.getById(fileId);
		if (file != null) {
			List <WordFile> files = fileService.getFilesOrdered (file.getFolder());
			int position = file.getOrderNumber();
			System.out.println("FileServlet: delete file: " + file.getName());
			for (int i = position + 1; i < files.size(); ++ i) {
				WordFile f = files.get(i);
				f.setOrderNumber(i - 1);
				fileDao.merge(f);
			}
			fileDao.remove(file, fileId);
			json.put("state", "deleted");
		} else {
			json.put("state", "notfound");
		}
		return json;
	}

	private JSONObject positionFile (Long folderId, int startPosition, int endPosition) throws JSONException {
		JSONObject json = new JSONObject();
		WordFolder folder = folderDao.getById(folderId);
		System.out.println("FileServlet: position in folder: " + folderId + ", " + startPosition + " to " + endPosition);
		List <WordFile> files = fileService.getFilesOrdered (folder);
		files.add(endPosition, files.remove(startPosition));
		for (int i = Math.min(startPosition, endPosition); i <= Math.max(startPosition, endPosition); ++ i) {
			WordFile file = files.get(i);
			file.setOrderNumber(i);
			fileDao.merge(file);
		}
		json.put("state", "positioned");
		return json;
	}

	private JSONObject moveFileToFolder (Long fileId, Long folderId) throws JSONException {
		JSONObject json = new JSONObject();
		WordFile file = fileDao.getById(fileId);
		WordFolder folder = folderDao.getById(folderId);
		if (file != null) {
			WordFolder oldFolder = file.getFolder();
			int oldPosition = file.getOrderNumber();
			List <WordFile> oldFolderfiles = fileService.getFilesOrdered (oldFolder);
			for (int i = oldPosition + 1; i < oldFolderfiles.size(); ++ i) {
				WordFile f = oldFolderfiles.get(i);
				f.setOrderNumber(i - 1);
				fileDao.merge(f);
			}
			System.out.println("FileServlet: move file: " + file.getName() + " to folder " + folder.getName());
			file.setFolder(folder);
			int orderNumber = fileService.countFilesInFolder (folder);
			file.setOrderNumber(orderNumber);
			fileDao.merge(file);
			json.put("state", "moved");
		} else {
			json.put("state", "notfound");
		}
		return json;
	}
}
