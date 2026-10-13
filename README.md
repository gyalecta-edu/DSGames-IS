# DS Games — repository aggiornato

Piattaforma web e-commerce per console e videogiochi. Base originaria: progetto TSW UniSa 2025/26. La revisione del codice è stata eseguita usando l'archivio DSGames fornito e la guida di progetto disponibile, senza assumere come acquisiti requisiti o verbali non disponibili.

## Tecnologie e architettura

- Java 17, `javax.servlet` 4.0.1, JSP/JSTL (Tomcat **9**, non Tomcat 10).
- MySQL 8 via JDBC e pool `javax.sql.DataSource` JNDI `jdbc/dsgames`.
- Maven WAR; pattern MVC (Servlet / JSP / Bean), DAO (interfacce + implementazioni).
- Client browser, server applicativo Tomcat, DBMS MySQL costituiscono un'architettura three-tier potenzialmente distribuibile su tre macchine.

## Avvio su ambiente locale

1. Installare JDK 17, Maven 3.8+, Apache Tomcat 9 e MySQL 8.
2. Creare tabelle e dati applicativi: `mysql -u NOME_UTENTE -p < sql/dsgames.sql`.
3. **Solo per sviluppo**, caricare account e catalogo fittizi: `mysql -u NOME_UTENTE -p < sql/seed-demo.sql`. Gli account dimostrativi sono pubblici, **non usarli in produzione**.
4. Copiare `config/context.xml.example` in `$CATALINA_BASE/conf/Catalina/localhost/DSGames.xml` (creare le directory se necessario). Valorizzare `username`, `password`, host MySQL. **Non committare la copia reale nel repository**. Verificare che Tomcat abbia il driver JDBC MySQL disponibile all'applicazione (nel WAR o in `lib/`).
5. Compilare e verificare: `mvn clean verify`. Il WAR è `target/DSGames.war` e si può distribuire in `$CATALINA_BASE/webapps/`.
6. Avviare Tomcat e visitare `http://localhost:8080/DSGames/`.

Se l'applicazione è già installata e il database esiste, **non eseguire nuovamente** lo script iniziale. Fare sempre un backup del DB prima degli aggiornamenti. La lettura dei vecchi hash SHA-256 rimane compatibile; le password di nuovi utenti e i cambi password usano PBKDF2-SHA256. Per sicurezza, gli utenti già esistenti dovrebbero cambiare password dopo il primo accesso.

### Account locali dimostrativi (solo `seed-demo.sql`)

- Amministratore: `admin@dsgames.it` / `admin123`
- Cliente: `mario@test.it` / `test1234`

Per configurare un amministratore reale: registrare l'utente attraverso la UI e, da un accesso autorizzato a MySQL, assegnargli il ruolo con `UPDATE utente SET ruolo='ADMIN' WHERE email='email-registrata';`. Effettuare logout e un nuovo login.

## Cosa è stato corretto

- Checkout atomico: lock sulle righe prodotto, controllo disponibilità, decremento giacenza, calcolo prezzo sul database e rollback completo se il salvataggio fallisce.
- Token CSRF casuale per la sessione e controllo di tutte le richieste POST (form e AJAX); operazioni che modificano dati non sono più eseguite con URL GET.
- Password PBKDF2 con salt e compatibilità SHA-256 legacy, prevenzione della session fixation al login/registrazione.
- Correzione HTML del layout condiviso, escaping delle stringhe in JSP e HTML generato dalla ricerca AJAX, controlli lato server.
- Separazione del file di configurazione DB reale (escluso da Git), esempio configurabile e dati di test SQL separati.
- Test JUnit 5 + H2 per password, prezzi, scorte e rollback, con GitHub Actions `mvn clean verify`.

## Limiti / note importanti

- **Pagamento non reale**: il menu dei metodi di pagamento memorizza una preferenza, non addebita denaro. Non raccogliere numeri di carte reali.
- Lo ZIP contiene sorgenti, file Maven, JSP, CSS/JS, SQL, test, istruzioni e documentazione tecnica. **Non contiene** un database esistente, i segreti di configurazione, file immagini non presenti nell'originale, cronologia Git, un deploy eseguito o verbali docenti non forniti.
- Questo progetto nasce come TSW; per la consegna di Ingegneria del Software vanno confrontati `Problem Statement`, `RAD`, `SDD`, `ODD`, `Test Plan`, `Test Execution Report` del gruppo (vedere `Deliverables/README.md`). Lo stato di approvazione dei requisiti **non è verificabile** dai file ricevuti.
- I test completi devono essere eseguiti in un ambiente con Maven e dipendenze disponibili. La CI GitHub è configurata per eseguirli automaticamente sul repository.

## Struttura

`pom.xml` alla radice, `src/main/java/it/unisa/dsgames/{control,dao,model,util}`, `src/main/webapp`, `src/test/java`, `sql`, `config`, `docs`, `Deliverables`, `.github/workflows/build.yml`.

## Caricamento su GitHub

Estrarre lo ZIP e aprire il terminale **nella cartella che contiene `pom.xml`**. Nel repository già creato: `git add . && git commit -m "Aggiornamento DS Games: checkout, sicurezza e test" && git push`. Non reinizializzare Git se la cartella del repository contiene già `.git`: copiare solo i file aggiornati. Ogni membro del team dovrebbe eseguire personalmente i commit del proprio lavoro; lo ZIP non ricostruisce tale storico.
