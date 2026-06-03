'use strict';

// ── D&D Data ──────────────────────────────────────────────
const RACES = [
    { id:'human',    name:'Human',    desc:'Versatile and ambitious. Adapt to any role.',          trait:'+1 to all attributes',   bonuses:{strength:1,dexterity:1,constitution:1,intelligence:1,wisdom:1,charisma:1} },
    { id:'elf',      name:'Elf',      desc:'Ancient, graceful, attuned to magic and nature.',      trait:'+2 DEX · +1 INT',        bonuses:{dexterity:2,intelligence:1} },
    { id:'dwarf',    name:'Dwarf',    desc:'Stout mountain folk renowned for resilience.',          trait:'+2 CON · +1 STR',        bonuses:{constitution:2,strength:1} },
    { id:'halforc',  name:'Half-Orc', desc:'Raw power and endurance beyond human limits.',         trait:'+2 STR · +1 CON',        bonuses:{strength:2,constitution:1} },
    { id:'halfling', name:'Halfling', desc:'Small, nimble, and remarkably lucky.',                  trait:'+2 DEX · +1 CHA',        bonuses:{dexterity:2,charisma:1} },
    { id:'tiefling', name:'Tiefling', desc:'Infernal heritage grants dark power and charm.',        trait:'+2 CHA · +1 INT',        bonuses:{charisma:2,intelligence:1} },
];

const CLASSES = [
    { id:'warrior', name:'Warrior', desc:'Master of combat and physical endurance.',              trait:'Second Wind — recover HP in battle',          primary:'STR · CON' },
    { id:'mage',    name:'Mage',    desc:'Wielder of devastating arcane spells.',                 trait:'Arcane Recovery — recover Mana after rest',   primary:'INT · WIS' },
    { id:'rogue',   name:'Rogue',   desc:'Strikes from shadow with deadly precision.',            trait:'Sneak Attack — bonus damage while unseen',    primary:'DEX · CHA' },
    { id:'cleric',  name:'Cleric',  desc:'Channels divine power to heal and smite.',             trait:'Divine Favor — restore HP mid-combat',        primary:'WIS · CON' },
    { id:'ranger',  name:'Ranger',  desc:'Hunter and tracker of the wild lands.',                 trait:'Favored Enemy — bonus vs. chosen foe type',   primary:'DEX · STR' },
    { id:'paladin', name:'Paladin', desc:'Holy warrior who smites evil with divine fury.',        trait:'Divine Smite — expend Mana for holy damage',  primary:'STR · CHA' },
];

const GENDERS = [
    { id:'male',      name:'Male' },
    { id:'female',    name:'Female' },
    { id:'nonbinary', name:'Non-binary' },
    { id:'other',     name:'Rather not say' },
];

const BACKGROUNDS = [
    { id:'noble',     name:'Noble',     desc:'Born into privilege and accustomed to power.',      trait:'+1 CHA · Social advantages',   bonus:{charisma:1} },
    { id:'soldier',   name:'Soldier',   desc:'Trained for war and forged in battle.',             trait:'+1 STR · Military connections', bonus:{strength:1} },
    { id:'scholar',   name:'Scholar',   desc:'Student of arcane arts and ancient lore.',          trait:'+1 INT · Research proficiency', bonus:{intelligence:1} },
    { id:'outlander', name:'Outlander', desc:'Raised in the wilderness, tough and self-reliant.', trait:'+1 CON · Survival skills',      bonus:{constitution:1} },
    { id:'criminal',  name:'Criminal',  desc:'Quick hands and a talent for staying alive.',       trait:'+1 DEX · Underworld contacts',  bonus:{dexterity:1} },
    { id:'acolyte',   name:'Acolyte',   desc:'Devoted servant of a deity, wise and attuned.',     trait:'+1 WIS · Temple connections',   bonus:{wisdom:1} },
];

const CANTRIP_SLOTS = { warrior:1, paladin:2, ranger:2, rogue:2, cleric:3, mage:4 };

