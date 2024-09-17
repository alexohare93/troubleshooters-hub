package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class CommunityPost {
	private final int id;
	private final int communityId;
	private final int userid;
	private String title;
	private String content;
	private final Date created;

	public CommunityPost(int id, int communityId, int userid, String title, String content, Date created)  {
		this.id = id;
		this.communityId = communityId;
		this.userid = userid;
		this.created = created;
		this.content = content;
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public int getCommunityId() {
		return communityId;
	}

	public int getUserid() {
		return userid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}
}
