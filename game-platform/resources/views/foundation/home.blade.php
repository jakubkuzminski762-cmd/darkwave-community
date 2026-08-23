@extends('layouts.public')

@section('title', 'Etap 1 — Design system | '.config('app.name'))

@section('content')
    <div class="container breadcrumbs" aria-label="Okruszki">
        <a href="{{ url('/') }}">Platforma</a>
        <span class="breadcrumb-sep" aria-hidden="true">/</span>
        <span aria-current="page">Design system</span>
    </div>

    <section class="hero">
        <div class="container hero-grid">
            <div>
                <p class="eyebrow">Etap 1 · system interfejsu</p>
                <h1 class="font-display">Jedna marka.<br>Wiele światów.</h1>
                <p class="hero-lead">Spokojny, filmowy i premium interfejs, który pozostaje neutralnym tłem dla RPG, strategii, horrorów, gier logicznych i wszystkiego pomiędzy.</p>
                <div class="hero-actions">
                    <a class="btn btn-primary" href="#games">Zobacz karty gier</a>
                    <button class="btn btn-ghost" type="button" @click="openModal()">Otwórz modal</button>
                    <button class="btn btn-secondary" type="button" @click="showToast()">Pokaż toast</button>
                </div>
            </div>

            <aside class="hero-panel" aria-label="Przykładowa prezentacja gry">
                <div class="hero-panel-content">
                    <div class="badges">
                        <span class="badge badge-success">Premiera 2027</span>
                        <span class="badge badge-secondary">PC</span>
                    </div>
                    <h2 class="font-display hero-panel-title">Project Meridian</h2>
                    <p class="muted" style="margin:0">Przykładowy key art używa własnego klimatu, ale CTA i statusy pozostają w palecie platformy.</p>
                </div>
            </aside>
        </div>
    </section>

    <section id="games" class="section">
        <div class="container">
            <div class="section-head">
                <div>
                    <p class="eyebrow">Karty i klasyfikacja</p>
                    <h2 class="font-display">Komponenty dla wielu gatunków</h2>
                </div>
                <div class="badges" aria-label="Przykładowe filtry">
                    <button class="chip" type="button" aria-pressed="true">Wszystkie</button>
                    <button class="chip" type="button" aria-pressed="false">RPG</button>
                    <button class="chip" type="button" aria-pressed="false">Strategia</button>
                </div>
            </div>

            <div class="grid-3">
                <article class="game-card">
                    <div class="game-art" role="img" aria-label="Abstrakcyjny placeholder grafiki Project Meridian"></div>
                    <div class="game-card-body">
                        <div class="badges">
                            <span class="badge badge-secondary">RPG</span>
                            <span class="badge badge-success">Wkrótce</span>
                        </div>
                        <h3 class="font-display game-card-title">Project Meridian</h3>
                        <p class="game-card-copy">Filmowe RPG science-fiction. Karta zachowuje czytelną hierarchię bez dominującego neonowego stylu.</p>
                    </div>
                </article>

                <article class="game-card">
                    <div class="game-art alt" role="img" aria-label="Abstrakcyjny placeholder grafiki Iron Vale"></div>
                    <div class="game-card-body">
                        <div class="badges">
                            <span class="badge">Strategia</span>
                            <span class="badge badge-warning">Early access</span>
                        </div>
                        <h3 class="font-display game-card-title">Iron Vale</h3>
                        <p class="game-card-copy">Strategia taktyczna z cieplejszym art direction. Funkcjonalne kolory interfejsu pozostają niezmienne.</p>
                    </div>
                </article>

                <div class="skeleton-card" aria-label="Ładowanie karty gry" aria-busy="true">
                    <div class="skeleton skeleton-media"></div>
                    <div class="skeleton skeleton-line short"></div>
                    <div class="skeleton skeleton-line"></div>
                    <div class="skeleton skeleton-line"></div>
                </div>
            </div>
        </div>
    </section>

    <section id="tokens" class="section">
        <div class="container">
            <div class="section-head">
                <div>
                    <p class="eyebrow">Tokeny projektowe</p>
                    <h2 class="font-display">Paleta marki</h2>
                </div>
                <p class="muted font-mono" style="margin:0;font-size:.78rem">content-max: 1280px · radius: 8/14px · unit: 4px</p>
            </div>

            <div class="token-grid">
                <div class="token"><div class="token-swatch" style="--swatch:#080B12"></div><div class="token-label font-mono">bg #080B12</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#111725"></div><div class="token-label font-mono">surface #111725</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#192234"></div><div class="token-label font-mono">elevated #192234</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#5CE1B9"></div><div class="token-label font-mono">primary #5CE1B9</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#7A6BFF"></div><div class="token-label font-mono">secondary #7A6BFF</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#FF5C74"></div><div class="token-label font-mono">danger #FF5C74</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#F5B94C"></div><div class="token-label font-mono">warning #F5B94C</div></div>
                <div class="token"><div class="token-swatch" style="--swatch:#34D399"></div><div class="token-label font-mono">success #34D399</div></div>
            </div>
        </div>
    </section>

    <section id="components" class="section">
        <div class="container grid-2">
            <div class="panel panel-pad">
                <p class="eyebrow">Przyciski</p>
                <h2 class="font-display" style="margin:.45rem 0 1.2rem">Akcje i stany</h2>
                <div style="display:flex;flex-wrap:wrap;gap:.6rem">
                    <button class="btn btn-primary" type="button">Primary</button>
                    <button class="btn btn-secondary" type="button">Secondary</button>
                    <button class="btn btn-ghost" type="button">Ghost</button>
                    <button class="btn btn-danger" type="button">Destructive</button>
                    <button class="btn btn-primary btn-loading" type="button" disabled>Ładowanie</button>
                    <button class="btn btn-ghost" type="button" disabled>Disabled</button>
                </div>
            </div>

            <div id="states" class="panel panel-pad">
                <p class="eyebrow">Badge i alerty</p>
                <h2 class="font-display" style="margin:.45rem 0 1.2rem">Czytelne stany</h2>
                <div class="badges" style="margin-bottom:1rem">
                    <span class="badge badge-success">Opublikowano</span>
                    <span class="badge badge-warning">Wymaga uwagi</span>
                    <span class="badge badge-secondary">Windows</span>
                    <span class="badge">PEGI 12</span>
                </div>
                <div style="display:grid;gap:.65rem">
                    <div class="alert alert-success" role="status"><span aria-hidden="true">✓</span><div><strong>Gotowe</strong><p>Zmiany zostały zapisane poprawnie.</p></div></div>
                    <div class="alert alert-warning"><span aria-hidden="true">!</span><div><strong>Sprawdź dane</strong><p>Ten komunikat nie polega wyłącznie na kolorze.</p></div></div>
                </div>
            </div>
        </div>
    </section>

    <section id="forms" class="section">
        <div class="container grid-2">
            <div class="panel panel-pad">
                <p class="eyebrow">Formularze</p>
                <h2 class="font-display" style="margin:.45rem 0 1.2rem">Etykieta, pomoc i błąd</h2>
                <form class="form-grid" action="#" method="get" @submit.prevent>
                    <div class="field">
                        <label class="field-label" for="demo-title">Tytuł projektu</label>
                        <input class="field-control" id="demo-title" name="demo-title" type="text" value="Project Meridian" aria-describedby="demo-title-help">
                        <p class="field-help" id="demo-title-help">Nazwa widoczna w panelu dewelopera.</p>
                    </div>
                    <div class="field" x-data="{ count: 18 }">
                        <div class="field-row">
                            <label class="field-label" for="demo-pitch">Krótki opis</label>
                            <span class="field-count" aria-live="polite"><span x-text="count">18</span>/160</span>
                        </div>
                        <textarea class="field-control" id="demo-pitch" maxlength="160" @input="count = $event.target.value.length">Filmowe RPG indie.</textarea>
                    </div>
                    <div class="field">
                        <label class="field-label" for="demo-error">Slug z błędem</label>
                        <input class="field-control" id="demo-error" value="Błędny Slug" aria-invalid="true" aria-describedby="demo-error-message">
                        <p class="field-error" id="demo-error-message">Użyj małych liter, cyfr i myślników.</p>
                    </div>
                </form>
            </div>

            <div id="accessibility" class="panel panel-pad">
                <p class="eyebrow">Dostępność</p>
                <h2 class="font-display" style="margin:.45rem 0 1.2rem">Projektowane pod klawiaturę</h2>
                <div style="display:grid;gap:.8rem">
                    <div class="alert"><span aria-hidden="true">⌨</span><div><strong>Widoczny fokus</strong><p>Interaktywne elementy mają focus-visible i logiczną kolejność DOM.</p></div></div>
                    <div class="alert"><span aria-hidden="true">↘</span><div><strong>320 px bez overflow</strong><p>Siatka składa się do jednej kolumny, a menu przechodzi w wariant mobilny.</p></div></div>
                    <div class="alert"><span aria-hidden="true">◼</span><div><strong>Reduced motion</strong><p>Przy prefers-reduced-motion animacje i transformacje są praktycznie wyłączone.</p></div></div>
                </div>
            </div>
        </div>
    </section>
