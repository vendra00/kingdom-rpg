'use strict';

import Sounds from './sounds.js';

import { STEPS, RACES, CLASSES, GENDERS, BACKGROUNDS,
         ATTR_DEFS, TOTAL_POINTS, POINT_COSTS, ATTR_MIN, ATTR_MAX,
         CANTRIPS, CANTRIP_SLOTS,
         COMMAND_COMPLETIONS, DIRECTION_COMPLETIONS, DICE_COMPLETIONS, ABILITY_COMPLETIONS,
         TAKE_COMPLETIONS, DROP_COMPLETIONS, UNEQUIP_COMPLETIONS, TALK_INTENT_COMPLETIONS
       } from './data.js';

const TALK_INTENTS = new Set(TALK_INTENT_COMPLETIONS.map(i => i.value));

const { createApp } = Vue;

const VERB_TYPES = {
    look:'simple', l:'simple',
    inventory:'simple', inv:'simple', i:'simple',
    status:'simple', stats:'simple',
    spells:'simple', cantrips:'simple', grimoire:'simple',
    abilities:'simple', ability:'simple', hab:'simple',
    help:'simple', '?':'simple',
    north:'bare', south:'bare', east:'bare', west:'bare',
    n:'bare', s:'bare', e:'bare', w:'bare',
    go:'direction', move:'direction',
    take:'target', get:'target', pick:'target',
    drop:'target',
    search:'target', examine:'target', inspect:'target',
    equip:'target', wear:'target',
    talk:'talk', speak:'talk', greet:'talk',
    steal:'steal', pickpocket:'steal', rob:'steal',
    roll:'dice',
    cast:'ability', use:'ability',
    attempt:'ability', try:'ability',
    unequip:'slot', remove:'slot',
};

