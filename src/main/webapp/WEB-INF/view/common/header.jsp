<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${fn:escapeXml(not empty pageTitle ? pageTitle : 'DS Games')}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body data-context="${pageContext.request.contextPath}" data-csrf="${sessionScope.token}">

<nav class="navbar">
    <div class="navbar-inner">

        <a href="${pageContext.request.contextPath}/" class="navbar-brand">
            DS <span>Games</span>
        </a>

        <form class="navbar-search" action="${pageContext.request.contextPath}/catalogo" method="get">
            <input type="text" name="q" placeholder="Cerca console, giochi..."
                   value="${fn:escapeXml(param.q)}">
            <button type="submit">&#128269;</button>
        </form>

        <div class="navbar-links" id="navLinks">
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
            <a href="${pageContext.request.contextPath}/catalogo?categoria=CONSOLE">Console</a>
            <a href="${pageContext.request.contextPath}/catalogo?categoria=VIDEOGIOCO">Giochi</a>
            <a href="${pageContext.request.contextPath}/offerte">Offerte</a>

            <c:choose>
<c:when test="${not empty sessionScope.utente}">
    <a href="${pageContext.request.contextPath}/ordini">
        📦 Ordini
    </a>
    <a href="${pageContext.request.contextPath}/wishlist">
        🤍
    </a>
    <a href="${pageContext.request.contextPath}/profilo">
        ${fn:escapeXml(sessionScope.utente.nome)}
    </a>
    <c:if test="${sessionScope.utente.admin}">
        <a href="${pageContext.request.contextPath}/admin/prodotti"
           class="btn-accent">
            ⚙️ Admin
        </a>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/logout" class="inline-action">
        <input type="hidden" name="token" value="${sessionScope.token}">
        <button type="submit" class="nav-link-button">Esci</button>
    </form>
</c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Accedi</a>
                    <a href="${pageContext.request.contextPath}/registrazione" class="btn-accent">
                        Registrati
                    </a>
                </c:otherwise>
            </c:choose>

            <a href="${pageContext.request.contextPath}/carrello" class="cart-icon">
                🛒
                <c:if test="${not empty sessionScope.carrello and sessionScope.carrello.size() > 0}">
                    <span class="cart-badge">${sessionScope.carrello.size()}</span>
                </c:if>
            </a>
        </div>

        <button class="navbar-toggle" onclick="
            document.getElementById('navLinks').classList.toggle('open')">
            &#9776;
        </button>

    </div>
</nav>