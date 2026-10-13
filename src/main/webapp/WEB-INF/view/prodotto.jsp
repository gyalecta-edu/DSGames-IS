<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="${fn:escapeXml(prodotto.nome)} — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <c:if test="${not empty prodotto}">

        <!-- Breadcrumb -->
        <nav style="font-size:0.85rem; color:#666; margin-bottom:1.5rem;" aria-label="Breadcrumb">
            <a href="${pageContext.request.contextPath}/">Home</a> /
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a> /
            <a href="${pageContext.request.contextPath}/catalogo?categoria=${prodotto.categoria}">
                ${prodotto.categoria}
            </a> /
            <span>${fn:escapeXml(prodotto.nome)}</span>
        </nav>

        <div style="display:grid; grid-template-columns:1fr 1fr; gap:3rem; align-items:start;">

            <!-- Immagine -->
            <div>
                <c:choose>
                    <c:when test="${not empty prodotto.immagine}">
                        <img src="${pageContext.request.contextPath}/images/prodotti/${fn:escapeXml(prodotto.immagine)}"
                             alt="${fn:escapeXml(prodotto.nome)}"
                             style="width:100%; border-radius:var(--radius); box-shadow:var(--shadow);"
                             loading="lazy">
                    </c:when>
                    <c:otherwise>
                        <div style="width:100%; aspect-ratio:1; border-radius:var(--radius);
                                    background:linear-gradient(135deg,#1a1a4e,#0d1117);
                                    display:flex; align-items:center; justify-content:center;
                                    font-size:6rem;">
                            <c:choose>
                                <c:when test="${prodotto.categoria == 'CONSOLE'}">🕹️</c:when>
                                <c:otherwise>🎮</c:otherwise>
                            </c:choose>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Info prodotto -->
            <div>
                <span style="font-size:0.8rem; font-weight:700; text-transform:uppercase;
                             letter-spacing:1px; color:var(--color-accent);">
                    ${prodotto.categoria}
                </span>

                <h1 style="font-size:2rem; margin:0.5rem 0 0.25rem;">
                    ${fn:escapeXml(prodotto.nome)}
                </h1>

                <p style="color:#666; margin-bottom:1.5rem;">
                    Piattaforma: <strong>${fn:escapeXml(prodotto.piattaforma)}</strong>
                </p>

                <!-- Prezzo -->
                <div style="margin-bottom:1.5rem;">
                    <c:choose>
                        <c:when test="${prodotto.inOfferta}">
                            <div style="display:flex; align-items:center; gap:1rem; flex-wrap:wrap;">
                                <span style="font-size:2.2rem; font-weight:800; color:var(--color-text);">
                                    <fmt:formatNumber value="${prodotto.prezzoEffettivo}" 
                                        type="currency" currencySymbol="€"/>
                                </span>
                                <span style="font-size:1.2rem; text-decoration:line-through; color:#999;">
                                    <fmt:formatNumber value="${prodotto.prezzo}" 
                                        type="currency" currencySymbol="€"/>
                                </span>
                                <span class="badge-sale" style="font-size:0.85rem; padding:0.3rem 0.7rem;">
                                    OFFERTA
                                </span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span style="font-size:2.2rem; font-weight:800;">
                                <fmt:formatNumber value="${prodotto.prezzo}" 
                                    type="currency" currencySymbol="€"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Disponibilità -->
                <div style="margin-bottom:1.5rem;">
                    <c:choose>
                        <c:when test="${prodotto.quantita > 5}">
                            <span style="color:var(--color-success); font-weight:600;">
                                ✓ Disponibile
                            </span>
                        </c:when>
                        <c:when test="${prodotto.quantita > 0}">
                            <span style="color:orange; font-weight:600;">
                                ⚠ Ultimi ${prodotto.quantita} rimasti
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span style="color:var(--color-danger); font-weight:600;">
                                ✗ Esaurito
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Bottoni azione - VERSIONE CORRETTA -->
                <div style="display:flex; gap:1rem; flex-wrap:wrap; margin-bottom:2rem;">
                    
                    <c:if test="${prodotto.quantita > 0 && not empty prodotto.id}">
                        <button class="btn btn-primary"
                                style="flex:1; text-align:center;"
                                data-product-id="${prodotto.id}"
                                onclick="aggiungiAlCarrello(this)"
                                aria-label="Aggiungi ${fn:escapeXml(prodotto.nome)} al carrello">
                            🛒 Aggiungi al carrello
                        </button>
                    </c:if>

                    <c:if test="${not empty sessionScope.utente && not empty prodotto.id}">
                        <button class="btn"
                                style="border:1px solid var(--color-border);"
                                data-product-id="${prodotto.id}"
                                data-in-wishlist="false"
                                title="Aggiungi alla lista desideri"
                                aria-label="Aggiungi alla lista desideri"
                                onclick="toggleWishlist(this)">
                            🤍
                        </button>
                    </c:if>
                </div>

                <!-- Descrizione -->
                <div style="background:var(--color-card); border-radius:var(--radius);
                            padding:1.5rem; box-shadow:var(--shadow);">
                    <h3 style="margin-bottom:0.75rem; font-size:1rem;">Descrizione</h3>
                    <p style="line-height:1.7; color:#444; white-space:pre-wrap;">
                        <c:out value="${fn:escapeXml(prodotto.descrizione)}" escapeXml="true"/>
                    </p>
                </div>

            </div>
        </div>

    </c:if>

</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/ajax.js"></script>
<%@ include file="common/footer.jsp" %>