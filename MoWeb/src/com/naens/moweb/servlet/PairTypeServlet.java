package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.dao.FolderDao;
import com.naens.moweb.dao.PtstDao;
import com.naens.moweb.dao.WordPairTypeDao;
import com.naens.moweb.dao.WordSideTypeDao;
import com.naens.moweb.model.PairTypeSideType;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.model.WordSideType;
import com.naens.moweb.service.FolderService;
import com.naens.moweb.service.StylesService;

@WebServlet(value = "/pairtype-servlet")
public class PairTypeServlet extends HttpServlet {

	private static final long serialVersionUID = 186232176118538949L;

	@EJB
	private WordPairTypeDao pairTypeDao;

	@EJB
	private WordSideTypeDao sideTypeDao;

	@EJB
	private FolderDao folderDao;

	@EJB
	private PtstDao ptstDao;

	@EJB
	private FolderService folderService;

	@EJB
	private StylesService stylesService;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getParameter("method");
		System.out.println("PairTypeServlet: method\t" + method);

//		WordSideType sideType = entityManager.find(WordSideType.class, Long.parseLong(req.getParameter("sidetype")));

//		String ptParameter = req.getParameter("pairtype");
//		System.out.println("ptParameter="+ ptParameter);
//		WordPairType pairType;
		//		if (!Pattern.matches("[0-9]+", ptParameter)) {
/*		if (ptParameter == null) {
			pairType = new WordPairType();
			//bind folders to pairtype
			entityManager.refresh(sideType.getTopic());
			List <WordFolder> topicFolders = sideType.getTopic().getFolders();
			List<WordFolder> foldersToMerge = new LinkedList<WordFolder>();
//			ArrayList<WordFolder> foldersToMerge = new ArrayList<WordFolder>();
			for (WordFolder folder : topicFolders) {
				System.out.println("PairTypeServlet:folder " + folder.getName() + "("+folder.getId()+").pt="+folder.getPairType());
				if (folder.getPairType() == null) {
					System.out.println("PairTypeServlet:add pt "+pairType.getId()+" to folder " + folder.getName() + "("+folder.getId()+")");
					folder.setPairType(pairType);
//					entityManager.merge(folder);
					foldersToMerge.add(folder);
				}
			}
			entityManager.persist(pairType);
			for (WordFolder wf : foldersToMerge) {
				System.out.printf("PairTypeServlet: new pairtype to folder %d", wf.getId());
				entityManager.merge (wf);
			}
//			entityManager.merge((WordFolder [])foldersToMerge.toArray(new WordFolder[foldersToMerge.size()]));
		} else {
			pairType =  entityManager.find(WordPairType.class, Long.parseLong(ptParameter));
		}*/

		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter printout = resp.getWriter();
//		System.out.println("PairTypeServlet: pairtype\t" + pairType.getId());
//		System.out.println("PairTypeServlet: sidetype\t" + sideType.getId());
		try {
			if (method.equals("add")) {
				Long pairTypeId = Long.parseLong(req.getParameter("pairTypeId"));
				Long styleId = Long.parseLong(req.getParameter("styleId"));
				int position = Integer.parseInt(req.getParameter("position"));
				printout.print(addStyleToPairType(pairTypeId, styleId, position));
			} else if (method.equals("setForFolder")) {
				Long pairTypeId = Long.parseLong(req.getParameter("pairTypeId"));
				Long folderId = Long.parseLong(req.getParameter("folderId"));
				int position = Integer.parseInt(req.getParameter("position"));
				printout.print(setPairTypeForFolder(pairTypeId, folderId, position));
			} else if (method.equals("delete")) {
				Long pairTypeId = Long.parseLong(req.getParameter("pairTypeId"));
				Long styleId = Long.parseLong(req.getParameter("styleId"));
				int position = Integer.parseInt(req.getParameter("position"));
				printout.print(deleteStyleFromPairType(pairTypeId, styleId, position));
			} else if (method.equals("position")) {
				Long pairTypeId = Long.parseLong(req.getParameter("pairTypeId"));
				int startPosition = Integer.parseInt(req.getParameter("startPosition"));
				int endPosition = Integer.parseInt(req.getParameter("endPosition"));
				printout.print(positionStyleInPairType(pairTypeId, startPosition, endPosition));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		printout.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("PairTypeServlet");
		System.out.println("PairTypeServlet");
	}

	private JSONObject addStyleToPairType (Long pairTypeId, Long styleId, int position) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("PairTypeServlet.add: " + styleId + " to pairtype " + pairTypeId + ", position: " + position);

		WordPairType pairType = pairTypeDao.getById(pairTypeId);
		WordSideType sideType = sideTypeDao.getById(styleId);
		System.out.println("PairTypeServlet.add: PairTypeServlet: index\t\t" + position);
		List <PairTypeSideType> ptsts = stylesService.getPtstsSortedByNumber(pairType);
		PairTypeSideType ptst = new PairTypeSideType();
		ptst.setNumber(position);
		ptst.setSideType(sideType);
		ptst.setPairType(pairType);
		ptstDao.persist(ptst);

		for (int i = position; i < ptsts.size(); ++ i) {
			PairTypeSideType p = ptsts.get(i);
			System.out.println(String.format("PairTypeServlet.add: st:%d\tpt:%d\tn:%d->%d", p.getSideType().getId(), p.getPairType().getId(), p.getNumber(), 
					p.getNumber() + 1));
			p.setNumber(p.getNumber() + 1);
			ptstDao.persist(p);
		}
		json.put("state", "inserted");
		return json;
	}

