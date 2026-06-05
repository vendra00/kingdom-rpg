# Kingdom RPG

```
 ██╗  ██╗██╗███╗   ██╗ ██████╗ ██████╗  ██████╗ ███╗   ███╗
 ██║ ██╔╝██║████╗  ██║██╔════╝ ██╔══██╗██╔═══██╗████╗ ████║
 █████╔╝ ██║██╔██╗ ██║██║  ███╗██║  ██║██║   ██║██╔████╔██║
 ██╔═██╗ ██║██║╚██╗██║██║   ██║██║  ██║██║   ██║██║╚██╔╝██║
 ██║  ██╗██║██║ ╚████║╚██████╔╝██████╔╝╚██████╔╝██║ ╚═╝ ██║
 ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝     ╚═╝
```

A browser-based text RPG engine built with Spring Boot and Vue 3.  
Real-time WebSocket communication, D&D 5e mechanics, AI-driven NPC dialogue, and a retro terminal aesthetic.

---

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.0.6 · Java 25 |
| Persistence | Spring Data JPA · Hibernate · H2 (dev) · PostgreSQL (prod) |
| Real-time | Spring WebSocket (`TextWebSocketHandler`) |
| AI | Anthropic Claude Haiku (NPC dialogue via Messages API) |
| Observability | Spring Actuator · Micrometer |
| Frontend | Vue 3 (CDN) · Vanilla JS ES modules · Web Audio API |
| Build | Gradle 9 |
| Containers | Docker Compose (app + Postgres 16) |

---

## Features

### Character Creation Wizard
A multi-step guided flow before entering the world.

- **Race** — Human, Elf, Dwarf, Half-Orc, Halfling, Tiefling — each with unique attribute bonuses
- **Class** — Warrior, Mage, Rogue, Cleric, Ranger, Bard, Paladin, Druid
- **Gender & Background** — affects roleplaying flavor and minor attribute bonuses
- **Point-buy attributes** — D&D 5e point-buy system across STR, DEX, CON, INT, WIS, CHA. All points must be spent before continuing
- **Cantrips** — class-specific innate spells (Mages get more slots, Warriors none)

### World & Exploration
- Rooms connected via cardinal directions (N/S/E/W)
- `look` shows only the room name and description — no item or exit spoilers
- Players must navigate by reading narrative hints in the room text
- Persistent save system — return to exactly where you left off

### Item System
Items are seeded into the world and persist per player session.

| Type | Description |
|---|---|
| `Weapon` | Attack min/max, melee or ranged, damage type (Slashing, Piercing, Fire, Cold…) |
| `Armor` | Armor class, light/medium/heavy classification |
| `Shield` | Defense bonus |
| `Consumable` | Charges-based (potions, rations, torches) |
| `KeyItem` | Quest items and story objects |

All items have a **durability system** — condition degrades from `Pristine` → `Good` → `Worn` → `Damaged` → `Broken`, scaling effective stats accordingly. Condition is color-coded in inventory.

### Discovery & Search
Items start hidden (`visible = false`). They are only revealed through active exploration:

- `search` — rolls **d20 + WIS modifier** against each hidden item's Perception DC. One attempt per room, result is permanent
- `search <container>` — physically rummages a named object (crate, book, chest), auto-reveals its contents, no roll required
- Container hints appear in `look` output without revealing what's inside

### Inventory & Economy
- Carry weight system based on STR + CON modifiers
- `take <item>` / `take all` — only visible (discovered) items can be picked up
- `drop <item>` / `drop all`
- Boxed ASCII inventory layout showing item type, weight, condition, and stats
- **Gold wallet** — displayed in inventory and on the status sheet; players start with 10 gold

### Ability Checks
25+ named abilities across 7 categories, resolved with **d20 + relevant modifier vs DC**:

| Category | Examples |
|---|---|
| Persuasion | Bribe, Intimidate, Deceive, Negotiate, Convince, Sense |
| Perception | Survey, Listen |
| Athletics | Jump, Climb, Swim, Shove |
| Stealth | Hide, Sneak, Pickpocket |
| Knowledge | Recall Lore, Identify, Investigate |
| Survival | Forage, Track, Navigate |
| Acrobatics | Balance, Tumble, Dodge |