const CANTRIPS = [
    // Damage
    { id:'fire_bolt',    name:'Fire Bolt',       school:'Evocation',     desc:'Hurl a mote of fire. Deals 1d10 fire damage.',                              classes:['mage'],                          effect:'damage',  dmg:'fire' },
    { id:'ray_of_frost', name:'Ray of Frost',    school:'Evocation',     desc:'A frigid beam. Deals 1d8 cold damage, reduces speed.',                      classes:['mage'],                          effect:'damage',  dmg:'cold' },
    { id:'shock_grasp',  name:'Shocking Grasp',  school:'Evocation',     desc:'Lightning from your hand. Deals 1d8 lightning; target loses reactions.',    classes:['mage'],                          effect:'damage',  dmg:'lightning' },
    { id:'sacred_flame', name:'Sacred Flame',    school:'Evocation',     desc:'Flame-like radiance descends. Deals 1d8 radiant damage.',                   classes:['cleric','paladin'],               effect:'damage',  dmg:'radiant' },
    { id:'eldritch',     name:'Eldritch Blast',  school:'Evocation',     desc:'A beam of crackling force. Deals 1d10 force damage.',                       classes:['mage','rogue'],                  effect:'damage',  dmg:'force' },
    { id:'toll_dead',    name:'Toll the Dead',   school:'Necromancy',    desc:'A doleful bell. Deals 1d8 necrotic (1d12 if wounded).',                     classes:['mage','cleric'],                 effect:'damage',  dmg:'necrotic' },
    { id:'chill_touch',  name:'Chill Touch',     school:'Necromancy',    desc:'A ghostly hand chills. 1d8 necrotic; prevents HP recovery.',                classes:['mage'],                          effect:'debuff',  dmg:'necrotic' },
    { id:'acid_splash',  name:'Acid Splash',     school:'Conjuration',   desc:'Hurl a bubble of acid. Deals 1d6 acid damage.',                             classes:['mage','rogue'],                  effect:'damage',  dmg:'acid' },
    { id:'poison_spray', name:'Poison Spray',    school:'Conjuration',   desc:'A cone of noxious gas. Deals 1d12 poison damage.',                          classes:['mage','cleric','ranger'],        effect:'damage',  dmg:'poison' },
    { id:'thorn_whip',   name:'Thorn Whip',      school:'Transmutation', desc:'A vine whip strikes. 1d6 piercing; pulls target 10 ft closer.',             classes:['cleric','ranger'],               effect:'damage',  dmg:'piercing' },
    { id:'vicious_mock', name:'Vicious Mockery', school:'Enchantment',   desc:'Magical insults. 1d4 psychic; disadvantage on target\'s next attack.',      classes:['mage'],                          effect:'debuff',  dmg:'psychic' },
    // Utility
    { id:'mage_hand',    name:'Mage Hand',        school:'Conjuration',   desc:'A spectral hand manipulates objects up to 30 ft away.',                    classes:['mage','rogue'],                  effect:'utility', dmg:null },
    { id:'minor_illus',  name:'Minor Illusion',   school:'Illusion',      desc:'Create a sound or image. Lasts 1 minute.',                                 classes:['mage','rogue'],                  effect:'utility', dmg:null },
    { id:'light',        name:'Light',            school:'Evocation',     desc:'Object sheds bright light in a 20-foot radius.',                           classes:['mage','cleric','paladin'],        effect:'utility', dmg:null },
    { id:'prestidig',    name:'Prestidigitation', school:'Transmutation', desc:'Minor magical tricks: light candles, clean objects, create brief effects.', classes:['mage','rogue'],                  effect:'utility', dmg:null },
    // Buff / Support
    { id:'guidance',     name:'Guidance',         school:'Divination',    desc:'Touch a creature. It may add 1d4 to one ability check.',                   classes:['cleric'],                        effect:'buff',    dmg:null },
    { id:'resistance',   name:'Resistance',       school:'Abjuration',    desc:'Touch a creature. It may add 1d4 to one saving throw.',                    classes:['cleric','paladin'],               effect:'buff',    dmg:null },
    { id:'blade_ward',   name:'Blade Ward',       school:'Abjuration',    desc:'Resistance to bludgeoning, piercing, and slashing damage until next turn.', classes:['warrior','paladin','rogue'],     effect:'buff',    dmg:null },
    { id:'true_strike',  name:'True Strike',      school:'Divination',    desc:'Insight into target\'s defenses. Advantage on next attack roll.',          classes:['mage','rogue','ranger','warrior'],effect:'buff',    dmg:null },
    { id:'spare_dying',  name:'Spare the Dying',  school:'Necromancy',    desc:'Touch a creature at 0 HP. It becomes stable.',                             classes:['cleric'],                        effect:'healing', dmg:null },
];

