'use strict';

import { STEPS, RACES, CLASSES, GENDERS, BACKGROUNDS,
         ATTR_DEFS, TOTAL_POINTS, POINT_COSTS, ATTR_MIN, ATTR_MAX,
         CANTRIPS, CANTRIP_SLOTS,
         COMMAND_COMPLETIONS, DIRECTION_COMPLETIONS, DICE_COMPLETIONS, ABILITY_COMPLETIONS
       } from './data.js';

const { createApp } = Vue;

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

            // ── Autocomplete ─────────────────────────────────
            ac: { items: [], idx: -1 },

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

    methods: {
        // ── Title menu ───────────────────────────────────────
        goToNewGame() { this.startMusic(); this.screen = 'login'; },

        async goToLoad() {
            this.startMusic();   // before await — stays inside the user-gesture context
            await this.fetchSaves();
            this.screen = 'load';
        },

        async goToContinue() {
            if (!this.saves.length) return;
            this.fadeOutMusic();
            const s = this.saves[0];
            this.playerName = s.name;
            this.screen = 'game';
            await this.$nextTick();
            this.connectDirect(s.name);
        },

        goToExit() { window.close(); },

        async fetchSaves() {
            try {
                const r  = await fetch('/api/players');
                this.saves = await r.json();
            } catch (_) {
                this.saves = [];
            }
            this.savesLoaded = true;
        },

        loadSave(s) {
            this.fadeOutMusic();
            this.playerName = s.name;
            this.screen = 'game';
            this.$nextTick(() => this.connectDirect(s.name));
        },

        connectDirect(name) {
            const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
            this.ws = new WebSocket(protocol + '//' + location.host + '/game-ws');
            this.ws.onopen    = () => {
                this.ws.send(JSON.stringify({ type: 'join', name }));
                this.$nextTick(() => this.$refs.commandInput?.focus());
            };
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
            this.isLastStep ? this.startGame() : this.wizardStep++;
        },
        prevStep() { if (this.wizardStep > 0) this.wizardStep--; },

        validateStep() {
            const s = this.currentStep;
            if (s === 'race'       && !this.char.race)            { this.flash('Choose a race to continue.');       return false; }
            if (s === 'class'      && !this.char.characterClass)  { this.flash('Choose a class to continue.');      return false; }
            if (s === 'gender'     && !this.char.gender)           { this.flash('Choose a gender to continue.');     return false; }
            if (s === 'background' && !this.char.background)       { this.flash('Choose a background to continue.'); return false; }
            return true;
        },

        flash(msg) {
            this.flashMsg = msg;
            setTimeout(() => { this.flashMsg = ''; }, 2500);
        },

        selectClass(cls) {
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
            return m > 0 ? 'pos' : m < 0 ? 'neg' : '';
        },

        incrementAttr(key) { if (this.canAfford(key))               this.char.attrs[key]++; },
        decrementAttr(key) { if (this.char.attrs[key] > ATTR_MIN)   this.char.attrs[key]--; },

        // ── Cantrips ─────────────────────────────────────────
        toggleCantrip(cantrip) {
            const idx = this.char.cantrips.indexOf(cantrip.id);
            if (idx >= 0) {
                this.char.cantrips.splice(idx, 1);
            } else if (this.char.cantrips.length < this.cantripSlots) {
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
            } else if (['cast', 'use'].includes(verb)) {
                pool = this.cantripCompletions();
            } else if (['attempt', 'try'].includes(verb)) {
                pool = ABILITY_COMPLETIONS;
            } else if (verb === 'roll') {
                pool = DICE_COMPLETIONS;
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

            if (endsWithSpace) {
                words.push(item.value);
            } else {
                words[words.length - 1] = item.value;
            }

            const NEEDS_ARGS = new Set(['go','move','take','get','pick','drop','cast','use','roll','attempt','try']);
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
                if (e.key === 'ArrowDown') {
                    e.preventDefault();
                    this.ac.idx = (this.ac.idx + 1) % this.ac.items.length;
                    this.$nextTick(() => this.scrollAcActive());
                    return;
                }
                if (e.key === 'ArrowUp') {
                    e.preventDefault();
                    this.ac.idx = this.ac.idx <= 0
                        ? this.ac.items.length - 1 : this.ac.idx - 1;
                    this.$nextTick(() => this.scrollAcActive());
                    return;
                }
                if (e.key === 'Tab') {
                    e.preventDefault();
                    this.acceptAc(this.ac.items[Math.max(0, this.ac.idx)]);
                    return;
                }
                if (e.key === 'Escape') { e.preventDefault(); this.closeAc(); return; }
                if (e.key === 'Enter' && this.ac.idx >= 0) {
                    e.preventDefault();
                    this.acceptAc(this.ac.items[this.ac.idx]);
                    return;
                }
            } else {
                if (e.key === 'ArrowUp')   { e.preventDefault(); this.historyUp();   return; }
                if (e.key === 'ArrowDown') { e.preventDefault(); this.historyDown(); return; }
            }
            if (e.key === 'Enter') this.sendCommand();
        },

        scrollAcActive() {
            document.querySelector('#ac-list .ac-active')?.scrollIntoView({ block: 'nearest' });
        },

        // ── Game ─────────────────────────────────────────────
        startGame() {
            this.fadeOutMusic();
            this.screen = 'game';
            this.$nextTick(() => this.connect());
        },

        connect() {
            const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
            this.ws = new WebSocket(protocol + '//' + location.host + '/game-ws');

            this.ws.onopen = () => {
                this.ws.send(JSON.stringify({
                    type: 'join', name: this.playerName,
                    race: this.char.race, characterClass: this.char.characterClass,
                    gender: this.char.gender, background: this.char.background,
                    attributes: this.char.attrs, cantrips: this.char.cantrips,
                }));
                this.$nextTick(() => this.$refs.commandInput?.focus());
            };
            this.ws.onmessage = e => { const m = JSON.parse(e.data); this.addMessage(m.text, m.type); };
            this.ws.onclose   = () => this.addMessage('Connection closed. Refresh to reconnect.', 'error');
            this.ws.onerror   = () => this.addMessage('WebSocket error — is the server running?', 'error');
        },

        sendCommand() {
            const text = this.commandText.trim();
            this.commandText = '';
            this.historyIdx  = -1;
            this.closeAc();
            if (!text) return;
            this.cmdHistory.unshift(text);
            this.addMessage('> ' + text, 'command');
            if (!this.ws || this.ws.readyState !== WebSocket.OPEN) { this.addMessage('Not connected.', 'error'); return; }
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
            const speakable = (type === 'output' || type === 'system') && !!text.trim();
            this.messages.push({ text, type: type || 'output', speakable });
            this.$nextTick(() => {
                const el = this.$refs.outputEl;
                if (el) el.scrollTop = el.scrollHeight;
                if (speakable && this.narratorEnabled) this.speak(text);
            });
        },

        parseMarkup(text) {
            const esc = text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
            return esc
                .replace(/\[narrate\](.*?)\[\/narrate\]/g, '$1')
                .replace(/\[room\](.*?)\[\/room\]/g,        '<span class="tag-room">$1</span>')
                .replace(/\[exit\](.*?)\[\/exit\]/g,         '<span class="tag-exit">$1</span>')
                .replace(/\[item\](.*?)\[\/item\]/g,         '<span class="tag-item">$1</span>')
                .replace(/\[c=([^\]]+)\](.*?)\[\/c\]/g,      '<span style="color:$1">$2</span>');
        },

        // ── Narrator ─────────────────────────────────────────
        toggleNarrator() {
            this.narratorEnabled = !this.narratorEnabled;
            if (!this.narratorEnabled) window.speechSynthesis?.cancel();
        },
        speak(text) {
            if (!window.speechSynthesis) return;
            window.speechSynthesis.cancel();
            const utt = new SpeechSynthesisUtterance(this.stripTags(text));
            utt.voice = this.availableVoices.find(v => v.lang === 'en-GB')
                     || this.availableVoices.find(v => v.lang.startsWith('en'))
                     || null;
            utt.rate = 0.88; utt.pitch = 0.85;
            window.speechSynthesis.speak(utt);
        },
        stripTags(text) {
            const m = text.match(/\[narrate\]([\s\S]*?)\[\/narrate\]/);
            return m ? m[1] : text
                .replace(/\[c=[^\]]+\]/g, '')
                .replace(/\[\/c\]/g, '')
                .replace(/\[\/?(?:room|exit|item|narrate)\]/g, '');
        },

        // ── Utility ──────────────────────────────────────────
        cap(s) {
            if (!s) return '—';
            return s.charAt(0).toUpperCase() + s.slice(1);
        },
    },

    mounted() {
        if (window.speechSynthesis) {
            const load = () => { this.availableVoices = window.speechSynthesis.getVoices(); };
            load();
            window.speechSynthesis.addEventListener('voiceschanged', load);
        }
        this.fetchSaves();
    },

}).mount('#app');
