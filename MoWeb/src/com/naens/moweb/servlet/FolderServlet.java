package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.dao.FolderDao;
import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.dao.WordPairTypeDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.service.EMF;
import com.naens.moweb.service.FolderService;

@WebServlet(value = "/folders-servlet")
public class FolderServlet extends HttpServlet {

	private static final long serialVersionUID = 154588212301086099L;

	private FolderService folderService = new FolderService();

	private FolderDao folderDao = new FolderDao();

	private WordPairTypeDao pairTypeDao = new WordPairTypeDao();

	private TopicDao topicDao = new TopicDao();

	private User user;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("FolderServlet");
		System.out.println("FolderServlet");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String method = req.getParameter("method");
			user = (User) req.getSession().getAttribute("user");
			if (user == null) {
				resp.sendRedirect("");
				return;
			}
			resp.setContentType("application/json; charset=UTF-8");
			PrintWriter printout = resp.getWriter();
			if (method.equals("add")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				String folderName = req.getParameter("folderName");
				printout.print(addFolder(topicId, folderName));
			} else if (method.equals("rename")) {
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				String folderName = req.getParameter("folderName");
				printout.print(renameFolder(folderId, folderName));
			} else if (method.equals("delete")) {
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				printout.print(deleteFolder(folderId));
			} else if (method.equals("position")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				String pairtypeIdParameter = req.getParameter("pairtypeId");
				Long pairtypeId = pairtypeIdParameter != null ? Long.parseLong(pairtypeIdParameter) : null;
				int startPosition = Integer.parseInt(req.getParameter("startPosition"));
				int endPosition = Integer.parseInt(req.getParameter("endPosition"));
				printout.print(positionFolder(topicId, pairtypeId, startPosition, endPosition));
			} 
/*			else if (method.equals("setpairtype")) {
				Long folderId = Long.parseLong(req.getParameter("folder-id"));
				Long pairTypeId = Long.parseLong(req.getParameter("pairtype-id"));
				int position = Integer.parseInt(req.getParameter("position"));

				WordPairType pairType = pairTypeDao.getById(pairTypeId);
				WordFolder folder = folderDao.getById(folderId);

				if (folder != null) {
					setPairType (topic, folder, pairType, position);
					json.put("state", "ok");
				} else {
					json.put("state", "notfound");
				}

			}*/
			printout.flush();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject addFolder (Long topicId, String folderName) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println("FolderServlet.addFolder: " + folderName + " to topic " + topicId);

		Topic topic = topicDao.getById(topicId);
		json.put("message", "Added Folder: " + folderName);
		if (folderService.getByName(folderName, topic) == null) {	//new
			EntityManager em = EMF.get().createEntityManager();
			em.getTransaction().begin();
			WordFolder folder = new WordFolder();
			folder.setName(folderName);
			folder.setTopic(topic);
			folder.setOrderNumber(folderService.countFoldersByPairType(topic, null));
			folder.setPairType(null);
			em.persist(folder);
			em.getTransaction().commit();
			json.put("state", "added");
			json.put("folderId", folder.getId());
		} else {	//not new
			json.put("state", "alreadyexists");
		}
		return json;
	}

	private JSONObject renameFolder (Long folderId, String folderName) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("FolderServlet.renameFolder: " + folderId + " to " + folderName);

		WordFolder folder = folderDao.getById(folderId);

		if (folder != null) {
			WordFolder folder2 = folderService.getByName(folderName, folder.getTopic());
			if (folder2 != null) {
				json.put("state", "alreadyexists");
			} else {
				folder.setName(folderName);
				System.out.println("Renamed Folder: " + folder.getName() + " to: " + folderName);
				folderDao.merge(folder);
				json.put("state", "renamed");
			}
		} else {
			json.put("state", "notfound");
		}

		return json;
	}

	private JSONObject deleteFolder (Long folderId) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println("FolderServlet.deleteFolder: " + folderId);

		WordFolder folder = folderDao.getById(folderId);
		System.out.println(String.format("FolderServlet.delete: folder[%d] '%s'", folderId, folder != null ? folder.getName() : "NULL"));

		if (folder != null) {
			EntityManager entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
	
			folder = entityManager.merge(folder);
	
			WordPairType pairType = folder.getPairType();
			List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(pairType);
			System.out.println("FolderServlet.delete.pairtype: " + pairType);
			int position = folder.getOrderNumber();
			//FIXME: sometimes not working	-> ?refresh
			List <WordFolder> toMerge = new LinkedList <WordFolder> ();
			folders.remove(position);
			for (int i = position; i < folders.size(); ++ i) {
				WordFolder f = folders.get(i);
				f.setOrderNumber(f.getOrderNumber() - 1);
				toMerge.add(f);
				System.out.println("FolderServlet.delete.toMerge["+i+"]: folder["+f.getOrderNumber()+"]=" + f.getName());
			}
	
			if (pairType != null && pairType.getFolders().size() == 1) {
				entityManager.remove(folder);
				entityManager.remove(pairType);
			} else {
				entityManager.remove(folder);
			}
	
			for (WordFolder wf : toMerge) {
				entityManager.merge(wf);
				System.out.println("FolderServlet.delete: merging folder " + wf.getName());
			}
	
			entityManager.getTransaction().commit();
	
	
			json.put("state", "deleted");
		} else {
			json.put("state", "notfound");
		}
		return json;
	}

