<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Catalogo — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container">

    <h1 class="section-title" style="margin-top:2rem;">
        <c:choose>
            <c:when test="${filtroCategoria == 'CONSOLE'}">🕹️ Console</c:when>
            <c:when test="${filtroCategoria == 'VIDEOGIOCO'}">🎮 Videogiochi</c:when>
            <c:otherwise>🗂️ Catalogo completo</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <div class="catalog-layout">

        <!-- ===== SIDEBAR FILTRI ===== -->
        <aside class="filters-sidebar">
            <form method="get" action="${pageContext.request.contextPath}/catalogo"
                  id="formFiltri">

                <div class="filter-group">
                    <h4>Cerca</h4>
                    <input type="text" name="q" id="inputRicerca"
                           placeholder="Nome prodotto..."
                           value="${fn:escapeXml(filtroQ)}">
                </div>

                <div class="filter-group">
                    <h4>Categoria</h4>
                    <label>
                        <input type="radio" name="categoria" value=""
                            ${empty filtroCategoria ? 'checked' : ''}>
                        Tutte
                    </label>
                    <label>
                        <input type="radio" name="categoria" value="CONSOLE"
                            ${'CONSOLE' == filtroCategoria ? 'checked' : ''}>
                        Console
                    </label>
                    <label>
                        <input type="radio" name="categoria" value="VIDEOGIOCO"
                            ${'VIDEOGIOCO' == filtroCategoria ? 'checked' : ''}>
                        Videogiochi
                    </label>
                </div>

                <div class="filter-group">
                    <h4>Piattaforma</h4>
                    <select name="piattaforma" id="selectPiattaforma"
                            onchange="document.getElementById('formFiltri').submit()">
                        <option value="">Tutte le piattaforme</option>
                        <c:forEach var="p" items="${piattaforme}">
                            <option value="${p}"
                                ${p == filtroPiattaforma ? 'selected' : ''}>
                                ${p}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary btn-full">
                    Applica filtri
                </button>

                <c:if test="${not empty filtroQ or not empty filtroCategoria or not empty filtroPiattaforma}">
                    <a href="${pageContext.request.contextPath}/catalogo"
                       class="btn btn-outline btn-full"
                       style="margin-top:0.5rem; color:var(--color-text); border-color:var(--color-border);">
                        Rimuovi filtri
                    </a>
                </c:if>

            </form>
        </aside>

        <!-- ===== GRIGLIA PRODOTTI ===== -->
        <section>

            <div style="display:flex; justify-content:space-between;
                        align-items:center; margin-bottom:1rem;">
                <span id="contatoreProdotti" style="color:#666; font-size:0.9rem;">
                    <c:choose>
                        <c:when test="${empty prodotti}">Nessun prodotto trovato</c:when>
                        <c:otherwise>${prodotti.size()} prodotti trovati</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <c:choose>
                <c:when test="${empty prodotti}">
                    <div style="text-align:center; padding:3rem; color:#666;">
                        <div style="font-size:3rem;">🔍</div>
                        <h3 style="margin-top:1rem;">Nessun risultato</h3>
                        <p style="margin-top:0.5rem;">
                            Prova a modificare i filtri di ricerca.
                        </p>
                        <a href="${pageContext.request.contextPath}/catalogo"
                           class="btn btn-primary" style="margin-top:1rem;">
                            Vedi tutto il catalogo
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <<div class="products-grid" id="gridProdotti">>
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
                                        <c:if test="${p.inOfferta}">
                                            <span class="price-original">
                                                <fmt:formatNumber value="${p.prezzo}"
                                                    type="currency" currencySymbol="€"/>
                                            </span>
                                            <span class="badge-sale">OFFERTA</span>
                                        </c:if>
                                    </div>

                                    <c:if test="${p.quantita == 0}">
                                        <span style="color:var(--color-danger);
                                                     font-size:0.8rem; font-weight:600;">
                                            Esaurito
                                        </span>
                                    </c:if>
                                </div>

                                <div class="product-card-footer">
                                    <a href="${pageContext.request.contextPath}/prodotto?id=${p.id}"
                                       class="btn btn-primary btn-sm" style="flex:1">
                                        Dettagli
                                    </a>
                                    <c:if test="${p.quantita > 0}">
                                        <form method="post" action="${pageContext.request.contextPath}/carrello" class="inline-action"><input type="hidden" name="token" value="${sessionScope.token}"><input type="hidden" name="azione" value="aggiungi"><input type="hidden" name="idProdotto" value="${p.id}"><button type="submit"
                                           class="btn btn-sm"
                                           style="border:1px solid var(--color-border);">
                                            🛒
                                        </button></form>
                                    </c:if>
                                </div>

                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

        </section>
    </div>
</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/ajax.js"></script>
<%@ include file="common/footer.jsp" %>