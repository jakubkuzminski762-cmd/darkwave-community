document.addEventListener('DOMContentLoaded',()=>{
  const q=(s,c=document)=>c.querySelector(s), qa=(s,c=document)=>[...c.querySelectorAll(s)];
  const body=document.body;
  const focusables=(root)=>qa('a[href],button:not([disabled]),input:not([disabled]),select:not([disabled]),textarea:not([disabled]),[tabindex]:not([tabindex="-1"])',root).filter(el=>!el.hasAttribute('hidden'));

  const filters=q('[data-filter-panel]'), filterOpen=q('[data-filter-open]'), filterClose=q('[data-filter-close]'), backdrop=q('[data-filter-backdrop]');
  let filterLastFocus=null;
  const setFilters=open=>{
    if(!filters)return;
    filters.classList.toggle('is-open',open); backdrop?.classList.toggle('is-open',open); filterOpen?.setAttribute('aria-expanded',String(open)); body.classList.toggle('filter-open',open);
    if(open){filterLastFocus=document.activeElement;requestAnimationFrame(()=>filterClose?.focus())}else if(filterLastFocus instanceof HTMLElement){filterLastFocus.focus()}
  };
  filterOpen?.addEventListener('click',()=>setFilters(true)); filterClose?.addEventListener('click',()=>setFilters(false)); backdrop?.addEventListener('click',()=>setFilters(false));

  let lastModalFocus=null;
  const openModal=modal=>{if(!modal)return;lastModalFocus=document.activeElement;modal.classList.add('is-open');modal.setAttribute('aria-hidden','false');body.classList.add('modal-open');requestAnimationFrame(()=>focusables(modal)[0]?.focus())};
  const closeModal=modal=>{if(!modal)return;modal.classList.remove('is-open');modal.setAttribute('aria-hidden','true');body.classList.remove('modal-open');if(lastModalFocus instanceof HTMLElement)lastModalFocus.focus()};
  const trailer=q('[data-trailer-modal]');
  q('[data-trailer-open]')?.addEventListener('click',()=>{const frame=trailer?.querySelector('[data-trailer-frame]');if(frame&&!frame.src)frame.src=frame.dataset.src;openModal(trailer)});
  q('[data-trailer-close]')?.addEventListener('click',()=>{const frame=trailer?.querySelector('[data-trailer-frame]');if(frame)frame.src='';closeModal(trailer)});
  const gallery=q('[data-gallery-modal]');
  qa('[data-gallery-item]').forEach(item=>item.addEventListener('click',()=>{const title=q('[data-gallery-title]',gallery),art=q('[data-gallery-art]',gallery);if(title)title.textContent=item.dataset.galleryTitle||'';if(art){art.className='gallery-modal-art';const tone=[...item.classList].find(c=>c.startsWith('tone-'));if(tone)art.classList.add(tone)}openModal(gallery)}));
  q('[data-gallery-close]')?.addEventListener('click',()=>closeModal(gallery));
  [trailer,gallery].forEach(modal=>modal?.addEventListener('click',e=>{if(e.target===modal)closeModal(modal)}));

  document.addEventListener('keydown',e=>{
    if(e.key==='Escape'){setFilters(false);closeModal(trailer);closeModal(gallery)}
    const active=[trailer,gallery].find(m=>m?.classList.contains('is-open'));
    if(e.key==='Tab'&&active){const list=focusables(active);if(!list.length)return;const first=list[0],last=list[list.length-1];if(e.shiftKey&&document.activeElement===first){e.preventDefault();last.focus()}else if(!e.shiftKey&&document.activeElement===last){e.preventDefault();first.focus()}}
    if(e.key==='Tab'&&filters?.classList.contains('is-open')&&innerWidth<=960){const list=focusables(filters);if(!list.length)return;const first=list[0],last=list[list.length-1];if(e.shiftKey&&document.activeElement===first){e.preventDefault();last.focus()}else if(!e.shiftKey&&document.activeElement===last){e.preventDefault();first.focus()}}
  });

  const filterForm=q('.catalog-filters');
  if(filterForm){qa('select',filterForm).forEach(select=>select.addEventListener('change',()=>{select.closest('.field')?.classList.toggle('has-value',Boolean(select.value))}))}
});
