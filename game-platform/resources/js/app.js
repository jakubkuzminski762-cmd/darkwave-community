import Alpine from 'alpinejs';

window.Alpine = Alpine;

Alpine.data('uiShell', () => ({
    mobileOpen: false,
    modalOpen: false,
    toastOpen: false,
    lastFocused: null,

    openMobile() {
        this.mobileOpen = true;
        this.$nextTick(() => this.$refs.mobileClose?.focus());
    },

    closeMobile() {
        this.mobileOpen = false;
        this.$nextTick(() => this.$refs.mobileTrigger?.focus());
    },

    openModal() {
        this.lastFocused = document.activeElement;
        this.modalOpen = true;
        document.documentElement.style.overflow = 'hidden';
        this.$nextTick(() => this.$refs.modalClose?.focus());
    },

    closeModal() {
        this.modalOpen = false;
        document.documentElement.style.overflow = '';
        this.$nextTick(() => this.lastFocused?.focus());
    },

    trapFocus(event) {
        if (!this.modalOpen) return;

        const modal = event.currentTarget;
        const focusable = [...modal.querySelectorAll(
            'a[href], button:not([disabled]), input:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
        )].filter((element) => !element.hasAttribute('hidden'));

        if (focusable.length === 0) return;

        const first = focusable[0];
        const last = focusable[focusable.length - 1];

        if (event.shiftKey && document.activeElement === first) {
            event.preventDefault();
            last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
            event.preventDefault();
            first.focus();
        }
    },

    showToast() {
        this.toastOpen = true;
        window.setTimeout(() => {
            this.toastOpen = false;
        }, 6000);
    },
}));

Alpine.start();
