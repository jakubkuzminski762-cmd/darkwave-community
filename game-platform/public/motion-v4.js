document.addEventListener('DOMContentLoaded',()=>{
  const reduce=matchMedia('(prefers-reduced-motion: reduce)').matches;
  const q=(s,c=document)=>c.querySelector(s),qa=(s,c=document)=>[...c.querySelectorAll(s)];

  const progress=document.createElement('div');
  progress.className='page-scroll-progress';
  progress.setAttribute('aria-hidden','true');
  progress.innerHTML='<span></span>';
  document.body.prepend(progress);
  let scrollTick=false;
  const updateScroll=()=>{const max=Math.max(1,document.documentElement.scrollHeight-innerHeight);document.documentElement.style.setProperty('--scroll-progress',Math.min(1,scrollY/max));scrollTick=false};
  addEventListener('scroll',()=>{if(!scrollTick){scrollTick=true;requestAnimationFrame(updateScroll)}},{passive:true});
  updateScroll();

  qa('[data-motion-card],.catalog-card,.news-card,.editorial-card,.release-card,.feature-card,.process-step,.requirements-card,.edition-card,.facts-card,.prose-card').forEach(card=>{
    if(reduce)return;
    card.addEventListener('pointermove',e=>{if(e.pointerType==='touch')return;const r=card.getBoundingClientRect();card.style.setProperty('--spot-x',`${e.clientX-r.left}px`);card.style.setProperty('--spot-y',`${e.clientY-r.top}px`)});
  });

  qa('[data-depth-scene],.article-cover,.game-keyart').forEach(scene=>{
    if(reduce)return;
    scene.addEventListener('pointermove',e=>{if(e.pointerType==='touch')return;const r=scene.getBoundingClientRect();const x=((e.clientX-r.left)/r.width-.5)*14;const y=((e.clientY-r.top)/r.height-.5)*14;scene.style.setProperty('--parallax-x',`${x}px`);scene.style.setProperty('--parallax-y',`${y}px`)});
    scene.addEventListener('pointerleave',()=>{scene.style.setProperty('--parallax-x','0px');scene.style.setProperty('--parallax-y','0px')});
  });

  qa('[data-hero-carousel]').forEach(carousel=>{
    const slides=qa('[data-hero-slide]',carousel), indicators=qa('[data-hero-progress]',carousel), prev=q('[data-hero-prev]',carousel), next=q('[data-hero-next]',carousel), current=q('[data-hero-current]',carousel);
    if(!slides.length)return;
    const duration=Number(carousel.dataset.interval)||5600;
    carousel.style.setProperty('--carousel-duration',`${duration}ms`);
    carousel.style.setProperty('--slides',String(slides.length));
    let index=Math.max(0,slides.findIndex(s=>s.classList.contains('is-active'))),timer=null,paused=false,touchStartX=null;

    const sync=(newIndex,{manual=false}={})=>{
      const previous=index;
      index=(newIndex+slides.length)%slides.length;
      slides.forEach((slide,i)=>{
        slide.classList.remove('is-active','is-leaving');
        slide.setAttribute('aria-hidden',String(i!==index));
        slide.tabIndex=i===index?0:-1;
      });
      if(previous!==index&&slides[previous])slides[previous].classList.add('is-leaving');
      slides[index].classList.add('is-active');
      indicators.forEach((item,i)=>{item.classList.toggle('is-active',i===index);item.classList.remove('is-running');item.setAttribute('aria-current',i===index?'true':'false')});
      if(current)current.textContent=String(index+1).padStart(2,'0');
      const activeIndicator=indicators[index];
      if(activeIndicator&&!reduce&&!paused){void activeIndicator.offsetWidth;activeIndicator.classList.add('is-running')}
      clearTimeout(timer);
      if(!reduce&&!paused&&slides.length>1)timer=setTimeout(()=>sync(index+1),duration);
      if(manual&&!reduce){carousel.classList.add('was-manual');clearTimeout(carousel.__manualClass);carousel.__manualClass=setTimeout(()=>carousel.classList.remove('was-manual'),900)}
    };
    const setPaused=value=>{paused=value;carousel.classList.toggle('is-paused',paused);clearTimeout(timer);if(!paused)sync(index)};
    prev?.addEventListener('click',()=>sync(index-1,{manual:true}));
    next?.addEventListener('click',()=>sync(index+1,{manual:true}));
    indicators.forEach((item,i)=>item.addEventListener('click',()=>sync(i,{manual:true})));
    carousel.addEventListener('mouseenter',()=>setPaused(true));
    carousel.addEventListener('mouseleave',()=>setPaused(false));
    carousel.addEventListener('focusin',()=>setPaused(true));
    carousel.addEventListener('focusout',e=>{if(!carousel.contains(e.relatedTarget))setPaused(false)});
    carousel.addEventListener('keydown',e=>{if(e.key==='ArrowLeft'){e.preventDefault();sync(index-1,{manual:true})}if(e.key==='ArrowRight'){e.preventDefault();sync(index+1,{manual:true})}});
    carousel.addEventListener('pointerdown',e=>{if(e.pointerType==='touch')touchStartX=e.clientX});
    carousel.addEventListener('pointerup',e=>{if(e.pointerType!=='touch'||touchStartX===null)return;const dx=e.clientX-touchStartX;touchStartX=null;if(Math.abs(dx)>45)sync(index+(dx<0?1:-1),{manual:true})});
    document.addEventListener('visibilitychange',()=>setPaused(document.hidden));
    sync(index);
  });

  if(!reduce&&'IntersectionObserver'in window){
    const io=new IntersectionObserver(entries=>entries.forEach(entry=>{if(entry.isIntersecting)entry.target.classList.add('motion-in')}),{threshold:.12});
    qa('.page-hero,.catalog-hero,.article-hero,.game-detail-hero,.publisher-panel,.community-band').forEach(el=>io.observe(el));
  }
});
