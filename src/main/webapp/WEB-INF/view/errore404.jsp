<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Pagina non trovata — DS Games"/>
<%@ include file="common/header.jsp" %>
<main>
<div class="container" style="text-align:center; padding:5rem 1rem;">
    <div style="font-size:6rem;">🕹️</div>
    <h1 style="font-size:4rem; font-weight:800; color:var(--color-accent);
               margin:0.5rem 0;">404</h1>
    <h2 style="font-size:1.4rem; margin-bottom:1rem;">
        Pagina non trovata
    </h2>
    <p style="color:#666; margin-bottom:2rem;">
        La pagina che cerchi non esiste o è stata spostata.
    </p>
    <div style="display:flex; gap:1rem; justify-content:center;">
        <a href="${pageContext.request.contextPath}/home"
           class="btn btn-primary">Torna alla Home</a>
        <a href="${pageContext.request.contextPath}/catalogo"
           class="btn" style="border:1px solid var(--color-border);">
            Vai al Catalogo
        </a>
    </div>
</div>
</main>
<%@ include file="common/footer.jsp" %>