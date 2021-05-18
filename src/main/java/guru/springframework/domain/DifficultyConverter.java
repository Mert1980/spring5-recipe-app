package guru.springframework.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class DifficultyConverter implements AttributeConverter<Difficulty, String> {

    @Override
    public String convertToDatabaseColumn(Difficulty difficulty) {
        if(difficulty == null) {
            return null;
        }
        return difficulty.name();

    }

    @Override
    public Difficulty convertToEntityAttribute(String difficultyLevel) {
        if(difficultyLevel == null) {
            return null;
        }

        return Stream.of(Difficulty.values())
                .filter(c -> c.name().equals(difficultyLevel))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
