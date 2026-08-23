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

    showToast() {
        this.toastOpen = true;
        window.setTimeout(() => {
            this.toastOpen = false;
        }, 6000);
    },
}));

Alpine.start();
