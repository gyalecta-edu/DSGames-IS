# DS Games — Piano di test tecnico

## Ambito

Stralcio implementato: catalogo → carrello → checkout → ordine, autenticazione, back-office. Test JUnit 5 nel modulo Maven. Sistema target: Java 17, Tomcat 9, MySQL 8, Servlet `javax.*`.

## Test automatici inclusi

| Test | Precondizioni/input | Oracolo |
|---|---|---|
| PasswordUtilTest | Password nuova, corretta e errata | Verifica PBKDF2 riuscita o fallita; salt differente |
| PasswordUtilTest | Hash legacy SHA-256 valido o corrotto | Compatibilità con hash 64 esadecimali; non accetta hash errati |
| OrdineDAOImplTest | 3 copie, ordine per 2, offerta 79,95 | Scorta finale 1, ordine 159,90, prezzo da DB |
| OrdineDAOImplTest | 3 copie, ordine per 4 | Eccezione, rollback: giacenza 3 e zero ordini |

## Category Partition — checkout (proposta test di sistema)

Categorie e scelte: utente `{autenticato, anonimo}`, carrello `{vuoto, valido, stock insufficiente}`, CSRF `{valido, mancante/errato}`, CAP `{5 cifre, invalido}`, pagamento `{tra metodi previsti, non previsto}`. Gli scenari selezionati:

| ID | Combinazione | Esito atteso |
|---|---|---|
| TC-CHK-01 | Autenticato, carrello valido, CSRF valido, CAP valido, pagamento valido | Ordine inserito, scorte scalate, carrello svuotato |
| TC-CHK-02 | Anonimo | Redirect al login, carrello conservato nella sessione |
| TC-CHK-03 | Autenticato, carrello vuoto | Redirect al carrello, nessun ordine |
| TC-CHK-04 | CSRF assente o errato | HTTP 403, nessuna modifica dati |
| TC-CHK-05 | CAP invalido | Messaggio inline, nessun ordine |
| TC-CHK-06 | Metodo pagamento non previsto | Messaggio, nessun ordine |
| TC-CHK-07 | Stock diminuito dopo l'aggiunta al carrello | Ordine non inserito, nessun decremento parziale |
| TC-CHK-08 | Due checkout concorrenti per l'ultima copia | Al massimo uno effettua l'acquisto; l'altro fallisce |
| TC-AUTH-01 | Credenziali errate | Login negato, messaggio generico |
| TC-ADM-01 | Utente non admin sulla URL `/admin/prodotti` | HTTP 403 |
| TC-ADM-02 | Elimina prodotto via GET | Nessun effetto persistente |

Per completare il Test Execution Report occorre eseguire i test con ambiente Tomcat+MySQL e registrare **data**, **versione**, **esito reale**, **difetti**, **operatore**. Qui sono specificati i casi e gli oracoli, non risultati non verificati.

## Comandi

`mvn clean verify` (test JUnit 5 + test DB in-memory H2) e `mvn clean package` (WAR in `target/DSGames.war`).
