'use strict';

export const STEPS = ['race', 'class', 'gender', 'background', 'attributes', 'cantrips', 'summary'];

export const RACES = [
    { id:'human',    name:'Human',    desc:'Versatile and ambitious. Adapt to any role.',          trait:'+1 to all attributes',   bonuses:{strength:1,dexterity:1,constitution:1,intelligence:1,wisdom:1,charisma:1} },
    { id:'elf',      name:'Elf',      desc:'Ancient, graceful, attuned to magic and nature.',      trait:'+2 DEX · +1 INT',        bonuses:{dexterity:2,intelligence:1} },
    { id:'dwarf',    name:'Dwarf',    desc:'Stout mountain folk renowned for resilience.',          trait:'+2 CON · +1 STR',        bonuses:{constitution:2,strength:1} },
    { id:'halforc',  name:'Half-Orc', desc:'Raw power and endurance beyond human limits.',         trait:'+2 STR · +1 CON',        bonuses:{strength:2,constitution:1} },
    { id:'halfling', name:'Halfling', desc:'Small, nimble, and remarkably lucky.',                  trait:'+2 DEX · +1 CHA',        bonuses:{dexterity:2,charisma:1} },
    { id:'tiefling', name:'Tiefling', desc:'Infernal heritage grants dark power and charm.',        trait:'+2 CHA · +1 INT',        bonuses:{charisma:2,intelligence:1} },
];

export const CLASSES = [
    { id:'warrior', name:'Warrior', desc:'Master of combat and physical endurance.',              trait:'Second Wind — recover HP in battle',          primary:'STR · CON' },
    { id:'mage',    name:'Mage',    desc:'Wielder of devastating arcane spells.',                 trait:'Arcane Recovery — recover Mana after rest',   primary:'INT · WIS' },
    { id:'rogue',   name:'Rogue',   desc:'Strikes from shadow with deadly precision.',            trait:'Sneak Attack — bonus damage while unseen',    primary:'DEX · CHA' },
    { id:'cleric',  name:'Cleric',  desc:'Channels divine power to heal and smite.',             trait:'Divine Favor — restore HP mid-combat',        primary:'WIS · CON' },
    { id:'ranger',  name:'Ranger',  desc:'Hunter and tracker of the wild lands.',                 trait:'Favored Enemy — bonus vs. chosen foe type',   primary:'DEX · STR' },
    { id:'paladin', name:'Paladin', desc:'Holy warrior who smites evil with divine fury.',        trait:'Divine Smite — expend Mana for holy damage',  primary:'STR · CHA' },
];

export const GENDERS = [
    { id:'male',      name:'Male' },
    { id:'female',    name:'Female' },
    { id:'nonbinary', name:'Non-binary' },
    { id:'other',     name:'Rather not say' },
];

export const BACKGROUNDS = [
    { id:'noble',     name:'Noble',     desc:'Born into privilege and accustomed to power.',      trait:'+1 CHA · Social advantages',   bonus:{charisma:1} },
    { id:'soldier',   name:'Soldier',   desc:'Trained for war and forged in battle.',             trait:'+1 STR · Military connections', bonus:{strength:1} },
    { id:'scholar',   name:'Scholar',   desc:'Student of arcane arts and ancient lore.',          trait:'+1 INT · Research proficiency', bonus:{intelligence:1} },
    { id:'outlander', name:'Outlander', desc:'Raised in the wilderness, tough and self-reliant.', trait:'+1 CON · Survival skills',      bonus:{constitution:1} },
    { id:'criminal',  name:'Criminal',  desc:'Quick hands and a talent for staying alive.',       trait:'+1 DEX · Underworld contacts',  bonus:{dexterity:1} },
    { id:'acolyte',   name:'Acolyte',   desc:'Devoted servant of a deity, wise and attuned.',     trait:'+1 WIS · Temple connections',   bonus:{wisdom:1} },
];
