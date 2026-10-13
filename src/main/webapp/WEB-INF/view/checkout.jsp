<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Checkout — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <h1 class="section-title">💳 Checkout</h1>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <div style="display:grid; grid-template-columns:1fr 360px;
                gap:2rem; align-items:start;">

        <!-- Form dati spedizione e pagamento -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); padding:2rem;">

            <form id="formCheckout" method="post"
                  action="${pageContext.request.contextPath}/checkout">

                <input type="hidden" name="token"
                       value="${sessionScope.token}">

                <h2 style="font-size:1.1rem; margin-bottom:1.5rem;
                           padding-bottom:0.75rem;
                           border-bottom:1px solid var(--color-border);">
                    📦 Indirizzo di spedizione
                </h2>

                <div class="form-group">
                    <label for="indirizzo">Via / Piazza *</label>
                    <input type="text" id="indirizzo" name="indirizzo"
                           placeholder="Es. Via Roma 42"
                           value="${fn:escapeXml(sessionScope.utente.indirizzo)}">
                    <div class="form-error" id="indirizzo-error"></div>
                </div>

                <div style="display:grid;
                            grid-template-columns:1fr 120px; gap:1rem;">
                    <div class="form-group">
                        <label for="citta">Città *</label>
                        <input type="text" id="citta" name="citta"
                               placeholder="Es. Napoli"
                               value="${fn:escapeXml(sessionScope.utente.citta)}">
                        <div class="form-error" id="citta-error"></div>
                    </div>
                    <div class="form-group">
                        <label for="cap">CAP *</label>
                        <input type="text" id="cap" name="cap"
                               placeholder="80100"
                               maxlength="5"
                               value="${fn:escapeXml(sessionScope.utente.cap)}">
                        <div class="form-error" id="cap-error"></div>
                    </div>
                </div>

                <h2 style="font-size:1.1rem; margin:1.5rem 0;
                           padding-bottom:0.75rem;
                           border-bottom:1px solid var(--color-border);">
                    💳 Metodo di pagamento
                </h2>

                <div class="form-group">
                    <label for="metodoPagamento">Seleziona metodo *</label>
                    <select id="metodoPagamento" name="metodoPagamento">
                        <option value="">-- Scegli --</option>
                        <option value="Carta di credito">💳 Carta di credito</option>
                        <option value="PayPal">🅿 PayPal</option>
                        <option value="Bonifico bancario">🏦 Bonifico bancario</option>
                        <option value="Contrassegno">📦 Contrassegno</option>
                    </select>
                    <div class="form-error" id="metodoPagamento-error"></div>
                </div>

                <button type="submit"
                        class="btn btn-primary btn-full"
                        style="margin-top:1rem; font-size:1rem; padding:0.85rem;">
                    ✅ Conferma ordine
                </button>

            </form>
        </div>

        <!-- Riepilogo carrello -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); padding:1.5rem;
                    position:sticky; top:80px;">

            <h3 style="margin-bottom:1rem; font-size:1rem;">
                Riepilogo ordine
            </h3>

            <c:set var="totale" value="0"/>
            <c:forEach var="entry" items="${sessionScope.carrello}">
                <c:set var="item" value="${entry.value}"/>
                <c:set var="p"    value="${item.prodotto}"/>
                <div style="display:flex; justify-content:space-between;
                            padding:0.5rem 0; font-size:0.9rem;
                            border-bottom:1px solid var(--color-border);">
                    <span style="flex:1; margin-right:0.5rem;">
                        ${fn:escapeXml(p.nome)}
                        <span style="color:#999;">x${item.quantita}</span>
                    </span>
                    <span style="font-weight:600; white-space:nowrap;">
                        <fmt:formatNumber value="${item.subtotale}"
                            type="currency" currencySymbol="€"/>
                    </span>
                </div>
                <c:set var="totale" value="${totale + item.subtotale}"/>
            </c:forEach>

            <div style="display:flex; justify-content:space-between;
                        padding:1rem 0 0; font-size:1.15rem; font-weight:700;">
                <span>Totale</span>
                <span>
                    <fmt:formatNumber value="${totale}"
                        type="currency" currencySymbol="€"/>
                </span>
            </div>

            <p style="font-size:0.8rem; color:#999; margin-top:0.5rem;">
                Spedizione gratuita su tutti gli ordini.
            </p>
        </div>
    </div>
</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/validazione.js"></script>
<%@ include file="common/footer.jsp" %>