document.addEventListener('DOMContentLoaded',()=>{
  const reduce=matchMedia('(prefers-reduced-motion: reduce)').matches;
  const interactive=[...document.querySelectorAll('.btn,.icon-btn,.chip,.catalog-pagination a,.pagination a')];

  if(!reduce){
    interactive.forEach(el=>{
      el.addEventListener('pointermove',event=>{
        const rect=el.getBoundingClientRect();
        el.style.setProperty('--mx',`${event.clientX-rect.left}px`);
        el.style.setProperty('--my',`${event.clientY-rect.top}px`);
      });
      el.addEventListener('pointerdown',event=>{
        const rect=el.getBoundingClientRect();
        const ripple=document.createElement('span');
        ripple.className='ui-ripple';
        ripple.style.left=`${event.clientX-rect.left}px`;
        ripple.style.top=`${event.clientY-rect.top}px`;
        el.append(ripple);
        ripple.addEventListener('animationend',()=>ripple.remove(),{once:true});
      });
    });
  }

  const searches=[...document.querySelectorAll('input[type="search"]')];
  document.addEventListener('keydown',event=>{
    const tag=document.activeElement?.tagName;
    const typing=tag==='INPUT'||tag==='TEXTAREA'||tag==='SELECT'||document.activeElement?.isContentEditable;
    if(event.key==='/'&&!typing){
      const target=document.querySelector('#global-search')||searches[0];
      if(target){event.preventDefault();target.focus();target.select?.();}
    }
    if(event.key==='Escape'&&document.activeElement?.matches?.('input[type="search"]'))document.activeElement.blur();
  });

  searches.forEach(input=>{
    input.addEventListener('input',()=>input.closest('.header-search,.mobile-menu-search,.catalog-search-control')?.classList.toggle('has-query',input.value.trim().length>0));
  });
});