	private JSONObject setPairTypeForFolder (Long pairTypeId, Long folderId, int position) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println("PairTypeServlet.setForFolder: " + pairTypeId + " for folder " + folderId + ", position: " + position);

		WordPairType pairType = pairTypeDao.getById(pairTypeId);
		WordFolder folder = folderDao.getById(folderId);
		folder.setPairType(pairType);

		List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(pairType);

		folders.add(position, folder);

		for (int i = position; i < folders.size(); ++ i) {
			WordFolder f = folders.get(i);
			f.setOrderNumber(i);
			System.out.printf("PairTypeServlet.setForFolder: folders [%d]: %s (id=%d). merge... ", i, f.getName(), f.getId());
			folderDao.merge(f);
			System.out.println("PairTypeServlet.setForFolder: ok!");
		}

		json.put("state", "inserted");
		return json;
	}

	private JSONObject deleteStyleFromPairType (Long pairTypeId, Long styleId, int position) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("PairTypeServlet.delete: " + styleId + " from paretype " + pairTypeId + ", position: " + position);

		WordPairType pairType = pairTypeDao.getById(pairTypeId);
		System.out.println("PairTypeServlet: index\t\t" + position);
		List <PairTypeSideType> ptsts = stylesService.getPtstsSortedByNumber(pairType);
		System.out.println("PairTypeServlet.delete: ptsts-------------");
		for (PairTypeSideType pp : ptsts) {
			System.out.println(String.format("PairTypeServlet.delete: st:%d\tpt:%d\tn:%d", pp.getSideType().getId(), pp.getPairType().getId(), pp.getNumber()));
		}
		System.out.println("PairTypeServlet.delete: shift--------------");
		PairTypeSideType ptst = ptsts.remove(position);

		for (int i = position; i < ptsts.size(); ++ i) {
			PairTypeSideType p = ptsts.get(i);
			System.out.println(String.format("PairTypeServlet.delete: st:%d\tpt:%d\tn:%d->%d", p.getSideType().getId(), p.getPairType().getId(), p.getNumber(), 
					p.getNumber() - 1));
			p.setNumber(p.getNumber() - 1);
			ptstDao.persist(p);
		}
		ptstDao.remove(ptst);
		System.out.println("PairTypeServlet.delete: -------------------");
		json.put("state", "deleted");
		return json;
	}

	private JSONObject positionStyleInPairType (Long pairTypeId, int startPosition, int endPosition) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("PairTypeServlet.position: " + startPosition + " to " + endPosition + " in pairtype " + pairTypeId);

		WordPairType pairType = pairTypeDao.getById(pairTypeId);
		List <PairTypeSideType> ptsts = stylesService.getPtstsSortedByNumber(pairType);
		ptsts.add(endPosition, ptsts.remove(startPosition));
		for (int i = Math.min(startPosition, endPosition); i <= Math.max(startPosition, endPosition); ++ i) {
			PairTypeSideType ptst = ptsts.get(i);
			ptst.setNumber(i);
			ptstDao.persist(ptst);
		}

		json.put("state", "positioned");
		return json;
	}

}
