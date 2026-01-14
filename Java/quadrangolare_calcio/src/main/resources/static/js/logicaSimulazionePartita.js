// LOGICA PER SIMULARE PARTITE CON AGGIORNAMENTO UI DINAMICO

const matchState = {
    tipoPartita: "",
    homeTeam: {
        nome: "",
        logo: "",
        gol: 0,
        giocatori: []
    },
    awayTeam: {
        nome: "",
        logo: "",
        gol: 0,
        giocatori: []
    },
    currentTime: 0, // Tempo in secondi
    half: 1,
    maxTime: 90*60,  // 90 minuti reali
    tickStep: 30,   // 30 secondi simulati per tick
    tickInterval: 1000,    // ms reali tra un tick e l’altro
    speedFactor: 3,
    recuperoMinuti: 0,
    mostraRecupero: false,
    pause: false,
    matchEnded: false,
    matchEndedAfterPenalties: false,
    penalties: {
        active: false,
        turn: 0,
        count: 0,
        results: { home: 0, away: 0 },
        tiratori: {
            home: [],
            away: []
        }
    }
};

// --- DIZIONARIO TELECRONACA ---
const DIZIONARIO_LIVE = {
    PORTIERE: [
        "Incredibile parata di {PT}! Salva il risultato per il {S}!",
        "{PT} vola da un palo all'altro e devia in angolo!",
        "Rinvio lunghissimo di {PT} a cercare le punte.",
        "{PT} blocca a terra un pallone insidioso.",
        "Mischia in area, {PT} esce sicuro e concede calcio d'angolo alla squadra avversaria"
    ],
    GOL: [
        "Incredibile rete di {G}! Un tiro imparabile che si insacca all'incrocio!",
        "GOAL! {G} svetta più in alto di tutti su corner e la mette dentro!",
        "Zampata vincente di {G} in area di rigore! Il {S} esplode di gioia!",
        "{G} palla al piede ne salta due e trafigge il portiere! Gol da antologia!"
    ],
    AZIONE_PERICOLOSA: [
        "{G} prova la conclusione dalla distanza... palla fuori di un soffio!",
        "Grande intervento di {PT} che nega il gol a {G}!",
        "Azione insistita del {S}, ma {G} viene fermato sul più bello.",
        "Traversa! {G} calcia a botta sicura ma il legno gli dice di no!",
        "Tiro che prende una strana traiettoria... palo! Sfortunatissimo qui {G}",
        "{G} prova il tiro dal limite! Palla fuori di molto, conclusione sballata.",
        "Grande giocata di {G} che salta l'uomo, ma crossa male."
    ],
    RIGORI: {
        SEGNATO: [
            "Rete! {G} spiazza il portiere con estrema freddezza.",
            "Gol! Conclusione potente di {G}, nulla da fare per {PT}.",
            "{G} trasforma con un tiro preciso sotto la traversa!"
        ],
        PARATO_ERRORE: [
            "Parato! {PT} intuisce l'angolo e neutralizza il tiro di {G}!",
            "Incredibile! {G} calcia fuori, il pallone sorvola la traversa!",
            "Il portiere rimane fermo e blocca il tiro centrale di {G}!",
            "Palo! Occasione sprecata per {G}, {PT} ringrazia e si prepara a riprendere il gioco."
        ]
    },
    LOTTERIA_RIGORI: {
        SEGNATO: [
            "GOL! {G} spiazza il portiere con un rigore perfetto!",
            "RETE! Conclusione potente di {G}, il portiere non ci arriva.",
            "{G} trasforma! Palla da una parte, portiere dall'altra."
        ],
        ERRORE: [
            "PARATO! {PT} vola e respinge il tiro di {G}!",
            "ERRORE! {G} calcia altissimo sopra la traversa!",
            "PALO! Il legno nega la gioia del gol a {G}!",
            "INCREDIBILE! {PT} rimane fermo e blocca il tiro centrale di {G}!"
        ]
    }
};

