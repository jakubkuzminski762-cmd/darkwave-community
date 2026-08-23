document.addEventListener('DOMContentLoaded',()=>{
  const q=(s,c=document)=>c.querySelector(s), qa=(s,c=document)=>[...c.querySelectorAll(s)];
  const body=document.body;
  const header=q('.site-header');
  const setHeader=()=>header?.classList.toggle('is-scrolled',window.scrollY>12);
  setHeader(); window.addEventListener('scroll',setHeader,{passive:true});

  const menu=q('[data-mobile-menu]'), opener=q('[data-mobile-open]'), closer=q('[data-mobile-close]'), panel=menu?.querySelector('.mobile-menu-inner');
  let menuLastFocus=null;
  const setMenu=(open)=>{
    if(!menu||!opener)return;
    menu.classList.toggle('is-open',open); opener.setAttribute('aria-expanded',String(open)); body.classList.toggle('nav-open',open);
    if(open){menuLastFocus=document.activeElement; requestAnimationFrame(()=>closer?.focus())}
    else if(menuLastFocus instanceof HTMLElement){menuLastFocus.focus()}
  };
  opener?.addEventListener('click',()=>setMenu(true)); closer?.addEventListener('click',()=>setMenu(false));
  menu?.addEventListener('click',e=>{if(e.target===menu)setMenu(false)}); qa('[data-mobile-link]').forEach(a=>a.addEventListener('click',()=>setMenu(false)));

  const focusables=(root)=>qa('a[href],button:not([disabled]),input:not([disabled]),select:not([disabled]),textarea:not([disabled]),[tabindex]:not([tabindex="-1"])',root).filter(el=>!el.hasAttribute('hidden'));
  document.addEventListener('keydown',e=>{
    if(e.key==='Escape'&&menu?.classList.contains('is-open'))setMenu(false);
    if(e.key==='Tab'&&menu?.classList.contains('is-open')&&panel){const list=focusables(panel);if(!list.length)return;const first=list[0],last=list[list.length-1];if(e.shiftKey&&document.activeElement===first){e.preventDefault();last.focus()}else if(!e.shiftKey&&document.activeElement===last){e.preventDefault();first.focus()}}
  });

  const reduce=matchMedia('(prefers-reduced-motion: reduce)').matches;
  const reveal=qa('[data-reveal]');
  reveal.forEach((el,i)=>el.style.setProperty('--reveal-delay',`${Math.min(i%6,5)*55}ms`));
  if(!reduce&&'IntersectionObserver'in window){
    const io=new IntersectionObserver(entries=>entries.forEach(entry=>{if(entry.isIntersecting){entry.target.style.transitionDelay=entry.target.style.getPropertyValue('--reveal-delay');entry.target.classList.add('is-visible');io.unobserve(entry.target)}}),{threshold:.08,rootMargin:'0px 0px -40px'});
    reveal.forEach(el=>io.observe(el));
  }else reveal.forEach(el=>el.classList.add('is-visible'));

  const toast=q('[data-toast]');
  const showToast=(message)=>{if(!toast)return;const msg=q('[data-toast-message]',toast);if(msg)msg.textContent=message||'Gotowe.';toast.classList.add('is-visible');clearTimeout(window.__toastTimer);window.__toastTimer=setTimeout(()=>toast.classList.remove('is-visible'),3600)};
  qa('[data-toast-trigger]').forEach(btn=>btn.addEventListener('click',()=>showToast(btn.dataset.toastMessage)));
  q('[data-toast-close]')?.addEventListener('click',()=>toast?.classList.remove('is-visible'));

  qa('a[href^="#"]').forEach(link=>link.addEventListener('click',()=>{const id=link.getAttribute('href');if(id&&id.length>1)q(id)?.setAttribute('tabindex','-1')}));
});
