// LOGICA PER SIMULARE PARTITE LATO BACKEND

const matchState = {
    tipoPartita: "",
    homeTeam: {
        nome: "",
        gol: 0,
        giocatori: []
    },
    awayTeam: {
        nome: "",
        gol: 0,
        giocatori: []
    },
    currentTime: 0,      // Tempo in secondi
    maxTime: 300,        // 5 minuti di partita (2.5 min per tempo)
    matchEnded: false,
    penalties: {
        active: false,
        turn: 0,         // 0 per Home, 1 per Away
        count: 0,        // Totale rigori calciati
        results: { home: 0, away: 0 }
    }
};


function tick() {
    if (matchState.matchEnded) return;

    if (matchState.currentTime >= matchState.maxTime) {
        gestisciFinePartita();
        return;
    }

    // Avanzamento tempo (simulo 10 secondi di gioco ogni secondo reale)
    matchState.currentTime += 10;

    // Probabilità che accada un evento (60%)
    if (Math.random() < 0.6) {
        generaEvento();
    } else {
        console.log(`[${formattaTempo(matchState.currentTime)}] Gioco a centrocampo tra ${matchState.homeTeam.nome} e ${matchState.awayTeam.nome}...`);
    }
}

/**
 * GENERATORE EVENTI (Tiri o Falli)
 */
function generaEvento() {
    const isHomeAttacking = Math.random() < 0.5;
    const teamAttacco = isHomeAttacking ? matchState.homeTeam : matchState.awayTeam;
    const giocatore = teamAttacco.giocatori[Math.floor(Math.random() * teamAttacco.giocatori.length)];
    const tempoCorrente = formattaTempo(matchState.currentTime);

    // Estrazione tra Azione di Tiro (70%) o Fallo (30%)
    if (Math.random() < 0.7) {
        logicaTiro(teamAttacco, giocatore, tempoCorrente);
    } else {
        logicaFallo(teamAttacco, giocatore, tempoCorrente);
    }
}

/**
 * LOGICA TIRI (Range 0-100)
 */
function logicaTiro(squadra, player, tempo) {
    const x = Math.floor(Math.random() * 101);
    let msg = `[${tempo}] AZIONE: ${player} (${squadra.nome}) si invola verso la porta... `;

    if (x <= 50) {
        msg += "conclusione sballata, palla fuori!";
    } else if (x <= 65) {
        msg += "tiro centrale, il portiere blocca a terra.";
    } else if (x <= 80) {
        msg += "mira l'incrocio! Il portiere devia miracolosamente in angolo.";
    } else {
        msg += "GOAL!!! Palla nel sacco, non può nulla il portiere!";
        squadra.gol++;
        aggiornaTabellino();
    }
    console.log(msg);
}

/**
 * LOGICA FALLI (Range 0-100)
 */
function logicaFallo(squadraAttacco, player, tempo) {
    const x = Math.floor(Math.random() * 101);
    let msg = `[${tempo}] FALLO: Intervento duro su ${player}... `;

    if (x <= 40) {
        msg += "L'arbitro fischia la punizione ma non estrae cartellini.";
    } else if (x <= 75) {
        msg += "AMMONIZIONE! Cartellino giallo per il difensore.";
    } else if (x <= 90) {
        msg += "ESPULSIONE! Rosso diretto, fallo bruttissimo!";
    } else {
        msg += "CALCIO DI RIGORE! L'arbitro indica il dischetto!";
        console.log(msg);
        const segnato = simulaSingoloCalcioRigore(player, squadraAttacco);
        if (segnato) {
            squadraAttacco.gol++;
            aggiornaTabellino();
        }
        return;
    }
    console.log(msg);
}

/**
 * LOGICA SINGOLO RIGORE (Range 0-10) - Usata sia in partita che ai rigori finali
 */
