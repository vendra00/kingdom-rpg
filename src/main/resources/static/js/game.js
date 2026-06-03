'use strict';

// ── DOM refs ──────────────────────────────────────────────
const gameScreen   = document.getElementById('game-screen');
const playerInfo   = document.getElementById('player-info');
const output       = document.getElementById('output');
const commandInput = document.getElementById('command-input');

let ws = null, history = [], historyIdx = -1;

// ── Transition from wizard to game ────────────────────────
function startGame() {
    charScreen.classList.add('hidden');
    gameScreen.classList.remove('hidden');
    playerInfo.textContent = playerName;
    narratorBtn.classList.remove('hidden');
    connect();
}

// ── WebSocket ─────────────────────────────────────────────
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

// ── Input handling ────────────────────────────────────────
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

// ── Output rendering ──────────────────────────────────────
function appendLine(text, type) {
    const div     = el('div', 'line ' + (type || 'output'));
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
    const esc = text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return esc
        .replace(/\[narrate\](.*?)\[\/narrate\]/g, '$1')
        .replace(/\[room\](.*?)\[\/room\]/g,        '<span class="tag-room">$1</span>')
        .replace(/\[exit\](.*?)\[\/exit\]/g,         '<span class="tag-exit">$1</span>')
        .replace(/\[item\](.*?)\[\/item\]/g,         '<span class="tag-item">$1</span>');
}

// ── Utility ───────────────────────────────────────────────
function el(tag, className) {
    const e = document.createElement(tag);
    if (className) e.className = className;
    return e;
}
