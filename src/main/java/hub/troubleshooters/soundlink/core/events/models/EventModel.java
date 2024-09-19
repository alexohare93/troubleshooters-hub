package hub.troubleshooters.soundlink.core.events.models;

import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;

public record EventModel(int id, String name, String description, Community community, String venue, int capacity, Date scheduled, Date created, Optional<Image> bannerImage) {
}
