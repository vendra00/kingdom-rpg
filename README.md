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
Real-time WebSocket communication, D&D 5e mechanics, and a retro terminal aesthetic.

---

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.0.6 · Java 25 |
| Persistence | Spring Data JPA · Hibernate · H2 (dev) · PostgreSQL (prod) |
| Real-time | Spring WebSocket (`TextWebSocketHandler`) |
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

### Inventory
- Carry weight system based on STR + CON modifiers
- `take <item>` / `take all` — only visible (discovered) items can be picked up
- `drop <item>` / `drop all`
- Boxed ASCII inventory layout showing item type, weight, condition, and stats

### Ability Checks
25+ named abilities across 7 categories, resolved with **d20 + relevant modifier vs DC**:

| Category | Examples |
|---|---|
| Persuasion | Bribe, Intimidate, Deceive, Negotiate |
| Perception | Survey, Listen, Sense Motive |
| Athletics | Jump, Climb, Swim, Shove |
| Stealth | Hide, Sneak, Pickpocket |
| Knowledge | Recall Lore, Identify, Investigate |
| Survival | Forage, Track, Navigate |
| Acrobatics | Balance, Tumble, Dodge |

Criticals (natural 20) and fumbles (natural 1) override raw totals.

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
- Tab/arrow-key autocomplete for all commands, directions, abilities, cantrips, and dice notation
- Command history (up/down arrows)
- Web Speech API narrator — reads output aloud with adjustable voice/rate
- Background music with fade-out on game start
- Synthesized UI sound effects (Web Audio API — no audio files required)
  - Menu click · Back · Attribute +/− · Confirm arpeggio

---

## Commands

```
look              Examine your surroundings
go [direction]    Move — or just type: north, south, east, west (n/s/e/w)
search            Perception scan for hidden items (one attempt per room)
search [target]   Rummage through a named container
take [item]       Pick up a discovered item
take all          Pick up all discovered items in the room
drop [item]       Drop an item from inventory
drop all          Drop everything
inventory / inv   View your inventory
status / stats    Full character sheet
abilities / hab   Browse all 25+ ability checks
attempt [name]    Attempt an ability check (e.g. attempt climb)
spells            List your cantrips
cast [cantrip]    Cast a cantrip
roll [notation]   Roll dice (d20 · 2d6 · 1d8+3)
help              Show all commands
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
│   └── init/           DataInitializer · RoomInitializer · ItemInitializer · CantripInitializer
├── domain/
│   ├── character/       Player · CharacterAttributes · CharacterResources · CharacterIdentity
│   ├── item/            Item (abstract) · Weapon · Armor · Shield · Consumable · KeyItem
│   ├── magic/           Cantrip
│   └── world/           Room
├── engine/
│   ├── commands/        One class per verb — LookCommand · GoCommand · SearchCommand …
│   ├── dice/            Dice enum · DiceRoll (immutable result)
│   ├── enums/           MarkupTag (BBCode-style markup)
│   ├── CommandParser    Verb routing + Micrometer timing
│   └── GameEngine       Transaction boundary · stats injection
├── websocket/           GameWebSocketHandler
└── web/                 PlayerController (save listing REST endpoint)

src/main/resources/static/
├── index.html           Vue 3 SPA shell
├── css/style.css        Terminal green theme
└── js/
    ├── app.js           Vue app — state, WebSocket, markup parsing
    ├── sounds.js        Web Audio API synthesized UI sounds
    └── data/            completions · wizard · attributes · cantrips
```

---

## Markup System

The server wraps text in BBCode-style tags. The frontend's `parseMarkup()` converts them to styled HTML:

| Tag | Purpose | Rendered as |
|---|---|---|
| `[room]...[/room]` | Room names | White bold |
| `[exit]...[/exit]` | Exits, containers | Cyan |
| `[item]...[/item]` | Item names | Gold bold |
| `[narrate]...[/narrate]` | Flavor text | Unstyled (narrator reads this) |
| `[c=#hex]...[/c]` | Inline color | `<span style="color">` |
| `[nerd]...[/nerd]` | Dice mechanics | Stripped from main output → nerd panel |
| `[stats]csv[/stats]` | Player vitals | Stripped → updates sidebar silently |

---

## Author

**t1tanic**
