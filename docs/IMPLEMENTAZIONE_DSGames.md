# DS Games — Stato implementazione e tracciabilità

**Base esaminata:** `DSGames.zip` fornito nel progetto, più la guida DS Games TSW e le istruzioni del corso IS. Questo documento è una ricognizione **del codice**, non un RAD approvato dal docente.

| Ambito | Componenti di codice | Risultato / limiti |
|---|---|---|
| Catalogo, ricerca e offerte | `CatalogoServlet`, `ProdottoServlet`, `RicercaAjaxServlet`, JSP | Filtri categoria/piattaforma/testo; ricerca AJAX |
| Carrello ospiti | `CarrelloServlet`, `CarrelloAjaxServlet`, JSP | Carrello in sessione, modifiche POST, limite scorte |
| Autenticazione e registrazione | `LoginServlet`, `RegistrazioneServlet`, `PasswordUtil` | Sessioni, PBKDF2, compatibilità vecchi hash SHA-256 |
| Checkout | `CheckoutServlet`, `OrdineDAOImpl` | Prezzo da DB, scorte bloccate e decrementate, rollback atomico |
| Storico ordini | `OrdiniServlet`, `OrdineDAOImpl` | Solo ordini del cliente proprietario; prezzo storico |
| Lista desideri | `WishlistServlet`, `WishlistAjaxServlet` | Modifiche POST e solo per utente autenticato |
| Gestione prodotti | `AdminProdottiServlet`, `ProdottoDAOImpl` | CRUD con soft-delete, ruolo amministratore |
| Gestione ordini | `AdminOrdiniServlet`, JSP | Consultazione e filtri amministratore |
| Sicurezza web | `CsrfFilter`, `AdminFilter`, `CsrfToken`, JSP | Token imprevedibile su POST, session fixation mitigata, escaping dati |
| Architettura | Servlet/JSP + DAO/JDBC + MySQL | 3-tier logico: client web, Tomcat 9, DBMS MySQL 8 separabile |

## Vincoli e chiarimenti

- I tre livelli presentation/application/database possono essere installati su macchine distinte. Per configurare un DB remoto si modifica l'URL JDBC nel file locale Tomcat.
- Lo stato pagamento è **solo un metodo dichiarato dall'utente**: non è integrato un provider di transazioni con carta/PayPal. Non gestire dati reali di carte.
- Le immagini sono percorsi statici; il caricamento file non è implementato.
- Le modifiche introdotte (CSRF, aggiornamento giacenze, PBKDF2) sono miglioramenti ingegneristici desunti da criticità del sorgente e **non sono da presentare come requisiti approvati** senza riscontro nel RAD del gruppo.
- Non è possibile ricostruire retroattivamente la storia dei commit dei singoli componenti del gruppo da uno ZIP.