Criticals (natural 20) and fumbles (natural 1) override raw totals.

### NPC System
NPCs are AI-driven characters powered by the Anthropic Messages API.

#### Dialogue
- `talk <npc> <message>` — free-form conversation; the LLM generates an in-character reply
- `talk [intent] <npc> <message>` — declare a persuasion intent before speaking:

| Intent | Ability | Description |
|---|---|---|
| `neutral` | — | Plain conversation, no check |
| `convince` | Persuasion | Reasoned argument or appeal to self-interest |
| `intimidate` | Persuasion | Explicit threat of physical harm |
| `deceive` | Persuasion | State a falsehood to manipulate |
| `negotiate` | Persuasion | Propose a specific mutual exchange |
| `bribe` | Persuasion | Offer coin, goods, or a reward |
| `sense` | Persuasion | Probe for true feelings or loyalty |

When an intent is declared the ability check resolves immediately, bypassing LLM detection. The LLM receives the check outcome and reacts in-character.

#### Trust System
Each NPC tracks a per-player trust score (0–100):

- Successful persuasion raises trust; failure and fumbles lower it
- The LLM emits `[TRUST:+N]` / `[TRUST:-N]` markers that the engine parses and applies silently
- Trust level maps to a faction disposition: **Friendly / Neutral / Hostile**
- Hostile NPCs refuse to talk

#### NPC Authored Flavor Text
Each NPC can have hand-authored outcome lines for any persuasion ability × success/failure combination. When present, the authored line is appended after the LLM reply for extra flavor.

#### NPC Inventory & Gold
- NPCs carry a gold wallet and an item inventory
- `bribe` deducts gold from the player (default: 5 gold) regardless of success; on success the NPC receives it
- NPC gold and items persist in the database

### Steal System
`steal <npc>` or `steal <npc> <item>` — attempt to pickpocket an NPC.

