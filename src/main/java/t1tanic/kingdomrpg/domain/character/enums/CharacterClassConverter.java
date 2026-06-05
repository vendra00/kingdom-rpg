package t1tanic.kingdomrpg.domain.character.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * JPA converter that maps a single {@link CharacterClass} to its lowercase {@code id()} string in the database
 * (e.g. {@code "warrior"}), keeping stored values consistent with {@link CharacterClassSetConverter}
 * and the frontend payload format.
 *
 * @author t1tanic
 * @version 1.0
 */
@Converter
public class CharacterClassConverter implements AttributeConverter<CharacterClass, String> {

    @Override
    public String convertToDatabaseColumn(CharacterClass cc) {
        return cc != null ? cc.id() : null;
    }

    @Override
    public CharacterClass convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Arrays.stream(CharacterClass.values())
                     .filter(cc -> cc.id().equals(dbData))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Unknown character class: " + dbData));
    }
}
