'use strict';

// ── Narrator (Web Speech API) ─────────────────────────────
let narratorEnabled = false;
let availableVoices = [];

const narratorBtn = document.getElementById('narrator-btn');

if (window.speechSynthesis) {
    const loadVoices = () => { availableVoices = window.speechSynthesis.getVoices(); };
    loadVoices();
    window.speechSynthesis.addEventListener('voiceschanged', loadVoices);
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
    utt.voice = availableVoices.find(v => v.lang === 'en-GB')
             || availableVoices.find(v => v.lang.startsWith('en'))
             || null;
    utt.rate = 0.88; utt.pitch = 0.85;
    window.speechSynthesis.speak(utt);
}

function stripTags(text) {
    const m = text.match(/\[narrate\]([\s\S]*?)\[\/narrate\]/);
    return m ? m[1] : text.replace(/\[\/?(?:room|exit|item|narrate)\]/g, '');
}
