package hub.troubleshooters.soundlink.data.models;

import java.util.Date;
import java.util.Optional;

/**
 * Represents a community in the system, with information such as name, genre, description, and privacy status.
 * Communities can be private or public, and they may have an associated banner image.
 */
public class Community {
	private final int id;
	private String name;
	private String genre;
	private String description;
	private final Date created;
	private Integer bannerImageId;
	private boolean isPrivate;

	/**
	 * Constructs a new {@code Community} object.
	 *
	 * @param id The unique ID of the community.
	 * @param name The name of the community.
	 * @param description The description of the community.
	 * @param genre The genre associated with the community.
	 * @param created The date when the community was created.
	 * @param bannerImageId The ID of the banner image for the community, or {@code null} if no banner exists.
	 * @param isPrivate Indicates if the community is private or public.
	 */
	public Community(int id, String name, String description, String genre, Date created, Integer bannerImageId, boolean isPrivate) {
		this.name = name;
		this.created = created;
		this.description = description;
		this.genre = genre;
		this.id = id;
		this.bannerImageId = bannerImageId;
		this.isPrivate = isPrivate;
	}

	/**
	 * Gets the unique ID of the community.
	 *
	 * @return The community ID.
	 */
	public int getId() { return id; }

	/**
	 * Gets the name of the community.
	 *
	 * @return The name of the community.
	 */
	public String getName() { return name; }

	/**
	 * Sets the name of the community.
	 *
	 * @param name The new name of the community.
	 */
	public void setName(String name) { this.name = name; }

	/**
	 * Gets the genre associated with the community.
	 *
	 * @return The genre of the community.
	 */
	public String getGenre() { return genre; }

	/**
	 * Sets the genre associated with the community.
	 *
	 * @param genre The new genre of the community.
	 */
	public void setGenre(String genre) { this.genre = genre; }

	/**
	 * Gets the description of the community.
	 *
	 * @return The description of the community.
	 */
	public String getDescription() { return description; }

	/**
	 * Sets the description of the community.
	 *
	 * @param description The new description of the community.
	 */
	public void setDescription(String description) { this.description = description; }

	/**
	 * Gets the date when the community was created.
	 *
	 * @return The creation date of the community.
	 */
	public Date getCreated() { return created; }

	/**
	 * Gets the optional banner image ID for the community.
	 *
	 * @return An {@link Optional} containing the banner image ID, or {@code Optional.empty()} if no banner exists.
	 */
	public Optional<Integer> getBannerImageId() {
		return bannerImageId == null ? Optional.empty() : Optional.of(bannerImageId);
	}

	/**
	 * Sets the banner image ID for the community.
	 *
	 * @param bannerImageId The new banner image ID, or {@code null} if there is no banner.
	 */
	public void setBannerImageId(Integer bannerImageId) {
		this.bannerImageId = bannerImageId;
	}

	/**
	 * Checks if the community is private.
	 *
	 * @return {@code true} if the community is private, {@code false} if it is public.
	 */
	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * Sets the privacy status of the community.
	 *
	 * @param isPrivate The new privacy status, {@code true} for private, {@code false} for public.
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
}