function simulaSingoloCalcioRigore(player, squadra) {
    const x = Math.floor(Math.random() * 11);
    let msg = `[RIGORE] ${player} (${squadra.nome}) prende la rincorsa... `;

    if (x <= 6) {
        console.log(msg + "RETE! Gol impeccabile.");
        return true;
    } else if (x === 7) {
        console.log(msg + "PARATO! Il portiere blocca il pallone.");
    } else if (x === 8) {
        console.log(msg + "PALO! La palla rimbalza e finisce fuori.");
    } else if (x === 9) {
        console.log(msg + "FUORI! Rigore calciato malissimo.");
    } else if (x === 10) {
        console.log(msg + "NON VALIDO! L'arbitro fa ripetere il tiro...");
        return simulaSingoloCalcioRigore(player, squadra); // Ricorsione
    }
    return false;
}

/**
 * GESTIONE FINE PARTITA E RIGORI FINALI
 */
function gestisciFinePartita() {
    matchState.matchEnded = true;
    console.warn(`FINISCONO I TEMPI REGOLAMENTARI: ${matchState.homeTeam.gol}-${matchState.awayTeam.gol}`);

    if (matchState.homeTeam.gol === matchState.awayTeam.gol) {
        console.log("SI VA AI CALCI DI RIGORE PER DECIDERE IL VINCITORE!");
        avviaRigoriFinali();
    } else {
        console.log("FISCHIO FINALE! Vince il " + (matchState.homeTeam.gol > matchState.awayTeam.gol ? matchState.homeTeam.nome : matchState.awayTeam.nome));
    }
}

function avviaRigoriFinali() {
    matchState.penalties.active = true;
    // Sorteggio chi inizia (0 o 1)
    matchState.penalties.turn = Math.random() < 0.5 ? 0 : 1;

    console.log(`Inizia la serie dei rigori. Tirerà per prima: ${matchState.penalties.turn === 0 ? matchState.homeTeam.nome : matchState.awayTeam.nome}`);

    const penaltyTimer = setInterval(() => {
        eseguiTurnoRigore();

        if (checkFineRigori()) {
            clearInterval(penaltyTimer);
            const vincitore = matchState.penalties.results.home > matchState.penalties.results.away ? matchState.homeTeam.nome : matchState.awayTeam.nome;
            console.warn(`FINE RIGORI! Risultato finale: ${matchState.penalties.results.home}-${matchState.penalties.results.away}. Vince il ${vincitore}!`);
        }
    }, 1500);
}

function eseguiTurnoRigore() {
    const isHomeTurn = matchState.penalties.turn === 0;
    const squadra = isHomeTurn ? matchState.homeTeam : matchState.awayTeam;
    const tiratore = squadra.giocatori[Math.floor(Math.random() * squadra.giocatori.length)];

    const segnato = simulaSingoloCalcioRigore(tiratore, squadra);

    if (segnato) {
        if (isHomeTurn) matchState.penalties.results.home++;
        else matchState.penalties.results.away++;
    }

    matchState.penalties.count++;
    matchState.penalties.turn = isHomeTurn ? 1 : 0; // Cambio turno alternato
}

function checkFineRigori() {
    const { home, away } = matchState.penalties.results;
    const count = matchState.penalties.count;

    // 1. Fase dei primi 10 rigori (5 a testa)
    if (count < 10) {
        const tiriRimastiHome = 5 - (matchState.penalties.turn === 0 ? Math.floor(count/2) : Math.ceil(count/2));
        const tiriRimastiAway = 5 - (matchState.penalties.turn === 1 ? Math.floor(count/2) : Math.ceil(count/2));

        if (home > away + tiriRimastiAway || away > home + tiriRimastiHome) return true;
    }
    // 2. Oltranza (dopo i 10 tiri, solo se count è pari per parità di turni)
    else if (count % 2 === 0 && home !== away) {
        return true;
    }
    return false;
}

/**
 * UTILS
 */
function formattaTempo(secondi) {
    const min = Math.floor(secondi / 60);
    const sec = secondi % 60;
    return `${min}:${sec < 10 ? '0' : ''}${sec}`;
}

function aggiornaTabellino() {
    console.log(`%c PUNTEGGIO: ${matchState.homeTeam.nome} ${matchState.homeTeam.gol} - ${matchState.awayTeam.gol} ${matchState.awayTeam.nome} `, "background: #222; color: #bada55; font-weight: bold;");
}

// AVVIO SIMULAZIONE (Esegue un tick ogni 1 secondo reale)
const gameInterval = setInterval(tick, 1000);