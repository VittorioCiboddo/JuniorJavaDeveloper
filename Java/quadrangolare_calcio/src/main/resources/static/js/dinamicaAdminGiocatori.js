document.addEventListener("DOMContentLoaded", function () {

    const squadraSelect = document.getElementById("squadraSelect");
    const moduloContainer = document.getElementById("moduloContainer");
    const moduloSelect = document.getElementById("moduloSelect");
    const ruoloContainer = document.getElementById("ruoloContainer");
    const ruoloSelect = document.getElementById("ruoloSelect");
    const campiPersonali = document.getElementById("campiPersonali");
    const categoria = document.querySelector("input[name='categoria']").value;

    // Reset iniziale
    moduloContainer.style.display = "none";
    ruoloContainer.style.display = "none";
    campiPersonali.style.display = "none";

    squadraSelect.addEventListener("change", function () {
        const squadraId = this.value;
        if (squadraId) {
            // Chiamata AJAX per ottenere il modulo della squadra
            fetch(`/api/squadra/${squadraId}/modulo`)
                .then(response => response.json())
                .then(modulo => {
                    moduloContainer.style.display = "block";
                    moduloSelect.innerHTML = `<option value="${modulo.id}">${modulo.nome}</option>`;
                });

            // Reset successivi
            ruoloContainer.style.display = "none";
            ruoloSelect.innerHTML = "";
            campiPersonali.style.display = "none";
        } else {
            moduloContainer.style.display = "none";
            ruoloContainer.style.display = "none";
            campiPersonali.style.display = "none";
        }
    });

    moduloSelect.addEventListener("change", function () {
        const moduloId = this.value;
        if (moduloId) {
            // Carica i ruoli disponibili per quella categoria e quel modulo
            fetch(`/api/ruoliDisponibili?categoria=${categoria}&moduloId=${moduloId}`)
                .then(response => response.json())
                .then(ruoli => {
                    ruoloContainer.style.display = "block";
                    ruoloSelect.innerHTML = "<option value=''>Scegli il ruolo</option>";
                    ruoli.forEach(ruolo => {
                        const option = document.createElement("option");
                        option.value = ruolo.id;
                        option.textContent = `${ruolo.descrizione} (${ruolo.sigla})`;
                        ruoloSelect.appendChild(option);
                    });
                });
        }
    });

    ruoloSelect.addEventListener("change", function () {
        if (this.value) {
            campiPersonali.style.display = "block";
        } else {
            campiPersonali.style.display = "none";
        }
    });
});