// Inizializzazione pagina al caricamento
document.addEventListener("DOMContentLoaded", () => {
    // 1. Recupero Nome Torneo dall'header
    const nomeTorneo = sessionStorage.getItem('nomeTorneoAttuale') || 'Torneo';
    document.getElementById('header-nome-torneo').innerText = nomeTorneo;

    // 2. Recupero Dati Match (assumendo siano salvati nel sessionStorage dal tabellone)
    const data = JSON.parse(sessionStorage.getItem('matchCorrente'));
    if (data) {
        matchState.tipoPartita = data.tipo;

        matchState.homeTeam.nome = data.home.nome;
        matchState.homeTeam.logo = data.home.logo;
        matchState.homeTeam.giocatori = data.home.giocatori || [];

        matchState.awayTeam.logo = data.away.logo;
        matchState.awayTeam.nome = data.away.nome;
        matchState.awayTeam.giocatori = data.away.giocatori || [];

        // Aggiornamento Grafica Iniziale
        document.getElementById('tipo-partita-titolo').innerText = data.tipo;
        document.getElementById('nome-home').innerText = data.home.nome;
        document.getElementById('logo-home').src = data.home.logo;
        document.getElementById('nome-away').innerText = data.away.nome;
        document.getElementById('logo-away').src = data.away.logo;

        // Avvio simulazione dopo 2 secondi
        setTimeout(avviaSimulazione, 2000);
    }
});

function getPortiere(team) {
    if (!team.giocatori || team.giocatori.length === 0) return null;
    return team.giocatori.find(g => g.categoria === 'Portiere') || null;
}


function selezionaGiocatorePerAzione(team, tipoAzione) {
    const giocatori = team.giocatori;
    if (!giocatori || giocatori.length === 0) return null;

    let pool = [];
    const r = Math.random() * 100;

    switch (tipoAzione) {
        case 'GOL':
        case 'TIRO':
            if (r < 65) {
                pool = giocatori.filter(g => g.categoria === 'Attaccante');
            } else if (r < 90) {
                pool = giocatori.filter(g => g.categoria === 'Centrocampista');
            } else {
                pool = giocatori.filter(g => g.categoria === 'Difensore');
            }
            break;

        case 'FALLO_SUBITO':
        case 'DRIBBLING':
            pool = giocatori.filter(g => g.categoria === 'Attaccante' || g.ruolo.tipologia.categoria === 'Centrocampista');
            break;

        case 'PORTIERE': // Aggiunto il caso esplicito per la categoria del dizionario
        case 'PARATA':
        case 'RINVIO':
            pool = giocatori.filter(g => g.categoria === 'Portiere');
            break;

        default:
            pool = giocatori.filter(g => g.categoria !== 'Portiere');
    }

    // Fallback: se il filtro è troppo stretto e il pool è vuoto, prendi un giocatore qualsiasi
    if (pool.length === 0) pool = giocatori;

    return pool[Math.floor(Math.random() * pool.length)];
}

function scegliRigorista(team, key) {
    const tutti = team.giocatori;
    let giaUsati = matchState.penalties.tiratori[key];

    // se tutti hanno già tirato, reset
    if (giaUsati.length >= tutti.length) {
        matchState.penalties.tiratori[key] = [];
        giaUsati = [];
    }

    // prendi solo i giocatori disponibili
    let disponibili = tutti.filter(g => !giaUsati.includes(g.id));

    // fallback: se disponibile è vuoto, reset completo
    if (disponibili.length === 0) {
        matchState.penalties.tiratori[key] = [];
        disponibili = [...tutti];
    }

    const scelto = disponibili[Math.floor(Math.random() * disponibili.length)];

    matchState.penalties.tiratori[key].push(scelto.id);
    return scelto;
}



