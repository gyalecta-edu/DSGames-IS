<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Registrati — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:3rem; padding-bottom:3rem;">
<div class="form-card" style="max-width:560px;">

    <h1 style="font-size:1.6rem; margin-bottom:0.25rem;">Crea un account</h1>
    <p style="color:#666; margin-bottom:1.5rem; font-size:0.9rem;">
        Hai già un account?
        <a href="${pageContext.request.contextPath}/login">Accedi</a>
    </p>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <form id="formRegistrazione" method="post"
          action="${pageContext.request.contextPath}/registrazione">
        <input type="hidden" name="token" value="${sessionScope.token}">

        <input type="hidden" name="redirect" value="${param.redirect}">

        <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
            <div class="form-group">
                <label for="nome">Nome *</label>
                <input type="text" id="nome" name="nome"
                       placeholder="Mario" value="${param.nome}">
                <div class="form-error" id="nome-error"></div>
            </div>
            <div class="form-group">
                <label for="cognome">Cognome *</label>
                <input type="text" id="cognome" name="cognome"
                       placeholder="Rossi" value="${param.cognome}">
                <div class="form-error" id="cognome-error"></div>
            </div>
        </div>

        <div class="form-group">
            <label for="email">Email *</label>
            <input type="email" id="email" name="email"
                   placeholder="mario.rossi@email.com"
                   value="${fn:escapeXml(param.email)}" autocomplete="email">
            <div class="form-error" id="email-error"></div>
        </div>

        <div class="form-group">
            <label for="password">Password *</label>
            <input type="password" id="password" name="password"
                   placeholder="Minimo 8 caratteri"
                   autocomplete="new-password">
            <div class="form-error" id="password-error"></div>
        </div>

        <div class="form-group">
            <label for="confermaPassword">Conferma Password *</label>
            <input type="password" id="confermaPassword"
                   name="confermaPassword"
                   placeholder="Ripeti la password"
                   autocomplete="new-password">
            <div class="form-error" id="confermaPassword-error"></div>
        </div>

        <button type="submit" class="btn btn-primary btn-full"
                style="margin-top:0.5rem;">
            Crea account
        </button>

    </form>

</div>
</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/validazione.js"></script>
<%@ include file="common/footer.jsp" %>