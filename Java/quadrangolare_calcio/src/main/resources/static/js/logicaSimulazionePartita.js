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
    matchEnded: false,
    penalties: {
        active: false,
        turn: 0,
        count: 0,
        results: { home: 0, away: 0 }
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

        matchState.awayTeam.logo = data.away.logo;
        matchState.awayTeam.nome = data.away.nome;

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

function avviaSimulazione() {
    aggiungiCommento("L'arbitro fischia l'inizio della partita!");

    const gameInterval = setInterval(() => {
        if (matchState.matchEnded) {
            clearInterval(gameInterval);
            return;
        }
        tick();
    }, matchState.tickInterval);
}


function tick() {
    if (matchState.matchEnded) return;
    if (matchState.pause) return;

    matchState.currentTime += matchState.tickStep * matchState.speedFactor;

    if (matchState.currentTime >= 45 * 60 && matchState.half === 1) {
        aggiungiCommento("FINE PRIMO TEMPO");
        matchState.half = 2;

        matchState.pause = true;

        setTimeout(() => {
            aggiungiCommento("INIZIO SECONDO TEMPO");
            matchState.pause = false;
        }, 3000);

        return;
    }

    if (matchState.currentTime >= matchState.maxTime) {
        gestisciFinePartita();
        return;
    }


    document.getElementById('tempo-match').innerText = `${matchState.half}T ${formattaTempo(matchState.currentTime)}`;

    // Probabilità evento ridotta
    if (Math.random() < 0.4) {
        generaEvento();
    }
}

function generaEvento() {
    const isHome = Math.random() > 0.5;
    const team = isHome ? matchState.homeTeam : matchState.awayTeam;

    // Semplificazione: 30% probabilità che l'evento sia un GOL
    if (Math.random() < 0.3) {
        team.gol++;
        // Aggiorno Punteggio UI
        document.getElementById('punteggio-live').innerText =
            `${matchState.homeTeam.gol} - ${matchState.awayTeam.gol}`;

        aggiungiCommento(`[${formattaTempo(matchState.currentTime)}] GOAL! Segna la squadra: ${team.nome}!`);
    } else {
        aggiungiCommento(`[${formattaTempo(matchState.currentTime)}] Azione pericolosa per il ${team.nome}... palla fuori!`);
    }
}

function gestisciFinePartita() {
    // 1. Controllo se siamo in una situazione di pareggio al termine dei tempi regolamentari
    if (matchState.homeTeam.gol === matchState.awayTeam.gol && !matchState.penalties.active) {
        aggiungiCommento("FISCHIO FINALE: Pareggio! La qualificazione si deciderà ai calci di rigore.");

        // Mostriamo il box dei rigori nell'HTML
        const boxRigori = document.getElementById('box-rigori');
        if (boxRigori) boxRigori.style.display = 'block';

        // Facciamo partire la sequenza dei rigori
        setTimeout(avviaRigori, 2000);
        return; // ESCIAMO dalla funzione, non dobbiamo salvare nulla ancora!
    }

    // 2. Se arriviamo qui, o la partita è finita con un vincitore nei 90',
    //    o abbiamo appena finito i rigori.
    matchState.matchEnded = true;

    let vincitore, perdente;

    // Determiniamo il vincitore considerando i rigori se sono stati disputati
    if (matchState.penalties.active) {
        const res = matchState.penalties.results;
        if (res.home > res.away) {
            vincitore = matchState.homeTeam;
            perdente = matchState.awayTeam;
        } else {
            vincitore = matchState.awayTeam;
            perdente = matchState.homeTeam;
        }
    } else {
        if (matchState.homeTeam.gol > matchState.awayTeam.gol) {
            vincitore = matchState.homeTeam;
            perdente = matchState.awayTeam;
        } else {
            vincitore = matchState.awayTeam;
            perdente = matchState.homeTeam;
        }
    }

    aggiungiCommento(`MATCH CONCLUSO! ${vincitore.nome.toUpperCase()} passa il turno.`);

    // 3. Ora possiamo finalmente salvare e passare alla fase successiva
    setTimeout(() => {
        salvaERiprosegui(vincitore, perdente);
    }, 3000);
}

/**
 * LOGICA RIGORI
 */
function avviaRigori() {
    matchState.penalties.active = true;
    aggiungiCommento("--- INIZIO CALCI DI RIGORE ---");

    const penaltyInterval = setInterval(() => {
        if (checkFineRigori()) {
            clearInterval(penaltyInterval);
            aggiungiCommento("Lotteria dei rigori conclusa!");

            // Fondamentale: richiamiamo la gestione fine partita
            // che ora troverà i rigori completati e salverà il risultato
            gestisciFinePartita();
            return;
        }
        eseguiTurnoRigore();
    }, 1500);
}

function eseguiTurnoRigore() {
    const isHomeTurn = matchState.penalties.turn === 0;
    const segnato = Math.random() > 0.3; // 70% di probabilità di segnare

    if (segnato) {
        if (isHomeTurn) matchState.penalties.results.home++;
        else matchState.penalties.results.away++;
    }

    const scoreElement = document.getElementById('score-rigori');
    if (scoreElement) {
        scoreElement.innerText = `${matchState.penalties.results.home} - ${matchState.penalties.results.away}`;
    }

    const squadraTira = isHomeTurn ? matchState.homeTeam.nome : matchState.awayTeam.nome;
    aggiungiCommento(`Rigore per ${squadraTira}: ${segnato ? 'RETE! ⚽' : 'PARATO! 🧤'}`);

    matchState.penalties.count++;
    matchState.penalties.turn = isHomeTurn ? 1 : 0; // Alternanza
}

function checkFineRigori() {
    const { home, away } = matchState.penalties.results;
    const count = matchState.penalties.count;

    // Se sono stati tirati almeno 10 rigori (5 a testa) e il punteggio è diverso
    if (count >= 10 && count % 2 === 0 && home !== away) {
        return true;
    }

    // Ad oltranza (dopo i primi 10) continua finché uno segna e l'altro sbaglia nello stesso turno
    if (count > 10 && count % 2 === 0 && home !== away) {
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

    div.innerText = testo;

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

    // Salviamo chi ha vinto e chi ha perso per calcolare Finale e Finalina
    if (stato.faseAttuale === 1) stato.risultati.semi1 = { vincente : vincitore, perdente };
    if (stato.faseAttuale === 2) stato.risultati.semi2 = { vincente : vincitore, perdente };

    // Incrementiamo la fase (es. da 1 a 2)
    stato.faseAttuale++;
    sessionStorage.setItem('statoTorneo', JSON.stringify(stato));

    if (stato.faseAttuale <= 4) {
        alert("Partita terminata! Prossima fase: " + getNomeFase(stato.faseAttuale));
        // Ricaricando la pagina, il switch-case in match-simulato.html
        // leggerà la nuova faseAttuale e caricherà le squadre corrette
        window.location.reload();
    } else {
        alert("Torneo Concluso! Vai alla premiazione.");
        window.location.href = "/archivio"; // O dove preferisci mandare l'utente
    }
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