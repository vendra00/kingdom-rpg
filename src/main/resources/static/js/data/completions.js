'use strict';

export const COMMAND_COMPLETIONS = [
    { value: 'look',       hint: 'Examine your surroundings' },
    { value: 'go',         hint: 'Move in a direction  (north south east west)' },
    { value: 'north',      hint: 'Go north' },
    { value: 'south',      hint: 'Go south' },
    { value: 'east',       hint: 'Go east' },
    { value: 'west',       hint: 'Go west' },
    { value: 'take',       hint: 'Pick up an item from the room' },
    { value: 'drop',       hint: 'Drop an item from your inventory' },
    { value: 'inventory',  hint: 'Check your inventory' },
    { value: 'status',     hint: 'View your character stats' },
    { value: 'spells',     hint: 'List your learned cantrips' },
    { value: 'cast',       hint: 'Cast a cantrip' },
    { value: 'roll',       hint: 'Roll dice  (d20, 2d6, 1d8+3 …)' },
    { value: 'abilities',  hint: 'Open your ability book' },
    { value: 'attempt',    hint: 'Attempt an ability check' },
    { value: 'search',     hint: 'Search the room or a specific container' },
    { value: 'equip',      hint: 'Equip an item from your inventory' },
    { value: 'unequip',    hint: 'Remove an item from an equipped slot' },
    { value: 'talk',       hint: 'Talk to an NPC  (talk [intent] <name> <message>)' },
    { value: 'help',       hint: 'Show all available commands' },
];

export const TAKE_COMPLETIONS = [
    { value: 'all', hint: 'Pick up everything visible in the room' },
];

export const DROP_COMPLETIONS = [
    { value: 'all', hint: 'Drop everything from your inventory' },
];

export const UNEQUIP_COMPLETIONS = [
    { value: 'main hand', hint: 'Remove weapon from main hand' },
    { value: 'off hand',  hint: 'Remove shield from off hand' },
    { value: 'body',      hint: 'Remove armor from body slot' },
];

export const DIRECTION_COMPLETIONS = [
    { value: 'north', hint: '' },
    { value: 'south', hint: '' },
    { value: 'east',  hint: '' },
    { value: 'west',  hint: '' },
];

export const DICE_COMPLETIONS = [
    { value: 'd4',     hint: '4-sided die' },
    { value: 'd6',     hint: '6-sided die' },
    { value: 'd8',     hint: '8-sided die' },
    { value: 'd10',    hint: '10-sided die' },
    { value: 'd12',    hint: '12-sided die' },
    { value: 'd20',    hint: '20-sided die  (attack / ability check)' },
    { value: 'd100',   hint: 'Percentile die' },
    { value: '2d6',    hint: 'Two six-sided dice' },
    { value: '2d6+3',  hint: 'Two d6 with +3 modifier' },
    { value: '1d20+5', hint: 'd20 with +5 modifier' },
];

export const TALK_INTENT_COMPLETIONS = [
    { value: 'neutral',    hint: 'No persuasion — just talking' },
    { value: 'convince',   hint: 'Persuasion · Reasoned argument or appeal to self-interest' },
    { value: 'intimidate', hint: 'Persuasion · Explicit threat of physical harm' },
    { value: 'deceive',    hint: 'Persuasion · State a falsehood to manipulate' },
    { value: 'negotiate',  hint: 'Persuasion · Propose a specific mutual exchange' },
    { value: 'bribe',      hint: 'Persuasion · Offer coin, goods, or a reward' },
    { value: 'sense',      hint: 'Persuasion · Probe for true feelings or loyalty' },
];

export const ABILITY_COMPLETIONS = [
    // Perception
    { value: 'survey',      hint: 'Perception · Scan for unusual details' },
    { value: 'listen',      hint: 'Perception · Focus hearing for nearby sounds' },
    // Athletics
    { value: 'jump',        hint: 'Athletics  · Leap across a gap or obstacle' },
    { value: 'climb',       hint: 'Athletics  · Scale a wall or vertical surface' },
    { value: 'swim',        hint: 'Athletics  · Propel yourself through water' },
    { value: 'shove',       hint: 'Athletics  · Forcefully push an obstacle aside' },
    // Stealth
    { value: 'hide',        hint: 'Stealth    · Conceal yourself in shadows' },
    { value: 'sneak',       hint: 'Stealth    · Move without making noise' },
    { value: 'pickpocket',  hint: 'Stealth    · Quietly lift a small item' },
    // Knowledge
    { value: 'recall',      hint: 'Knowledge  · Draw upon memory of lore' },
    { value: 'identify',    hint: 'Knowledge  · Examine an unknown object' },
    { value: 'investigate', hint: 'Knowledge  · Analyze clues for deeper meaning' },
    // Survival
    { value: 'forage',      hint: 'Survival   · Search for food or herbs' },
    { value: 'track',       hint: 'Survival   · Follow the trail of a creature' },
    { value: 'navigate',    hint: 'Survival   · Determine your bearings' },
    // Acrobatics
    { value: 'balance',     hint: 'Acrobatics · Maintain footing on unstable surface' },
    { value: 'tumble',      hint: 'Acrobatics · Roll past an obstacle or hazard' },
    { value: 'dodge',       hint: 'Acrobatics · Evade an incoming hazard' },
];
