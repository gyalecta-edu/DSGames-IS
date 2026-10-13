<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Offerte — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <h1 class="section-title">🔥 Offerte in corso</h1>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty offerte}">
            <div style="text-align:center; padding:4rem; color:#666;">
                <div style="font-size:3rem;">🏷️</div>
                <h3 style="margin-top:1rem;">Nessuna offerta al momento</h3>
                <p style="margin-top:0.5rem;">Torna presto per scoprire le nuove promozioni!</p>
                <a href="${pageContext.request.contextPath}/catalogo"
                   class="btn btn-primary" style="margin-top:1.5rem;">
                    Vai al catalogo
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="products-grid">
                <c:forEach var="p" items="${offerte}">
                    <div class="product-card">
                        <c:choose>
                            <c:when test="${not empty p.immagine}">
                                <img class="product-card-img"
                                     src="${pageContext.request.contextPath}/images/prodotti/${fn:escapeXml(p.immagine)}"
                                     alt="${fn:escapeXml(p.nome)}">
                            </c:when>
                            <c:otherwise>
                                <div class="product-card-img-placeholder">
                                    <c:choose>
                                        <c:when test="${p.categoria == 'CONSOLE'}">🕹️</c:when>
                                        <c:otherwise>🎮</c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="product-card-body">
                            <span class="product-card-category">${p.categoria}</span>
                            <span class="product-card-name">${fn:escapeXml(p.nome)}</span>
                            <span class="product-card-platform">${fn:escapeXml(p.piattaforma)}</span>
                            <div class="product-card-price">
                                <span class="price-current">
                                    <fmt:formatNumber value="${p.prezzoEffettivo}"
                                        type="currency" currencySymbol="€"/>
                                </span>
                                <span class="price-original">
                                    <fmt:formatNumber value="${p.prezzo}"
                                        type="currency" currencySymbol="€"/>
                                </span>
                                <span class="badge-sale">OFFERTA</span>
                            </div>
                        </div>

                        <div class="product-card-footer">
                            <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                               class="btn btn-primary btn-sm" style="flex:1">Dettagli</a>
                            <c:if test="${p.quantita > 0}">
                                <form method="post" action="${pageContext.request.contextPath}/carrello" class="inline-action"><input type="hidden" name="token" value="${sessionScope.token}"><input type="hidden" name="azione" value="aggiungi"><input type="hidden" name="idProdotto" value="${p.id}"><button type="submit"
                                   class="btn btn-sm"
                                   style="border:1px solid var(--color-border);">🛒</button></form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</div>
</main>

<%@ include file="common/footer.jsp" %>