- Resolves a **PICKPOCKET** ability check (d20 + DEX modifier vs DC)
- **Gold theft** — on success, transfers all of the NPC's gold to the player
- **Item theft** — on success, transfers the named item (if it fits the player's carry weight)
- Failure: −8 trust with the NPC; fumble (natural 1): −20 trust

### Dice Engine
`roll d20`, `roll 2d6`, `roll 1d8+3` — full dice notation parser with modifier support.

### Cantrips & Spells
Class-gated cantrip selection during character creation. Cast in-game with `cast <name>`.

### Game HUD
PS2-inspired persistent interface:

- **Top bar** — brand, character name/race/class, current room name
- **Left sidebar** — always-visible HP / MP / ST bars with animated fills, carry weight indicator. HP color shifts green → yellow → red as health drops
- **Nerd stats panel** — collapsible panel showing dice roll breakdowns with timestamps (d20 → 14 + 2 (WIS) = 16)
- **Input row** — command input, autocomplete dropdown, Nerd toggle, Voice narrator

### UI & Accessibility
- Tab/arrow-key autocomplete for all commands, directions, abilities, cantrips, dice notation, and NPC names
- Two-stage talk autocomplete: intent keywords → NPC names
- Room items discovered so far suggested in `take` / `equip` autocomplete
- Command history (up/down arrows)
- Web Speech API narrator — reads output aloud with adjustable voice/rate
- Background music with fade-out on game start
- Synthesized UI sound effects (Web Audio API — no audio files required)
  - Menu click · Back · Attribute +/− · Confirm arpeggio

---

## Commands

```
look                          Examine your surroundings
go [direction]                Move — or just type: north, south, east, west (n/s/e/w)
search                        Perception scan for hidden items (one attempt per room)
search [target]               Rummage through a named container
take [item]                   Pick up a discovered item
take all                      Pick up all discovered items in the room
drop [item]                   Drop an item from inventory
drop all                      Drop everything
equip [item]                  Equip a weapon, armor, or shield from inventory
unequip [slot]                Remove equipped item (main hand · off hand · body)
inventory / inv               View your inventory and gold
status / stats                Full character sheet
abilities / hab               Browse all 25+ ability checks
attempt [name]                Attempt an ability check (e.g. attempt climb)
talk <npc> <message>          Talk to an NPC
talk [intent] <npc> <message> Talk with declared persuasion intent
steal <npc>                   Attempt to pickpocket an NPC's gold
steal <npc> <item>            Attempt to steal a specific item from an NPC
spells                        List your cantrips
cast [cantrip]                Cast a cantrip
roll [notation]               Roll dice (d20 · 2d6 · 1d8+3)
help                          Show all commands
```

---

## Balance Configuration

Game balance knobs are in `application.properties` and bound via `GameProperties`:

```properties
game.npc.bribe-cost=5      # Gold deducted from player when using bribe intent
```

---

## Getting Started

### Local (H2 in-memory)

```bash
./gradlew bootRun
```

Open [http://localhost:8080](http://localhost:8080).  
The database is recreated on every run (`create-drop`). No setup needed.

### Docker (PostgreSQL)

```bash
docker compose up --build
```

Open [http://localhost:8080](http://localhost:8080).  
Data persists in the `pgdata` Docker volume between restarts.

### H2 Console (dev only)

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:mem:kingdomrpg` · User: `sa` · Password: *(empty)*

### Actuator

```
/actuator/health   Application health
/actuator/metrics  All Micrometer metrics
/actuator/info     Build info
```

Command execution time is tracked under the `game.command.duration` metric, tagged by verb.

---

## Project Structure

```
src/main/java/t1tanic/kingdomrpg/
├── config/
│   ├── GameProperties       Balance knobs (@ConfigurationProperties)
│   └── init/                DataInitializer · RoomInitializer · ItemInitializer
│                            CantripInitializer · NpcInitializer
├── domain/
│   ├── character/           Player · Npc · BaseCharacter (MappedSuperclass)
│   │                        CharacterAttributes · CharacterResources · CharacterIdentity
│   │                        Equipment · NpcAbilityOutcomes
│   ├── item/                Item (abstract) · Weapon · Armor · Shield · Consumable · KeyItem
│   ├── magic/               Cantrip
│   └── world/               Room
├── engine/
│   ├── ai/                  NpcAiService (Anthropic API) · NpcTrustService
│   ├── commands/            One class per verb:
│   │                        LookCommand · GoCommand · SearchCommand · TakeCommand
│   │                        DropCommand · InventoryCommand · StatusCommand
│   │                        EquipCommand · UnequipCommand · TalkCommand
│   │                        StealCommand · AbilitiesCommand · AttemptCommand
│   │                        SpellsCommand · CastCommand · RollCommand · HelpCommand
│   ├── dice/                Dice enum · DiceRoll (immutable result)
│   ├── enums/               MarkupTag (BBCode-style markup)
│   ├── CommandParser        Verb routing + Micrometer timing
│   └── GameEngine           Transaction boundary · stats injection
├── websocket/               GameWebSocketHandler
└── web/                     PlayerController (save listing REST endpoint)

src/main/resources/static/
├── index.html               Vue 3 SPA shell
├── css/style.css            Terminal green theme
└── js/
    ├── app.js               Vue app — state, WebSocket, markup parsing, autocomplete
    ├── sounds.js            Web Audio API synthesized UI sounds
    └── data/                completions · wizard · attributes · cantrips
```

---

## Markup System

The server wraps text in BBCode-style tags. The frontend's `parseMarkup()` converts them to styled HTML:

| Tag | Purpose | Rendered as |
|---|---|---|
| `[room]...[/room]` | Room names | White bold |
| `[exit]...[/exit]` | Exit directions | Cyan |
| `[item]...[/item]` | Item names in the world | Gold bold |
| `[invitem]...[/invitem]` | Item names in inventory | Gold bold |
| `[container]...[/container]` | Container hints | Cyan italic |
| `[npc]...[/npc]` | NPC names | Purple/violet |
| `[equipped]...[/equipped]` | Equipped item names | Warm white |
| `[narrate]...[/narrate]` | Flavor text | Unstyled (narrator reads this) |
| `[c=#hex]...[/c]` | Inline color | `<span style="color">` |
| `[nerd]...[/nerd]` | Dice mechanics | Stripped from main output → nerd panel |
| `[stats]csv[/stats]` | Player vitals | Stripped → updates sidebar silently |

---

## Author

**t1tanic**
