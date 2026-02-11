import { userService } from '../../../domains/user/UserService.js';

// Ensure navigate is available (it should be global from app.js)
if (!window.navigate) {
    console.error("Navigation function not found!");
}

export class UserManager {
    constructor() {
        this.users = [];
        this.searchQuery = '';
        this.currentSort = {
            field: 'id',
            order: 'asc'
        };
        this.init();
    }

    async init() {
        this.bindEvents();
        await this.loadUsers();
    }

    bindEvents() {
        // Search Input
        const searchInput = document.getElementById('user-search');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchQuery = e.target.value.toLowerCase();
                this.renderTable();
            });
        }







        // Table Sorting
        document.querySelectorAll('.sortable').forEach(th => {
            th.addEventListener('click', () => this.handleSort(th.dataset.field));
        });
    }

    async loadUsers() {
        try {
            this.users = await userService.getAll();
            this.applySort();
            this.renderTable();
        } catch (error) {
            console.error('Error loading users:', error);
            this.showToast('error', 'Failed to load users.');
        }
    }

    handleSort(field) {
        if (this.currentSort.field === field) {
            this.currentSort.order = this.currentSort.order === 'asc' ? 'desc' : 'asc';
        } else {
            this.currentSort.field = field;
            this.currentSort.order = 'asc';
        }

        this.updateSortIcons();
        this.applySort();
        this.renderTable();
    }

    applySort() {
        const { field, order } = this.currentSort;
        this.users.sort((a, b) => {
            let valA = a[field];
            let valB = b[field];

            if (valA === null || valA === undefined) valA = '';
            if (valB === null || valB === undefined) valB = '';

            if (field === 'id') {
                return order === 'asc' ? valA - valB : valB - valA;
            }

            valA = valA.toString().toLowerCase();
            valB = valB.toString().toLowerCase();

            if (valA < valB) return order === 'asc' ? -1 : 1;
            if (valA > valB) return order === 'asc' ? 1 : -1;
            return 0;
        });
    }

    updateSortIcons() {
        document.querySelectorAll('.sortable i').forEach(icon => {
            icon.className = 'fas fa-sort';
            icon.style.opacity = '0.5';
        });

        const activeTh = document.querySelector(`.sortable[data-field="${this.currentSort.field}"]`);
        if (activeTh) {
            const icon = activeTh.querySelector('i');
            icon.className = this.currentSort.order === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
            icon.style.opacity = '1';
        }
    }

    getFilteredUsers() {
        if (!this.searchQuery) return this.users;

        return this.users.filter(user => {
            const username = (user.username || '').toLowerCase();
            const email = (user.email || '').toLowerCase();
            const phone = (user.phone || '').toLowerCase();
            const role = (user.role || '').toLowerCase();
            const id = (user.id || '').toString();

            return username.includes(this.searchQuery) ||
                email.includes(this.searchQuery) ||
                phone.includes(this.searchQuery) ||
                role.includes(this.searchQuery) ||
                id.includes(this.searchQuery);
        });
    }

    renderTable() {
        const tbody = document.getElementById('user-table-body');
        tbody.innerHTML = '';

        const filteredUsers = this.getFilteredUsers();

        if (filteredUsers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #eee; padding: 3rem; font-size: 1.2rem; font-weight: 700; background: rgba(0,0,0,0.2);">No users found.</td></tr>`;
            return;
        }

        filteredUsers.forEach(user => {
            const tr = document.createElement('tr');
            const isAdmin = user.role && user.role.toUpperCase() === 'ADMIN';

            tr.innerHTML = `
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.phone || '-'}</td>
                <td>
                    ${isAdmin ? `
                        <span class="role-badge admin">${user.role}</span>
                    ` : `
                        <select class="table-select role-select" data-id="${user.id}">
                            <option value="CUSTOMER" ${(user.role || '').toUpperCase() === 'CUSTOMER' ? 'selected' : ''}>Customer</option>
                            <option value="EMPLOYEE" ${(user.role || '').toUpperCase() === 'EMPLOYEE' ? 'selected' : ''}>Employee</option>
                            <option value="ADMIN" ${(user.role || '').toUpperCase() === 'ADMIN' ? 'selected' : ''}>Admin</option>
                        </select>
                    `}
                </td>
                <td>
                    <div class="action-buttons">
                        ${!isAdmin ? `
                            <button class="action-btn delete-btn" data-id="${user.id}" title="Delete User">
                                <i class="fas fa-trash"></i>
                            </button>
                        ` : ''}
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Add event listeners to buttons
        tbody.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => this.handleDelete(btn.dataset.id));
        });

        // Add event listener for direct role update from table
        tbody.querySelectorAll('.role-select').forEach(select => {
            select.addEventListener('change', (e) => this.handleRoleUpdate(select.dataset.id, e.target.value));
        });
    }

    async handleRoleUpdate(userId, newRole) {
        try {
            await userService.updateStatus(userId, newRole);
            Swal.fire({
                icon: 'success',
                title: 'Updated!',
                text: 'User role updated successfully.',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                background: '#1a1a1a',
                color: '#fff'
            });
            await this.loadUsers();
        } catch (error) {
            console.error(error);
            Swal.fire({
                icon: 'error',
                title: 'Update Failed',
                text: error.message,
                background: '#1a1a1a',
                color: '#fff'
            });
            await this.loadUsers(); // Revert on failure
        }
    }

    async handleDelete(userId) {
        const result = await Swal.fire({
            title: 'Delete User?',
            text: "Are you sure you want to delete this user? This action cannot be undone.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff5e00',
            cancelButtonColor: '#333',
            confirmButtonText: 'Yes, Delete',
            background: '#1a1a1a',
            color: '#fff',
            customClass: {
                popup: 'premium-modal-swal'
            }
        });

        if (result.isConfirmed) {
            try {
                // Mock delete as backend doesn't support it yet
                Swal.fire({
                    title: 'Not Implemented',
                    text: 'Delete functionality is not yet implemented on the backend.',
                    icon: 'info',
                    background: '#1a1a1a',
                    color: '#fff',
                    confirmButtonColor: '#ff5e00'
                });
            } catch (error) {
                this.showToast('error', 'Failed to delete user.');
            }
        }
    }



    showToast(type, message) {
        Swal.fire({
            icon: type === 'success' ? 'success' : 'error',
            title: message,
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            background: '#1a1a1a',
            color: '#fff'
        });
    }
}

export function init() {
    new UserManager();
}
