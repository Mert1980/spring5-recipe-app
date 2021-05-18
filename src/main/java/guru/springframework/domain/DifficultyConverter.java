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
        return difficulty.getCode();

    }

    @Override
    public Difficulty convertToEntityAttribute(String code) {
        if(code == null) {
            return null;
        }

        return Stream.of(Difficulty.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
