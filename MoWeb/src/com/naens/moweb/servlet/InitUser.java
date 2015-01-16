package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.Results;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordFile;
import com.naens.moweb.model.WordPair;
import com.naens.moweb.model.WordSide;
import com.naens.moweb.service.EMF;
import com.naens.tools.Tools;

@WebServlet (name="InitUser", urlPatterns={"/inituser"})
public class InitUser extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static EntityManager em = EMF.get().createEntityManager();
	public static final String GAMELOG_COLUMN_FOLDER = "folder";
	public static final String GAMELOG_COLUMN_DATE = "date";
	public static final String GAMELOG_COLUMN_FILES = "files";
	public static final String GAMELOG_COLUMN_DONE = "done";
	public static final String GAMELOG_COLUMN_TOTAL = "total";
	public static final String GAMELOG_COLUMN_GAME_TIME = "gametime";
	public static final String GAMELOG_COLUMN_SIDE = "side";
	public static final String GAMELOG_COLUMN_SIDES = "sides";
	public static final String GAMELOG_COLUMN_INVERSE = "inverse";

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("init-user->post length: " + req.getHeader("content-length"));
		try {
			String jsonString = Tools.getStringFromInputStream(new GZIPInputStream(req.getInputStream()), "UTF-8");
			JSONObject json = new JSONObject(jsonString);
			System.out.println("json string size: " + jsonString.length());

			//user:email...
			JSONObject jsonUser = json.getJSONObject("user");
			System.out.println("user=" + jsonUser.toString(4));
			String email = jsonUser.getString("email");

			User user = new User();
			GoogleProfile googleProfile = new GoogleProfile();
			googleProfile.setEmail(email);
			user.setGoogleProfile(googleProfile);

			//data:folder.file.pair.side
			JSONObject jsonData = json.getJSONObject("data");
			System.out.println("data=" + jsonData.toString(4).substring(0, jsonData.length() > 100 ? 100 : jsonData.length()));

			Map <String, List<WordFolder>> topicFolderMap = new HashMap<String, List<WordFolder>>();
			Map <String, Map <String, WordFile>> fileMap = new HashMap <String, Map <String, WordFile>> ();
			Iterator<String> folderKeys = jsonData.keys();
			while(folderKeys.hasNext()){
				String folderName_old = (String)folderKeys.next();
//				System.out.println("folder=" + folderName_old);
				int index = folderName_old.indexOf('-');
				String topicName;
				String folderName;
				if (index > 0 && index <= folderName_old.length() - 2) {
					topicName = folderName_old.substring(0, index);
					folderName = folderName_old.substring(index + 1, folderName_old.length());
				} else {
					topicName = folderName_old;
					folderName = "";
				}
				WordFolder wordFolder = new WordFolder();
				wordFolder.setName(folderName);
//				System.out.println(String.format("folderName='%s' => index=%d, length=%d => topic=%s and folder=%s", folderName_old, index, folderName_old.length(), topicName, folderName));
				if (!topicFolderMap.containsKey(topicName)) {
					topicFolderMap.put(topicName, new LinkedList<WordFolder>());
				}
				topicFolderMap.get(topicName).add(wordFolder);
				JSONObject folderJson = (JSONObject) jsonData.get(folderName_old);
				Iterator<String> fileKeys = folderJson.keys();
				Map <String, WordFile> files = new HashMap<String, WordFile>();
				while(fileKeys.hasNext()){
					String fileName = (String)fileKeys.next();
					WordFile wordFile = new WordFile();
					wordFile.setName(fileName);

					files.put (fileName, wordFile);
//					System.out.println(String.format("folderfileput: {%s}\t{%s}\t=>put:%s", folderName_old, fileName, wordList.toString()));

					wordFile.setFolder(wordFolder);
//					System.out.println("file=" + fileName);
					JSONArray fileJson = (JSONArray) folderJson.get(fileName);
					for (int i = 0; i < fileJson.length(); ++ i) {
						JSONObject wordPairJson = (JSONObject) fileJson.get(i);
						WordPair wordPair = new WordPair();
						//TODO set pairtype
						Iterator<String> sideIterator = wordPairJson.keys();
						while(sideIterator.hasNext()){
							String side = (String)sideIterator.next();
							JSONObject sideJson = (JSONObject) wordPairJson.get(side);
							String type = sideJson.getString("type");
							String text = sideJson.getString("text");
							WordSide wordSide = new WordSide();
							wordSide.setText(text);
							//TODO set sidetype, set data
							wordSide.setNumber(Integer.parseInt(side));
							wordSide.setWordPair(wordPair);
						}
						wordPair.setWordFile(wordFile);
					}
				}
				fileMap.put(folderName_old, files);
			}

			System.out.print("getting entity manager... ... ...");
//			EntityManager em = EMF.get().createEntityManager();
			System.out.println("ok!");
			EntityTransaction et = em.getTransaction();
			et.begin();

			//TODO:get results

			//results: * [id, record(folder, date, files, inverse, done, total, time, side, sides)]
			JSONArray jsonResults = json.getJSONArray("results");
//			System.out.println("results=" + jsonResults.toString(4).substring(0, jsonResults.length() > 100 ? 100 : jsonResults.length()));

			for (int i = 0; i < jsonResults.length(); ++ i) {
				JSONObject recs = (JSONObject) jsonResults.get(i);
				JSONObject rec1 = recs.getJSONObject("rec1");

				long date = rec1.getLong(GAMELOG_COLUMN_DATE);
				String folder = rec1.getString(GAMELOG_COLUMN_FOLDER);
				String [] fileNames = rec1.getString(GAMELOG_COLUMN_FILES).split(", ");
				List <WordFile> files = new LinkedList<WordFile>();
//				System.out.println("add in folder: " + folder);
				boolean hasnull = false;
				for (String string : fileNames) {
					Map<String, WordFile> wfm = fileMap.get(folder);
					WordFile wl = wfm.get(string);
					if (wl == null) {
						hasnull = true;
						System.out.print(String.format("folderfileget: {%s}\t{%s}\twl == null", folder, string));
					} else {
						files.add(wl);
//						System.out.println(String.format("add file: %s old=%s", wl.getName(), string));
					}
				}
				if (!hasnull) {
					boolean inverse = rec1.getBoolean(GAMELOG_COLUMN_INVERSE);
					int done = rec1.getInt(GAMELOG_COLUMN_DONE);
					int gameTime = rec1.getInt(GAMELOG_COLUMN_GAME_TIME);
					int total = rec1.getInt(GAMELOG_COLUMN_TOTAL);
					if (!recs.has("rec2")) {
						em.persist(new Results(user, date, files, done, total, inverse, gameTime));
					} else {
						JSONObject rec2 = recs.getJSONObject("rec2");
						int done2 = rec2.getInt(GAMELOG_COLUMN_DONE);
						int gameTime2 = rec2.getInt(GAMELOG_COLUMN_GAME_TIME);
						em.persist(new Results(user, date, files, done, done2, total, inverse, gameTime, gameTime2));
					}
				}
			}

			try {
				em.persist(user);
			// save
				for (String topicName: topicFolderMap.keySet()) {
					Topic topic = new Topic();
					topic.setName(topicName);
					topic.setOwner(user);
					for (WordFolder wordFolder : topicFolderMap.get(topicName)) {
						wordFolder.setTopic(topic);
					}
					em.persist(topic);
				}
				//TODO: save results
			} finally {
				et.commit();
//				em.close();
			}
		} catch (JSONException e) {
			System.out.println("init-user->exception");
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("init-user->get");
		PrintWriter out = resp.getWriter();
		out.println("InitUser servlet");

		User user = new User();
		GoogleProfile googleProfile = new GoogleProfile();
		googleProfile.setEmail("em@em.em");
		user.setGoogleProfile(googleProfile);

		EntityManager em = EMF.get().createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		try {
			em.persist(user);
		}  finally {
			et.commit();
			em.close();
		}
	}
}
