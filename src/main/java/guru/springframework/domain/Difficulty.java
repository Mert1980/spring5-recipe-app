package guru.springframework.domain;

import javax.persistence.Id;

public enum Difficulty {
    EASY("E"), MODERATE("M"), HARDEST("H");

    private Long id;
    private String code;

    Difficulty(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
