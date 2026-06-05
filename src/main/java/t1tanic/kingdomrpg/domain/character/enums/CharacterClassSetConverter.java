package t1tanic.kingdomrpg.domain.character.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA converter that maps a {@code Set<CharacterClass>} to a comma-separated string stored in the database
 * (e.g. {@code "mage,rogue"}) and back, preserving the same lowercase id format used by
 * {@code CantripRepository}'s LIKE queries.
 *
 * @author t1tanic
 * @version 1.0
 */
@Converter
public class CharacterClassSetConverter implements AttributeConverter<Set<CharacterClass>, String> {

    @Override
    public String convertToDatabaseColumn(Set<CharacterClass> classes) {
        if (classes == null || classes.isEmpty()) return "";
        return classes.stream()
                      .map(CharacterClass::id)
                      .sorted()
                      .collect(Collectors.joining(","));
    }

    @Override
    public Set<CharacterClass> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return EnumSet.noneOf(CharacterClass.class);
        return Arrays.stream(dbData.split(","))
                     .map(String::trim)
                     .map(token -> Arrays.stream(CharacterClass.values())
                                         .filter(cc -> cc.id().equals(token))
                                         .findFirst()
                                         .orElseThrow(() -> new IllegalArgumentException("Unknown class id: " + token)))
                     .collect(Collectors.toCollection(() -> EnumSet.noneOf(CharacterClass.class)));
    }
}
