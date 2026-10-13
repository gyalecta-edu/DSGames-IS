<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="DS Games — Console e Videogiochi" />
<%@ include file="common/header.jsp" %>

<main>
    <!-- Hero Banner -->
    <section class="hero">
        <h1>Il tuo negozio di<br><span>Console &amp; Videogiochi</span></h1>
        <p>Trova le migliori console e i giochi più attesi al prezzo giusto.</p>
        <div class="hero-buttons">
            <a href="${pageContext.request.contextPath}/catalogo?categoria=CONSOLE"
               class="btn btn-primary">Scopri le Console</a>
            <a href="${pageContext.request.contextPath}/catalogo?categoria=VIDEOGIOCO"
               class="btn btn-outline">Esplora i Giochi</a>
        </div>
    </section>

    <div class="container">

        <!-- Prodotti in offerta -->
        <c:if test="${not empty offerte}">
            <section style="margin-top: 3rem;">
                <h2 class="section-title">🔥 Offerte in corso</h2>
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
                                    <div class="product-card-img-placeholder">🎮</div>
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
                                <form method="post" action="${pageContext.request.contextPath}/carrello" class="inline-action"><input type="hidden" name="token" value="${sessionScope.token}"><input type="hidden" name="azione" value="aggiungi"><input type="hidden" name="idProdotto" value="${p.id}"><button type="submit"
                                   class="btn btn-outline btn-sm" style="color:var(--color-text)">🛒</button></form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <!-- Ultimi prodotti -->
        <c:if test="${not empty prodotti}">
            <section style="margin-top: 3rem;">
                <h2 class="section-title">🎮 Prodotti in catalogo</h2>
                <div class="products-grid">
                    <c:forEach var="p" items="${prodotti}">
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
                                </div>
                            </div>
                            <div class="product-card-footer">
                                <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                                   class="btn btn-primary btn-sm" style="flex:1">Dettagli</a>
                                <form method="post" action="${pageContext.request.contextPath}/carrello" class="inline-action"><input type="hidden" name="token" value="${sessionScope.token}"><input type="hidden" name="azione" value="aggiungi"><input type="hidden" name="idProdotto" value="${p.id}"><button type="submit"
                                   class="btn btn-outline btn-sm" style="color:var(--color-text)">🛒</button></form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <!-- Messaggio se catalogo vuoto -->
        <c:if test="${empty prodotti and empty offerte}">
            <div style="text-align:center; padding: 4rem 0; color: #666;">
                <div style="font-size:4rem;">🎮</div>
                <h3 style="margin-top:1rem;">Catalogo in arrivo!</h3>
                <p style="margin-top:0.5rem;">I prodotti saranno disponibili a breve.</p>
            </div>
        </c:if>

    </div>
</main>

<%@ include file="common/footer.jsp" %>