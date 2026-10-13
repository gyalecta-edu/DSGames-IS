<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Ordine #${ordine.id} — Admin"/>
<%@ include file="../common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <nav style="font-size:0.85rem; color:#666; margin-bottom:1.5rem;">
        <a href="${pageContext.request.contextPath}/admin/ordini">
            Gestione ordini
        </a> /
        <span>Ordine #${ordine.id}</span>
    </nav>

    <h1 style="font-size:1.4rem; font-weight:700; margin-bottom:1.5rem;">
        Ordine #${ordine.id}
    </h1>

    <div style="display:grid; grid-template-columns:1fr 300px;
                gap:2rem; align-items:start;">

        <!-- Prodotti -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); overflow:hidden;">

            <div style="padding:1rem 1.5rem;
                        border-bottom:1px solid var(--color-border);
                        font-weight:700;">
                Prodotti ordinati
            </div>

            <c:forEach var="d" items="${ordine.dettagli}" varStatus="st">
                <div style="display:grid;
                            grid-template-columns:1fr auto auto;
                            gap:1rem; padding:1rem 1.5rem; align-items:center;
                            ${!st.last ? 'border-bottom:1px solid var(--color-border);' : ''}">
                    <div>
                        <div style="font-weight:600;">${d.prodotto.nome}</div>
                        <div style="font-size:0.85rem; color:#666;">
                            Prezzo acquisto:
                            <fmt:formatNumber value="${d.prezzoAcquisto}"
                                type="currency" currencySymbol="€"/>
                        </div>
                    </div>
                    <span style="color:#666;">× ${d.quantita}</span>
                    <span style="font-weight:700;">
                        <fmt:formatNumber value="${d.subtotale}"
                            type="currency" currencySymbol="€"/>
                    </span>
                </div>
            </c:forEach>

            <div style="display:flex; justify-content:flex-end;
                        padding:1rem 1.5rem; font-weight:700; font-size:1.1rem;
                        border-top:2px solid var(--color-border);">
                Totale:&nbsp;
                <fmt:formatNumber value="${ordine.totale}"
                    type="currency" currencySymbol="€"/>
            </div>
        </div>

        <!-- Info -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); padding:1.5rem;">
            <h3 style="margin-bottom:1rem; font-size:1rem;">Info ordine</h3>
            <div style="font-size:0.9rem; line-height:2.2;">
                <div><strong>Data:</strong>
                    <fmt:formatDate value="${ordine.dataOrdine}"
                        pattern="dd/MM/yyyy HH:mm"/>
                </div>
                <div><strong>Cliente ID:</strong> #${ordine.idUtente}</div>
                <div><strong>Pagamento:</strong> ${fn:escapeXml(ordine.metodoPagamento)}</div>
                <div><strong>Stato:</strong> ${ordine.stato}</div>
            </div>
            <hr style="margin:1rem 0;">
            <h3 style="margin-bottom:0.75rem; font-size:1rem;">Spedizione</h3>
            <div style="font-size:0.9rem; line-height:1.8;">
                ${fn:escapeXml(ordine.indirizzoSped)}<br>
                ${fn:escapeXml(ordine.capSped)} ${fn:escapeXml(ordine.cittaSped)}
            </div>
            <a href="${pageContext.request.contextPath}/admin/ordini"
               class="btn btn-full"
               style="margin-top:1.5rem; border:1px solid var(--color-border);">
                ← Torna agli ordini
            </a>
        </div>
    </div>
</div>
</main>

<%@ include file="../common/footer.jsp" %>