function formattaMessaggio(
    categoria,
    sottocategoria,
    teamAttacco,
    teamDifesa,
    giocatoreForzato = null
) {

    let frasi;

    if (categoria === 'LOTTERIA_RIGORI') {
        frasi = DIZIONARIO_LIVE.LOTTERIA_RIGORI[sottocategoria];
    } else if (categoria === 'RIGORI') {
        frasi = DIZIONARIO_LIVE.RIGORI[sottocategoria];
    } else {
        frasi = DIZIONARIO_LIVE[categoria];
    }

    if (!frasi || frasi.length === 0) {
        return "Azione confusa in campo.";
    }

    const fraseBase = frasi[Math.floor(Math.random() * frasi.length)];

    const giocatoreAzione = giocatoreForzato
        ? giocatoreForzato
        : selezionaGiocatorePerAzione(teamAttacco, categoria);

    const portiere = getPortiere(teamDifesa);

    const nomeG = giocatoreAzione ? giocatoreAzione.cognome : "un giocatore";
    const nomePT = portiere ? portiere.cognome : "il portiere";

    return fraseBase
        .replace(/{G}/g, `<strong>${nomeG}</strong>`)
        .replace(/{PT}/g, `<strong>${nomePT}</strong>`)
        .replace(/{S}/g, `<strong>${teamAttacco.nome}</strong>`);
}



let gameInterval = null;
let penaltyInterval = null;

function avviaSimulazione() {
    aggiungiCommento("L'arbitro fischia l'inizio della partita!");

    if (gameInterval) clearInterval(gameInterval);

    gameInterval = setInterval(() => {
        tick();
    }, matchState.tickInterval);
}



function tick() {
    if (matchState.matchEnded || matchState.pause) return;

    // Avanzamento del tempo
    matchState.currentTime += matchState.tickStep * matchState.speedFactor;

    // Definiamo il limite del tempo regolamentare (2700s per 45', 5400s per 90')
    const limiteRegolamentare = (matchState.half === 1) ? 45 * 60 : 90 * 60;

    // 1. GESTIONE RECUPERO (Calcolo una sola volta quando si arriva al limite)
    if (matchState.currentTime >= limiteRegolamentare && !matchState.mostraRecupero) {
        // Calcolo recupero: 10% probabilità che il 1°T sia >= 2°T (se <= 3 min)
        let r = Math.floor(Math.random() * 8); // 0-7 minuti
        if (matchState.half === 1) {
            const colpoDiScena = Math.random() < 0.10;
            if (!colpoDiScena && r > 3) r = Math.floor(Math.random() * 3); // Spesso corto nel 1°T
        }
        matchState.recuperoMinuti = r;
        matchState.mostraRecupero = true;

        if (r > 0) {
            aggiungiCommento(`[${matchState.half === 1 ? '45:00' : '90:00'}] Segnalati ${r} minuti di recupero.`);
        }
    }

    // 2. LOGICA DI FINE TEMPO / FINE PARTITA (con recupero)
    const tempoTotaleInclusoRecupero = limiteRegolamentare + (matchState.recuperoMinuti * 60);

    if (matchState.currentTime >= tempoTotaleInclusoRecupero) {
        matchState.currentTime = tempoTotaleInclusoRecupero; // Forza il tempo finale
        aggiornaGraficaTimer(); // Funzione di supporto che creiamo sotto

        if (matchState.half === 1) {
            aggiungiCommento(`FINE PRIMO TEMPO (${matchState.recuperoMinuti > 0 ? '+'+matchState.recuperoMinuti : ''})`);
            matchState.half = 2;
            matchState.pause = true;
            matchState.mostraRecupero = false; // Reset per il secondo tempo
            matchState.recuperoMinuti = 0;

            setTimeout(() => {
                aggiungiCommento("INIZIO SECONDO TEMPO");
                matchState.currentTime = 45 * 60; // Inizia esattamente da 45:00
                matchState.pause = false;
            }, 3000);
        } else {
            matchState.pause = true;
            gestisciFinePartita();
        }
        return;
    }

    // 3. AGGIORNAMENTO GRAFICA E EVENTI
    aggiornaGraficaTimer();

    // Eventi: manteniamo il tuo 0.4 di probabilità
    if (Math.random() < 0.4) {
        generaEvento();
    }
}

function aggiornaGraficaTimer() {
    const limiteRegolamentare = (matchState.half === 1) ? 45 * 60 : 90 * 60;
    let testoTempo = "";

    if (matchState.currentTime > limiteRegolamentare) {
        // Se siamo nel recupero, blocca il timer sul 45:00 o 90:00 e aggiungi il +X
        const base = (matchState.half === 1) ? "45:00" : "90:00";
        testoTempo = `${base} <span style="color: red; font-weight: bold;">+${matchState.recuperoMinuti}</span>`;
    } else {
        // Tempo normale
        testoTempo = formattaTempo(matchState.currentTime);
    }

    document.getElementById('tempo-match').innerHTML = `${matchState.half}T ${testoTempo}`;
}

