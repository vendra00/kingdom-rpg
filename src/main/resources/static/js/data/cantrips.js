'use strict';

export const CANTRIP_SLOTS = { warrior:1, paladin:2, ranger:2, rogue:2, cleric:3, mage:4 };

export const CANTRIPS = [
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
