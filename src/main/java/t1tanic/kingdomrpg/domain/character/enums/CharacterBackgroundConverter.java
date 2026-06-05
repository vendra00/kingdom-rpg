package t1tanic.kingdomrpg.domain.character.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter that maps a {@link CharacterBackground} to its lowercase name string
 * (e.g. {@code "outlander"}) and back, keeping stored values consistent with the frontend
 * payload format and {@link CharacterBackground#fromString(String)}.
 *
 * @author t1tanic
 * @version 1.0
 */
@Converter
public class CharacterBackgroundConverter implements AttributeConverter<CharacterBackground, String> {

    @Override
    public String convertToDatabaseColumn(CharacterBackground bg) {
        return bg != null ? bg.name().toLowerCase() : null;
    }

    @Override
    public CharacterBackground convertToEntityAttribute(String dbData) {
        return CharacterBackground.fromString(dbData);
    }
}
