document.getElementById('squadraSelect').addEventListener('change', function() {
    var squadraId = this.value;

    // Disabilitare il select della squadra una volta che è stato scelto
    document.getElementById('squadraSelect').disabled = true;
    document.getElementById('moduloSelect').disabled = false;

    // Reset e popolare la lista dei moduli
    fetch(`/getModuloPerSquadra/${squadraId}`)
        .then(response => response.json())
        .then(data => {
            let moduloSelect = document.getElementById('moduloSelect');
            moduloSelect.innerHTML = ''; // Pulisce le vecchie opzioni

            // Assicurati che i dati contengano un modulo
            if (data.modulo) {
                let option = document.createElement('option');
                option.value = data.modulo.id;
                option.text = data.modulo.schemaGioco; // Supponendo che 'schemaGioco' sia il nome del modulo
                moduloSelect.appendChild(option);
            }

            // Abilita il campo Tipologia
            document.getElementById('tipologiaSelect').disabled = false;
        })
        .catch(error => console.error('Error:', error));
});

// Aggiungi un event listener per il modulo per abilitare la tipologia
document.getElementById('moduloSelect').addEventListener('change', function() {
    var moduloId = this.value;

    // Abilita il select della tipologia dopo che è stato selezionato un modulo
    document.getElementById('tipologiaSelect').disabled = false;

    // Carica le tipologie per il modulo selezionato
    fetch(`/getTipologiePerModulo/${moduloId}`)
        .then(response => response.json())
        .then(data => {
            let tipologiaSelect = document.getElementById('tipologiaSelect');
            tipologiaSelect.innerHTML = ''; // Pulisce le vecchie opzioni

            // Aggiungi le nuove tipologie
            if (data.tipologie) {
                data.tipologie.forEach(tipologia => {
                    let option = document.createElement('option');
                    option.value = tipologia.id;
                    option.text = tipologia.categoria; // Supponendo che 'categoria' sia il nome della tipologia
                    tipologiaSelect.appendChild(option);
                });
            }
        })
        .catch(error => console.error('Error:', error));

    // Carica i ruoli per il modulo selezionato
    fetch(`/getRuoliDisponibili/${moduloId}`)
        .then(response => response.json())
        .then(data => {
            const ruoloSelect = document.getElementById('ruoloSelect');
            ruoloSelect.innerHTML = ''; // Reset ruoli

            // Aggiungi i ruoli disponibili e disabilita quelli già assegnati
            if (data.ruoli) {
                data.ruoli.forEach(ruolo => {
                    let option = document.createElement('option');
                    option.value = ruolo.id;
                    option.text = `${ruolo.descrizione} (${ruolo.sigla})`;

                    // Se il ruolo è già assegnato (giocatore registrato), disabilitalo
                    if (ruolo.giocatoreRegistrato) {
                        option.disabled = true;
                    }
                    ruoloSelect.appendChild(option);
                });
            }
        })
        .catch(error => console.error('Error:', error));
});
