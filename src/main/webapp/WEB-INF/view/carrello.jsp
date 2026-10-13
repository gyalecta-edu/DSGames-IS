<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Carrello — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <h1 class="section-title">🛒 Il tuo carrello</h1>
    <c:if test="${not empty errore}"><div class="alert alert-error"><c:out value="${errore}"/></div></c:if>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty carrello}">
            <div style="text-align:center; padding:4rem; color:#666;">
                <div style="font-size:4rem;">🛒</div>
                <h3 style="margin-top:1rem;">Il carrello è vuoto</h3>
                <p style="margin-top:0.5rem;">
                    Aggiungi qualche prodotto dal catalogo!
                </p>
                <a href="${pageContext.request.contextPath}/catalogo"
                   class="btn btn-primary" style="margin-top:1.5rem;">
                    Vai al catalogo
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <div style="display:grid; grid-template-columns:1fr 340px;
                        gap:2rem; align-items:start;">

                <!-- Lista prodotti nel carrello -->
                <div>
                    <form method="post"
                          action="${pageContext.request.contextPath}/carrello"
                          id="formCarrello">
        <input type="hidden" name="token" value="${sessionScope.token}">

                        <div style="background:white; border-radius:var(--radius);
                                    box-shadow:var(--shadow); overflow:hidden;">

                            <c:forEach var="entry" items="${carrello}"
                                       varStatus="status">
                                <c:set var="item" value="${entry.value}"/>
                                <c:set var="p"    value="${item.prodotto}"/>

                                <div style="display:grid;
                                            grid-template-columns:80px 1fr auto auto;
                                            gap:1rem; align-items:center;
                                            padding:1rem 1.5rem;
                                            ${!status.last ? 'border-bottom:1px solid var(--color-border);' : ''}">

                                    <!-- Immagine -->
                                    <c:choose>
                                        <c:when test="${not empty p.immagine}">
                                            <img src="${pageContext.request.contextPath}/images/prodotti/${fn:escapeXml(p.immagine)}"
                                                 alt="${fn:escapeXml(p.nome)}"
                                                 style="width:80px; height:80px;
                                                        object-fit:cover;
                                                        border-radius:var(--radius);">
                                        </c:when>
                                        <c:otherwise>
                                            <div style="width:80px; height:80px;
                                                        background:linear-gradient(135deg,#1a1a4e,#0d1117);
                                                        border-radius:var(--radius);
                                                        display:flex; align-items:center;
                                                        justify-content:center;
                                                        font-size:1.8rem;">
                                                <c:choose>
                                                    <c:when test="${p.categoria == 'CONSOLE'}">🕹️</c:when>
                                                    <c:otherwise>🎮</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <!-- Info prodotto -->
                                    <div>
                                        <div style="font-weight:600; margin-bottom:0.2rem;">
                                            <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                                               style="color:var(--color-text);">
                                                ${fn:escapeXml(p.nome)}
                                            </a>
                                        </div>
                                        <div style="font-size:0.85rem; color:#666;">
                                            ${fn:escapeXml(p.piattaforma)}
                                        </div>
                                        <div style="font-size:0.9rem; margin-top:0.3rem;">
                                            <fmt:formatNumber value="${p.prezzoEffettivo}"
                                                type="currency" currencySymbol="€"/>
                                            <c:if test="${p.inOfferta}">
                                                <span class="badge-sale"
                                                      style="font-size:0.65rem;">
                                                    OFFERTA
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Quantità -->
                                    <div style="display:flex; align-items:center; gap:0.5rem;">
                                        <input type="hidden" name="idProdotto"
                                               value="${p.id}">
                                        <input type="number" name="quantita"
                                               value="${item.quantita}"
                                               min="0"
                                               max="${p.quantita}"
                                               id="qty-${p.id}"
                                               style="width:65px; padding:0.4rem;
                                                      border:1px solid var(--color-border);
                                                      border-radius:var(--radius);
                                                      text-align:center; font-size:0.95rem;"
                                               onchange="validaQuantita(this, ${p.quantita})">
                                        <span style="font-size:0.75rem; color:#999;">
                                            max ${p.quantita}
                                        </span>
                                    </div>

                                    <!-- Subtotale + rimuovi -->
                                    <div style="text-align:right; min-width:90px;">
                                        <div style="font-weight:700;">
                                            <fmt:formatNumber value="${item.subtotale}"
                                                type="currency" currencySymbol="€"/>
                                        </div>
                                        <button type="submit" formaction="${pageContext.request.contextPath}/carrello?azione=rimuovi&amp;idProdotto=${p.id}" formmethod="post"
                                           style="font-size:0.8rem; color:var(--color-danger);
                                                  margin-top:0.3rem; display:block;"
                                           onclick="return confirm('Rimuovere il prodotto dal carrello?')">
                                            Rimuovi
                                        </button>
                                    </div>

                                </div>
                            </c:forEach>
                        </div>

                        <!-- Azioni carrello -->
                        <div style="display:flex; gap:1rem; margin-top:1rem;
                                    flex-wrap:wrap;">
                            <button type="submit"
                                    class="btn"
                                    style="border:1px solid var(--color-border);">
                                🔄 Aggiorna quantità
                            </button>
                            <button type="submit" formaction="${pageContext.request.contextPath}/carrello?azione=svuota" formmethod="post"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Svuotare il carrello?')">
                                🗑 Svuota carrello
                            </button>
                        </div>

                    </form>
                </div>

                <!-- Riepilogo ordine -->
                <div style="background:white; border-radius:var(--radius);
                            box-shadow:var(--shadow); padding:1.5rem;
                            position:sticky; top:80px;">

                    <h3 style="margin-bottom:1rem;">Riepilogo ordine</h3>

                    <div style="display:flex; justify-content:space-between;
                                padding:0.5rem 0; border-bottom:1px solid var(--color-border);">
                        <span style="color:#666;">Prodotti</span>
                        <span>${carrello.size()}</span>
                    </div>

                    <div style="display:flex; justify-content:space-between;
                                padding:0.75rem 0; font-size:1.2rem; font-weight:700;">
                        <span>Totale</span>
                        <span>
                            <fmt:formatNumber value="${totale}"
                                type="currency" currencySymbol="€"/>
                        </span>
                    </div>

                    <p style="font-size:0.8rem; color:#999; margin-bottom:1rem;">
                        Spedizione calcolata al checkout.
                    </p>

                    <!-- Bottone checkout -->
                    <c:choose>
                        <c:when test="${not empty sessionScope.utente}">
                            <a href="${pageContext.request.contextPath}/checkout"
                               class="btn btn-primary btn-full">
                                Procedi all'acquisto →
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login?redirect=checkout"
                               class="btn btn-primary btn-full">
                                Accedi per acquistare
                            </a>
                            <a href="${pageContext.request.contextPath}/registrazione?redirect=carrello"
                               class="btn btn-outline btn-full"
                               style="margin-top:0.5rem; color:var(--color-text);
                                      border-color:var(--color-border); text-align:center;">
                                Registrati gratis
                            </a>
                            <p style="font-size:0.8rem; color:#999;
                                      text-align:center; margin-top:0.5rem;">
                                La registrazione è richiesta solo per completare l'acquisto.
                            </p>
                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
        </c:otherwise>
    </c:choose>

</div>
</main>

<script>
function validaQuantita(input, max) {
    const val = parseInt(input.value);
    const errId = 'err-qty-' + input.id.replace('qty-', '');
    let errDiv = document.getElementById(errId);

    if (!errDiv) {
        errDiv = document.createElement('div');
        errDiv.id = errId;
        errDiv.style.cssText = 'color:var(--color-danger);font-size:0.78rem;margin-top:2px;';
        input.parentNode.appendChild(errDiv);
    }

    if (isNaN(val) || val < 0) {
        errDiv.textContent = 'Quantità non valida.';
        input.value = 1;
    } else if (val > max) {
        errDiv.textContent = 'Disponibilità massima: ' + max;
        input.value = max;
    } else {
        errDiv.textContent = '';
    }
}
</script>

<%@ include file="common/footer.jsp" %>