/* ============================================================
   CareerPro AI - Global JavaScript & API Client
   ============================================================ */

const API_BASE_URL = 'http://localhost:8080/api';

// --- Local Storage Keys ---
const TOKEN_KEY = 'careerpro_token';
const USER_ID_KEY = 'careerpro_user_id';
const USER_ROLE_KEY = 'careerpro_user_role';
const USER_NAME_KEY = 'careerpro_user_name';
const USER_EMAIL_KEY = 'careerpro_user_email';

// --- API Client ---
const api = {
    async request(endpoint, method = 'GET', body = null, isMultipart = false) {
        const url = `${API_BASE_URL}${endpoint}`;
        const headers = {};

        const token = localStorage.getItem(TOKEN_KEY);
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method,
            headers
        };

        if (body) {
            if (isMultipart) {
                config.body = body; // Let browser set multipart/form-data with boundary
            } else {
                headers['Content-Type'] = 'application/json';
                config.body = JSON.stringify(body);
            }
        }

        try {
            const response = await fetch(url, config);

            // Safely parse JSON — handle cases where backend returns non-JSON
            let data;
            try {
                data = await response.json();
            } catch (parseErr) {
                throw new Error('Server error. Please try again later.');
            }

            if (!response.ok) {
                // Handle 401 Unauthorized
                if (response.status === 401) {
                    auth.logout(false);
                    const path = window.location.pathname;
                    const isLoginPage = path.endsWith('login.html') || path.endsWith('register.html') || path.endsWith('index.html');
                    if (!isLoginPage) {
                        window.location.href = 'login.html';
                    }
                }
                throw new Error(data.message || 'Something went wrong');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    get(endpoint) { return this.request(endpoint); },
    post(endpoint, body) { return this.request(endpoint, 'POST', body); },
    put(endpoint, body) { return this.request(endpoint, 'PUT', body); },
    delete(endpoint) { return this.request(endpoint, 'DELETE'); },
    upload(endpoint, formData) { return this.request(endpoint, 'POST', formData, true); }
};

// --- Authentication Module ---
const auth = {
    isAuthenticated() {
        return !!localStorage.getItem(TOKEN_KEY);
    },
    
    isAdmin() {
        return localStorage.getItem(USER_ROLE_KEY) === 'ROLE_ADMIN';
    },

    setSession(authData) {
        localStorage.setItem(TOKEN_KEY, authData.token);
        localStorage.setItem(USER_ID_KEY, authData.userId);
        localStorage.setItem(USER_ROLE_KEY, authData.role);
        localStorage.setItem(USER_NAME_KEY, authData.fullName);
        localStorage.setItem(USER_EMAIL_KEY, authData.email);
    },

    logout(redirect = true) {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_ID_KEY);
        localStorage.removeItem(USER_ROLE_KEY);
        localStorage.removeItem(USER_NAME_KEY);
        localStorage.removeItem(USER_EMAIL_KEY);
        if (redirect) {
            // Use relative path that works both on file:// and http://
            const depth = window.location.pathname.includes('/pages/') ? '../' : '';
            window.location.href = depth + 'index.html';
        }
    },

    checkAuth() {
        const currentPath = window.location.pathname;
        // Pages that do NOT require authentication
        const publicPageNames = ['index.html', 'login.html', 'register.html', 'forgot-password.html'];
        const isPublicPage = publicPageNames.some(name => currentPath.endsWith(name))
            || currentPath === '/' || currentPath.endsWith('/');

        if (!this.isAuthenticated() && !isPublicPage) {
            // Not logged in on a protected page → go to login
            window.location.href = 'login.html';
        } else if (this.isAuthenticated() && (currentPath.endsWith('login.html') || currentPath.endsWith('register.html'))) {
            // Already logged in, skip login/register
            window.location.href = this.isAdmin() ? 'admin-dashboard.html' : 'dashboard.html';
        }

        // Prevent non-admin from accessing admin pages
        if (currentPath.includes('admin') && !this.isAdmin()) {
            window.location.href = 'dashboard.html';
        }
    }
};

// --- UI Utilities ---
const ui = {
    showToast(message, type = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        let icon = '';
        if (type === 'success') icon = '<i class="bi bi-check-circle-fill text-success" style="font-size:1.2rem;"></i>';
        if (type === 'error') icon = '<i class="bi bi-exclamation-triangle-fill text-error" style="font-size:1.2rem;"></i>';
        if (type === 'warning') icon = '<i class="bi bi-info-circle-fill text-warning" style="font-size:1.2rem;"></i>';

        toast.innerHTML = `
            ${icon}
            <div style="flex:1">
                <p style="margin:0; font-weight:600; color:#1e293b;">${type === 'success' ? 'Success' : type === 'error' ? 'Error' : 'Notice'}</p>
                <p style="margin:0; font-size:0.85rem; color:#475569;">${message}</p>
            </div>
            <button style="background:none; border:none; color:#94a3b8; font-size:1.2rem; cursor:pointer;" onclick="this.parentElement.remove()">
                <i class="bi bi-x"></i>
            </button>
        `;

        container.appendChild(toast);
        
        // Auto remove after 4 seconds
        setTimeout(() => {
            if(toast.parentElement) {
                toast.style.animation = 'fadeOut 0.3s ease forwards';
                setTimeout(() => toast.remove(), 300);
            }
        }, 4000);
    },

    showLoading(message = 'Processing...') {
        const overlay = document.createElement('div');
        overlay.id = 'global-loading';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="spinner"></div>
            <p style="font-weight:600; color:var(--primary); font-size:1.1rem;">${message}</p>
        `;
        document.body.appendChild(overlay);
    },

    hideLoading() {
        const overlay = document.getElementById('global-loading');
        if (overlay) overlay.remove();
    },
    
    toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        if(sidebar) sidebar.classList.toggle('open');
    },

    setUserName() {
        const nameElems = document.querySelectorAll('.user-display-name');
        const userName = localStorage.getItem(USER_NAME_KEY) || 'User';
        nameElems.forEach(el => el.textContent = userName);
        
        const avatarElems = document.querySelectorAll('.user-avatar-initials');
        const initials = userName.split(' ').map(n => n[0]).join('').substring(0,2).toUpperCase();
        avatarElems.forEach(el => el.textContent = initials);
    },

    initTheme() {
        const theme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', theme);
        const toggleBtn = document.getElementById('theme-toggle');
        if(toggleBtn) {
            toggleBtn.innerHTML = theme === 'dark' ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-stars-fill"></i>';
        }
    },

    toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        localStorage.setItem('theme', next);
        const toggleBtn = document.getElementById('theme-toggle');
        if(toggleBtn) {
            toggleBtn.innerHTML = next === 'dark' ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-stars-fill"></i>';
        }
    }
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    // Check Authentication on every page load
    auth.checkAuth();
    
    // Initialize Theme
    ui.initTheme();

    // Set user info if logged in
    if (auth.isAuthenticated()) {
        ui.setUserName();
    }

    // Bind Logout Button
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            auth.logout();
        });
    }

    // Bind Theme Toggle
    const themeBtn = document.getElementById('theme-toggle');
    if (themeBtn) {
        themeBtn.addEventListener('click', ui.toggleTheme);
    }

    // Bind Sidebar Toggle
    const menuToggle = document.getElementById('mobile-menu-btn');
    if (menuToggle) {
        menuToggle.addEventListener('click', ui.toggleSidebar);
    }
});