const ATTR_DEFS = [
    { key:'strength',     label:'STRENGTH',     desc:'Physical power, melee damage' },
    { key:'dexterity',    label:'DEXTERITY',    desc:'Agility, hit chance, dodge' },
    { key:'constitution', label:'CONSTITUTION', desc:'Endurance, max HP' },
    { key:'intelligence', label:'INTELLIGENCE', desc:'Arcane power, max Mana' },
    { key:'wisdom',       label:'WISDOM',       desc:'Perception, Mana regen' },
    { key:'charisma',     label:'CHARISMA',     desc:'Persuasion, dialogue' },
];

// ── Point-buy ─────────────────────────────────────────────
const TOTAL_POINTS = 27;
const POINT_COSTS  = [0, 1, 2, 3, 4, 5, 7, 9]; // cost to reach 8..15
const ATTR_MIN = 8, ATTR_MAX = 15;

function pointCost(v)  { return POINT_COSTS[v - ATTR_MIN] ?? 0; }
function pointsUsed()  { return Object.values(char.attrs).reduce((s,v) => s + pointCost(v), 0); }
function pointsLeft()  { return TOTAL_POINTS - pointsUsed(); }
function modifier(v)   { return Math.floor((v - 10) / 2); }
function fmtMod(v)     { const m = modifier(v); return (m >= 0 ? '+' : '') + m; }

function raceBonuses() {
    const r = RACES.find(r => r.id === char.race);
    return r ? r.bonuses : {};
}
function bgBonuses() {
    const b = BACKGROUNDS.find(b => b.id === char.background);
    return b ? b.bonus : {};
}
function totalBonus(key) { return (raceBonuses()[key] || 0) + (bgBonuses()[key] || 0); }
function finalAttr(key)  { return char.attrs[key] + totalBonus(key); }

function previewHP()      { return 50 + modifier(finalAttr('constitution')) * 10; }
function previewMana()    { return 30 + (modifier(finalAttr('intelligence')) + modifier(finalAttr('wisdom'))) * 8; }
function previewStamina() { return 40 + (modifier(finalAttr('strength')) + modifier(finalAttr('dexterity')) + modifier(finalAttr('constitution'))) * 6; }

// ── Wizard state ──────────────────────────────────────────
const STEPS = ['race', 'class', 'gender', 'background', 'attributes', 'cantrips', 'summary'];
let wizardStep = 0;
let char = {
    race: null, characterClass: null, gender: null, background: null,
    attrs: { strength:10, dexterity:10, constitution:10, intelligence:10, wisdom:10, charisma:10 },
    cantrips: []
};

// ── Narrator ──────────────────────────────────────────────
let narratorEnabled = false;
let availableVoices = [];

const narratorBtn = document.getElementById('narrator-btn');
if (window.speechSynthesis) {
    const load = () => { availableVoices = window.speechSynthesis.getVoices(); };
    load();
    window.speechSynthesis.addEventListener('voiceschanged', load);
} else {
    narratorBtn.style.display = 'none';
}
narratorBtn.addEventListener('click', () => {
    narratorEnabled = !narratorEnabled;
    narratorBtn.classList.toggle('active', narratorEnabled);
    narratorBtn.textContent = (narratorEnabled ? '🔊' : '🔇') + ' Narrator';
    if (!narratorEnabled) window.speechSynthesis.cancel();
});
function speak(text) {
    if (!window.speechSynthesis) return;
    window.speechSynthesis.cancel();
    const utt = new SpeechSynthesisUtterance(stripTags(text));
    utt.voice = availableVoices.find(v => v.lang === 'en-GB') || availableVoices.find(v => v.lang.startsWith('en')) || null;
    utt.rate = 0.88; utt.pitch = 0.85;
    window.speechSynthesis.speak(utt);
}
function stripTags(text) {
    const m = text.match(/\[narrate\]([\s\S]*?)\[\/narrate\]/);
    return m ? m[1] : text.replace(/\[\/?(?:room|exit|item|narrate)\]/g, '');
}

