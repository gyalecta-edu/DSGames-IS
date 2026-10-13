<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Admin — Prodotti"/>
<%@ include file="../common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <div style="display:flex; justify-content:space-between;
                align-items:center; margin-bottom:1.5rem;">
        <h1 style="font-size:1.5rem; font-weight:700;">
            ⚙️ Gestione Prodotti
        </h1>
        <div style="display:flex; gap:0.75rem;">
            <a href="${pageContext.request.contextPath}/admin/ordini"
               class="btn" style="border:1px solid var(--color-border);">
                📦 Ordini
            </a>
            <a href="${pageContext.request.contextPath}/admin/prodotti?azione=nuovo"
               class="btn btn-primary">
                + Nuovo prodotto
            </a>
        </div>
    </div>

    <c:if test="${not empty messaggio}">
        <div class="alert alert-success">${messaggio}</div>
    </c:if>
    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <div style="background:white; border-radius:var(--radius);
                box-shadow:var(--shadow); overflow:hidden;">

        <div style="display:grid;
                    grid-template-columns:60px 1fr 100px 80px 80px 80px 120px;
                    padding:0.75rem 1.5rem; background:var(--color-bg);
                    font-size:0.8rem; font-weight:700; text-transform:uppercase;
                    color:#666; border-bottom:1px solid var(--color-border);">
            <span>ID</span>
            <span>Nome</span>
            <span>Categoria</span>
            <span>Prezzo</span>
            <span>Qtà</span>
            <span>Offerta</span>
            <span>Azioni</span>
        </div>

        <c:forEach var="p" items="${prodotti}" varStatus="st">
            <div style="display:grid;
                        grid-template-columns:60px 1fr 100px 80px 80px 80px 120px;
                        padding:0.85rem 1.5rem; align-items:center;
                        ${!st.last ? 'border-bottom:1px solid var(--color-border);' : ''}
                        ${p.eliminato ? 'opacity:0.5;' : ''}">

                <span style="color:#999; font-size:0.85rem;">#${p.id}</span>

                <span style="font-weight:600;">
                    ${fn:escapeXml(p.nome)}
                    <c:if test="${p.eliminato}">
                        <span style="font-size:0.75rem; color:var(--color-danger);
                                     font-weight:400;"> (eliminato)</span>
                    </c:if>
                </span>

                <span style="font-size:0.85rem;">${p.categoria}</span>

                <span>
                    <fmt:formatNumber value="${p.prezzo}"
                        type="currency" currencySymbol="€"/>
                </span>

                <span style="${p.quantita == 0 ? 'color:var(--color-danger);' : ''}
                             font-weight:${p.quantita == 0 ? '700' : '400'};">
                    ${p.quantita}
                </span>

                <span>
                    <c:choose>
                        <c:when test="${p.inOfferta}">
                            <span style="color:var(--color-success);">✓</span>
                        </c:when>
                        <c:otherwise>
                            <span style="color:#ccc;">—</span>
                        </c:otherwise>
                    </c:choose>
                </span>

                <div style="display:flex; gap:0.4rem;">
                    <c:if test="${!p.eliminato}">
                        <a href="${pageContext.request.contextPath}/admin/prodotti?azione=modifica&id=${p.id}"
                           class="btn btn-sm"
                           style="border:1px solid var(--color-border);
                                  font-size:0.8rem;">
                            ✏️
                        </a>
                        <form method="post" action="${pageContext.request.contextPath}/admin/prodotti" class="inline-action"><input type="hidden" name="token" value="${sessionScope.token}"><input type="hidden" name="azione" value="elimina"><input type="hidden" name="id" value="${p.id}"><button type="submit"
                           class="btn btn-sm btn-danger"
                           style="font-size:0.8rem;"
                           onclick="return confirm('Eliminare ${fn:escapeXml(p.nome)}? L\'operazione è reversibile dal database.')">
                            🗑
                        </button></form>
                    </c:if>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty prodotti}">
            <div style="text-align:center; padding:3rem; color:#666;">
                Nessun prodotto nel catalogo.
            </div>
        </c:if>
    </div>
</div>
</main>

<%@ include file="../common/footer.jsp" %>