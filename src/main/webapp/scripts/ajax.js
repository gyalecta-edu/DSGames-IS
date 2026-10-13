/**
 * DS Games — Funzionalità AJAX
 */

// Escaping per testo/attributi costruiti dinamicamente dai risultati JSON.
function escHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, ch => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[ch]);
}

// =============================================
// NOTIFICHE TOAST
// =============================================
function showToast(messaggio, tipo) {
    // Rimuove toast esistenti
    const esistente = document.getElementById('ds-toast');
    if (esistente) esistente.remove();

    const toast = document.createElement('div');
    toast.id = 'ds-toast';
    toast.textContent = messaggio;
    toast.style.cssText = `
        position: fixed;
        bottom: 2rem;
        right: 2rem;
        padding: 0.85rem 1.4rem;
        border-radius: 8px;
        font-size: 0.95rem;
        font-weight: 600;
        color: white;
        z-index: 9999;
        box-shadow: 0 4px 20px rgba(0,0,0,0.2);
        animation: slideIn 0.3s ease;
        background: ${tipo === 'success' ? '#3fb950' : tipo === 'error' ? '#e05252' : '#4f8ef7'};
    `;

    // Aggiunge animazione CSS
    if (!document.getElementById('toast-style')) {
        const style = document.createElement('style');
        style.id = 'toast-style';
        style.textContent = `
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to   { transform: translateX(0);    opacity: 1; }
            }
            @keyframes slideOut {
                from { transform: translateX(0);    opacity: 1; }
                to   { transform: translateX(100%); opacity: 0; }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// =============================================
// AGGIUNTA AL CARRELLO VIA AJAX
// =============================================
function aggiungiAlCarrello(idProdotto, bottone) {
    const contextPath = document.body.dataset.context || '';

    if (bottone) {
        bottone.disabled = true;
        bottone.textContent = '...';
    }

    fetch(contextPath + '/ajax/carrello', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-CSRF-Token': document.body.dataset.csrf || '' },
        body: 'azione=aggiungi&idProdotto=' + idProdotto
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            showToast(data.messaggio, 'success');

            // Aggiorna il badge del carrello nella navbar
            const badge = document.querySelector('.cart-badge');
            if (data.cartSize > 0) {
                if (badge) {
                    badge.textContent = data.cartSize;
                } else {
                    const cartIcon = document.querySelector('.cart-icon');
                    if (cartIcon) {
                        const newBadge = document.createElement('span');
                        newBadge.className = 'cart-badge';
                        newBadge.textContent = data.cartSize;
                        cartIcon.appendChild(newBadge);
                    }
                }
            }
        } else {
            showToast(data.messaggio, 'error');
        }
    })
    .catch(() => showToast('Errore di connessione.', 'error'))
    .finally(() => {
        if (bottone) {
            bottone.disabled = false;
            bottone.textContent = '🛒';
        }
    });
}

// =============================================
// TOGGLE WISHLIST VIA AJAX
// =============================================
function toggleWishlist(idProdotto, bottone) {
    const contextPath = document.body.dataset.context || '';
    const inWishlist  = bottone.dataset.inWishlist === 'true';
    const azione      = inWishlist ? 'rimuovi' : 'aggiungi';

    fetch(contextPath + '/ajax/wishlist', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-CSRF-Token': document.body.dataset.csrf || '' },
        body: 'azione=' + azione + '&idProdotto=' + idProdotto
    })
    .then(r => r.json())
    .then(data => {
        if (data.loginRequired) {
            window.location.href = contextPath + '/login';
            return;
        }
        if (data.success) {
            bottone.dataset.inWishlist = data.inWishlist ? 'true' : 'false';
            bottone.textContent = data.inWishlist ? '❤️' : '🤍';
            bottone.title = data.inWishlist
                ? 'Rimuovi dalla lista desideri'
                : 'Aggiungi alla lista desideri';
            showToast(data.messaggio, 'success');
        } else {
            showToast(data.messaggio, 'error');
        }
    })
    .catch(() => showToast('Errore di connessione.', 'error'));
}

// =============================================
// RICERCA IN TEMPO REALE NEL CATALOGO
// =============================================
function initRicercaAjax() {
    const inputRicerca = document.getElementById('inputRicerca');
    const gridProdotti = document.getElementById('gridProdotti');
    const contatore    = document.getElementById('contatoreProdotti');

    if (!inputRicerca || !gridProdotti) return;

    const contextPath = document.body.dataset.context || '';
    let timeout = null;

    inputRicerca.addEventListener('input', function() {
        clearTimeout(timeout);

        const q = this.value.trim();

        // Aspetta 400ms dopo l'ultimo carattere digitato
        timeout = setTimeout(() => {
            const categoria    = document.querySelector(
                'input[name="categoria"]:checked')?.value || '';
            const piattaforma  = document.getElementById(
                'selectPiattaforma')?.value || '';

            const params = new URLSearchParams({ q, categoria, piattaforma });

            // Mostra spinner
            gridProdotti.innerHTML =
                '<div style="text-align:center;padding:3rem;color:#666;">' +
                '⏳ Ricerca in corso...</div>';

            fetch(contextPath + '/ajax/ricerca?' + params.toString())
            .then(r => r.json())
            .then(data => {
                if (!data.success) {
                    gridProdotti.innerHTML =
                        '<div class="alert alert-error">Errore nella ricerca.</div>';
                    return;
                }

                if (contatore) {
                    contatore.textContent = data.totale +
                        (data.totale === 1 ? ' prodotto trovato' : ' prodotti trovati');
                }

                if (data.prodotti.length === 0) {
                    gridProdotti.innerHTML = `
                        <div style="text-align:center;padding:3rem;color:#666;
                                    grid-column:1/-1;">
                            <div style="font-size:3rem;">🔍</div>
                            <h3 style="margin-top:1rem;">Nessun risultato</h3>
                            <p>Prova a modificare i termini di ricerca.</p>
                        </div>`;
                    return;
                }

                gridProdotti.innerHTML = data.prodotti.map(p => {
                    const prezzo = p.inOfferta
                        ? `<span class="price-current">€${p.prezzoEffettivo.toFixed(2)}</span>
                           <span class="price-original">€${p.prezzo.toFixed(2)}</span>
                           <span class="badge-sale">OFFERTA</span>`
                        : `<span class="price-current">€${p.prezzo.toFixed(2)}</span>`;

                    const img = p.immagine
                        ? `<img class="product-card-img"
                               src="${contextPath}/images/prodotti/${encodeURIComponent(p.immagine)}"
                               alt="${escHtml(p.nome)}">`
                        : `<div class="product-card-img-placeholder">
                               ${p.categoria === 'CONSOLE' ? '🕹️' : '🎮'}
                           </div>`;

                    const esaurito = p.quantita === 0;

                    return `
                        <div class="product-card">
                            ${img}
                            <div class="product-card-body">
                                <span class="product-card-category">
                                    ${escHtml(p.categoria)}
                                </span>
                                <span class="product-card-name">${escHtml(p.nome)}</span>
                                <span class="product-card-platform">
                                    ${escHtml(p.piattaforma)}
                                </span>
                                <div class="product-card-price">${prezzo}</div>
                                ${esaurito
                                    ? '<span style="color:var(--color-danger);font-size:0.8rem;font-weight:600;">Esaurito</span>'
                                    : ''}
                            </div>
                            <div class="product-card-footer">
                                <a href="${contextPath}/prodotto?id=${p.id}"
                                   class="btn btn-primary btn-sm"
                                   style="flex:1">Dettagli</a>
                                ${!esaurito
                                    ? `<button class="btn btn-sm"
                                               style="border:1px solid var(--color-border);"
                                               onclick="aggiungiAlCarrello(${p.id}, this)">
                                           🛒
                                       </button>`
                                    : ''}
                            </div>
                        </div>`;
                }).join('');
            })
            .catch(() => {
                gridProdotti.innerHTML =
                    '<div class="alert alert-error">' +
                    'Errore di connessione.</div>';
            });
        }, 400);
    });
}

// =============================================
// AVVIO
// =============================================
document.addEventListener('DOMContentLoaded', function() {
    initRicercaAjax();
});