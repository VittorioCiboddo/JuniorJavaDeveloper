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
    },
    rigoreInCorso: null,

};
const marcatori = {
    home: [],
    away: []
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
    RIGORE_PARTITA: {
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

// DIZIONARIO EMOJI EVENTI TELECRONACA
const EMOJI_EVENTI = {
    INIZIO_FINE_PARTITA: "📢",
    FISCHIO: "/images/icona_fischietto.png",
    RIGORE_ARBITRO: "/images/fischio_rigore.png",
    RECUPERO: "/images/timer_recupero.png",
    GOL: "⚽",
    AZIONE: "👟",
    PALO_TRAVERSA: "🥅",
    PARATA: "🧤",
    RIGORE_OK: "/images/check_verde.png",       // solo lotteria rigori
    RIGORE_KO: "❌",                           // solo lotteria rigori
    RIGORE_PARTITA_OK: "/images/rigore_gol.png",
    RIGORE_PARTITA_KO: "/images/rigore_fail.png"
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

        case 'PORTIERE':
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

    if (!matchState.penalties.tiratori[key]) {
        matchState.penalties.tiratori[key] = [];
    }

    const giaUsati = matchState.penalties.tiratori[key];

    // se tutti hanno già tirato → reset (oltranza vera)
    if (giaUsati.length >= tutti.length) {
        matchState.penalties.tiratori[key] = [];
    }

    const disponibili = tutti.filter((_, index) =>
        !matchState.penalties.tiratori[key].includes(index)
    );

    const indiceScelto =
        disponibili.length > 0
            ? tutti.indexOf(disponibili[Math.floor(Math.random() * disponibili.length)])
            : Math.floor(Math.random() * tutti.length);

    matchState.penalties.tiratori[key].push(indiceScelto);

    return tutti[indiceScelto];
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
    } else if (categoria === 'RIGORE_PARTITA') {
        frasi = DIZIONARIO_LIVE.RIGORE_PARTITA[sottocategoria];
    } else {
        frasi = DIZIONARIO_LIVE[categoria];
    }

    if (!frasi || frasi.length === 0) {
        return "Azione confusa in campo.";
    }

    const fraseBase = frasi[Math.floor(Math.random() * frasi.length)];

    // CASO SPECIALE PORTIERE
    if (categoria === 'PORTIERE') {
        const portiere = getPortiere(teamDifesa);
        const nomePT = portiere ? portiere.cognome : "il portiere";

        return fraseBase
            .replace(/{PT}/g, `<strong>${nomePT}</strong>`)
            .replace(/{S}/g, `<strong>${teamDifesa.nome}</strong>`);
    }

    // CASO NORMALE
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

    document.getElementById('col-neutral').innerHTML = "";

    aggiungiCommento("L'arbitro fischia l'inizio della partita!", "FISCHIO", "neutral");

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
            aggiungiCommento(`[${matchState.half === 1 ? '45:00' : '90:00'}] Segnalati ${r} minuti di recupero.`, "RECUPERO", "neutral");
        }
    }

    // 2. LOGICA DI FINE TEMPO / FINE PARTITA (con recupero)
    const tempoTotaleInclusoRecupero = limiteRegolamentare + (matchState.recuperoMinuti * 60);

    if (matchState.currentTime >= tempoTotaleInclusoRecupero) {
        matchState.currentTime = tempoTotaleInclusoRecupero; // Forza il tempo finale

        // --- esegui eventuale rigore in sospeso ---
        if (matchState.rigoreInCorso) {
            const r = matchState.rigoreInCorso;
            const segnato = Math.random() < 0.7;

            // Aggiorna gol e tabellino
            if (segnato) r.teamAttacco.gol++;

            const tipoEvento = segnato ? "GOL" : "RIGORE_KO";
            const iconaForzata = segnato ? "RIGORE_PARTITA_OK" : "RIGORE_PARTITA_KO";
            const sottocategoria = segnato ? "SEGNATO" : "PARATO_ERRORE";

            const tempoFormattato = formattaTempo(r.tempoEsecuzione);

            registraMarcatore(
                r.target,
                r.rigorista.cognome,
                r.tempoEsecuzione,
                segnato
            );

            document.getElementById('punteggio-live').innerText =
                `${matchState.homeTeam.gol} - ${matchState.awayTeam.gol}`;

            aggiungiCommento(
                `[${tempoFormattato}] ${formattaMessaggio(
                    'RIGORE_PARTITA',
                    sottocategoria,
                    r.teamAttacco,
                    r.teamDifesa,
                    r.rigorista
                )}`,
                tipoEvento,
                r.target,
                iconaForzata
            );

            matchState.rigoreInCorso = null; // Rigore consumato
        }

        aggiornaGraficaTimer(); // Funzione di supporto che creiamo sotto

        if (matchState.half === 1) {
            aggiungiCommento(`FINE PRIMO TEMPO (${matchState.recuperoMinuti > 0 ? '+'+matchState.recuperoMinuti : ''})`, "FISCHIO", "neutral");
            matchState.half = 2;
            matchState.pause = true;
            matchState.mostraRecupero = false; // Reset per il secondo tempo
            matchState.recuperoMinuti = 0;

            setTimeout(() => {
                aggiungiCommento("INIZIO SECONDO TEMPO", "FISCHIO", "neutral");
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
    const target = isHome ? 'home' : 'away';
    const tempo = formattaTempo(matchState.currentTime);

    // --- Gestione rigore in partita ---
    if (matchState.rigoreInCorso) {
        const { tempoEsecuzione, teamAttacco, teamDifesa, rigorista, target } = matchState.rigoreInCorso;

        // Se il tempo della simulazione ha raggiunto o superato il momento del tiro
        if (matchState.currentTime >= tempoEsecuzione) {
            const segnato = Math.random() < 0.70;

            // Usiamo il tempo esatto dell'esecuzione per il commento e il tabellino
            const tempoFormattato = formattaTempo(tempoEsecuzione);

            if (segnato) {
                teamAttacco.gol++;
                // Usa tempoEsecuzione, così il minuto nel tabellino è coerente (es. 74')
                registraMarcatore(target, rigorista.cognome, tempoEsecuzione, true);

                document.getElementById('punteggio-live').innerText =
                    `${matchState.homeTeam.gol} - ${matchState.awayTeam.gol}`;
            }

            const tipoEvento = segnato ? "GOL" : "RIGORE_KO";
            const iconaForzata = segnato ? "RIGORE_PARTITA_OK" : "RIGORE_PARTITA_KO";
            const sottocategoria = segnato ? "SEGNATO" : "PARATO_ERRORE";

            // NOTA: Passiamo tempoFormattato per evitare che esca il minuto 91' se la simulazione è corsa avanti
            aggiungiCommento(
                `[${tempoFormattato}] ${formattaMessaggio(
                    'RIGORE_PARTITA',
                    sottocategoria,
                    teamAttacco,
                    teamDifesa,
                    rigorista
                )}`,
                tipoEvento,
                target,
                iconaForzata
            );

            matchState.rigoreInCorso = null;
        }
        return; // ESCI dalla funzione: se c'è un rigore in corso, non vogliamo altri eventi nello stesso tick
    }

    // --- Altri eventi del match ---
    const rand = Math.random();

    if (rand < 0.20) {
        // AZIONE PORTIERE (difesa)
        const frase = formattaMessaggio('PORTIERE', null, teamAttacco, teamDifesa);
        const targetPortiere = teamDifesa === matchState.homeTeam ? 'home' : 'away';

        aggiungiCommento(
            `[${tempo}] ${frase}`,
            "PARATA",
            targetPortiere
        );

    } else if (rand < 0.30 && !matchState.rigoreInCorso) {
        // Assegna nuovo rigore in partita
        const rigorista = scegliRigorista(teamAttacco, isHome ? 'home' : 'away');
        const delayRigore = 30 + Math.random() * 150;

        matchState.rigoreInCorso = {
            teamAttacco,
            teamDifesa,
            rigorista,
            target,
            tempoFischio: matchState.currentTime,
            tempoEsecuzione: matchState.currentTime + delayRigore
        };

        aggiungiCommento(
            `[${tempo}] Attenzione: l'arbitro fischia calcio di rigore per ${teamAttacco.nome}!`,
            "RIGORE_ARBITRO",
            target
        );

    } else if (rand < 0.50) {
        // GOAL NORMALE
        const marcatore = selezionaGiocatorePerAzione(teamAttacco, 'GOL');
        teamAttacco.gol++;

        registraMarcatore(
            target,
            marcatore.cognome,
            matchState.currentTime,
            false
        );

        aggiungiCommento(
            `[${tempo}] GOAL! ${formattaMessaggio('GOL', null, teamAttacco, teamDifesa, marcatore)}`,
            "GOL",
            target
        );

        document.getElementById('punteggio-live').innerText =
            `${matchState.homeTeam.gol} - ${matchState.awayTeam.gol}`;

    } else {
        // AZIONE PERICOLOSA
        const marcatore = selezionaGiocatorePerAzione(teamAttacco, 'AZIONE_PERICOLOSA');
        const frase = formattaMessaggio('AZIONE_PERICOLOSA', null, teamAttacco, teamDifesa, marcatore);

        let tipoEvento = "AZIONE";
        if (frase.includes("Traversa") || frase.includes("palo")) tipoEvento = "PALO_TRAVERSA";
        else if (frase.includes("Parata") || frase.includes("blocca") || frase.includes("nega")) tipoEvento = "PARATA";

        aggiungiCommento(`[${tempo}] ${frase}`, tipoEvento, target);
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

        aggiungiCommento("FISCHIO FINALE: Pareggio! Si va ai calci di rigore.", "FISCHIO", "neutral");

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

    aggiungiCommento(`MATCH CONCLUSO! Vince ${vincitore.nome.toUpperCase()}.`, "INIZIO_FINE_PARTITA", "neutral");

    matchState.matchEnded = true;

    salvaERiprosegui(vincitore, perdente);
}



/* LOGICA LOTTERIA RIGORI */
function avviaRigori() {

    // BLOCCO TOTALE DEL MATCH
    matchState.pause = true;

    matchState.penalties.active = true;
    matchState.penalties.turn = 0;
    matchState.penalties.count = 0;
    matchState.penalties.results = { home: 0, away: 0 };
    matchState.penalties.tiratori = { home: [], away: [] };

    aggiungiCommento("--- INIZIO CALCI DI RIGORE ---", "FISCHIO", "neutral");

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

    // Target
    const target = isHomeTurn ? 'home' : 'away';

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

    const tipoEmoji = segnato ? "RIGORE_OK" : "RIGORE_KO";

    const esitoClasse = categoria === 'SEGNATO' ? 'segnato' : 'sbagliato';

    aggiungiCommento(
        `[RIGORI] ${formattaMessaggio('LOTTERIA_RIGORI', categoria, teamAttacco, teamDifesa, rigorista)}`,
        tipoEmoji,
        target,
        null,
        "LOTTERIA",
        esitoClasse
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

    // variabile di controllo per i rigori ad oltranza
    const fineCoppia = matchState.penalties.turn === 0;

    if (rounds >= 5 && fineCoppia && home !== away) {
        return true;
    }

    return false;
}




/**
 * UI HELPERS
 */

 /* function per renderizzare a icone anche immagini standard (e non solo emoji unicode) */
function renderIconaEvento(valore) {
    // se è un path a immagine
    if (typeof valore === "string" && valore.includes("/")) {
        return `<img src="${valore}" class="icona-evento" alt="">`;
    }

    // altrimenti è una emoji unicode
    return `<span class="emoji-evento">${valore}</span>`;
}


function aggiungiCommento(testo, tipoEvento = "AZIONE", target = 'neutral', iconaForzata = null, contesto = "PARTITA", esitoClasse="") {
    const colonne = {
        home: document.getElementById('col-home'),
        neutral: document.getElementById('col-neutral'),
        away: document.getElementById('col-away')
    };

    // 1. Creiamo il messaggio reale
    const divMessaggio = document.createElement('div');
    divMessaggio.classList.add('commentary-event');

    // Classi grafiche

    // Lotteria rigori
    if (contesto === "LOTTERIA") {
        divMessaggio.classList.add('lotteria-rigori');
        if (esitoClasse) divMessaggio.classList.add(esitoClasse);
    } else {
        // PARTITA NORMALE
        if (tipoEvento === "GOL") divMessaggio.classList.add('goal');
        if (tipoEvento === "RIGORE_KO") divMessaggio.classList.add('rigore-ko');
    }

    if (tipoEvento === "INIZIO_FINE_PARTITA" || tipoEvento === "FISCHIO") divMessaggio.classList.add('info');
    if (tipoEvento === "PARATA") divMessaggio.classList.add('parata');
    if (tipoEvento === "AZIONE") divMessaggio.classList.add('azione');

    const valoreIcona = iconaForzata
        ? EMOJI_EVENTI[iconaForzata]
        : EMOJI_EVENTI[tipoEvento] || EMOJI_EVENTI.AZIONE;

    const iconaHtml = renderIconaEvento(valoreIcona);
    divMessaggio.innerHTML = `${iconaHtml} ${testo}`;

    // 2. Inseriamo il messaggio nella colonna bersaglio
    colonne[target].prepend(divMessaggio);

    // 3. CALCOLO ALTEZZA: Usiamo requestAnimationFrame per essere sicuri che il browser
    // abbia renderizzato il div e ne conosca l'altezza reale (offsetHeight)
    requestAnimationFrame(() => {
        const altezzaReale = divMessaggio.offsetHeight;
        const margineSotto = 15; // Deve corrispondere al margin-bottom del CSS

        // 4. Creiamo gli spacer nelle altre colonne con l'altezza identica al pixel
        Object.keys(colonne).forEach(key => {
            if (key !== target) {
                const spacer = document.createElement('div');
                spacer.classList.add('commentary-spacer');
                spacer.style.height = altezzaReale + "px";
                spacer.style.marginBottom = margineSotto + "px";
                colonne[key].prepend(spacer);
            }
        });
    });
}



function formattaTempo(secondi) {
    const secondiInteri = Math.floor(secondi); // Rimuove i decimali come 184094...
    const m = Math.floor(secondiInteri / 60);
    const s = secondiInteri % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}


function calcolaMinutoUfficiale(secondiEvento) {
    const half = matchState.half;
    const limite = half === 1 ? 45 * 60 : 90 * 60;

    // minuto reale (si conta da 1)
    const minutoBase = Math.floor(secondiEvento / 60) + 1;

    // NON recupero
    if (secondiEvento <= limite) {
        return `${minutoBase}'`;
    }

    // RECUPERO
    const base = half === 1 ? 45 : 90;
    const extra = minutoBase - base;

    return `${base}+${extra}'`;
}

function registraMarcatore(teamKey, nome, secondi, isRigore = false) {
    const lista = marcatori[teamKey];
    const minuto = calcolaMinutoUfficiale(secondi);

    let entry = lista.find(m => m.nome === nome);

    if (!entry) {
        entry = {
            nome,
            gol: [],
            ordine: lista.length
        };
        lista.push(entry);
    }

    entry.gol.push({ minuto, rigore: isRigore });

    renderMarcatori();
}


function renderMarcatori() {
    ['home', 'away'].forEach(teamKey => {
        const container = document.getElementById(`marcatori-${teamKey}`);
        container.innerHTML = ""; // Svuota il div dedicato alla squadra

        // Ordina i marcatori per ordine di inserimento
        marcatori[teamKey]
            .sort((a, b) => a.ordine - b.ordine)
            .forEach(m => {
                const divGiocatore = document.createElement('div');
                divGiocatore.className = "marcatore";

                const minuti = m.gol
                    .map(g => `${g.minuto}${g.rigore ? ' (R)' : ''}`)
                    .join(", ");

                // Allineamento pulito: Minuti e poi Nome (o viceversa per away)
                if (teamKey === 'home') {
                    divGiocatore.innerHTML = `<small>${minuti}</small> <strong>${m.nome}</strong>`;
                } else {
                    divGiocatore.innerHTML = `<strong>${m.nome}</strong> <small>${minuti}</small>`;
                }

                container.appendChild(divGiocatore);
            });
    });
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
        4: "FINALE"
    };
    return nomi[fase] || "FINE";
}