'use strict';

// ── Wizard steps ──────────────────────────────────────────
const STEPS = ['race', 'class', 'gender', 'background', 'attributes', 'cantrips', 'summary'];

// ── Races ─────────────────────────────────────────────────
const RACES = [
    { id:'human',    name:'Human',    desc:'Versatile and ambitious. Adapt to any role.',          trait:'+1 to all attributes',   bonuses:{strength:1,dexterity:1,constitution:1,intelligence:1,wisdom:1,charisma:1} },
    { id:'elf',      name:'Elf',      desc:'Ancient, graceful, attuned to magic and nature.',      trait:'+2 DEX · +1 INT',        bonuses:{dexterity:2,intelligence:1} },
    { id:'dwarf',    name:'Dwarf',    desc:'Stout mountain folk renowned for resilience.',          trait:'+2 CON · +1 STR',        bonuses:{constitution:2,strength:1} },
    { id:'halforc',  name:'Half-Orc', desc:'Raw power and endurance beyond human limits.',         trait:'+2 STR · +1 CON',        bonuses:{strength:2,constitution:1} },
    { id:'halfling', name:'Halfling', desc:'Small, nimble, and remarkably lucky.',                  trait:'+2 DEX · +1 CHA',        bonuses:{dexterity:2,charisma:1} },
    { id:'tiefling', name:'Tiefling', desc:'Infernal heritage grants dark power and charm.',        trait:'+2 CHA · +1 INT',        bonuses:{charisma:2,intelligence:1} },
];

// ── Classes ───────────────────────────────────────────────
const CLASSES = [
    { id:'warrior', name:'Warrior', desc:'Master of combat and physical endurance.',              trait:'Second Wind — recover HP in battle',          primary:'STR · CON' },
    { id:'mage',    name:'Mage',    desc:'Wielder of devastating arcane spells.',                 trait:'Arcane Recovery — recover Mana after rest',   primary:'INT · WIS' },
    { id:'rogue',   name:'Rogue',   desc:'Strikes from shadow with deadly precision.',            trait:'Sneak Attack — bonus damage while unseen',    primary:'DEX · CHA' },
    { id:'cleric',  name:'Cleric',  desc:'Channels divine power to heal and smite.',             trait:'Divine Favor — restore HP mid-combat',        primary:'WIS · CON' },
    { id:'ranger',  name:'Ranger',  desc:'Hunter and tracker of the wild lands.',                 trait:'Favored Enemy — bonus vs. chosen foe type',   primary:'DEX · STR' },
    { id:'paladin', name:'Paladin', desc:'Holy warrior who smites evil with divine fury.',        trait:'Divine Smite — expend Mana for holy damage',  primary:'STR · CHA' },
];

// ── Genders ───────────────────────────────────────────────
const GENDERS = [
    { id:'male',      name:'Male' },
    { id:'female',    name:'Female' },
    { id:'nonbinary', name:'Non-binary' },
    { id:'other',     name:'Rather not say' },
];

// ── Backgrounds ───────────────────────────────────────────
const BACKGROUNDS = [
    { id:'noble',     name:'Noble',     desc:'Born into privilege and accustomed to power.',      trait:'+1 CHA · Social advantages',   bonus:{charisma:1} },
    { id:'soldier',   name:'Soldier',   desc:'Trained for war and forged in battle.',             trait:'+1 STR · Military connections', bonus:{strength:1} },
    { id:'scholar',   name:'Scholar',   desc:'Student of arcane arts and ancient lore.',          trait:'+1 INT · Research proficiency', bonus:{intelligence:1} },
    { id:'outlander', name:'Outlander', desc:'Raised in the wilderness, tough and self-reliant.', trait:'+1 CON · Survival skills',      bonus:{constitution:1} },
    { id:'criminal',  name:'Criminal',  desc:'Quick hands and a talent for staying alive.',       trait:'+1 DEX · Underworld contacts',  bonus:{dexterity:1} },
    { id:'acolyte',   name:'Acolyte',   desc:'Devoted servant of a deity, wise and attuned.',     trait:'+1 WIS · Temple connections',   bonus:{wisdom:1} },
];

// ── Cantrips ──────────────────────────────────────────────
const CANTRIP_SLOTS = { warrior:1, paladin:2, ranger:2, rogue:2, cleric:3, mage:4 };

