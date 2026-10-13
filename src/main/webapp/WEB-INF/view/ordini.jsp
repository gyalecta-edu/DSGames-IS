<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="I miei ordini — DS Games"/>
<%@ include file="common/header.jsp" %>

<main>
<div class="container" style="padding-top:2rem; padding-bottom:3rem;">

    <h1 class="section-title">📦 I miei ordini</h1>

    <c:if test="${not empty conferma}">
        <div class="alert alert-success">${conferma}</div>
    </c:if>

    <c:if test="${not empty errore}">
        <div class="alert alert-error">${errore}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty ordini}">
            <div style="text-align:center; padding:4rem; color:#666;">
                <div style="font-size:3rem;">📭</div>
                <h3 style="margin-top:1rem;">Nessun ordine ancora</h3>
                <p style="margin-top:0.5rem;">
                    Inizia a fare acquisti!
                </p>
                <a href="${pageContext.request.contextPath}/catalogo"
                   class="btn btn-primary" style="margin-top:1.5rem;">
                    Vai al catalogo
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div style="background:white; border-radius:var(--radius);
                        box-shadow:var(--shadow); overflow:hidden;">

                <!-- Header tabella -->
                <div style="display:grid;
                            grid-template-columns:80px 1fr 140px 120px 100px;
                            padding:0.75rem 1.5rem;
                            background:var(--color-bg);
                            font-size:0.8rem; font-weight:700;
                            text-transform:uppercase; color:#666;
                            border-bottom:1px solid var(--color-border);">
                    <span>N° ordine</span>
                    <span>Data</span>
                    <span>Totale</span>
                    <span>Stato</span>
                    <span></span>
                </div>

                <c:forEach var="o" items="${ordini}" varStatus="status">
                    <div style="display:grid;
                                grid-template-columns:80px 1fr 140px 120px 100px;
                                padding:1rem 1.5rem; align-items:center;
                                ${!status.last ? 'border-bottom:1px solid var(--color-border);' : ''}">

                        <span style="font-weight:700; color:var(--color-accent);">
                            #${o.id}
                        </span>

                        <span style="font-size:0.9rem;">
                            <fmt:formatDate value="${o.dataOrdine}"
                                pattern="dd/MM/yyyy HH:mm"/>
                        </span>

                        <span style="font-weight:600;">
                            <fmt:formatNumber value="${o.totale}"
                                type="currency" currencySymbol="€"/>
                        </span>

                        <span>
                            <c:choose>
                                <c:when test="${o.stato == 'IN_ELABORAZIONE'}">
                                    <span style="background:#fff3cd; color:#856404;
                                                 padding:0.25rem 0.6rem;
                                                 border-radius:4px; font-size:0.8rem;
                                                 font-weight:600;">
                                        In elaborazione
                                    </span>
                                </c:when>
                                <c:when test="${o.stato == 'SPEDITO'}">
                                    <span style="background:#cfe2ff; color:#084298;
                                                 padding:0.25rem 0.6rem;
                                                 border-radius:4px; font-size:0.8rem;
                                                 font-weight:600;">
                                        Spedito
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span style="background:#d1fae5; color:#065f46;
                                                 padding:0.25rem 0.6rem;
                                                 border-radius:4px; font-size:0.8rem;
                                                 font-weight:600;">
                                        Consegnato
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </span>

                        <a href="${pageContext.request.contextPath}/ordini?id=${o.id}"
                           class="btn btn-sm"
                           style="border:1px solid var(--color-border);">
                            Dettagli
                        </a>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</div>
</main>

<%@ include file="common/footer.jsp" %>