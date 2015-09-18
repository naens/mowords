
package com.naens.moweb.model;

import java.util.ArrayList;
import java.util.List;

public class GoogleProfile {

	private int pk;
	private String kind;
	private String etag;
	private String nickname;
	private String occupation;
	private String skills;
	private String birthday;
	private String gender;
	private List<Email> emails = new ArrayList<Email>();
	private List<URL> urls = new ArrayList<URL>();
	private String objectType;
	private String id;
	private String displayName;
	private Name name;
	private String tagline;
	private String braggingRights;
	private String aboutMe;
	private String relationshipStatus;
	private String url;
	private Image image;
	private List<Organization> organizations = new ArrayList<Organization>();
	private List<PlaceLived> placesLived = new ArrayList<PlaceLived>();
	private boolean isPlusUser;
	private String language;
	private AgeRange ageRange;
	private String plusOneCount;
	private String circledByCount;
	private boolean verified;
	private Cover cover;
	private String domain;

	public static class Email {
	    public Integer id;
		private String value;
		private String type;

		private GoogleProfile profile;

		public Email() {
		}

		public Email(String value, String type) {
			this.value = value;
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "Email [value=" + value + ", type=" + type + "]";
		}
		public GoogleProfile getProfile() {
			return profile;
		}

		public void setProfile(GoogleProfile profile) {
			this.profile = profile;
		}

	}

	public static class URL {
	    public Integer id;
		private String value;
		private String type;
		private String label;

		public URL() {
		}

		public URL(String value, String type, String label) {
			this.value = value;
			this.type = type;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return "URL [value=" + value + ", type=" + type + ", label=" + label + "]";
		}

		private GoogleProfile profile;
		public GoogleProfile getProfile() {
			return profile;
		}

		public void setProfile(GoogleProfile profile) {
			this.profile = profile;
		}
		
	}

	public static class Name {

		private String formatted;
		private String familyName;
		private String givenName;
		private String middleName;
		private String honorificPrefix;
		private String honorificSuffix;

		public Name() {
		}

		public Name(String formatted, String familyName, String givenName, String middleName, String honorificPrefix,
				String honorificSuffix) {
			super();
			this.formatted = formatted;
			this.familyName = familyName;
			this.givenName = givenName;
			this.middleName = middleName;
			this.honorificPrefix = honorificPrefix;
			this.honorificSuffix = honorificSuffix;
		}

		public String getFormatted() {
			return formatted;
		}

		public void setFormatted(String formatted) {
			this.formatted = formatted;
		}

		public String getFamilyName() {
			return familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		public String getMiddleName() {
			return middleName;
		}

		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}

		public String getHonorificPrefix() {
			return honorificPrefix;
		}

		public void setHonorificPrefix(String honorificPrefix) {
			this.honorificPrefix = honorificPrefix;
		}

		public String getHonorificSuffix() {
			return honorificSuffix;
		}

		public void setHonorificSuffix(String honorificSuffix) {
			this.honorificSuffix = honorificSuffix;
		}

		@Override
		public String toString() {
			return "Name [formatted=" + formatted + ", familyName=" + familyName + ", givenName=" + givenName
					+ ", middleName=" + middleName + ", honorificPrefix=" + honorificPrefix + ", honorificSuffix="
					+ honorificSuffix + "]";
		}

	}

	public static class Image {

		public String url;

		public Image() {
		}

		public Image(String url) {
			super();
			this.url = url;
		}

		@Override
		public String toString() {
			return "Image [url=" + url + "]";
		}

	}

	public static class Organization {
	    public Integer id;

		private String name;
		private String department;
		private String title;
		private String type;
		private String startDate;
		private String endDate;
		private String location;
		private String description;
		private boolean primary;

		public Organization() {
		}

		public Organization(String name, String department, String title, String type, String startDate,
				String endDate, String location, String description, boolean primary) {
			super();
			this.name = name;
			this.department = department;
			this.title = title;
			this.type = type;
			this.startDate = startDate;
			this.endDate = endDate;
			this.location = location;
			this.description = description;
			this.primary = primary;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public boolean isPrimary() {
			return primary;
		}

		public void setPrimary(boolean primary) {
			this.primary = primary;
		}

		private GoogleProfile profile;
		public GoogleProfile getProfile() {
			return profile;
		}

		public void setProfile(GoogleProfile profile) {
			this.profile = profile;
		}
		
	}

	public static class PlaceLived {
	    public Integer id;
		private String value;
		private boolean primary;

		public PlaceLived() {
		}

		public PlaceLived(String value, boolean primary) {
			this.value = value;
			this.primary = primary;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isPrimary() {
			return primary;
		}

		public void setPrimary(boolean primary) {
			this.primary = primary;
		}

		private GoogleProfile profile;
		public GoogleProfile getProfile() {
			return profile;
		}

		public void setProfile(GoogleProfile profile) {
			this.profile = profile;
		}

	}

	public static class AgeRange {
		private int min;
		private int max;

		public AgeRange() {
		}

