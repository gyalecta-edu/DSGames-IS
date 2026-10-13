<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Admin — Ordini"/>
<%@ include file="../common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <div style="display:flex; justify-content:space-between;
                align-items:center; margin-bottom:1.5rem;">
        <h1 style="font-size:1.5rem; font-weight:700;">
            📦 Gestione Ordini
        </h1>
        <a href="${pageContext.request.contextPath}/admin/prodotti"
           class="btn" style="border:1px solid var(--color-border);">
            ⚙️ Prodotti
        </a>
    </div>

    <!-- Filtri -->
    <div style="background:white; border-radius:var(--radius);
                box-shadow:var(--shadow); padding:1.5rem;
                margin-bottom:1.5rem;">
        <form method="get"
              action="${pageContext.request.contextPath}/admin/ordini">
            <div style="display:flex; gap:1rem; align-items:flex-end;
                        flex-wrap:wrap;">

                <div class="form-group" style="margin:0; flex:1; min-width:150px;">
                    <label style="font-size:0.85rem;">Dal</label>
                    <input type="date" name="dal" value="${filtroDal}">
                </div>

                <div class="form-group" style="margin:0; flex:1; min-width:150px;">
                    <label style="font-size:0.85rem;">Al</label>
                    <input type="date" name="al" value="${filtroAl}">
                </div>

                <div class="form-group" style="margin:0; flex:1; min-width:150px;">
                    <label style="font-size:0.85rem;">ID Cliente</label>
                    <input type="number" name="idCliente"
                           placeholder="Es. 3" min="1">
                </div>

                <button type="submit" class="btn btn-primary"
                        style="margin-bottom:1px;">
                    Filtra
                </button>

                <a href="${pageContext.request.contextPath}/admin/ordini"
                   class="btn" style="border:1px solid var(--color-border);
                                      margin-bottom:1px;">
                    Reset
                </a>
            </div>
        </form>
    </div>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <!-- Tabella ordini -->
    <div style="background:white; border-radius:var(--radius);
                box-shadow:var(--shadow); overflow:hidden;">

        <div style="display:grid;
                    grid-template-columns:80px 100px 1fr 140px 120px 100px;
                    padding:0.75rem 1.5rem; background:var(--color-bg);
                    font-size:0.8rem; font-weight:700;
                    text-transform:uppercase; color:#666;
                    border-bottom:1px solid var(--color-border);">
            <span>Ordine</span>
            <span>Cliente</span>
            <span>Spedizione</span>
            <span>Data</span>
            <span>Totale</span>
            <span>Stato</span>
        </div>

        <c:forEach var="o" items="${ordini}" varStatus="st">
            <div style="display:grid;
                        grid-template-columns:80px 100px 1fr 140px 120px 100px;
                        padding:0.85rem 1.5rem; align-items:center;
                        ${!st.last ? 'border-bottom:1px solid var(--color-border);' : ''}">

                <a href="${pageContext.request.contextPath}/admin/ordini?id=${o.id}"
                   style="font-weight:700; color:var(--color-accent);">
                    #${o.id}
                </a>

                <span style="font-size:0.85rem; color:#666;">
                    #${o.idUtente}
                </span>

                <span style="font-size:0.85rem;">
                    ${fn:escapeXml(o.indirizzoSped)}, ${fn:escapeXml(o.capSped)} ${fn:escapeXml(o.cittaSped)}
                </span>

                <span style="font-size:0.85rem;">
                    <fmt:formatDate value="${o.dataOrdine}"
                        pattern="dd/MM/yyyy HH:mm"/>
                </span>

                <span style="font-weight:600;">
                    <fmt:formatNumber value="${o.totale}"
                        type="currency" currencySymbol="€"/>
                </span>

                <span>
                    <c:choose>
                        <c:when test="${o.stato == 'IN_ELABORAZIONE'}">
                            <span style="background:#fff3cd; color:#856404;
                                         padding:0.2rem 0.5rem; border-radius:4px;
                                         font-size:0.75rem; font-weight:600;">
                                In elaborazione
                            </span>
                        </c:when>
                        <c:when test="${o.stato == 'SPEDITO'}">
                            <span style="background:#cfe2ff; color:#084298;
                                         padding:0.2rem 0.5rem; border-radius:4px;
                                         font-size:0.75rem; font-weight:600;">
                                Spedito
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span style="background:#d1fae5; color:#065f46;
                                         padding:0.2rem 0.5rem; border-radius:4px;
                                         font-size:0.75rem; font-weight:600;">
                                Consegnato
                            </span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </c:forEach>

        <c:if test="${empty ordini}">
            <div style="text-align:center; padding:3rem; color:#666;">
                Nessun ordine trovato con i filtri selezionati.
            </div>
        </c:if>
    </div>

</div>
</main>

<%@ include file="../common/footer.jsp" %>