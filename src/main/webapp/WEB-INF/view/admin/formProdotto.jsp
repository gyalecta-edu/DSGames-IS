<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle"
       value="${empty prodotto ? 'Nuovo prodotto' : 'Modifica prodotto'} — Admin"/>
<%@ include file="../common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <nav style="font-size:0.85rem; color:#666; margin-bottom:1.5rem;">
        <a href="${pageContext.request.contextPath}/admin/prodotti">
            Gestione prodotti
        </a> /
        <span>${fn:escapeXml(empty prodotto ? 'Nuovo prodotto' : 'Modifica: '.concat(prodotto.nome))}</span>
    </nav>

    <h1 style="font-size:1.4rem; font-weight:700; margin-bottom:1.5rem;">
        ${empty prodotto ? '➕ Nuovo prodotto' : '✏️ Modifica prodotto'}
    </h1>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <div style="background:white; border-radius:var(--radius);
                box-shadow:var(--shadow); padding:2rem; max-width:700px;">

        <form id="formProdottoAdmin" method="post"
              action="${pageContext.request.contextPath}/admin/prodotti">
        <input type="hidden" name="token" value="${sessionScope.token}">

            <c:if test="${not empty prodotto}">
                <input type="hidden" name="id" value="${prodotto.id}">
            </c:if>

            <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">

                <div class="form-group" style="grid-column:1/-1;">
                    <label for="nome">Nome prodotto *</label>
                    <input type="text" id="nome" name="nome"
                           placeholder="Es. PlayStation 5"
                           value="${fn:escapeXml(prodotto.nome)}">
                    <div class="form-error" id="nome-error"></div>
                </div>

                <div class="form-group">
                    <label for="categoria">Categoria *</label>
                    <select id="categoria" name="categoria">
                        <option value="">-- Seleziona --</option>
                        <option value="CONSOLE"
                            ${'CONSOLE' == prodotto.categoria ? 'selected' : ''}>
                            Console
                        </option>
                        <option value="VIDEOGIOCO"
                            ${'VIDEOGIOCO' == prodotto.categoria ? 'selected' : ''}>
                            Videogioco
                        </option>
                    </select>
                    <div class="form-error" id="categoria-error"></div>
                </div>

                <div class="form-group">
                    <label for="piattaforma">Piattaforma</label>
                    <input type="text" id="piattaforma" name="piattaforma"
                           placeholder="Es. PlayStation 5"
                           value="${fn:escapeXml(prodotto.piattaforma)}">
                </div>

                <div class="form-group">
                    <label for="prezzo">Prezzo (€) *</label>
                    <input type="number" id="prezzo" name="prezzo"
                           step="0.01" min="0"
                           placeholder="Es. 59.99"
                           value="${prodotto.prezzo}">
                    <div class="form-error" id="prezzo-error"></div>
                </div>

                <div class="form-group">
                    <label for="quantita">Quantità disponibile</label>
                    <input type="number" id="quantita" name="quantita"
                           min="0" placeholder="0"
                           value="${prodotto.quantita}">
                </div>

                <div class="form-group" style="grid-column:1/-1;">
                    <label for="descrizione">Descrizione</label>
                    <textarea id="descrizione" name="descrizione"
                              rows="4"
                              placeholder="Descrizione del prodotto..."
                              style="resize:vertical;">${fn:escapeXml(prodotto.descrizione)}</textarea>
                </div>

                <div class="form-group" style="grid-column:1/-1;">
                    <label for="immagine">
                        Nome file immagine
                        <span style="font-weight:400; color:#999;">
                            (carica il file in images/prodotti/)
                        </span>
                    </label>
                    <input type="text" id="immagine" name="immagine"
                           placeholder="Es. ps5.jpg"
                           value="${fn:escapeXml(prodotto.immagine)}">
                </div>

                <div class="form-group" style="grid-column:1/-1;">
                    <label style="display:flex; align-items:center; gap:0.5rem;
                                  cursor:pointer;">
                        <input type="checkbox" id="inOfferta" name="inOfferta"
                               ${prodotto.inOfferta ? 'checked' : ''}
                               onchange="toggleOfferta()">
                        Prodotto in offerta
                    </label>
                </div>

                <div class="form-group" id="gruppoOfferta"
                     style="${prodotto.inOfferta ? '' : 'display:none;'}">
                    <label for="prezzoOfferta">Prezzo in offerta (€)</label>
                    <input type="number" id="prezzoOfferta"
                           name="prezzoOfferta" step="0.01" min="0"
                           placeholder="Es. 39.99"
                           value="${prodotto.prezzoOfferta > 0 ? prodotto.prezzoOfferta : ''}">
                </div>

            </div>

            <div style="display:flex; gap:1rem; margin-top:1.5rem;">
                <button type="submit" class="btn btn-primary">
                    ${empty prodotto ? 'Aggiungi prodotto' : 'Salva modifiche'}
                </button>
                <a href="${pageContext.request.contextPath}/admin/prodotti"
                   class="btn"
                   style="border:1px solid var(--color-border);">
                    Annulla
                </a>
            </div>

        </form>
    </div>
</div>
</main>

<script>
function toggleOfferta() {
    const gruppo = document.getElementById('gruppoOfferta');
    const check  = document.getElementById('inOfferta');
    gruppo.style.display = check.checked ? 'block' : 'none';
}

document.getElementById('formProdottoAdmin')
    .addEventListener('submit', function(e) {
    let valid = true;

    const nome = document.getElementById('nome');
    if (!nome.value.trim()) {
        nome.style.borderColor = 'var(--color-danger)';
        document.getElementById('nome-error').textContent =
            'Il nome è obbligatorio.';
        document.getElementById('nome-error').classList.add('visible');
        valid = false;
    }

    const cat = document.getElementById('categoria');
    if (!cat.value) {
        cat.style.borderColor = 'var(--color-danger)';
        document.getElementById('categoria-error').textContent =
            'Seleziona una categoria.';
        document.getElementById('categoria-error').classList.add('visible');
        valid = false;
    }

    const prezzo = document.getElementById('prezzo');
    if (!prezzo.value || parseFloat(prezzo.value) < 0) {
        prezzo.style.borderColor = 'var(--color-danger)';
        document.getElementById('prezzo-error').textContent =
            'Inserisci un prezzo valido.';
        document.getElementById('prezzo-error').classList.add('visible');
        valid = false;
    }

    if (!valid) e.preventDefault();
});
</script>

<%@ include file="../common/footer.jsp" %>