package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class Community {
  private final int id;

  private String name;
  private String genre;
  private String description;
  private Date created;

  public Community(int id, String name, String description, String genre, Date created) {
    this.name = name;
    this.created = created;
    this.description = description;
    this.genre = genre;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
}