function generaEvento() {
    const isHome = Math.random() > 0.5;
    const teamAttacco = isHome ? matchState.homeTeam : matchState.awayTeam;
    const teamDifesa = isHome ? matchState.awayTeam : matchState.homeTeam;
    const tempo = formattaTempo(matchState.currentTime);

    const rand = Math.random();

    if (rand < 0.20) {
        // AZIONE PORTIERE
        aggiungiCommento(
            `[${tempo}] ${formattaMessaggio(
                'PORTIERE',
                null,
                teamDifesa,
                teamAttacco
            )}`
        );

    } else if (rand < 0.30) {
        // GOAL
        teamAttacco.gol++;
        document.getElementById('punteggio-live').innerText =
            `${matchState.homeTeam.gol} - ${matchState.awayTeam.gol}`;

        aggiungiCommento(
            `[${tempo}] GOAL! ${formattaMessaggio(
                'GOL',
                null,
                teamAttacco,
                teamDifesa
            )}`
        );

    } else {
        // AZIONE PERICOLOSA
        aggiungiCommento(
            `[${tempo}] ${formattaMessaggio(
                'AZIONE_PERICOLOSA',
                null,
                teamAttacco,
                teamDifesa
            )}`
        );
    }
}


function gestisciFinePartita() {

    // BLOCCO TOTALE: questa funzione deve entrare UNA SOLA VOLTA
    if (matchState.matchEnded) return;

    // Pareggio → rigori
    if (
        matchState.homeTeam.gol === matchState.awayTeam.gol &&
        !matchState.penalties.active
    ) {
        matchState.penalties.active = true;

        // FERMIAMO LA PARTITA
        matchState.pause = true;
        if (gameInterval) clearInterval(gameInterval);

        aggiungiCommento("FISCHIO FINALE: Pareggio! Si va ai calci di rigore.");

        document.getElementById('box-rigori').style.display = 'block';

        setTimeout(avviaRigori, 2000);
        return;
    }

    // DA QUI IN POI: PARTITA FINITA DAVVERO
    matchState.matchEnded = true;
    matchState.pause = true;

    if (gameInterval) clearInterval(gameInterval);
    if (penaltyInterval) clearInterval(penaltyInterval);

    let vincitore, perdente;

    if (matchState.penalties.active) {
        const res = matchState.penalties.results;
        vincitore = res.home > res.away ? matchState.homeTeam : matchState.awayTeam;
        perdente = vincitore === matchState.homeTeam
            ? matchState.awayTeam
            : matchState.homeTeam;
    } else {
        vincitore = matchState.homeTeam.gol > matchState.awayTeam.gol
            ? matchState.homeTeam
            : matchState.awayTeam;
        perdente = vincitore === matchState.homeTeam
            ? matchState.awayTeam
            : matchState.homeTeam;
    }

    aggiungiCommento(`MATCH CONCLUSO! ${vincitore.nome.toUpperCase()} passa il turno.`);

    salvaERiprosegui(vincitore, perdente);
}



/**
 * LOGICA RIGORI
 */
function avviaRigori() {

    // BLOCCO TOTALE DEL MATCH
    matchState.pause = true;

    // RESET COMPLETO STATO RIGORI
    matchState.penalties = {
        active: true,
        turn: 0,
        count: 0,
        results: { home: 0, away: 0 },
        tiratori: { home: [], away: [] }
    };

    aggiungiCommento("--- INIZIO CALCI DI RIGORE ---");

    if (penaltyInterval) clearInterval(penaltyInterval);

    penaltyInterval = setInterval(() => {
        if (checkFineRigori()) {
            clearInterval(penaltyInterval);
            gestisciFinePartita();
            return;
        }
        eseguiTurnoRigore();
    }, 1500);
}



