/**
 * DS Games — Validazione form lato client
 * Mostra messaggi di errore inline senza usare alert()
 */

// Mostra un messaggio di errore sotto un campo
function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    let errorDiv = document.getElementById(fieldId + '-error');

    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.id = fieldId + '-error';
        errorDiv.className = 'form-error';
        field.parentNode.appendChild(errorDiv);
    }

    errorDiv.textContent = message;
    errorDiv.classList.add('visible');
    field.style.borderColor = 'var(--color-danger)';
}

// Rimuove il messaggio di errore da un campo
function clearError(fieldId) {
    const field = document.getElementById(fieldId);
    const errorDiv = document.getElementById(fieldId + '-error');

    if (errorDiv) errorDiv.classList.remove('visible');
    if (field) field.style.borderColor = '';
}

// Regex comuni
const REGEX = {
    email:    /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    nome:     /^[a-zA-ZàèéìòùÀÈÉÌÒÙ\s'-]{2,50}$/,
    cap:      /^\d{5}$/,
    telefono: /^[+\d\s\-()]{7,20}$/
};

// =============================================
// VALIDAZIONE FORM REGISTRAZIONE
// =============================================
function initValidazioneRegistrazione() {
    const form = document.getElementById('formRegistrazione');
    if (!form) return;

    const campi = {
        nome:             { regex: REGEX.nome,  msg: 'Inserisci un nome valido (solo lettere, 2-50 caratteri).' },
        cognome:          { regex: REGEX.nome,  msg: 'Inserisci un cognome valido (solo lettere, 2-50 caratteri).' },
        email:            { regex: REGEX.email, msg: 'Inserisci un indirizzo email valido.' },
        password:         { min: 8,             msg: 'La password deve contenere almeno 8 caratteri.' },
        confermaPassword: { match: 'password',  msg: 'Le password non coincidono.' }
    };

    // Validazione al cambio campo (evento change)
    Object.keys(campi).forEach(id => {
        const field = document.getElementById(id);
        if (!field) return;

        field.addEventListener('change', () => validateField(id, campi[id]));
        field.addEventListener('blur',   () => validateField(id, campi[id]));
    });

    // Validazione al submit
    form.addEventListener('submit', function(e) {
        let valid = true;
        Object.keys(campi).forEach(id => {
            if (!validateField(id, campi[id])) valid = false;
        });
        if (!valid) e.preventDefault();
    });
}

function validateField(id, rules) {
    const field = document.getElementById(id);
    if (!field) return true;

    const value = field.value.trim();

    if (!value) {
        showError(id, 'Questo campo è obbligatorio.');
        return false;
    }

    if (rules.regex && !rules.regex.test(value)) {
        showError(id, rules.msg);
        return false;
    }

    if (rules.min && value.length < rules.min) {
        showError(id, rules.msg);
        return false;
    }

    if (rules.match) {
        const matchField = document.getElementById(rules.match);
        if (matchField && value !== matchField.value) {
            showError(id, rules.msg);
            return false;
        }
    }

    clearError(id);
    return true;
}

// =============================================
// VALIDAZIONE FORM LOGIN
// =============================================
function initValidazioneLogin() {
    const form = document.getElementById('formLogin');
    if (!form) return;

    const emailField    = document.getElementById('email');
    const passwordField = document.getElementById('password');

    emailField.addEventListener('change', () => {
        if (!REGEX.email.test(emailField.value.trim())) {
            showError('email', 'Inserisci un indirizzo email valido.');
        } else {
            clearError('email');
        }
    });

    form.addEventListener('submit', function(e) {
        let valid = true;

        if (!emailField.value.trim()) {
            showError('email', 'Inserisci la tua email.'); valid = false;
        } else if (!REGEX.email.test(emailField.value.trim())) {
            showError('email', 'Email non valida.'); valid = false;
        } else {
            clearError('email');
        }

        if (!passwordField.value) {
            showError('password', 'Inserisci la tua password.'); valid = false;
        } else {
            clearError('password');
        }

        if (!valid) e.preventDefault();
    });
}

// =============================================
// VALIDAZIONE FORM CHECKOUT
// =============================================
function initValidazioneCheckout() {
    const form = document.getElementById('formCheckout');
    if (!form) return;

    const campi = {
        indirizzo: { min: 5,  msg: 'Inserisci un indirizzo valido (minimo 5 caratteri).' },
        citta:     { regex: REGEX.nome, msg: 'Inserisci una città valida.' },
        cap:       { regex: REGEX.cap,  msg: 'Il CAP deve essere composto da 5 cifre.' }
    };

    Object.keys(campi).forEach(id => {
        const field = document.getElementById(id);
        if (!field) return;
        field.addEventListener('change', () => validateField(id, campi[id]));
        field.addEventListener('blur',   () => validateField(id, campi[id]));
    });

    form.addEventListener('submit', function(e) {
        let valid = true;
        Object.keys(campi).forEach(id => {
            if (!validateField(id, campi[id])) valid = false;
        });

        const pagamento = document.getElementById('metodoPagamento');
        if (pagamento && !pagamento.value) {
            showError('metodoPagamento', 'Seleziona un metodo di pagamento.');
            valid = false;
        } else if (pagamento) {
            clearError('metodoPagamento');
        }

        if (!valid) e.preventDefault();
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initValidazioneRegistrazione();
    initValidazioneLogin();
    initValidazioneCheckout();
});