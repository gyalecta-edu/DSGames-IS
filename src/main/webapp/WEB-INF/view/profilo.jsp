<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Il mio profilo — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <h1 class="section-title">👤 Il mio profilo</h1>

    <div style="display:grid; grid-template-columns:200px 1fr;
                gap:2rem; align-items:start;">

        <!-- Menu laterale profilo -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); overflow:hidden;">
            <div style="padding:1.5rem; text-align:center;
                        border-bottom:1px solid var(--color-border);">
                <div style="width:64px; height:64px; border-radius:50%;
                            background:var(--color-accent);
                            display:flex; align-items:center;
                            justify-content:center; font-size:1.8rem;
                            margin:0 auto 0.75rem;">
                    👤
                </div>
                <div style="font-weight:700;">
                    ${fn:escapeXml(sessionScope.utente.nome)}
                    ${fn:escapeXml(sessionScope.utente.cognome)}
                </div>
                <div style="font-size:0.8rem; color:#666; margin-top:0.2rem;">
                    ${fn:escapeXml(sessionScope.utente.email)}
                </div>
            </div>
            <nav style="padding:0.5rem 0;">
                <a href="${pageContext.request.contextPath}/profilo"
                   style="display:block; padding:0.7rem 1.5rem;
                          font-size:0.9rem; color:var(--color-text);
                          background:var(--color-bg); font-weight:600;">
                    ✏️ Modifica profilo
                </a>
                <a href="${pageContext.request.contextPath}/ordini"
                   style="display:block; padding:0.7rem 1.5rem;
                          font-size:0.9rem; color:var(--color-text);">
                    📦 I miei ordini
                </a>
                <a href="${pageContext.request.contextPath}/wishlist"
                   style="display:block; padding:0.7rem 1.5rem;
                          font-size:0.9rem; color:var(--color-text);">
                    🤍 Lista desideri
                </a>
                <form method="post" action="${pageContext.request.contextPath}/logout">
                    <input type="hidden" name="token" value="${sessionScope.token}">
                    <button type="submit" class="nav-link-button"
                        style="padding:0.7rem 1.5rem; color:var(--color-danger);">🚪 Esci</button>
                </form>
            </nav>
        </div>

        <!-- Form modifica profilo -->
        <div style="background:white; border-radius:var(--radius);
                    box-shadow:var(--shadow); padding:2rem;">

            <c:if test="${not empty errore}">
                <div class="alert alert-error">${errore}</div>
            </c:if>
            <c:if test="${not empty successo}">
                <div class="alert alert-success">${successo}</div>
            </c:if>

            <form id="formProfilo" method="post"
                  action="${pageContext.request.contextPath}/profilo">

                <input type="hidden" name="token"
                       value="${sessionScope.token}">

                <h2 style="font-size:1rem; font-weight:700;
                           margin-bottom:1.5rem; padding-bottom:0.75rem;
                           border-bottom:1px solid var(--color-border);">
                    Dati personali
                </h2>

                <div style="display:grid;
                            grid-template-columns:1fr 1fr; gap:1rem;">
                    <div class="form-group">
                        <label for="nome">Nome *</label>
                        <input type="text" id="nome" name="nome"
                               value="${fn:escapeXml(sessionScope.utente.nome)}">
                        <div class="form-error" id="nome-error"></div>
                    </div>
                    <div class="form-group">
                        <label for="cognome">Cognome *</label>
                        <input type="text" id="cognome" name="cognome"
                               value="${fn:escapeXml(sessionScope.utente.cognome)}">
                        <div class="form-error" id="cognome-error"></div>
                    </div>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input type="email" value="${fn:escapeXml(sessionScope.utente.email)}"
                           disabled
                           style="background:#f5f5f5; cursor:not-allowed;">
                    <small style="color:#999;">
                        L'email non può essere modificata.
                    </small>
                </div>

                <div class="form-group">
                    <label for="telefono">Telefono</label>
                    <input type="tel" id="telefono" name="telefono"
                           placeholder="+39 333 1234567"
                           value="${fn:escapeXml(sessionScope.utente.telefono)}">
                </div>

                <h2 style="font-size:1rem; font-weight:700;
                           margin:1.5rem 0; padding-bottom:0.75rem;
                           border-bottom:1px solid var(--color-border);">
                    Indirizzo di spedizione predefinito
                </h2>

                <div class="form-group">
                    <label for="indirizzo">Via / Piazza</label>
                    <input type="text" id="indirizzo" name="indirizzo"
                           placeholder="Es. Via Roma 42"
                           value="${fn:escapeXml(sessionScope.utente.indirizzo)}">
                </div>

                <div style="display:grid;
                            grid-template-columns:1fr 120px; gap:1rem;">
                    <div class="form-group">
                        <label for="citta">Città</label>
                        <input type="text" id="citta" name="citta"
                               placeholder="Es. Napoli"
                               value="${fn:escapeXml(sessionScope.utente.citta)}">
                    </div>
                    <div class="form-group">
                        <label for="cap">CAP</label>
                        <input type="text" id="cap" name="cap"
                               placeholder="80100" maxlength="5"
                               value="${fn:escapeXml(sessionScope.utente.cap)}">
                    </div>
                </div>

                <h2 style="font-size:1rem; font-weight:700;
                           margin:1.5rem 0; padding-bottom:0.75rem;
                           border-bottom:1px solid var(--color-border);">
                    Cambia password
                    <span style="font-weight:400; font-size:0.85rem;
                                 color:#999;">
                        (lascia vuoto per non cambiare)
                    </span>
                </h2>

                <div class="form-group">
                    <label for="vecchiaPassword">Password attuale</label>
                    <input type="password" id="vecchiaPassword"
                           name="vecchiaPassword"
                           placeholder="Inserisci la password attuale">
                </div>

                <div class="form-group">
                    <label for="nuovaPassword">Nuova password</label>
                    <input type="password" id="nuovaPassword"
                           name="nuovaPassword"
                           placeholder="Minimo 8 caratteri">
                    <div class="form-error" id="nuovaPassword-error"></div>
                </div>

                <button type="submit" class="btn btn-primary"
                        style="margin-top:0.5rem;">
                    Salva modifiche
                </button>

            </form>
        </div>
    </div>
</div>
</main>

<script src="${pageContext.request.contextPath}/scripts/validazione.js"></script>
<%@ include file="common/footer.jsp" %>