function eseguiTurnoRigore() {

    const isHomeTurn = matchState.penalties.turn === 0;

    // Squadra che tira e squadra che difende
    const teamAttacco = isHomeTurn ? matchState.homeTeam : matchState.awayTeam;
    const teamDifesa = isHomeTurn ? matchState.awayTeam : matchState.homeTeam;

    const keyTiratore = isHomeTurn ? 'home' : 'away';

    // SCELTA RIGORISTA (una sola volta per turno)
    const rigorista = scegliRigorista(teamAttacco, keyTiratore);

    // Probabilità di segnare
    const segnato = Math.random() < 0.7;

    if (segnato) {
        isHomeTurn
            ? matchState.penalties.results.home++
            : matchState.penalties.results.away++;
    }

    // Aggiornamento tabellino rigori
    document.getElementById('score-rigori').innerText =
        `${matchState.penalties.results.home} - ${matchState.penalties.results.away}`;

    // Messaggio telecronaca
    const categoria = segnato ? 'SEGNATO' : 'ERRORE';

    aggiungiCommento(
        `[RIGORI] ${formattaMessaggio(
            'LOTTERIA_RIGORI',
            categoria,
            teamAttacco,
            teamDifesa,
            rigorista
        )}`
    );

    // Alternanza turni
    matchState.penalties.turn = isHomeTurn ? 1 : 0;

    // Conta solo dopo entrambi i tiri
    if (matchState.penalties.turn === 0) {
        matchState.penalties.count++;
    }
}



function checkFineRigori() {
    const { home, away } = matchState.penalties.results;
    const rounds = matchState.penalties.count;

    // Dopo 5 rigori a testa
    if (rounds >= 5 && home !== away) {
        return true;
    }

    return false;
}



/**
 * UI HELPERS
 */
function aggiungiCommento(testo) {
    const box = document.getElementById('telecronaca');

    // Creiamo un div contenitore per il singolo messaggio
    const div = document.createElement('div');
    div.classList.add('commentary-event');

    // Gestione icone e classi speciali in base al contenuto
    if (testo.includes("GOAL") || testo.includes("RETE")) {
        div.classList.add('goal');
        testo = "⚽ " + testo;
    } else if (testo.includes("FINE") || testo.includes("INIZIO") || testo.includes("FISCHIO")) {
        div.classList.add('info');
        testo = "📢 " + testo;
    } else if (testo.includes("PARATO") || testo.includes("🧤")) {
        testo = "🧤 " + testo;
    } else {
        testo = "👟 " + testo;
    }

    div.innerHTML = testo;

    // Inseriamo il messaggio in cima
    box.prepend(div);
}

function formattaTempo(secondi) {
    const m = Math.floor(secondi / 60);
    const s = secondi % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}


function salvaERiprosegui(vincitore, perdente) {
    let stato = JSON.parse(sessionStorage.getItem('statoTorneo'));

    // Salvataggi risultati
    if (stato.faseAttuale === 1) stato.risultati.semi1 = { vincente: vincitore, perdente };
    if (stato.faseAttuale === 2) stato.risultati.semi2 = { vincente: vincitore, perdente };
    if (stato.faseAttuale === 3) stato.risultati.finale34 = { vincente: vincitore, perdente };
    if (stato.faseAttuale === 4) stato.risultati.finalissima = { vincente: vincitore, perdente };

    const faseCorrente = stato.faseAttuale;
    const prossimaFase = faseCorrente + 1;

    const container = document.getElementById('container-navigazione');
    const btn = document.getElementById('btn-prosegui-fase');

    if (!btn || !container) return;

    container.style.display = 'block';

    if (prossimaFase <= 4) {
        btn.innerText = `VAI A: ${getNomeFase(prossimaFase)}`;
        btn.onclick = () => {
            stato.faseAttuale = prossimaFase;
            sessionStorage.setItem('statoTorneo', JSON.stringify(stato));
            window.location.reload();
        };
    } else {
        btn.innerText = "VAI ALLA PREMIAZIONE";
        btn.onclick = () => window.location.href = "/archivio";
    }

    btn.style.display = 'block';
}


// Funzione di supporto per gli alert
function getNomeFase(fase) {
    const nomi = {
        1: "SEMIFINALE 1",
        2: "SEMIFINALE 2",
        3: "FINALE 3°-4° POSTO",
        4: "FINALISSIMA"
    };
    return nomi[fase] || "FINE";
}