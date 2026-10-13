<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Ordine #${ordine.id} — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <nav style="font-size:0.85rem; color:#666; margin-bottom:1.5rem;">
        <a href="${pageContext.request.contextPath}/ordini">I miei ordini</a> /
        <span>Ordine #${ordine.id}</span>
    </nav>

    <h1 class="section-title">Ordine #${ordine.id}</h1>

    <div style="display:grid; grid-template-columns:1fr 320px;
                gap:2rem; align-items:start;">

        <!-- Prodotti ordinati -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); overflow:hidden;">

            <div style="padding:1rem 1.5rem;
                        border-bottom:1px solid var(--color-border);
                        font-weight:700;">
                Prodotti ordinati
            </div>

            <c:forEach var="d" items="${ordine.dettagli}" varStatus="status">
                <div style="display:grid;
                            grid-template-columns:60px 1fr auto;
                            gap:1rem; align-items:center;
                            padding:1rem 1.5rem;
                            ${!status.last ? 'border-bottom:1px solid var(--color-border);' : ''}">

                    <div style="width:60px; height:60px;
                                background:linear-gradient(135deg,#1a1a4e,#0d1117);
                                border-radius:var(--radius);
                                display:flex; align-items:center;
                                justify-content:center; font-size:1.5rem;">
                        🎮
                    </div>

                    <div>
                        <div style="font-weight:600;">
                            ${d.prodotto.nome}
                        </div>
                        <div style="font-size:0.85rem; color:#666;">
                            Quantità: ${d.quantita} ×
                            <fmt:formatNumber value="${d.prezzoAcquisto}"
                                type="currency" currencySymbol="€"/>
                        </div>
                    </div>

                    <div style="font-weight:700;">
                        <fmt:formatNumber value="${d.subtotale}"
                            type="currency" currencySymbol="€"/>
                    </div>
                </div>
            </c:forEach>

            <!-- Totale -->
            <div style="display:flex; justify-content:flex-end;
                        padding:1rem 1.5rem; font-size:1.1rem;
                        font-weight:700;
                        border-top:2px solid var(--color-border);">
                Totale:&nbsp;
                <fmt:formatNumber value="${ordine.totale}"
                    type="currency" currencySymbol="€"/>
            </div>
        </div>

        <!-- Info ordine -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); padding:1.5rem;">

            <h3 style="margin-bottom:1rem; font-size:1rem;">
                Dettagli ordine
            </h3>

            <div style="font-size:0.9rem; line-height:2;">
                <div><strong>Data:</strong>
                    <fmt:formatDate value="${ordine.dataOrdine}"
                        pattern="dd/MM/yyyy HH:mm"/>
                </div>
                <div><strong>Stato:</strong> ${ordine.stato}</div>
                <div><strong>Pagamento:</strong>
                    ${fn:escapeXml(ordine.metodoPagamento)}
                </div>
            </div>

            <hr style="margin:1rem 0; border-color:var(--color-border);">

            <h3 style="margin-bottom:0.75rem; font-size:1rem;">
                Indirizzo di spedizione
            </h3>
            <div style="font-size:0.9rem; line-height:1.8; color:#444;">
                ${fn:escapeXml(ordine.indirizzoSped)}<br>
                ${fn:escapeXml(ordine.capSped)} ${fn:escapeXml(ordine.cittaSped)}
            </div>

            <a href="${pageContext.request.contextPath}/ordini"
               class="btn btn-full"
               style="margin-top:1.5rem;
                      border:1px solid var(--color-border);">
                ← Torna agli ordini
            </a>
        </div>
    </div>

</div>
</main>

<%@ include file="common/footer.jsp" %>