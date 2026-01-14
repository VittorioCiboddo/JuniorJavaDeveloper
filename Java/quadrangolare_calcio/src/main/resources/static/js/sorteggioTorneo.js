let squadreDisponibili = [];
let contatoreEstratti = 0;
let partecipanti = [];
const TOTAL_CARD_SPACE = 200; // 180px card + 10px margin left + 10px margin right

document.addEventListener("DOMContentLoaded", function() {
    // IMPORTANTE: listaSquadreIniziale contiene già TUTTO dal DB (giocatori inclusi)
    if (typeof listaSquadreIniziale !== 'undefined' && listaSquadreIniziale !== null) {
        squadreDisponibili = listaSquadreIniziale.map(s => ({
            id: s.idSquadra,
            nome: s.nome,
            logo: s.logo,
            giocatori: s.giocatori || [] // <--- QUI NASCE IL DATO
        }));
    }
    console.log("Squadre caricate con giocatori:", squadreDisponibili);
});

function avviaSorteggio() {
    if (squadreDisponibili.length === 0) return;

    const btn = document.getElementById('btn-estrai');
    const selectorLine = document.querySelector('.selector-line');
    const carousel = document.getElementById('carousel-inner');
    const viewport = document.querySelector('.carousel-viewport');
    const msgErrore = document.getElementById('messaggio-errore');

    if(msgErrore) msgErrore.style.display = 'none';
    btn.disabled = true;
    selectorLine.style.display = 'block';

    // Gestione dell'ultima squadra rimasta
    if (squadreDisponibili.length === 1) {
        setTimeout(() => {
            riempiSlot(squadreDisponibili[0]);
            squadreDisponibili = [];
            btn.style.display = 'none';
            document.getElementById('btn-partita').style.display = 'block';
            selectorLine.style.display = 'none';
        }, 600);
        return;
    }

    /* --- LOGICA DI TRANSIZIONE LAYOUT --- */
    // Disattiviamo la centratura Flexbox del CSS per passare al posizionamento assoluto
    carousel.style.transition = 'none';
    carousel.style.justifyContent = 'flex-start';
    carousel.style.position = 'absolute';
    carousel.style.width = 'auto';
    carousel.style.left = '0px';
    /* ------------------------------------ */

    // 1. Rigenerazione carosello (creazione del nastro lungo per lo scorrimento)
    carousel.innerHTML = '';
    const ripetizioni = 15;
    for (let i = 0; i < ripetizioni; i++) {
        squadreDisponibili.forEach(s => {
            const div = document.createElement('div');
            div.className = 'card-sorteggio';
            div.setAttribute('data-id', s.id);
            div.setAttribute('data-nome', s.nome);
            div.setAttribute('data-logo', s.logo);
            // pointer-events: none evita che le immagini blocchino il sensore di collisione
            div.innerHTML = `<img src="${s.logo}" style="pointer-events:none; width:120px;"><p style="pointer-events:none;">${s.nome}</p>`;
            carousel.appendChild(div);
        });
    }

    // 2. Calcolo posizione finale di arresto
    const centroVetrina = viewport.offsetWidth / 2;
    const minScroll = (squadreDisponibili.length * 10) * TOTAL_CARD_SPACE;
    const maxScroll = (squadreDisponibili.length * 13) * TOTAL_CARD_SPACE;
    const stopPixel = Math.floor(Math.random() * (maxScroll - minScroll)) + minScroll;
    const finalLeft = -(stopPixel - centroVetrina);

    // Avvio animazione con un piccolo delay per permettere al browser di recepire il cambio layout
    setTimeout(() => {
        carousel.style.transition = 'left 4s cubic-bezier(0.15, 0, 0.15, 1)';
        carousel.style.left = finalLeft + 'px';
    }, 50);

    // 3. Rilevamento della squadra sotto il selettore rosso
    setTimeout(() => {
        const selectorRect = selectorLine.getBoundingClientRect();
        const selectorX = selectorRect.left + selectorRect.width / 2;

        const cards = document.querySelectorAll('.card-sorteggio');
        let cardSelezionata = null;

        cards.forEach(card => {
            const rect = card.getBoundingClientRect();
            // Controlla se il centro del selettore cade dentro i confini della card
            if (selectorX >= rect.left && selectorX <= rect.right) {
                cardSelezionata = card;
            }
        });

        if (cardSelezionata) {

            const idVinta = cardSelezionata.getAttribute('data-id');
            const vinta = squadreDisponibili.find(s => s.id == idVinta);


            riempiSlot(vinta);
            // Rimuoviamo la squadra estratta da quelle disponibili per il prossimo turno
            squadreDisponibili = squadreDisponibili.filter(s => s.id != vinta.id);

            if (squadreDisponibili.length === 1) {
                btn.innerText = "Rivela ultima squadra";
            }
            btn.disabled = false;
        } else {
            if (msgErrore) msgErrore.style.display = 'block';
            btn.disabled = false;
        }
    }, 4500); // 4000ms di transizione + 500ms di margine
}

function riempiSlot(squadra) {
    contatoreEstratti++;
    const slot = document.getElementById(`slot-${contatoreEstratti}`);

    partecipanti.push({ ...squadra });

    if (slot) {
        slot.innerHTML = `<img src="${squadra.logo}" style="width:70px; height:70px; object-fit:contain;"><br><b>${squadra.nome}</b>`;
        slot.classList.add('estratta');
    }

    // Dentro riempiSlot, quando contatoreEstratti === 4
    if (contatoreEstratti === 4) {
        document.getElementById('btn-estrai').style.display = 'none';
        const btnPartita = document.getElementById('btn-partita');
        btnPartita.style.display = 'block';

        const statoTorneo = {
            faseAttuale: 1,
            squadre: partecipanti,
            risultati: { semi1: null, semi2: null }
        };
        sessionStorage.setItem('statoTorneo', JSON.stringify(statoTorneo));

        // CHIAMATA ALLA TUA FUNZIONE
        preparaMatch(1); // Inizializza la Semifinale 1
    }


    function preparaMatch(numeroMatch) {
        let matchData = {};

        if (numeroMatch === 1) {
            // Semifinale 1: Slot 1 vs Slot 2
            matchData = {
                tipo: "SEMIFINALE 1",
                home: partecipanti[0], // Squadra nello slot 1
                away: partecipanti[1]  // Squadra nello slot 2
            };
        } else if (numeroMatch === 2) {
            // Semifinale 2: Slot 3 vs Slot 4
            matchData = {
                tipo: "SEMIFINALE 2",
                home: partecipanti[2], // Squadra nello slot 3
                away: partecipanti[3]  // Squadra nello slot 4
            };
        }

        // Salva l'oggetto nel sessionStorage per match-simulato.html
        sessionStorage.setItem('matchCorrente', JSON.stringify(matchData));
    }
}