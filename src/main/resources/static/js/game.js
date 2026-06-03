'use strict';

let ws = null;
let playerName = '';
let history = [];
let historyIdx = -1;

// ── Narrator ──────────────────────────────────────────────
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

function getVoice() {
    return availableVoices.find(v => v.lang === 'en-GB')
        || availableVoices.find(v => v.lang.startsWith('en'))
        || null;
}

function speak(text) {
    if (!window.speechSynthesis) return;
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(stripTags(text));
    utterance.voice = getVoice();
    utterance.rate  = 0.88;
    utterance.pitch = 0.85;
    window.speechSynthesis.speak(utterance);
}

function stripTags(text) {
    const match = text.match(/\[narrate\]([\s\S]*?)\[\/narrate\]/);
    if (match) return match[1];
    return text.replace(/\[\/?(?:room|exit|item|narrate)\]/g, '');
}
// ──────────────────────────────────────────────────────────

const loginScreen  = document.getElementById('login-screen');
const gameScreen   = document.getElementById('game-screen');
const playerInfo   = document.getElementById('player-info');
const output       = document.getElementById('output');
const commandInput = document.getElementById('command-input');
const playerNameEl = document.getElementById('player-name');
const startBtn     = document.getElementById('start-btn');

startBtn.addEventListener('click', startGame);
playerNameEl.addEventListener('keydown', e => { if (e.key === 'Enter') startGame(); });

function startGame() {
    const name = playerNameEl.value.trim();
    if (!name) return;
    playerName = name;

    loginScreen.classList.add('hidden');
    gameScreen.classList.remove('hidden');
    playerInfo.textContent = playerName;
    narratorBtn.classList.remove('hidden');

    connect();
}

function connect() {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    ws = new WebSocket(protocol + '//' + location.host + '/game-ws');

    ws.onopen = () => {
        ws.send(JSON.stringify({ type: 'join', name: playerName }));
        commandInput.focus();
    };

    ws.onmessage = event => {
        const msg = JSON.parse(event.data);
        appendLine(msg.text, msg.type);
    };

    ws.onclose = () => appendLine('Connection closed. Refresh to reconnect.', 'error');
    ws.onerror = () => appendLine('WebSocket error — is the server running?', 'error');

    commandInput.addEventListener('keydown', onKey);
}

function onKey(e) {
    if (e.key === 'Enter') {
        const text = commandInput.value.trim();
        commandInput.value = '';
        historyIdx = -1;
        if (!text) return;
        history.unshift(text);
        appendLine('> ' + text, 'command');
        send(text);
    } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (historyIdx < history.length - 1) commandInput.value = history[++historyIdx];
    } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        historyIdx > 0 ? commandInput.value = history[--historyIdx] : (historyIdx = -1, commandInput.value = '');
    }
}

function send(text) {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        appendLine('Not connected. Refresh the page.', 'error');
        return;
    }
    ws.send(JSON.stringify({ type: 'command', text }));
}

function appendLine(text, type) {
    const div = document.createElement('div');
    div.className = 'line ' + (type || 'output');

    const content = document.createElement('span');
    content.className = 'line-content';
    content.innerHTML = parseMarkup(text);
    div.appendChild(content);

    // Speaker button on narrate-able lines
    const speakable = type === 'output' || type === 'system';
    if (speakable && text.trim()) {
        const btn = document.createElement('button');
        btn.className = 'speak-btn';
        btn.title = 'Read aloud';
        btn.textContent = '🔊';
        btn.addEventListener('click', () => speak(text));
        div.appendChild(btn);

        if (narratorEnabled) speak(text);
    }

    output.appendChild(div);
    output.scrollTop = output.scrollHeight;
}

function parseMarkup(text) {
    const escaped = text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
    return escaped
        .replace(/\[narrate\](.*?)\[\/narrate\]/g, '$1')
        .replace(/\[room\](.*?)\[\/room\]/g, '<span class="tag-room">$1</span>')
        .replace(/\[exit\](.*?)\[\/exit\]/g, '<span class="tag-exit">$1</span>')
        .replace(/\[item\](.*?)\[\/item\]/g, '<span class="tag-item">$1</span>');
}
