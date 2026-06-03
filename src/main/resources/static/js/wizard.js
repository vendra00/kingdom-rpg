'use strict';

// ── Wizard state ──────────────────────────────────────────
const STEPS = ['race', 'class', 'gender', 'background', 'attributes', 'cantrips', 'summary'];
let wizardStep = 0;
let char = {
    race: null, characterClass: null, gender: null, background: null,
    attrs: { strength:10, dexterity:10, constitution:10, intelligence:10, wisdom:10, charisma:10 },
    cantrips: []
};
let playerName = '';

// ── DOM refs ──────────────────────────────────────────────
const loginScreen  = document.getElementById('login-screen');
const charScreen   = document.getElementById('char-screen');
const playerNameEl = document.getElementById('player-name');
const startBtn     = document.getElementById('start-btn');
const btnBack      = document.getElementById('btn-back');
const btnNext      = document.getElementById('btn-next');

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
    if (step === 'race'       && !char.race)            { flash('Choose a race to continue.');       return false; }
    if (step === 'class'      && !char.characterClass)  { flash('Choose a class to continue.');      return false; }
    if (step === 'gender'     && !char.gender)           { flash('Choose a gender to continue.');     return false; }
    if (step === 'background' && !char.background)       { flash('Choose a background to continue.'); return false; }
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
    const indicator = document.getElementById('step-indicator');
    indicator.innerHTML = STEPS.map((s, i) => {
        const lbl = s.charAt(0).toUpperCase() + s.slice(1);
        const cls = i < wizardStep ? 'done' : i === wizardStep ? 'current' : 'future';
        return `<span class="step-label ${cls}">${i < wizardStep ? '✓ ' : ''}${lbl}</span>`
             + (i < STEPS.length - 1 ? '<span class="step-sep">›</span>' : '');
    }).join('');
}

function renderStepContent() {
    const content = document.getElementById('step-content');
    content.innerHTML = '';
    switch (STEPS[wizardStep]) {
        case 'race':       content.appendChild(buildCardStep('Choose Your Race',       'Your race shapes your natural gifts.',          RACES,       'race',       char.race));       break;
        case 'class':      content.appendChild(buildClassStep());                                                                                                                      break;
        case 'gender':     content.appendChild(buildGenderStep());                                                                                                                     break;
        case 'background': content.appendChild(buildCardStep('Choose Your Background', 'Your past defines your skills.',                BACKGROUNDS, 'background', char.background)); break;
        case 'attributes': content.appendChild(buildAttributesStep());                                                                                                                 break;
        case 'cantrips':   content.appendChild(buildCantripsStep());                                                                                                                   break;
        case 'summary':    content.appendChild(buildSummaryStep());                                                                                                                    break;
    }
}

// ── Point-buy helpers ─────────────────────────────────────
function pointCost(v)  { return POINT_COSTS[v - ATTR_MIN] ?? 0; }
function pointsUsed()  { return Object.values(char.attrs).reduce((s, v) => s + pointCost(v), 0); }
function pointsLeft()  { return TOTAL_POINTS - pointsUsed(); }
function modifier(v)   { return Math.floor((v - 10) / 2); }
function fmtMod(v)     { const m = modifier(v); return (m >= 0 ? '+' : '') + m; }

function raceBonuses()       { const r = RACES.find(r => r.id === char.race);          return r ? r.bonuses : {}; }
function bgBonuses()         { const b = BACKGROUNDS.find(b => b.id === char.background); return b ? b.bonus  : {}; }
function totalBonus(key)     { return (raceBonuses()[key] || 0) + (bgBonuses()[key] || 0); }
function finalAttr(key)      { return char.attrs[key] + totalBonus(key); }

function previewHP()      { return 50 + modifier(finalAttr('constitution')) * 10; }
function previewMana()    { return 30 + (modifier(finalAttr('intelligence')) + modifier(finalAttr('wisdom'))) * 8; }
function previewStamina() { return 40 + (modifier(finalAttr('strength')) + modifier(finalAttr('dexterity')) + modifier(finalAttr('constitution'))) * 6; }

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
            <td class="attr-val"   id="val-${key}">${char.attrs[key]}</td>
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

        document.getElementById('val-'   + key).textContent = base;
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

function updateAttributesStep() { refreshAttributesStep(); }

function buildCantripsStep() {
    const slots     = CANTRIP_SLOTS[char.characterClass] || 0;
    const available = CANTRIPS.filter(c => c.classes.includes(char.characterClass));
    const clsName   = char.characterClass.charAt(0).toUpperCase() + char.characterClass.slice(1);

    const wrap = el('div');
    wrap.innerHTML = `<p class="step-title">Choose Your Cantrips</p><p class="step-subtitle">Select up to ${slots} cantrip${slots !== 1 ? 's' : ''} available to the ${clsName}.</p>`;

    const slotsBar = el('div', 'cantrip-slots-bar');
    slotsBar.id = 'cantrip-slots-bar';
    wrap.appendChild(slotsBar);

    const grid = el('div', 'cantrip-grid');
    const effectColor = { damage:'var(--red)', debuff:'#ff8c00', buff:'var(--green)', utility:'#00cfff', healing:'var(--yellow)' };

    available.forEach(cantrip => {
        const card  = el('div', 'cantrip-card' + (char.cantrips.includes(cantrip.id) ? ' selected' : ''));
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
                const final     = finalAttr(key);
                const bonus     = totalBonus(key);
                const bonusStr  = bonus ? ` <span style="color:var(--yellow)">+${bonus}</span>` : '';
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
            ${cls?.trait  ?? ''}<br>
            ${bg?.trait   ?? ''}
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