const CANTRIPS = [
    // Damage
    { id:'fire_bolt',    name:'Fire Bolt',       school:'Evocation',     desc:'Hurl a mote of fire. Deals 1d10 fire damage.',                              classes:['mage'],                           effect:'damage',  dmg:'fire' },
    { id:'ray_of_frost', name:'Ray of Frost',    school:'Evocation',     desc:'A frigid beam. Deals 1d8 cold damage, reduces speed.',                      classes:['mage'],                           effect:'damage',  dmg:'cold' },
    { id:'shock_grasp',  name:'Shocking Grasp',  school:'Evocation',     desc:'Lightning from your hand. Deals 1d8 lightning; target loses reactions.',    classes:['mage'],                           effect:'damage',  dmg:'lightning' },
    { id:'sacred_flame', name:'Sacred Flame',    school:'Evocation',     desc:'Flame-like radiance descends. Deals 1d8 radiant damage.',                   classes:['cleric','paladin'],                effect:'damage',  dmg:'radiant' },
    { id:'eldritch',     name:'Eldritch Blast',  school:'Evocation',     desc:'A beam of crackling force. Deals 1d10 force damage.',                       classes:['mage','rogue'],                   effect:'damage',  dmg:'force' },
    { id:'toll_dead',    name:'Toll the Dead',   school:'Necromancy',    desc:'A doleful bell. Deals 1d8 necrotic (1d12 if wounded).',                     classes:['mage','cleric'],                  effect:'damage',  dmg:'necrotic' },
    { id:'chill_touch',  name:'Chill Touch',     school:'Necromancy',    desc:'A ghostly hand chills. 1d8 necrotic; prevents HP recovery.',                classes:['mage'],                           effect:'debuff',  dmg:'necrotic' },
    { id:'acid_splash',  name:'Acid Splash',     school:'Conjuration',   desc:'Hurl a bubble of acid. Deals 1d6 acid damage.',                             classes:['mage','rogue'],                   effect:'damage',  dmg:'acid' },
    { id:'poison_spray', name:'Poison Spray',    school:'Conjuration',   desc:'A cone of noxious gas. Deals 1d12 poison damage.',                          classes:['mage','cleric','ranger'],         effect:'damage',  dmg:'poison' },
    { id:'thorn_whip',   name:'Thorn Whip',      school:'Transmutation', desc:'A vine whip strikes. 1d6 piercing; pulls target 10 ft closer.',             classes:['cleric','ranger'],                effect:'damage',  dmg:'piercing' },
    { id:'vicious_mock', name:'Vicious Mockery', school:'Enchantment',   desc:'Magical insults. 1d4 psychic; disadvantage on target\'s next attack.',      classes:['mage'],                           effect:'debuff',  dmg:'psychic' },
    // Utility
    { id:'mage_hand',    name:'Mage Hand',        school:'Conjuration',   desc:'A spectral hand manipulates objects up to 30 ft away.',                    classes:['mage','rogue'],                   effect:'utility', dmg:null },
    { id:'minor_illus',  name:'Minor Illusion',   school:'Illusion',      desc:'Create a sound or image. Lasts 1 minute.',                                 classes:['mage','rogue'],                   effect:'utility', dmg:null },
    { id:'light',        name:'Light',            school:'Evocation',     desc:'Object sheds bright light in a 20-foot radius.',                           classes:['mage','cleric','paladin'],         effect:'utility', dmg:null },
    { id:'prestidig',    name:'Prestidigitation', school:'Transmutation', desc:'Minor magical tricks: light candles, clean objects, create brief effects.', classes:['mage','rogue'],                   effect:'utility', dmg:null },
    // Buff / Support
    { id:'guidance',     name:'Guidance',         school:'Divination',    desc:'Touch a creature. It may add 1d4 to one ability check.',                   classes:['cleric'],                         effect:'buff',    dmg:null },
    { id:'resistance',   name:'Resistance',       school:'Abjuration',    desc:'Touch a creature. It may add 1d4 to one saving throw.',                    classes:['cleric','paladin'],                effect:'buff',    dmg:null },
    { id:'blade_ward',   name:'Blade Ward',       school:'Abjuration',    desc:'Resistance to bludgeoning, piercing, and slashing damage until next turn.', classes:['warrior','paladin','rogue'],      effect:'buff',    dmg:null },
    { id:'true_strike',  name:'True Strike',      school:'Divination',    desc:'Insight into target\'s defenses. Advantage on next attack roll.',          classes:['mage','rogue','ranger','warrior'], effect:'buff',    dmg:null },
    // Healing
    { id:'spare_dying',  name:'Spare the Dying',  school:'Necromancy',    desc:'Touch a creature at 0 HP. It becomes stable.',                             classes:['cleric'],                         effect:'healing', dmg:null },
];

// ── Attributes ────────────────────────────────────────────
const ATTR_DEFS = [
    { key:'strength',     label:'STRENGTH',     desc:'Physical power, melee damage' },
    { key:'dexterity',    label:'DEXTERITY',    desc:'Agility, hit chance, dodge' },
    { key:'constitution', label:'CONSTITUTION', desc:'Endurance, max HP' },
    { key:'intelligence', label:'INTELLIGENCE', desc:'Arcane power, max Mana' },
    { key:'wisdom',       label:'WISDOM',       desc:'Perception, Mana regen' },
    { key:'charisma',     label:'CHARISMA',     desc:'Persuasion, dialogue' },
];

// ── Point-buy constants ───────────────────────────────────
const TOTAL_POINTS = 27;
const POINT_COSTS  = [0, 1, 2, 3, 4, 5, 7, 9]; // cost to reach values 8–15
const ATTR_MIN = 8, ATTR_MAX = 15;
