'use strict';

const Sounds = {
    _ctx: null,

    _getCtx() {
        if (!this._ctx) {
            this._ctx = new (window.AudioContext || window.webkitAudioContext)();
        }
        return this._ctx;
    },

    _tone(freq, duration, volume = 0.12, type = 'square') {
        try {
            const ctx  = this._getCtx();
            const osc  = ctx.createOscillator();
            const gain = ctx.createGain();
            osc.connect(gain);
            gain.connect(ctx.destination);
            osc.type            = type;
            osc.frequency.value = freq;
            gain.gain.setValueAtTime(volume, ctx.currentTime);
            gain.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + duration);
            osc.start(ctx.currentTime);
            osc.stop(ctx.currentTime + duration);
        } catch (_) {}
    },

    // Short terminal blip — menu navigation
    click() {
        this._tone(660, 0.07);
    },

    // Soft lower blip — back / cancel
    back() {
        this._tone(440, 0.07);
    },

    // Ascending blip — attribute point added
    attrUp() {
        this._tone(784, 0.06);
    },

    // Descending blip — attribute point removed
    attrDown() {
        this._tone(523, 0.06);
    },

    // Three-note ascending arpeggio — confirm / enter game
    confirm() {
        this._tone(523, 0.10);
        setTimeout(() => this._tone(659, 0.10), 90);
        setTimeout(() => this._tone(784, 0.14), 180);
    },
};

export default Sounds;
