document.addEventListener("DOMContentLoaded", function () {
    const squadraSelect = document.getElementById('squadraSelect');
    const moduloSelect = document.getElementById('moduloSelect');
    const tipologiaSelect = document.getElementById('tipologiaSelect');
    const ruoloSelect = document.getElementById('ruoloSelect');
    const otherFields = document.querySelectorAll(
        'input[type="text"], input[type="file"], input[type="number"], input[type="date"], select[name="nazionalita"], textarea'
    );
    const submitButton = document.querySelector('input[type="submit"]');

    let moduloBloccato = false;
    let tipologiaBloccata = false;

    const selectedSquadraId = document.body.getAttribute('data-selected-squadra-id');
    const selectedModuloId = document.body.getAttribute('data-selected-modulo-id');

    // STEP 5 - Se siamo al giocatore successivo
    if (selectedSquadraId && selectedModuloId) {
        squadraSelect.value = selectedSquadraId;
        squadraSelect.disabled = true;

        moduloSelect.innerHTML = '';
        const option = document.createElement('option');
        option.value = selectedModuloId;
        option.text = 'Caricamento modulo...';
        moduloSelect.appendChild(option);
        moduloSelect.disabled = true;
        setVisible(moduloSelect);

        fetch(`/registra-giocatori/getModuloPerSquadra/${selectedSquadraId}`)
            .then(response => response.json())
            .then(data => {
                if (data.modulo) {
                    moduloSelect.innerHTML = '';
                    const fixedOption = document.createElement('option');
                    fixedOption.value = data.modulo.idModulo;
                    fixedOption.text = data.modulo.schemaGioco;
                    moduloSelect.appendChild(fixedOption);
                }

                setVisible(tipologiaSelect);
                tipologiaSelect.innerHTML = `<option value="">Scegli la tipologia di giocatore</option>`;

                fetch(`/registra-giocatori/getTipologiePerModulo/${selectedModuloId}`)
                    .then(response => response.json())
                    .then(data => {
                        if (data.tipologie) {
                            const ordine = ["Portiere", "Difensore", "Centrocampista", "Attaccante"];
                            const tipologieOrdinate = ordine
                                .map(cat => data.tipologie.find(t => t.categoria === cat))
                                .filter(Boolean);

                            tipologieOrdinate.forEach(t => {
                                const option = document.createElement('option');
                                option.value = t.idTipologia;
                                option.text = t.categoria;
                                option.dataset.categoria = t.categoria;
                                tipologiaSelect.appendChild(option);
                            });

                            fetch(`/registra-giocatori/getCategorieDisponibili/${selectedSquadraId}/${selectedModuloId}`)
                                .then(res => res.json())
                                .then(catData => {
                                    catData.categorieDisponibili.forEach(cat => {
                                        const option = Array.from(tipologiaSelect.options).find(
                                            opt => opt.text === cat.categoria
                                        );
                                        if (cat.completata && option) {
                                            option.disabled = true;
                                            option.text += " (completata)";
                                        }
                                    });
                                    tipologiaSelect.disabled = false;
                                });
                        }
                    });
            });
    } else {
        // Primo giocatore - visibilità iniziale
        setInvisible(moduloSelect);
        setInvisible(tipologiaSelect);
        setInvisible(ruoloSelect);
        otherFields.forEach(f => {
            setInvisible(f);
            f.disabled = true;
        });
        setInvisible(submitButton);
    }

    // STEP 1 - Squadra scelta
    squadraSelect.addEventListener('change', function () {
        if (moduloBloccato) return;

        const squadraId = this.value;
        if (!squadraId) return;

        setVisible(moduloSelect);
        moduloSelect.innerHTML = `<option value="">Scegli il modulo di gioco</option>`;
        moduloSelect.disabled = false;

        fetch(`/registra-giocatori/getModuloPerSquadra/${squadraId}`)
            .then(response => response.json())
            .then(data => {
                if (data.modulo) {
                    const option = document.createElement('option');
                    option.value = data.modulo.idModulo;
                    option.text = data.modulo.schemaGioco;
                    moduloSelect.appendChild(option);
                }
            });
    });

    // STEP 2 - Modulo scelto
    moduloSelect.addEventListener('change', function () {
        const moduloId = this.value;
        if (!moduloId) return;

        moduloBloccato = true;
        squadraSelect.disabled = true;
        moduloSelect.disabled = true;

        tipologiaSelect.innerHTML = `<option value="">Scegli la tipologia di giocatore</option>`;
        setVisible(tipologiaSelect);
        tipologiaSelect.disabled = true;

        fetch(`/registra-giocatori/getTipologiePerModulo/${moduloId}`)
            .then(response => response.json())
            .then(data => {
                if (data.tipologie) {
                    const ordine = ["Portiere", "Difensore", "Centrocampista", "Attaccante"];
                    const tipologieOrdinate = ordine
                        .map(cat => data.tipologie.find(t => t.categoria === cat))
                        .filter(Boolean);

                    tipologieOrdinate.forEach(t => {
                        const option = document.createElement('option');
                        option.value = t.idTipologia;
                        option.text = t.categoria;
                        option.dataset.categoria = t.categoria;
                        tipologiaSelect.appendChild(option);
                    });

                    const squadraId = squadraSelect.value;
                    fetch(`/registra-giocatori/getCategorieDisponibili/${squadraId}/${moduloId}`)
                        .then(res => res.json())
                        .then(catData => {
                            catData.categorieDisponibili.forEach(cat => {
                                const option = Array.from(tipologiaSelect.options).find(
                                    opt => opt.text === cat.categoria
                                );
                                if (cat.completata && option) {
                                    option.disabled = true;
                                    option.text += " (completata)";
                                }
                            });
                            tipologiaSelect.disabled = false;
                        });
                }
            });
    });

    // STEP 3 - Tipologia scelta
    tipologiaSelect.addEventListener('change', function () {
        if (tipologiaBloccata) return;

        const tipologiaId = this.value;
        const tipologiaText = this.options[this.selectedIndex].text;
        const moduloId = moduloSelect.value;
        if (!tipologiaId || !moduloId) return;

        ruoloSelect.innerHTML = `<option value="">Scegli il ruolo del giocatore</option>`;
        setVisible(ruoloSelect);
        ruoloSelect.disabled = true;

        fetch(`/registra-giocatori/getRuoliDisponibili/${moduloId}`)
            .then(response => response.json())
            .then(data => {
                if (data.ruoli) {
                    const ruoliFiltrati = data.ruoli.filter(
                        r => r.tipologia.categoria === tipologiaText
                    );

                    ruoliFiltrati.forEach(ruolo => {
                        const option = document.createElement('option');
                        option.value = ruolo.idRuolo;
                        option.text = `${ruolo.sigla} - ${ruolo.descrizione}`;
                        if (ruolo.giocatoreRegistrato) {
                            option.disabled = true;
                            option.text += " (già assegnato)";
                        }
                        ruoloSelect.appendChild(option);
                    });

                    ruoloSelect.disabled = false;
                }
            });
    });

    // STEP 4 - Ruolo scelto
    ruoloSelect.addEventListener('change', function () {
        const ruoloId = this.value;
        if (!ruoloId) return;

        tipologiaBloccata = true;
        tipologiaSelect.disabled = true;
        ruoloSelect.disabled = true;

        otherFields.forEach(f => {
            setVisible(f);
            f.disabled = false;
        });
        setVisible(submitButton);
    });

    function setVisible(el) {
        el.style.visibility = "visible";
    }

    function setInvisible(el) {
        el.style.visibility = "hidden";
    }
});
