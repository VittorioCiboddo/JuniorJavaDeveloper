document.addEventListener("DOMContentLoaded", function () {
    const squadraSelect = document.getElementById("squadraSelect");
    const moduloContainer = document.getElementById("moduloContainer");
    const moduloSelect = document.getElementById("moduloSelect");
    const ruoloContainer = document.getElementById("ruoloContainer");
    const ruoloSelect = document.getElementById("ruoloSelect");
    const campiPersonali = document.getElementById("campiPersonali");
    const categoria = document.getElementById("categoriaInput").value;

    squadraSelect.addEventListener("change", function () {
        const squadraId = this.value;
        if (!squadraId) return;

        // Reset
        hide(moduloContainer);
        hide(ruoloContainer);
        hide(campiPersonali);
        moduloSelect.innerHTML = '';
        ruoloSelect.innerHTML = '';

        // Fetch modulo
        fetch(`/api/squadra/${squadraId}/modulo`)
            .then(response => response.json())
            .then(modulo => {
                if (!modulo || !modulo.idModulo) {
                    alert("Modulo non disponibile per questa squadra.");
                    return;
                }

                moduloSelect.innerHTML = `<option value="${modulo.idModulo}">${modulo.schemaGioco}</option>`;
                show(moduloContainer);
                moduloSelect.disabled = true;

                // Fetch ruoli
                fetch(`/api/ruoliDisponibili?categoria=${categoria}&moduloId=${modulo.idModulo}`)
                    .then(response => response.json())
                    .then(ruoli => {
                        ruoloSelect.innerHTML = "<option value=''>Scegli il ruolo</option>";

                        ruoli.forEach(ruolo => {
                            const option = document.createElement("option");
                            option.value = ruolo.idRuolo;
                            option.textContent = `${ruolo.descrizione} (${ruolo.sigla})`;
                            if (ruolo.giocatoreRegistrato) {
                                option.disabled = true;
                                option.textContent += " (già assegnato)";
                            }
                            ruoloSelect.appendChild(option);
                        });

                        show(ruoloContainer);
                    });
            });
    });

    ruoloSelect.addEventListener("change", function () {
        if (this.value) {
            show(campiPersonali);
        } else {
            hide(campiPersonali);
        }
    });

    function show(el) {
        el.classList.add("visibile");
    }

    function hide(el) {
        el.classList.remove("visibile");
    }
});

