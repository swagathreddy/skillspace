package io.aiven.spring.mysql.demo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="roadmaps")
public class Roadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String title;
    private String description;
    private String basicsLink;
    private String intermediateLink;
    private String advancedLink;

    public Roadmap() {
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBasicsLink() {
        return basicsLink;
    }

    public void setBasicsLink(String basicsLink) {
        this.basicsLink = basicsLink;
    }

    public String getIntermediateLink() {
        return intermediateLink;
    }

    public void setIntermediateLink(String intermediateLink) {
        this.intermediateLink = intermediateLink;
    }

    public String getAdvancedLink() {
        return advancedLink;
    }

    public void setAdvancedLink(String advancedLink) {
        this.advancedLink = advancedLink;
    }

    // toString method to represent the object as a string
    @Override
    public String toString() {
    return "Roadmap{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", basicsLink='" + basicsLink + '\'' +
            ", intermediateLink='" + intermediateLink + '\'' +
            ", advancedLink='" + advancedLink + '\'' +
            '}';
}

}

