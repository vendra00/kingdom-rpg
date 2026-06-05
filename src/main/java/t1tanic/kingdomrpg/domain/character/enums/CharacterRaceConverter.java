package t1tanic.kingdomrpg.domain.character.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter that maps a {@link CharacterRace} to its normalised lowercase {@code id()} string
 * (e.g. {@code "halforc"}) and back, preserving the format used by the frontend payload and
 * keeping stored values human-readable.
 *
 * @author t1tanic
 * @version 1.0
 */
@Converter
public class CharacterRaceConverter implements AttributeConverter<CharacterRace, String> {

    @Override
    public String convertToDatabaseColumn(CharacterRace race) {
        return race != null ? race.id() : null;
    }

    @Override
    public CharacterRace convertToEntityAttribute(String dbData) {
        return CharacterRace.fromString(dbData);
    }
}
