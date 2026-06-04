'use strict';

export const ATTR_DEFS = [
    { key:'strength',     label:'STRENGTH',     desc:'Physical power, melee damage' },
    { key:'dexterity',    label:'DEXTERITY',    desc:'Agility, hit chance, dodge' },
    { key:'constitution', label:'CONSTITUTION', desc:'Endurance, max HP' },
    { key:'intelligence', label:'INTELLIGENCE', desc:'Arcane power, max Mana' },
    { key:'wisdom',       label:'WISDOM',       desc:'Perception, Mana regen' },
    { key:'charisma',     label:'CHARISMA',     desc:'Persuasion, dialogue' },
];

export const TOTAL_POINTS = 27;
export const POINT_COSTS  = [0, 1, 2, 3, 4, 5, 7, 9]; // cost to reach values 8–15
export const ATTR_MIN = 8;
export const ATTR_MAX = 15;