// ── DOM refs ──────────────────────────────────────────────
const loginScreen  = document.getElementById('login-screen');
const charScreen   = document.getElementById('char-screen');
const gameScreen   = document.getElementById('game-screen');
const playerInfo   = document.getElementById('player-info');
const output       = document.getElementById('output');
const commandInput = document.getElementById('command-input');
const playerNameEl = document.getElementById('player-name');
const startBtn     = document.getElementById('start-btn');
const btnBack      = document.getElementById('btn-back');
const btnNext      = document.getElementById('btn-next');

let playerName = '';
let ws = null, history = [], historyIdx = -1;

// ── Login ─────────────────────────────────────────────────
startBtn.addEventListener('click', goToWizard);
playerNameEl.addEventListener('keydown', e => { if (e.key === 'Enter') goToWizard(); });

function goToWizard() {
    const name = playerNameEl.value.trim();
    if (!name) return;
    playerName = name;
    document.getElementById('wizard-player-name').textContent = 'Creating hero for: ' + name;
    loginScreen.classList.add('hidden');
    charScreen.classList.remove('hidden');
    wizardStep = 0;
    renderWizard();
}

// ── Wizard navigation ─────────────────────────────────────
btnBack.addEventListener('click', () => { if (wizardStep > 0) { wizardStep--; renderWizard(); } });
btnNext.addEventListener('click', () => {
    if (!validateStep()) return;
    if (wizardStep < STEPS.length - 1) { wizardStep++; renderWizard(); }
    else startGame();
});

function validateStep() {
    const step = STEPS[wizardStep];
    if (step === 'race'       && !char.race)           { flash('Choose a race to continue.'); return false; }
    if (step === 'class'      && !char.characterClass)  { flash('Choose a class to continue.'); return false; }
    if (step === 'gender'     && !char.gender)          { flash('Choose a gender to continue.'); return false; }
    if (step === 'background' && !char.background)      { flash('Choose a background to continue.'); return false; }
    return true;
}

function flash(msg) {
    let el = document.getElementById('wizard-flash');
    if (!el) {
        el = document.createElement('p');
        el.id = 'wizard-flash';
        el.style.cssText = 'color:#ff4444;font-size:0.78rem;text-align:center;margin-top:8px;';
        document.getElementById('wizard-nav').prepend(el);
    }
    el.textContent = msg;
    setTimeout(() => { el.textContent = ''; }, 2500);
}

function renderWizard() {
    renderStepIndicator();
    renderStepContent();
    btnBack.disabled = wizardStep === 0;
    const isLast = wizardStep === STEPS.length - 1;
    btnNext.textContent = isLast ? 'Enter the Kingdom ⚔' : 'Next →';
    btnNext.classList.toggle('confirm', isLast);
}

function renderStepIndicator() {
    const el = document.getElementById('step-indicator');
    el.innerHTML = STEPS.map((s, i) => {
        const lbl = s.charAt(0).toUpperCase() + s.slice(1);
        const cls = i < wizardStep ? 'done' : i === wizardStep ? 'current' : 'future';
        return `<span class="step-label ${cls}">${i < wizardStep ? '✓ ' : ''}${lbl}</span>${i < STEPS.length-1 ? '<span class="step-sep">›</span>' : ''}`;
    }).join('');
}

function renderStepContent() {
    const el = document.getElementById('step-content');
    el.innerHTML = '';
    switch (STEPS[wizardStep]) {
        case 'race':       el.appendChild(buildCardStep('Choose Your Race', 'Your race shapes your natural gifts.', RACES, 'race', char.race)); break;
        case 'class':      el.appendChild(buildClassStep()); break;
        case 'gender':     el.appendChild(buildGenderStep()); break;
        case 'background': el.appendChild(buildCardStep('Choose Your Background', 'Your past defines your skills.', BACKGROUNDS, 'background', char.background)); break;
        case 'attributes': el.appendChild(buildAttributesStep()); break;
        case 'cantrips':   el.appendChild(buildCantripsStep()); break;
        case 'summary':    el.appendChild(buildSummaryStep()); break;
    }
}

