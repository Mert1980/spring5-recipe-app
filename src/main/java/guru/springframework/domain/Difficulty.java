package guru.springframework.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

public enum Difficulty {

    EASY("E"), MODERATE("M"), HARDEST("H");

    @Id
    private Long id;

    @Getter
    @Setter
    private String code;

    Difficulty(String code) {
        this.code = code;
    }
}
