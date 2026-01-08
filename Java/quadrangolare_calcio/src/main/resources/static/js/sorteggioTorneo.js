let squadreDisponibili = [];
let contatoreEstratti = 0;
const TOTAL_CARD_SPACE = 200; // 180px + 10px + 10px

document.addEventListener("DOMContentLoaded", function() {
    const cardsIniziali = document.querySelectorAll('.card-sorteggio');
    cardsIniziali.forEach(c => {
        squadreDisponibili.push({
            id: c.getAttribute('data-id'),
            nome: c.getAttribute('data-nome'),
            logo: c.getAttribute('data-logo')
        });
    });
    console.log("Squadre pronte:", squadreDisponibili);
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

    // 1. Rigenerazione carosello
    carousel.innerHTML = '';
    const ripetizioni = 15;
    for (let i = 0; i < ripetizioni; i++) {
        squadreDisponibili.forEach(s => {
            const div = document.createElement('div');
            div.className = 'card-sorteggio';
            div.setAttribute('data-id', s.id);
            div.setAttribute('data-nome', s.nome);
            div.setAttribute('data-logo', s.logo);
            // pointer-events: none è fondamentale per non bloccare il raggio del sensore
            div.innerHTML = `<img src="${s.logo}" style="pointer-events:none; width:120px;"><p style="pointer-events:none;">${s.nome}</p>`;
            carousel.appendChild(div);
        });
    }

    // 2. Calcolo posizione
    const centroVetrina = viewport.offsetWidth / 2;
    const minScroll = (squadreDisponibili.length * 10) * TOTAL_CARD_SPACE;
    const maxScroll = (squadreDisponibili.length * 13) * TOTAL_CARD_SPACE;
    const stopPixel = Math.floor(Math.random() * (maxScroll - minScroll)) + minScroll;
    const finalLeft = -(stopPixel - centroVetrina);

    carousel.style.transition = 'none';
    carousel.style.left = '0px';

    setTimeout(() => {
        carousel.style.transition = 'left 4s cubic-bezier(0.15, 0, 0.15, 1)';
        carousel.style.left = finalLeft + 'px';
    }, 50);

    // 3. RILEVAMENTO CON TOLLERANZA
    setTimeout(() => {
        const selectorRect = selectorLine.getBoundingClientRect();
        const selectorX = selectorRect.left + selectorRect.width / 2;

        const cards = document.querySelectorAll('.card-sorteggio');
        let cardSelezionata = null;

        cards.forEach(card => {
            const rect = card.getBoundingClientRect();
            if (selectorX >= rect.left && selectorX <= rect.right) {
                cardSelezionata = card;
            }
        });

        if (cardSelezionata) {
            const vinta = {
                id: cardSelezionata.getAttribute('data-id'),
                nome: cardSelezionata.getAttribute('data-nome'),
                logo: cardSelezionata.getAttribute('data-logo')
            };

            console.log("Squadra estratta:", vinta.nome);
            riempiSlot(vinta);
            squadreDisponibili = squadreDisponibili.filter(s => s.id != vinta.id);

            if (squadreDisponibili.length === 1) {
                btn.innerText = "Rivela ultima squadra";
            }
            btn.disabled = false;
        } else {
            console.log("Caduto nello spazio bianco");
            if (msgErrore) msgErrore.style.display = 'block';
            btn.disabled = false;
        }
    }, 4500);

}

function riempiSlot(squadra) {
    contatoreEstratti++;
    const slot = document.getElementById(`slot-${contatoreEstratti}`);
    if (slot) {
        slot.innerHTML = `<img src="${squadra.logo}" style="width:70px; height:70px; object-fit:contain;"><br><b>${squadra.nome}</b>`;
        slot.classList.add('estratta');
    }
}