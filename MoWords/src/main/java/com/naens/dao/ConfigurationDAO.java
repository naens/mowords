package com.naens.dao;


public interface ConfigurationDAO {

	public String getSideFontSize (String folderName, int side);

	public String getSideFontName (String folderName, int side);

	public boolean isOneDirection (String wordFolder);

	public String [] getLimitList (String wordFolder);

	public String getDefaultLimit (String wordFolder);

	public String getRootFolder();

	public boolean isMdCSide(String folderName, int i);

}
