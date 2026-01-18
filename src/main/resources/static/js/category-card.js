function initCategoryCard(userMode) {

  const checks = Array.from(document.querySelectorAll('.category-check'));
  const inputs = Array.from(document.querySelectorAll('.percent-input'));
  const totalEl = document.getElementById('percentTotal');
  const submitBtn = document.getElementById('submitBtn');
  const form = document.getElementById('categoryForm');

  function getCheckedIndexes() {
    return checks
      .map((chk, i) => chk.checked ? i : -1)
      .filter(i => i !== -1);
  }

  function redistribute() {
    const idxs = getCheckedIndexes();

    if (idxs.length === 0) {
      totalEl.textContent = '0%';
      submitBtn.disabled = true;
     inputs.forEach((inp, i) => {
  if (!checks[i].checked) {
    inp.value = '';
    inp.disabled = true;
    inp.dataset.manual = '';
    inp.closest('.category-card')?.classList.remove('manual-set');
  }
});
      return;
    }

    let manualTotal = 0;
    const autoIdxs = [];

    idxs.forEach(i => {
      const inp = inputs[i];
      inp.disabled = false; // ⭐ 勾選的先啟用

      if (inp.dataset.manual === 'true') {
        manualTotal += Number(inp.value || 0);
      } else {
        autoIdxs.push(i);
      }
    });

    let remain = Math.max(0, 100 - manualTotal);

    if (autoIdxs.length > 0) {
      const base = Math.floor(remain / autoIdxs.length);
      let rest = remain - base * autoIdxs.length;

      autoIdxs.forEach(i => {
        inputs[i].value = base;
      });

      if (rest > 0) {
        inputs[autoIdxs[0]].value = base + rest;
      }
    }

    recalc();
  }

  function recalc() {
    let total = 0;
    inputs.forEach(inp => {
      if (!inp.disabled) {
        total += Number(inp.value || 0);
      }
    });

    totalEl.textContent = total + '%';

    submitBtn.disabled = total !== 100;
    totalEl.classList.toggle('text-success', total === 100);
    totalEl.classList.toggle('text-danger', total !== 100);
  }

  // ✅ checkbox 控制啟用 + 重分配
  checks.forEach(chk => {
    chk.addEventListener('change', redistribute);
  });

  // ✅ input 才標記為 manual
  inputs.forEach(inp => {
    inp.addEventListener('input', () => {
      inp.dataset.manual = 'true';
      inp.closest('.category-card')
         ?.classList.add('manual-set');
      redistribute();
    });
  });

  // submit 前解除 disabled
  if (form) {
    form.addEventListener('submit', () => {
      inputs.forEach(inp => {
        if (inp.value !== '') inp.disabled = false;
      });
    });
  }

  redistribute();
}

window.initCategoryCard = initCategoryCard;