		public AgeRange(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public int getMin() {
			return min;
		}

		public void setMin(int min) {
			this.min = min;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

	}

	public static class Cover {
	    public Integer id;
		private String layout;
		private CoverPhoto coverPhoto;
		private CoverInfo coverInfo;

		public Cover() {
		}

		public Cover(String layout, CoverPhoto coverPhoto, CoverInfo coverInfo) {
			super();
			this.layout = layout;
			this.coverPhoto = coverPhoto;
			this.coverInfo = coverInfo;
		}

		public String getLayout() {
			return layout;
		}

		public void setLayout(String layout) {
			this.layout = layout;
		}

		public CoverPhoto getCoverPhoto() {
			return coverPhoto;
		}

		public void setCoverPhoto(CoverPhoto coverPhoto) {
			this.coverPhoto = coverPhoto;
		}

		public CoverInfo getCoverInfo() {
			return coverInfo;
		}

		public void setCoverInfo(CoverInfo coverInfo) {
			this.coverInfo = coverInfo;
		}

	}

	public static class CoverPhoto {
		private String url;
		private int height;
		private int width;

		public CoverPhoto() {
		}

		public CoverPhoto(String url, int height, int width) {
			super();
			this.url = url;
			this.height = height;
			this.width = width;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

	}

	public static class CoverInfo {
		private int topImageOffset;
	    private int leftImageOffset;

	    public CoverInfo() {
		}

		public CoverInfo(int topImageOffset, int leftImageOffset) {
			super();
			this.topImageOffset = topImageOffset;
			this.leftImageOffset = leftImageOffset;
		}

		public int getTopImageOffset() {
			return topImageOffset;
		}

		public void setTopImageOffset(int topImageOffset) {
			this.topImageOffset = topImageOffset;
		}

		public int getLeftImageOffset() {
			return leftImageOffset;
		}

		public void setLeftImageOffset(int leftImageOffset) {
			this.leftImageOffset = leftImageOffset;
		}
		
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public List<URL> getUrls() {
		return urls;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 
	 * @param displayName
	 *	 The displayName
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 
	 * @return
	 *	 The name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *	 The name
	 */
	public void setName(Name name) {
		this.name = name;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String getBraggingRights() {
		return braggingRights;
	}

	public void setBraggingRights(String braggingRights) {
		this.braggingRights = braggingRights;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getRelationshipStatus() {
		return relationshipStatus;
	}

	public void setRelationshipStatus(String relationshipStatus) {
		this.relationshipStatus = relationshipStatus;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public List<PlaceLived> getPlacesLived() {
		return placesLived;
	}

	public void setPlacesLived(List<PlaceLived> placesLived) {
		this.placesLived = placesLived;
	}

	public boolean getIsPlusUser() {
		return isPlusUser;
	}

	public void setIsPlusUser(boolean isPlusUser) {
		this.isPlusUser = isPlusUser;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public AgeRange getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(AgeRange ageRange) {
		this.ageRange = ageRange;
	}

	public String getPlusOneCount() {
		return plusOneCount;
	}

	public void setPlusOneCount(String plusOneCount) {
		this.plusOneCount = plusOneCount;
	}

	public String getCircledByCount() {
		return circledByCount;
	}

	public void setCircledByCount(String circledByCount) {
		this.circledByCount = circledByCount;
	}

	public boolean getVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public Cover getCover() {
		return cover;
	}

	public void setCover(Cover cover) {
		this.cover = cover;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void fillProfile () {
		if (emails != null) {
			for (Email email : emails) {
				email.setProfile(this);
			}
		}
		if (urls != null) {
			for (URL url : urls) {
				url.setProfile(this);
			}
		}
		if (organizations != null) {
			for (Organization organization : organizations) {
				organization.setProfile(this);
			}
		}
		if (placesLived != null) {
			for (PlaceLived placeLived : placesLived) {
				placeLived.setProfile(this);
			}
		}
	}

	@Override
	public String toString() {
		return "GoogleProfile [pk=" + pk + ",\nkind=" + kind + ",\netag=" + etag + ",\nnickname=" + nickname + ",\noccupation=" + occupation
				+ ",\nskills=" + skills + ",\nbirthday=" + birthday + ",\ngender=" + gender + ",\nemails=" + emails
				+ ",\nurls=" + urls + ",\nobjectType=" + objectType + ",\nid=" + id + ",\ndisplayName=" + displayName
				+ ",\nname=" + name + ",\ntagline=" + tagline + ",\nbraggingRights=" + braggingRights + ",\naboutMe="
				+ aboutMe + ",\nrelationshipStatus=" + relationshipStatus + ",\nurl=" + url + ",\nimage=" + image
				+ ",\norganizations=" + organizations + ",\nplacesLived=" + placesLived + ",\nisPlusUser=" + isPlusUser
				+ ",\nlanguage=" + language + ",\nageRange=" + ageRange + ",\nplusOneCount=" + plusOneCount
				+ ",\ncircledByCount=" + circledByCount + ",\nverified=" + verified + ",\ncover=" + cover + ",\ndomain="
				+ domain + "]";
	}

	public String getEmail() {
		return emails.get(0).value;
	}

	public int getPk() {
		return pk;
	}

	public void setPlusUser(boolean isPlusUser) {
		this.isPlusUser = isPlusUser;
	}

	public void setEmail(String email) {
		if (emails.size() == 0) {
			System.out.println("add email:" + email);
			emails.add(new Email(email, null));
		} else {
			System.out.println("set email:" + email);
			emails.set(0, new Email(email, null));
		}
		System.out.println("emails=" + emails);
	}

	public void setPk(int pk) {
		this.pk = pk;
	}
}
