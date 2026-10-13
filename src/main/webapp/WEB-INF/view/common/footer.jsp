<%@ page contentType="text/html;charset=UTF-8" %>

<footer>
    <div class="footer-inner">
        <div class="footer-col">
            <h4>DS Games</h4>
            <p style="font-size:0.9rem; line-height:1.6;">
                La tua piattaforma e-commerce per console e videogiochi.
            </p>
        </div>
        <div class="footer-col">
            <h4>Catalogo</h4>
            <a href="${pageContext.request.contextPath}/catalogo?categoria=CONSOLE">Console</a>
            <a href="${pageContext.request.contextPath}/catalogo?categoria=VIDEOGIOCO">Videogiochi</a>
            <a href="${pageContext.request.contextPath}/offerte">Offerte</a>
        </div>
        <div class="footer-col">
            <h4>Account</h4>
            <a href="${pageContext.request.contextPath}/login">Accedi</a>
            <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
            <a href="${pageContext.request.contextPath}/profilo">Il mio profilo</a>
            <a href="${pageContext.request.contextPath}/ordini">I miei ordini</a>
        </div>
        <div class="footer-col">
            <h4>Info</h4>
            <a href="#">Chi siamo</a>
            <a href="#">Spedizioni</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Contatti</a>
        </div>
    </div>
    <div class="footer-bottom">
        &copy; 2026 DS Games — Progetto TSW, Università degli Studi di Salerno
    </div>
</footer>

</body>
</html>