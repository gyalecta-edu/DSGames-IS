<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Accedi — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:3rem; padding-bottom:3rem;">
<div class="form-card">

    <h1 style="font-size:1.6rem; margin-bottom:0.25rem;">Accedi</h1>
    <p style="color:#666; margin-bottom:1.5rem; font-size:0.9rem;">
        Non hai un account?
        <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
    </p>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <c:if test="${not empty param.messaggio}">
        <div class="alert alert-success">${param.messaggio}</div>
    </c:if>

    <form id="formLogin" method="post"
          action="${pageContext.request.contextPath}/login">
        <input type="hidden" name="token" value="${sessionScope.token}">

        <input type="hidden" name="redirect" value="${param.redirect}">

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   placeholder="la-tua@email.com"
                   value="${fn:escapeXml(param.email)}" autocomplete="email">
            <div class="form-error" id="email-error"></div>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   placeholder="••••••••" autocomplete="current-password">
            <div class="form-error" id="password-error"></div>
        </div>

        <button type="submit" class="btn btn-primary btn-full"
                style="margin-top:0.5rem;">
            Accedi
        </button>

    </form>

</div>
</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/validazione.js"></script>
<%@ include file="common/footer.jsp" %>