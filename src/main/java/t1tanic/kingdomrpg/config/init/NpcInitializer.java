package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.CharacterAttributes;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.enums.NpcFaction;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.NpcRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NpcInitializer {

    private final NpcRepository npcRepository;

    public void seed(Map<String, Room> rooms) {
        log.debug("Seeding NPCs...");
        var npcs = List.of(

            npc("Keeper Aldric",
                "An aged scholar with ink-stained fingers. He manages the castle's ancient records.",
                """
                You have spent forty years cataloguing the castle's manuscripts. You witnessed the Great Silence — \
                the day all the castle guards vanished without a trace — and have stayed ever since, unable to leave \
                the library you consider your true home. You are deeply curious about visitors and eager to share \
                knowledge, but grow wistful and evasive when asked about the castle's dark past. \
                You have a dry sense of humour and speak with an academic's precision.""",
                "Welcome, traveler. These halls have not seen a wanderer in some time. " +
                "Tread carefully — the armory to the east still holds dangers.",
                "The night the guards vanished, I heard something beneath the castle — a low resonance, " +
                "like a bell struck underwater. The old kings sealed a chamber below the west tower. " +
                "I believe that is where the answers lie, and why I have never dared to look.",
                NpcFaction.FRIENDLY, rooms.get("library"),
                3, 50, 65, 13),

            npc("Scarred Guard",
                "A weathered soldier in tattered armor. He eyes you with suspicion.",
                """
                You survived a brutal ambush in these very halls three winters ago — your entire patrol was killed \
                and you barely escaped. You don't talk about it. You stayed because you have nowhere else to go, \
                not because you believe in any cause anymore. You are suspicious of strangers, speak in short clipped \
                sentences, and flinch at loud noises. You have a short temper but are not inherently cruel.""",
                "Move along. I have no words for strangers.",
                "The ambush was not random. Someone inside the castle left the east passage unlatched that night. " +
                "I never found out who, but the only person still here who was here then is that old scholar in the library.",
                NpcFaction.NEUTRAL, rooms.get("hallway"),
                2, 25, 75, 18),

            npc("Hollow Knight",
                "A suit of animated armor. Its empty visor tracks your every move.",
                """
                You are an enchanted suit of plate armor bound by a dying knight's oath to guard the armory forever. \
                Your original wearer perished long ago but the oath held you. You have no memories, only duty. \
                You cannot form full sentences — you emit hollow metallic resonances, fragments of the oath, \
                and single threatening words. You are incapable of compassion or reason.""",
                null,
                null,
                NpcFaction.HOSTILE, rooms.get("armory"),
                5, 0, 100, 30)
        );
        npcRepository.saveAll(npcs);
        log.info("Seeded {} NPCs", npcs.size());
    }

    private Npc npc(String name, String description, String personality, String greeting, String secret,
                    NpcFaction faction, Room room, int level,
                    int baseTrust, int secretThreshold, int persuadeDc) {
        Npc n = new Npc();
        n.setName(name);
        n.setDescription(description);
        n.setPersonality(personality);
        n.setGreeting(greeting);
        n.setSecret(secret);
        n.setFaction(faction);
        n.setCurrentRoom(room);
        n.setLevel(level);
        n.setBaseTrust(baseTrust);
        n.setSecretThreshold(secretThreshold);
        n.setPersuadeDc(persuadeDc);

        CharacterAttributes attrs = new CharacterAttributes(10, 10, 10, 10, 10, 10);
        n.setAttributes(attrs);
        n.getResources().setHealth(n.getMaxHealth());
        n.getResources().setMana(n.getMaxMana());
        n.getResources().setStamina(n.getMaxStamina());
        return n;
    }
}