//	private JSONObject positionFolder (Long folderId, int position) throws JSONException {
//		JSONObject json = new JSONObject();
//		System.out.println("FolderServlet.positionFolder: " + folderId + " to " + position);
//
//		WordFolder folder = folderDao.getById(folderId);
//
//		if (folder != null) {
//			WordPairType pairType = folder.getPairType();
//			List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(folder.getTopic(), pairType);
//			folder = folders.get(folder.getOrderNumber());
//			System.out.println("folders:");
//			for (WordFolder wordFolder : folders) {
//				System.out.println("folders["+wordFolder.getOrderNumber()+"]:"+wordFolder.getName());
//			}
//			System.out.printf("position:%s %d->%d\n", folder.getName(), folder.getOrderNumber(), position);
//			int oldPosition = folder.getOrderNumber();
//			int unit = (position - oldPosition) < 0 ? -1 : 1;
//			int tot = Math.abs(position - oldPosition) + 1;
//			System.out.println("tot = " + tot);
//			WordFolder [] toMerge = new WordFolder [tot];
//			int c = 0;
//			folder.setOrderNumber(position);
//			for (int i = position; i != oldPosition; i -= unit) {
//				WordFolder f = folders.get(i);
//				f.setOrderNumber(i - unit);
//				toMerge [c] = f;
//				System.out.println("toMerge["+c+"]: add folder["+(i - unit)+"]=" + f.getName());
//				++ c;
//			}
//			toMerge[tot-1] = folder;
//			System.out.println("toMerge["+(tot-1)+"]: add folder["+position+"]=" + folder.getName());
//			folderDao.merge(toMerge);
//			json.put("state", "positioned");
//		} else {
//			json.put("state", "notfound");
//		}
//		return json;
//	}

	private JSONObject positionFolder (Long topicId, Long pairtypeId, int startPosition, int endPosition) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println("FolderServlet.positionFolder: in partype: " + pairtypeId + "; " + startPosition + " to " + endPosition);

		List <WordFolder> folders = pairtypeId == null ? 
				folderService.getByPairTypeSortedByNumber(topicDao.getById(topicId)) :
				folderService.getByPairTypeSortedByNumber(pairTypeDao.getById(pairtypeId));
		WordFolder removedFolder = folders.remove(startPosition);
		folders.add(endPosition, removedFolder);
		for (int i = Math.min(startPosition, endPosition); i <= Math.max(startPosition, endPosition); ++ i) {
			WordFolder folder = folders.get(i);
			folder.setOrderNumber(i);
			folderDao.merge(folder);
		}

		json.put("state", "positioned");
		return json;
	}
}
