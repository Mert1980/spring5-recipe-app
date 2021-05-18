package guru.springframework.domain;

import javax.persistence.Id;

public enum Difficulty {
    EASY, MODERATE, HARD;

    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
