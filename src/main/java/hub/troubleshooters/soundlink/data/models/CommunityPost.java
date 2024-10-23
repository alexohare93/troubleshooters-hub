package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a post made within a community, containing the post's title, content,
 * and associated metadata such as the community, user, and creation date.
 */
public class CommunityPost {
	private final int id;
	private final int communityId;
	private final int userid;
	private String title;
	private String content;
	private final Date created;

	/**
	 * Constructs a new {@code CommunityPost} object.
	 *
	 * @param id The unique ID of the community post.
	 * @param communityId The ID of the community the post belongs to.
	 * @param userid The ID of the user who created the post.
	 * @param title The title of the post.
	 * @param content The content of the post.
	 * @param created The date when the post was created.
	 */
	public CommunityPost(int id, int communityId, int userid, String title, String content, Date created)  {
		this.id = id;
		this.communityId = communityId;
		this.userid = userid;
		this.created = created;
		this.content = content;
		this.title = title;
	}

	/**
	 * Gets the unique ID of the community post.
	 *
	 * @return The post ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the ID of the community the post belongs to.
	 *
	 * @return The community ID.
	 */
	public int getCommunityId() {
		return communityId;
	}

	/**
	 * Gets the ID of the user who created the post.
	 *
	 * @return The user ID.
	 */
	public int getUserid() {
		return userid;
	}

	/**
	 * Gets the title of the post.
	 *
	 * @return The post title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the post.
	 *
	 * @param title The new title of the post.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the content of the post.
	 *
	 * @return The post content.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content of the post.
	 *
	 * @param content The new content of the post.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the date when the post was created.
	 *
	 * @return The creation date of the post.
	 */
	public Date getCreated() {
		return created;
	}
}