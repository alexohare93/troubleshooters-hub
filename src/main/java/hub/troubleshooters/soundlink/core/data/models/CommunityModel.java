package hub.troubleshooters.soundlink.core.data.models;

import java.util.Date;

/**
 * Represents a user in the system. Does not include the password.
 * @param id The communities unique identifier.
 * @param name The name of the community.
 * @param description The description of the community.
 * @param genre The genre the community is a part of.
 * @param created the datetime the community object was created
 */
public record CommunityModel (int id, String name, String description, String genre, Date created) {}