createApp({

    setup() {
        // Expose D&D constants to the template without making them reactive
        return { STEPS, RACES, CLASSES, GENDERS, BACKGROUNDS, ATTR_DEFS, TOTAL_POINTS, ATTR_MIN, ATTR_MAX };
    },

    data() {
        return {
            // ── Screen ──────────────────────────────────────
            screen: 'title',   // 'title' | 'login' | 'wizard' | 'load' | 'game'

            // ── Saves ───────────────────────────────────────
            saves:       [],
            savesLoaded: false,

            // ── Player ──────────────────────────────────────
            playerName: '',
            flashMsg:   '',

            // ── Wizard ──────────────────────────────────────
            wizardStep: 0,
            char: {
                race:           null,
                characterClass: null,
                gender:         null,
                background:     null,
                attrs:    { strength:10, dexterity:10, constitution:10, intelligence:10, wisdom:10, charisma:10 },
                cantrips: [],
            },

            // ── Game ────────────────────────────────────────
            messages:    [],
            commandText: '',
            ws:          null,
            cmdHistory:  [],
            historyIdx:  -1,

            // ── HUD ─────────────────────────────────────────
            gameClass:   '',
            gameRace:    '',
            currentRoom: '',

            // ── Nerd stats ───────────────────────────────────
            nerdMode: false,
            nerdLog:  [],

            // ── Player stats sidebar ─────────────────────────
            playerStats: null,

            // ── Autocomplete ─────────────────────────────────
            ac:             { items: [], idx: -1 },
            roomNpcs:       [],   // visible NPC names in current room, updated from [npc] tags
            roomItems:      [],   // visible item names in current room, updated from [item] tags
            inventoryItems: [],   // items in player's inventory, updated from [invitem] tags

            // ── Music ───────────────────────────────────────
            musicMuted: false,

            // ── Narrator ────────────────────────────────────
            narratorEnabled: false,
            availableVoices: [],
        };
    },

    computed: {
        // ── Wizard ──────────────────────────────────────────
        currentStep() { return STEPS[this.wizardStep]; },
        isLastStep()  { return this.wizardStep === STEPS.length - 1; },
        nextBtnText() { return this.isLastStep ? 'Enter the Kingdom ⚔' : 'Next →'; },

        raceBonuses() {
            const r = RACES.find(r => r.id === this.char.race);
            return r ? r.bonuses : {};
        },
        bgBonuses() {
            const b = BACKGROUNDS.find(b => b.id === this.char.background);
            return b ? b.bonus : {};
        },

        // ── Point-buy ───────────────────────────────────────
        pointsUsed() {
            return Object.values(this.char.attrs).reduce((s, v) => s + this.pointCost(v), 0);
        },
        pointsLeft() { return TOTAL_POINTS - this.pointsUsed; },

        previewHP()      { return 50 + this.modifier(this.finalAttr('constitution')) * 10; },
        previewMana()    { return 30 + (this.modifier(this.finalAttr('intelligence')) + this.modifier(this.finalAttr('wisdom'))) * 8; },
        previewStamina() { return 40 + (this.modifier(this.finalAttr('strength'))    + this.modifier(this.finalAttr('dexterity')) + this.modifier(this.finalAttr('constitution'))) * 6; },

        // ── Cantrips ────────────────────────────────────────
        cantripSlots()      { return CANTRIP_SLOTS[this.char.characterClass] || 0; },
        availableCantrips() { return this.char.characterClass ? CANTRIPS.filter(c => c.classes.includes(this.char.characterClass)) : []; },
        selectedCantrips()  { return CANTRIPS.filter(c => this.char.cantrips.includes(c.id)); },

        hasSaves() { return this.saves.length > 0; },

        // ── Summary lookups ─────────────────────────────────
        summaryRace()   { return RACES.find(r => r.id === this.char.race); },
        summaryClass()  { return CLASSES.find(c => c.id === this.char.characterClass); },
        summaryBg()     { return BACKGROUNDS.find(b => b.id === this.char.background); },
        summaryGender() { return GENDERS.find(g => g.id === this.char.gender); },
    },

    watch: {
        commandText() { this.highlightInput(); },
    },

    methods: {
        // ── Title menu ───────────────────────────────────────
        goToNewGame() { Sounds.click(); this.startMusic(); this.screen = 'login'; },

        async goToLoad() {
            Sounds.click();
            this.startMusic();   // before await — stays inside the user-gesture context
            await this.fetchSaves();
            this.screen = 'load';
        },

        async goToContinue() {
            if (!this.saves.length) return;
            Sounds.confirm();
            this.fadeOutMusic();
            const s = this.saves[0];
            this.playerName = s.name;
            this.gameClass  = s.characterClass || '';
            this.gameRace   = s.race || '';
            this.screen = 'game';
            await this.$nextTick();
            this.connectDirect(s.name);
        },

        goToExit() { Sounds.back(); window.close(); },

        async fetchSaves() {
            try {
                const r  = await fetch('/api/players');
                this.saves = await r.json();
            } catch (e) {
                console.warn('Failed to load saves:', e);
                this.saves = [];
            }
            this.savesLoaded = true;
        },

        loadSave(s) {
            Sounds.confirm();
            this.fadeOutMusic();
            this.playerName = s.name;
            this.gameClass  = s.characterClass || '';
            this.gameRace   = s.race || '';
            this.screen = 'game';
            this.$nextTick(() => this.connectDirect(s.name));
        },

        connectDirect(name) {
            const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
            this.ws = new WebSocket(protocol + '//' + location.host + '/game-ws');
            this.setupWsHandlers(() => {
                this.ws.send(JSON.stringify({ type: 'join', name }));
                this.$nextTick(() => this.$refs.commandInput?.focus());
            });
        },

        setupWsHandlers(onOpen) {
            this.ws.onopen    = onOpen;
            this.ws.onmessage = e => { const m = JSON.parse(e.data); this.addMessage(m.text, m.type); };
            this.ws.onclose   = () => this.addMessage('Connection closed. Refresh to reconnect.', 'error');
            this.ws.onerror   = () => this.addMessage('WebSocket error — is the server running?', 'error');
        },

        fmtDate(iso) {
            if (!iso) return '';
            const d = new Date(iso);
            return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
        },

        // ── Wizard navigation ────────────────────────────────
        goToWizard() {
            if (!this.playerName.trim()) return;
            this.playerName = this.playerName.trim();
            this.screen     = 'wizard';
            this.wizardStep = 0;
        },

        nextStep() {
            if (!this.validateStep()) return;
            if (this.isLastStep) { Sounds.confirm(); this.startGame(); }
            else                 { Sounds.click();   this.wizardStep++; }
        },
        prevStep() { if (this.wizardStep > 0) { Sounds.back(); this.wizardStep--; } },

        validateStep() {
            const s = this.currentStep;
            if (s === 'race'       && !this.char.race)            { this.flash('Choose a race to continue.');                           return false; }
            if (s === 'class'      && !this.char.characterClass)  { this.flash('Choose a class to continue.');                          return false; }
            if (s === 'gender'     && !this.char.gender)          { this.flash('Choose a gender to continue.');                         return false; }
            if (s === 'background' && !this.char.background)      { this.flash('Choose a background to continue.');                     return false; }
            if (s === 'attributes' && this.pointsLeft > 0)        { this.flash(`Spend all ${this.pointsLeft} remaining point${this.pointsLeft > 1 ? 's' : ''} before continuing.`); return false; }
            return true;
        },

        flash(msg) {
            this.flashMsg = msg;
            setTimeout(() => { this.flashMsg = ''; }, 2500);
        },

        selectClass(cls) {
            Sounds.click();
            this.char.characterClass = cls.id;
            this.char.cantrips = [];
        },

        // ── Point-buy helpers ────────────────────────────────
        pointCost(v)    { return POINT_COSTS[v - ATTR_MIN] ?? 0; },
        modifier(v)     { return Math.floor((v - 10) / 2); },
        fmtMod(v)       { const m = this.modifier(v); return (m >= 0 ? '+' : '') + m; },

        totalBonus(key) { return (this.raceBonuses[key] || 0) + (this.bgBonuses[key] || 0); },
        finalAttr(key)  { return this.char.attrs[key] + this.totalBonus(key); },

        canAfford(key) {
            const cur = this.char.attrs[key];
            if (cur >= ATTR_MAX) return false;
            return (this.pointCost(cur + 1) - this.pointCost(cur)) <= this.pointsLeft;
        },
        modClass(key) {
            const m = this.modifier(this.finalAttr(key));
            if (m > 0) return 'pos';
            if (m < 0) return 'neg';
            return '';
        },

        incrementAttr(key) { if (this.canAfford(key))             { this.char.attrs[key]++; Sounds.attrUp();   } },
        decrementAttr(key) { if (this.char.attrs[key] > ATTR_MIN) { this.char.attrs[key]--; Sounds.attrDown(); } },

        // ── Cantrips ─────────────────────────────────────────
        toggleCantrip(cantrip) {
            const idx = this.char.cantrips.indexOf(cantrip.id);
            if (idx >= 0) {
                Sounds.back();
                this.char.cantrips.splice(idx, 1);
            } else if (this.char.cantrips.length < this.cantripSlots) {
                Sounds.click();
                this.char.cantrips.push(cantrip.id);
            }
        },
        cantripColor(effect) {
            return { damage:'var(--red)', debuff:'#ff8c00', buff:'var(--green)', utility:'#00cfff', healing:'var(--yellow)' }[effect] || 'var(--gray)';
        },

        // ── Music ────────────────────────────────────────────
        startMusic() {
            if (this.musicMuted) return;
            const el = document.getElementById('bg-music');
            if (!el) return;
            el.volume = 1;
            el.play().catch(() => {}); // silently ignored if file is missing
        },

        fadeOutMusic(ms = 1500) {
            const el = document.getElementById('bg-music');
            if (!el || el.paused) return;
            const steps     = 30;
            const interval  = ms / steps;
            const decrement = el.volume / steps;
            const timer = setInterval(() => {
                el.volume = Math.max(0, el.volume - decrement);
                if (el.volume <= 0) {
                    clearInterval(timer);
                    el.pause();
                    el.currentTime = 0;
                    el.volume = 1;   // restore for next playback
                }
            }, interval);
        },

        toggleMusic() {
            this.musicMuted = !this.musicMuted;
            const el = document.getElementById('bg-music');
            if (!el) return;
            if (this.musicMuted) {
                el.pause();
            } else {
                el.volume = 1;
                el.play().catch(() => {});
            }
        },

        // ── Autocomplete ─────────────────────────────────────
        updateAc() {
            const text = this.commandText;
            if (!text.trim()) { this.ac.items = []; this.ac.idx = -1; return; }

            const endsWithSpace = text.endsWith(' ');
            const words   = text.trimEnd().split(/\s+/);
            const verb    = words[0].toLowerCase();
            const partial = endsWithSpace ? '' : words[words.length - 1].toLowerCase();
            const isFirst = words.length === 1 && !endsWithSpace;

            let pool;
            if (isFirst) {
                pool = COMMAND_COMPLETIONS;
            } else if (['go', 'move'].includes(verb)) {
                pool = DIRECTION_COMPLETIONS;
            } else if (['take', 'get', 'pick'].includes(verb)) {
                pool = [
                    ...TAKE_COMPLETIONS,
                    ...this.roomItems.map(n => ({ value: n.toLowerCase(), hint: 'Pick up this item' })),
                ];
            } else if (['drop'].includes(verb)) {
                pool = [
                    ...DROP_COMPLETIONS,
                    ...this.inventoryItems.map(n => ({ value: n.toLowerCase(), hint: 'Drop this item' })),
                ];
            } else if (['equip', 'wear'].includes(verb)) {
                pool = this.inventoryItems.map(n => ({ value: n.toLowerCase(), hint: 'Equip this item' }));
            } else if (['unequip', 'remove'].includes(verb)) {
                pool = UNEQUIP_COMPLETIONS;
            } else if (['cast', 'use'].includes(verb)) {
                pool = this.cantripCompletions();
            } else if (['attempt', 'try'].includes(verb)) {
                pool = ABILITY_COMPLETIONS;
            } else if (verb === 'roll') {
                pool = DICE_COMPLETIONS;
            } else if (['steal', 'pickpocket', 'rob'].includes(verb)) {
                // Direct NPC completions — no intent stage
                const afterVerb = words.slice(1).join(' ').toLowerCase();
                this.ac.items = this.roomNpcs
                    .filter(n => n.toLowerCase().startsWith(afterVerb))
                    .map(n => ({ value: n.toLowerCase(), hint: 'Steal from this character', npcCompletion: true }))
                    .slice(0, 8);
                this.ac.idx = -1;
                return;
            } else if (['talk', 'speak', 'greet'].includes(verb)) {
                const afterVerb = words.slice(1); // words after verb (trimEnd already applied)

                // "First arg complete" = trailing space OR there are 2+ words after verb
                const firstArgRaw  = afterVerb[0]?.toLowerCase() || '';
                const firstArgDone = endsWithSpace ? firstArgRaw : (afterVerb.length > 1 ? firstArgRaw : null);
                const intentWord   = firstArgDone !== null && TALK_INTENTS.has(firstArgDone) ? firstArgDone : null;

                if (intentWord) {
                    // Stage 2 — NPC completions
                    const npcWords   = afterVerb.slice(1);
                    const npcPartial = npcWords.join(' ').toLowerCase();
                    // Exact NPC match with trailing space → ready for message, close AC
                    if (endsWithSpace && this.roomNpcs.some(n => n.toLowerCase() === npcPartial)) {
                        this.ac.items = []; this.ac.idx = -1; return;
                    }
                    this.ac.items = this.roomNpcs
                        .filter(n => n.toLowerCase().startsWith(npcPartial))
                        .map(n => ({ value: intentWord + ' ' + n.toLowerCase(), hint: 'Speak with this character', npcCompletion: true }))
                        .slice(0, 8);
                    this.ac.idx = -1;
                    return;
                }

                // Stage 1 — intent completions (filter by partial or fall back to NPC if no intent matches)
                const intentMatches = TALK_INTENT_COMPLETIONS.filter(i => i.value.startsWith(firstArgRaw));
                if (intentMatches.length > 0 || firstArgRaw === '') {
                    this.ac.items = intentMatches.map(i => ({ ...i, intentCompletion: true })).slice(0, 8);
                    this.ac.idx = -1;
                    return;
                }
                // Partial doesn't match any intent — fall back to NPC completions (backward compat)
                const npcFallback = afterVerb.join(' ').toLowerCase();
                this.ac.items = this.roomNpcs
                    .filter(n => n.toLowerCase().startsWith(npcFallback))
                    .map(n => ({ value: n.toLowerCase(), hint: 'Speak with this character', npcCompletion: true }))
                    .slice(0, 8);
                this.ac.idx = -1;
                return;
            } else {
                pool = [];
            }

            this.ac.items = pool.filter(item =>
                item.value.toLowerCase().startsWith(partial)
            ).slice(0, 8);
            this.ac.idx = -1;
        },

        cantripCompletions() {
            const known = this.char.cantrips;
            const src   = known.length ? CANTRIPS.filter(c => known.includes(c.id)) : CANTRIPS;
            return src.map(c => ({ value: c.name.toLowerCase(), hint: c.school + ' · ' + c.effect }));
        },

        acceptAc(item) {
            const text          = this.commandText;
            const endsWithSpace = text.endsWith(' ');
            const words         = text.trimEnd().split(/\s+/);
            const isFirst       = words.length === 1 && !endsWithSpace;

            // Intent completion selected: append intent, then trigger NPC stage
            if (item.intentCompletion) {
                const verb = words[0].toLowerCase();
                this.commandText = verb + ' ' + item.value + ' ';
                this.ac.idx = -1;
                this.updateAc();
                this.$nextTick(() => this.$refs.commandInput?.focus());
                return;
            }

            // NPC completion replaces everything after the verb (value may include "intent npcname")
            if (item.npcCompletion) {
                const verb = words[0].toLowerCase();
                this.commandText = verb + ' ' + item.value + ' ';
                this.ac.items = [];
                this.ac.idx   = -1;
                this.$nextTick(() => this.$refs.commandInput?.focus());
                return;
            }

            if (endsWithSpace) {
                words.push(item.value);
            } else {
                words[words.length - 1] = item.value;
            }

            const NEEDS_ARGS = new Set(['go','move','take','get','pick','drop','cast','use','roll','attempt','try','equip','wear','unequip','remove']);
            const newText    = words.join(' ');

            if (isFirst && NEEDS_ARGS.has(words[0].toLowerCase())) {
                this.commandText = newText + ' ';
                this.updateAc();
            } else {
                this.commandText = newText;
                this.ac.items = [];
                this.ac.idx   = -1;
            }
            this.$nextTick(() => this.$refs.commandInput?.focus());
        },

        closeAc() { this.ac.items = []; this.ac.idx = -1; },

        handleKey(e) {
            if (this.ac.items.length) {
                if (this.handleAcKey(e)) return;
            } else if (this.handleHistoryKey(e)) return;
            if (e.key === 'Enter') this.sendCommand();
        },

        handleAcKey(e) {
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                this.ac.idx = (this.ac.idx + 1) % this.ac.items.length;
                this.$nextTick(() => this.scrollAcActive());
                return true;
            }
            if (e.key === 'ArrowUp') {
                e.preventDefault();
                this.ac.idx = this.ac.idx <= 0 ? this.ac.items.length - 1 : this.ac.idx - 1;
                this.$nextTick(() => this.scrollAcActive());
                return true;
            }
            if (e.key === 'Tab') {
                e.preventDefault();
                this.acceptAc(this.ac.items[Math.max(0, this.ac.idx)]);
                return true;
            }
            if (e.key === 'Escape') { e.preventDefault(); this.closeAc(); return true; }
            if (e.key === 'Enter' && this.ac.idx >= 0) {
                e.preventDefault();
                this.acceptAc(this.ac.items[this.ac.idx]);
                return true;
            }
            return false;
        },

        handleHistoryKey(e) {
            if (e.key === 'ArrowUp')   { e.preventDefault(); this.historyUp();   return true; }
            if (e.key === 'ArrowDown') { e.preventDefault(); this.historyDown(); return true; }
            return false;
        },

        scrollAcActive() {
            document.querySelector('#ac-list .ac-active')?.scrollIntoView({ block: 'nearest' });
        },

        // ── Game ─────────────────────────────────────────────
        startGame() {
            this.fadeOutMusic();
            this.gameClass = this.char.characterClass || '';
            this.gameRace  = this.char.race || '';
            this.screen = 'game';
            this.$nextTick(() => this.connect());
        },

        connect() {
            const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
            this.ws = new WebSocket(protocol + '//' + location.host + '/game-ws');

            this.setupWsHandlers(() => {
                this.ws.send(JSON.stringify({
                    type: 'join', name: this.playerName,
                    race: this.char.race, characterClass: this.char.characterClass,
                    gender: this.char.gender, background: this.char.background,
                    attributes: this.char.attrs, cantrips: this.char.cantrips,
                }));
                this.$nextTick(() => this.$refs.commandInput?.focus());
            });
        },

        sendCommand() {
            const text = this.commandText.trim();
            this.commandText = '';
            this.historyIdx  = -1;
            this.closeAc();
            if (!text) return;
            this.cmdHistory.unshift(text);
            this.addMessage('> ' + text, 'command');
            if (this.ws?.readyState !== WebSocket.OPEN) { this.addMessage('Not connected.', 'error'); return; }

            // Optimistically remove taken item from room list so AC stays tidy
            const parts = text.toLowerCase().split(/\s+/);
            if (['take', 'get', 'pick'].includes(parts[0]) && parts[1] && parts[1] !== 'all') {
                const target = parts.slice(1).join(' ');
                this.roomItems = this.roomItems.filter(i => i.toLowerCase() !== target);
            }
            // Optimistically remove dropped/equipped item from inventory list
            if (['drop', 'equip', 'wear'].includes(parts[0]) && parts[1] && parts[1] !== 'all') {
                const target = parts.slice(1).join(' ');
                this.inventoryItems = this.inventoryItems.filter(i => i.toLowerCase() !== target);
            }

            this.ws.send(JSON.stringify({ type: 'command', text }));
        },

        historyUp() {
            if (this.historyIdx < this.cmdHistory.length - 1)
                this.commandText = this.cmdHistory[++this.historyIdx];
        },
        historyDown() {
            if (this.historyIdx > 0) { this.commandText = this.cmdHistory[--this.historyIdx]; }
            else                     { this.historyIdx = -1; this.commandText = ''; }
        },

        addMessage(text, type) {
            // Route [nerd]...[/nerd] blocks to the nerd log
            const nerdRe = /\[nerd]([\s\S]*?)\[\/nerd]/g;
            let m;
            let hasNerd = false;
            while ((m = nerdRe.exec(text)) !== null) {
                hasNerd = true;
                const now = new Date();
                const ts  = String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
                this.nerdLog.push({ ts, text: m[1].trim() });
            }

            // Extract [stats] block and update sidebar
            const statsM = text.match(/\[stats]([^[]+)\[\/stats]/);
            if (statsM) {
                const [hp, maxHp, mana, maxMana, stamina, maxStamina, carry, maxCarry] =
                    statsM[1].split(',').map(Number);
                this.playerStats = { hp, maxHp, mana, maxMana, stamina, maxStamina, carry, maxCarry };
            }

            // Track current room from "=== [room]Name[/room] ===" header lines
            // Clear room items on room change so stale items don't linger
            const roomM = text.match(/===\s*\[room](.*?)\[\/room]\s*===/);
            if (roomM) {
                if (roomM[1] !== this.currentRoom) this.roomItems = [];
                this.currentRoom = roomM[1];
            }

            // Extract visible NPC names from [npc]...[/npc] tags (populated by look/go)
            const npcTags = [...text.matchAll(/\[npc](.*?)\[\/npc]/g)];
            if (npcTags.length > 0) this.roomNpcs = npcTags.map(m => m[1]);

            // Merge visible room items from [item] tags (look output replaces; search merges)
            const itemTags = [...text.matchAll(/\[item](.*?)\[\/item]/g)];
            if (itemTags.length > 0) {
                const found = itemTags.map(m => m[1]);
                for (const name of found) {
                    if (!this.roomItems.includes(name)) this.roomItems.push(name);
                }
            }

            // Replace inventory item list from [invitem] tags (inventory / status commands)
            const invTags = [...text.matchAll(/\[invitem](.*?)\[\/invitem]/g)];
            if (invTags.length > 0) this.inventoryItems = invTags.map(m => m[1]);

            // Strip nerd and stats blocks from the main output text
            const mainText = text
                .replaceAll(/\n?\[nerd][\s\S]*?\[\/nerd]\n?/g, '\n')
                .replaceAll(/\[stats][^[]*\[\/stats]/g, '')
                .replace(/^\n+/, '')
                .trimEnd();

            if (!mainText.trim()) {
                if (hasNerd) this.$nextTick(() => {
                    const ne = this.$refs.nerdOutputEl;
                    if (ne) ne.scrollTop = ne.scrollHeight;
                });
                return;
            }

            const speakable = (type === 'output' || type === 'system') && !!mainText.trim();
            this.messages.push({ text: mainText, type: type || 'output', speakable });
            this.$nextTick(() => {
                const el = this.$refs.outputEl;
                if (el) el.scrollTop = el.scrollHeight;
                const ne = this.$refs.nerdOutputEl;
                if (ne) ne.scrollTop = ne.scrollHeight;
                if (speakable && this.narratorEnabled) this.speak(mainText);
            });
        },

        parseMarkup(text) {
            const esc = text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
            return esc
                .replaceAll(/\[nerd][\s\S]*?\[\/nerd]/g,      '')
                .replaceAll(/\[stats][^[]*\[\/stats]/g,       '')
                .replaceAll(/[[]narrate]([\s\S]*?)[[]\/narrate]/g, '$1')
                .replaceAll(/[[]room](.*?)[[]\/room]/g,           '<span class="tag-room">$1</span>')
                .replaceAll(/[[]exit](.*?)[[]\/exit]/g,           '<span class="tag-exit">$1</span>')
                .replaceAll(/[[]item](.*?)[[]\/item]/g,           (_, name) =>
                    `<span class="tag-item cmd-link" data-cmd="take ${name.toLowerCase()}" title="take ${name.toLowerCase()}">${name}</span>`)
                .replaceAll(/[[]invitem](.*?)[[]\/invitem]/g,     (_, name) =>
                    `<span class="tag-item cmd-link" data-cmd="equip ${name.toLowerCase()}" title="equip ${name.toLowerCase()}">${name}</span>`)
                .replaceAll(/[[]equipped](.*?)[[]\/equipped]/g,   (_, name) =>
                    `<span class="tag-item cmd-link" data-cmd="unequip ${name.toLowerCase()}" title="unequip ${name.toLowerCase()}">${name}</span>`)
                .replaceAll(/[[]container](.*?)[[]\/container]/g, (_, name) =>
                    `<span class="tag-container cmd-link" data-cmd="search ${name.toLowerCase()}" title="search ${name.toLowerCase()}">${name}</span>`)
                .replaceAll(/[[]npc](.*?)[[]\/npc]/g, (_, name) =>
                    `<span class="tag-npc cmd-link" data-cmd="talk ${name.toLowerCase()}" title="talk ${name.toLowerCase()}">${name}</span>`)
                .replaceAll(/[[]c=([^\]]+)](.*?)[[]\/c]/g,        '<span style="color:$1">$2</span>');
        },

        handleOutputClick(e) {
            const link = e.target.closest('.cmd-link');
            if (!link?.dataset.cmd) return;

            const cmd      = link.dataset.cmd;                      // e.g. "take short sword"
            const itemName = cmd.split(' ').slice(1).join(' ');     // "short sword"
            const current  = this.commandText.trimEnd();
            const typedVerb = current.split(' ')[0];

            // If the player already typed a verb, honor it and just fill the argument
            this.commandText = (current && typedVerb && itemName)
                ? typedVerb + ' ' + itemName
                : cmd;

            Sounds.click();
            this.$nextTick(() => this.$refs.commandInput?.focus());
        },

        // ── Nerd mode ────────────────────────────────────────
        toggleNerdMode() { this.nerdMode = !this.nerdMode; },

        // ── Narrator ─────────────────────────────────────────
        toggleNarrator() {
            this.narratorEnabled = !this.narratorEnabled;
            if (!this.narratorEnabled) globalThis.speechSynthesis?.cancel();
        },
        speak(text) {
            if (!globalThis.speechSynthesis) return;
            globalThis.speechSynthesis.cancel();
            const utt = new SpeechSynthesisUtterance(this.stripTags(text));
            utt.voice = this.availableVoices.find(v => v.lang === 'en-GB')
                     || this.availableVoices.find(v => v.lang.startsWith('en'))
                     || null;
            utt.rate = 0.88; utt.pitch = 0.85;
            globalThis.speechSynthesis.speak(utt);
        },
        stripTags(text) {
            const m = text.match(/\[narrate]([\s\S]*?)\[\/narrate]/);
            return m ? m[1] : text
                .replaceAll(/\[nerd][\s\S]*?\[\/nerd]/g, '')
                .replaceAll(/\[stats][^[]*\[\/stats]/g, '')
                .replaceAll(/\[c=[^\]]+]/g, '')
                .replaceAll('[/c]', '')
                .replaceAll(/\[\/?(?:room|exit|item|invitem|equipped|container|narrate)]/g, '');
        },

        pct(val, max) {
            return max > 0 ? Math.max(2, Math.round(val / max * 100)) + '%' : '0%';
        },
        hpColor(hp, max) {
            const p = max > 0 ? hp / max : 1;
            if (p > 0.5)  return '#00ff41';
            if (p > 0.25) return '#ffd700';
            return '#ff4444';
        },

        uiClick() { Sounds.click(); },

        // ── Input syntax highlight ────────────────────────────
        highlightInput() {
            const hl = this.$refs.inputHighlight;
            if (!hl) return;

            const raw = this.commandText;
            if (!raw || !raw.trim()) { hl.innerHTML = ''; return; }

            const DIRS = new Set(['north','south','east','west','n','s','e','w']);
            const DICE_RE = /^\d*d\d+([+-]\d+)?$/i;
            const esc = s => s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');

            const tokens = raw.split(' ');
            const verb = tokens[0].toLowerCase();
            const type = VERB_TYPES[verb];
            const verbCls = type !== undefined ? 'hl-verb' : 'hl-unknown';

            let html = `<span class="${verbCls}">${esc(tokens[0])}</span>`;

            if (tokens.length > 1) {
                const rest = tokens.slice(1);
                const restStr = rest.join(' ');

                if (type === 'direction') {
                    const cls = DIRS.has(rest[0]?.toLowerCase()) ? 'hl-dir' : 'hl-arg';
                    html += ` <span class="${cls}">${esc(restStr)}</span>`;
                } else if (type === 'dice') {
                    const cls = DICE_RE.test(rest[0]) ? 'hl-dice' : 'hl-arg';
                    html += ` <span class="${cls}">${esc(restStr)}</span>`;
                } else if (type === 'ability') {
                    html += ` <span class="hl-ability">${esc(restStr)}</span>`;
                } else if (type === 'steal') {
                    // NPC name in gold, optional item name in gray — no intent stage
                    const argsStr   = restStr;
                    const argsLower = argsStr.toLowerCase();
                    let   matchedLen = 0;
                    for (const n of (this.roomNpcs || [])) {
                        const nl = n.toLowerCase();
                        if (argsLower.startsWith(nl) && nl.length > matchedLen) matchedLen = nl.length;
                    }
                    if (matchedLen > 0 && argsStr.length > matchedLen + 1) {
                        html += ` <span class="hl-target">${esc(argsStr.slice(0, matchedLen))}</span>`;
                        html += ` <span class="hl-message">${esc(argsStr.slice(matchedLen + 1))}</span>`;
                    } else {
                        html += ` <span class="hl-target">${esc(argsStr)}</span>`;
                    }
                } else if (type === 'talk') {
                    // Optional intent token before NPC name: color it as hl-ability, then do NPC matching
                    let npcRest = rest;
                    if (rest.length > 0 && TALK_INTENTS.has(rest[0].toLowerCase())) {
                        html += ` <span class="hl-ability">${esc(rest[0])}</span>`;
                        npcRest = rest.slice(1);
                    }
                    const npcStr   = npcRest.join(' ');
                    const npcLower = npcStr.toLowerCase();
                    let   matchedLen = 0;
                    for (const n of (this.roomNpcs || [])) {
                        const nl = n.toLowerCase();
                        if (npcLower.startsWith(nl) && nl.length > matchedLen)
                            matchedLen = nl.length;
                    }
                    if (npcStr) {
                        if (matchedLen > 0 && npcStr.length > matchedLen + 1) {
                            html += ` <span class="hl-target">${esc(npcStr.slice(0, matchedLen))}</span>`;
                            html += ` <span class="hl-message">${esc(npcStr.slice(matchedLen + 1))}</span>`;
                        } else {
                            html += ` <span class="hl-target">${esc(npcStr)}</span>`;
                        }
                    }
                } else if (type === 'target' || type === 'slot') {
                    html += ` <span class="hl-target">${esc(restStr)}</span>`;
                } else {
                    html += ` <span class="hl-arg">${esc(restStr)}</span>`;
                }
            }

            hl.innerHTML = `<span class="hl-inner">${html}</span>`;
            const input = this.$refs.commandInput;
            if (input && hl.firstElementChild)
                hl.firstElementChild.style.transform = `translateX(-${input.scrollLeft}px)`;
        },

        syncHighlightScroll() {
            const hl = this.$refs.inputHighlight;
            const input = this.$refs.commandInput;
            if (hl && input && hl.firstElementChild)
                hl.firstElementChild.style.transform = `translateX(-${input.scrollLeft}px)`;
        },

        // ── Utility ──────────────────────────────────────────
        cap(s) {
            if (!s) return '—';
            return s.charAt(0).toUpperCase() + s.slice(1);
        },
    },

    mounted() {
        // Browsers block autoplay until the first user gesture — start music on first click
        const onFirstClick = () => {
            this.startMusic();
            document.removeEventListener('click', onFirstClick, true);
        };
        document.addEventListener('click', onFirstClick, true);

        if (globalThis.speechSynthesis) {
            const load = () => { this.availableVoices = globalThis.speechSynthesis.getVoices(); };
            load();
            globalThis.speechSynthesis.addEventListener('voiceschanged', load);
        }
        void this.fetchSaves();
    },

}).mount('#app');