// ── Step builders ─────────────────────────────────────────
function buildCardStep(title, subtitle, items, field, selected) {
    const wrap = el('div');
    wrap.innerHTML = `<p class="step-title">${title}</p><p class="step-subtitle">${subtitle}</p>`;
    const grid = el('div', 'card-grid');
    items.forEach(item => {
        const card = el('div', 'card' + (selected === item.id ? ' selected' : ''));
        card.innerHTML = `<div class="card-name">${item.name}</div><div class="card-desc">${item.desc}</div><div class="card-trait">${item.trait}</div>`;
        card.addEventListener('click', () => {
            char[field] = item.id;
            grid.querySelectorAll('.card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            if (field === 'background') updateAttributesStep();
        });
        grid.appendChild(card);
    });
    wrap.appendChild(grid);
    return wrap;
}

function buildClassStep() {
    const wrap = el('div');
    wrap.innerHTML = '<p class="step-title">Choose Your Class</p><p class="step-subtitle">Your class determines how you fight and grow.</p>';
    const grid = el('div', 'card-grid');
    CLASSES.forEach(cls => {
        const card = el('div', 'card' + (char.characterClass === cls.id ? ' selected' : ''));
        card.innerHTML = `<div class="card-name">${cls.name}</div><div class="card-desc">${cls.desc}</div><div class="card-trait">${cls.trait}</div><div class="card-trait" style="color:var(--green-dim);margin-top:4px">Primary: ${cls.primary}</div>`;
        card.addEventListener('click', () => {
            char.characterClass = cls.id;
            char.cantrips = [];
            grid.querySelectorAll('.card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
        });
        grid.appendChild(card);
    });
    wrap.appendChild(grid);
    return wrap;
}

function buildGenderStep() {
    const wrap = el('div');
    wrap.innerHTML = '<p class="step-title">Choose Your Gender</p><p class="step-subtitle">This is purely for your character\'s identity.</p>';
    const grid = el('div', 'gender-grid');
    GENDERS.forEach(g => {
        const btn = el('button', 'gender-btn' + (char.gender === g.id ? ' selected' : ''));
        btn.textContent = g.name;
        btn.addEventListener('click', () => {
            char.gender = g.id;
            grid.querySelectorAll('.gender-btn').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
        });
        grid.appendChild(btn);
    });
    wrap.appendChild(grid);
    return wrap;
}

function buildAttributesStep() {
    const wrap = el('div');
    wrap.innerHTML = '<p class="step-title">Distribute Attributes</p><p class="step-subtitle">You have 27 points. Costs increase above 13.</p>';

    const pointsBar = el('div', 'points-bar');
    pointsBar.id = 'points-bar';
    pointsBar.innerHTML = 'Points remaining: <span id="pts-left">' + pointsLeft() + '</span> / ' + TOTAL_POINTS;
    wrap.appendChild(pointsBar);

    const table = el('table', 'attr-table');
    ATTR_DEFS.forEach(({ key, label, desc }) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="attr-label">${label}</td>
            <td><button class="attr-btn" id="minus-${key}" data-key="${key}" data-dir="-1">−</button></td>
            <td class="attr-val" id="val-${key}">${char.attrs[key]}</td>
            <td><button class="attr-btn" id="plus-${key}"  data-key="${key}" data-dir="1">+</button></td>
            <td class="attr-bonus" id="bonus-${key}"></td>
            <td class="attr-final" id="final-${key}"></td>
            <td class="attr-mod-cell" id="mod-${key}"></td>
            <td class="attr-desc-cell">${desc}</td>`;
        table.appendChild(tr);
    });
    wrap.appendChild(table);

    table.addEventListener('click', e => {
        const btn = e.target.closest('.attr-btn');
        if (!btn) return;
        const key = btn.dataset.key;
        const dir = parseInt(btn.dataset.dir);
        const next = char.attrs[key] + dir;
        if (next < ATTR_MIN || next > ATTR_MAX) return;
        const costDiff = pointCost(next) - pointCost(char.attrs[key]);
        if (dir > 0 && costDiff > pointsLeft()) return;
        char.attrs[key] = next;
        refreshAttributesStep();
    });

    const derived = el('div', 'derived-bar');
    derived.id = 'derived-bar';
    wrap.appendChild(derived);

    refreshAttributesStep();
    return wrap;
}

function refreshAttributesStep() {
    const ptsEl = document.getElementById('pts-left');
    if (!ptsEl) return;
    const left = pointsLeft();
    ptsEl.textContent = left;
    ptsEl.className = left === 0 ? 'warn' : '';

    ATTR_DEFS.forEach(({ key }) => {
        const base  = char.attrs[key];
        const bonus = totalBonus(key);
        const final = finalAttr(key);
        const mod   = modifier(final);

        document.getElementById('val-' + key).textContent   = base;
        document.getElementById('bonus-' + key).textContent = bonus ? '+' + bonus + ' bonus' : '';
        document.getElementById('final-' + key).textContent = '= ' + final;

        const modEl = document.getElementById('mod-' + key);
        modEl.textContent = '(' + fmtMod(final) + ')';
        modEl.className = 'attr-mod-cell ' + (mod > 0 ? 'pos' : mod < 0 ? 'neg' : '');

        const minus = document.getElementById('minus-' + key);
        const plus  = document.getElementById('plus-'  + key);
        if (minus) minus.disabled = base <= ATTR_MIN;
        if (plus)  plus.disabled  = base >= ATTR_MAX || (pointCost(base + 1) - pointCost(base)) > left;
    });

    const db = document.getElementById('derived-bar');
    if (db) db.innerHTML = `HP <b>${previewHP()}</b> &nbsp;·&nbsp; Mana <b>${previewMana()}</b> &nbsp;·&nbsp; Stamina <b>${previewStamina()}</b>`;
}

function updateAttributesStep() {
    refreshAttributesStep();
}

function buildCantripsStep() {
    const slots = CANTRIP_SLOTS[char.characterClass] || 0;
    const available = CANTRIPS.filter(c => c.classes.includes(char.characterClass));

    const wrap = el('div');
    const clsName = char.characterClass.charAt(0).toUpperCase() + char.characterClass.slice(1);
    wrap.innerHTML = `<p class="step-title">Choose Your Cantrips</p><p class="step-subtitle">Select up to ${slots} cantrip${slots !== 1 ? 's' : ''} available to the ${clsName}.</p>`;

    const slotsBar = el('div', 'cantrip-slots-bar');
    slotsBar.id = 'cantrip-slots-bar';
    wrap.appendChild(slotsBar);

    const grid = el('div', 'cantrip-grid');
    const effectColor = { damage:'var(--red)', debuff:'#ff8c00', buff:'var(--green)', utility:'#00cfff', healing:'var(--yellow)' };

    available.forEach(cantrip => {
        const card = el('div', 'cantrip-card' + (char.cantrips.includes(cantrip.id) ? ' selected' : ''));
        const color = effectColor[cantrip.effect] || 'var(--gray)';
        card.innerHTML = `
            <div class="cantrip-name">${cantrip.name}</div>
            <div class="cantrip-school">${cantrip.school}</div>
            <div class="cantrip-desc">${cantrip.desc}</div>
            <div class="cantrip-effect" style="color:${color}">${cantrip.effect}${cantrip.dmg ? ' · ' + cantrip.dmg : ''}</div>`;
        card.addEventListener('click', () => {
            const idx = char.cantrips.indexOf(cantrip.id);
            if (idx >= 0) {
                char.cantrips.splice(idx, 1);
                card.classList.remove('selected');
            } else if (char.cantrips.length < slots) {
                char.cantrips.push(cantrip.id);
                card.classList.add('selected');
            }
            const bar = document.getElementById('cantrip-slots-bar');
            if (bar) bar.innerHTML = slotsBadge(char.cantrips.length, slots);
        });
        grid.appendChild(card);
    });

    slotsBar.innerHTML = slotsBadge(char.cantrips.length, slots);
    wrap.appendChild(grid);
    return wrap;
}

function slotsBadge(used, total) {
    const color = used >= total ? 'var(--yellow)' : 'var(--green)';
    return `Cantrip slots: <span style="color:${color}">${used} / ${total}</span>`;
}

function buildSummaryStep() {
    const race = RACES.find(r => r.id === char.race);
    const cls  = CLASSES.find(c => c.id === char.characterClass);
    const bg   = BACKGROUNDS.find(b => b.id === char.background);
    const gnd  = GENDERS.find(g => g.id === char.gender);

    const wrap = el('div', 'summary-box');
    wrap.innerHTML = `
        <p class="step-title">Character Summary</p>

        <div>
            <p class="summary-identity">${playerName} &nbsp;·&nbsp; ${race?.name ?? '—'} ${cls?.name ?? '—'}</p>
            <p class="summary-sub">Background: ${bg?.name ?? '—'} &nbsp;·&nbsp; Gender: ${gnd?.name ?? '—'}</p>
        </div>

        <div class="summary-attrs">
            ${ATTR_DEFS.map(({ key, label }) => {
                const final = finalAttr(key);
                const bonus = totalBonus(key);
                const bonusStr = bonus ? ` <span style="color:var(--yellow)">+${bonus}</span>` : '';
                return `<div class="summary-attr"><span>${label}</span> <b>${final}</b> (${fmtMod(final)})${bonusStr}</div>`;
            }).join('')}
        </div>

        <div class="summary-derived">
            <span>HP <b>${previewHP()}</b></span>
            <span>Mana <b>${previewMana()}</b></span>
            <span>Stamina <b>${previewStamina()}</b></span>
        </div>

        <p style="font-size:0.75rem;color:var(--green-dim)">
            ${race?.trait ?? ''}<br>
            ${cls?.trait ?? ''}<br>
            ${bg?.trait ?? ''}
        </p>`;

    const selectedCantrips = CANTRIPS.filter(c => char.cantrips.includes(c.id));
    if (selectedCantrips.length > 0) {
        const cantripEl = el('div', 'summary-cantrips');
        cantripEl.innerHTML = '<span>CANTRIPS:</span> ' +
            selectedCantrips.map(c => `<span class="summary-cantrip-name">${c.name}</span>`).join(' · ');
        wrap.appendChild(cantripEl);
    }
    return wrap;
}

// ── Step 3: Start game ────────────────────────────────────
function startGame() {
    charScreen.classList.add('hidden');
    gameScreen.classList.remove('hidden');
    playerInfo.textContent = playerName;
    narratorBtn.classList.remove('hidden');
    connect();
}

function connect() {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    ws = new WebSocket(protocol + '//' + location.host + '/game-ws');

    ws.onopen = () => {
        ws.send(JSON.stringify({
            type: 'join', name: playerName,
            race: char.race, characterClass: char.characterClass,
            gender: char.gender, background: char.background,
            attributes: char.attrs, cantrips: char.cantrips
        }));
        commandInput.focus();
    };
    ws.onmessage = event => { const m = JSON.parse(event.data); appendLine(m.text, m.type); };
    ws.onclose   = ()    => appendLine('Connection closed. Refresh to reconnect.', 'error');
    ws.onerror   = ()    => appendLine('WebSocket error — is the server running?', 'error');
    commandInput.addEventListener('keydown', onKey);
}

function onKey(e) {
    if (e.key === 'Enter') {
        const text = commandInput.value.trim();
        commandInput.value = ''; historyIdx = -1;
        if (!text) return;
        history.unshift(text);
        appendLine('> ' + text, 'command');
        if (!ws || ws.readyState !== WebSocket.OPEN) { appendLine('Not connected.', 'error'); return; }
        ws.send(JSON.stringify({ type: 'command', text }));
    } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (historyIdx < history.length - 1) commandInput.value = history[++historyIdx];
    } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        historyIdx > 0 ? commandInput.value = history[--historyIdx] : (historyIdx = -1, commandInput.value = '');
    }
}

function appendLine(text, type) {
    const div = el('div', 'line ' + (type || 'output'));
    const content = el('span', 'line-content');
    content.innerHTML = parseMarkup(text);
    div.appendChild(content);
    if ((type === 'output' || type === 'system') && text.trim()) {
        const btn = el('button', 'speak-btn');
        btn.title = 'Read aloud'; btn.textContent = '🔊';
        btn.addEventListener('click', () => speak(text));
        div.appendChild(btn);
        if (narratorEnabled) speak(text);
    }
    output.appendChild(div);
    output.scrollTop = output.scrollHeight;
}

function parseMarkup(text) {
    const esc = text.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    return esc
        .replace(/\[narrate\](.*?)\[\/narrate\]/g, '$1')
        .replace(/\[room\](.*?)\[\/room\]/g,       '<span class="tag-room">$1</span>')
        .replace(/\[exit\](.*?)\[\/exit\]/g,        '<span class="tag-exit">$1</span>')
        .replace(/\[item\](.*?)\[\/item\]/g,        '<span class="tag-item">$1</span>');
}

// ── Utility ───────────────────────────────────────────────
function el(tag, className) {
    const e = document.createElement(tag);
    if (className) e.className = className;
    return e;
}