@endsection

@push('overlays')
    <div
        class="modal-backdrop"
        x-cloak
        x-show="modalOpen"
        x-transition.opacity
        @click.self="closeModal()"
        role="presentation"
    >
        <section class="modal" role="dialog" aria-modal="true" aria-labelledby="demo-modal-title" @keydown.tab="trapFocus($event)">
            <div class="modal-head">
                <div>
                    <p class="eyebrow">Modal potwierdzenia</p>
                    <h2 id="demo-modal-title" class="font-display">Przykład bez ryzykownej akcji</h2>
                </div>
                <button class="icon-btn" type="button" x-ref="modalClose" @click="closeModal()" aria-label="Zamknij modal">×</button>
            </div>
            <p>Escape zamyka okno, fokus wraca do elementu otwierającego, a podczas otwarcia tło nie przewija się.</p>
            <div class="modal-actions">
                <button class="btn btn-ghost" type="button" @click="closeModal()">Anuluj</button>
                <button class="btn btn-primary" type="button" @click="closeModal(); showToast()">Potwierdź przykład</button>
            </div>
        </section>
    </div>

    <div class="toast-stack" aria-live="polite" aria-atomic="true">
        <div class="toast" x-cloak x-show="toastOpen" x-transition>
            <div>
                <strong>Design system działa</strong>
                <p>Toast jest czytelny również bez interpretowania samego koloru.</p>
            </div>
            <button class="icon-btn" type="button" @click="toastOpen = false" aria-label="Zamknij powiadomienie">×</button>
        </div>
    </div>
@endpush
