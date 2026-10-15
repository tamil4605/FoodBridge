/**
 * FoodBridge Platform - Client-side Interactivity
 * Plain Vanilla JavaScript + Fetch API
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize notification badge
    updateNotificationBadge();

    // 2. Setup preset contribution buttons if present
    initPresetDonationButtons();

    // 3. Setup quick demo login buttons if on login page
    initQuickDemoLogin();

    // 4. Setup quantity input validation
    initQuantityValidation();
});

// Periodic notification badge update (every 60 seconds)
setInterval(updateNotificationBadge, 60000);

function updateNotificationBadge() {
    const badge = document.getElementById('notifBadge');
    if (!badge) return;

    fetch('/api/notifications/unread-count')
        .then(res => res.json())
        .then(data => {
            if (data && data.unreadCount > 0) {
                badge.innerText = data.unreadCount;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        })
        .catch(() => {
            // Silently ignore if not logged in or network error
        });
}

function markNotificationAsRead(id, element) {
    fetch('/api/notifications/read/' + id, {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            if (element) {
                element.classList.remove('unread-notification');
                element.classList.add('read-notification');
            }
            updateNotificationBadge();
        }
    })
    .catch(err => console.error('Error marking notification as read:', err));
}

function markAllNotificationsAsRead() {
    fetch('/api/notifications/read-all', {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            document.querySelectorAll('.unread-notification').forEach(el => {
                el.classList.remove('unread-notification');
                el.classList.add('read-notification');
            });
            updateNotificationBadge();
            const btn = document.getElementById('markAllReadBtn');
            if (btn) btn.disabled = true;
        }
    })
    .catch(err => console.error('Error marking all notifications:', err));
}

function initPresetDonationButtons() {
    const presetButtons = document.querySelectorAll('.preset-btn');
    const amountInput = document.getElementById('donationAmountInput');

    if (!presetButtons.length || !amountInput) return;

    presetButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            presetButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const val = btn.getAttribute('data-amount');
            if (val) {
                amountInput.value = val;
            }
        });
    });

    amountInput.addEventListener('input', () => {
        presetButtons.forEach(b => {
            if (b.getAttribute('data-amount') === amountInput.value) {
                b.classList.add('active');
            } else {
                b.classList.remove('active');
            }
        });
    });
}

function initQuickDemoLogin() {
    const emailField = document.getElementById('loginEmail');
    const passField = document.getElementById('loginPassword');
    if (!emailField || !passField) return;

    window.fillDemoCredentials = function(email, password) {
        emailField.value = email;
        passField.value = password;
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.classList.add('highlight-form');
        }
    };
}

function initQuantityValidation() {
    const qtyInputs = document.querySelectorAll('input[type="number"][data-max-qty]');
    qtyInputs.forEach(input => {
        const max = parseFloat(input.getAttribute('data-max-qty'));
        input.addEventListener('input', () => {
            const val = parseFloat(input.value);
            const errorMsg = document.getElementById(input.id + '-error');
            if (val > max) {
                if (errorMsg) errorMsg.innerText = 'Cannot exceed maximum available (' + max + ')';
                input.setCustomValidity('Cannot exceed available quantity');
            } else if (val <= 0) {
                if (errorMsg) errorMsg.innerText = 'Quantity must be greater than zero';
                input.setCustomValidity('Quantity must be greater than zero');
            } else {
                if (errorMsg) errorMsg.innerText = '';
                input.setCustomValidity('');
            }
        });
    });
}

function confirmAction(message) {
    return confirm(message || 'Are you sure you want to proceed with this action?');
}
