function initCategoryCard() {

  const cards  = Array.from(document.querySelectorAll('.hs-cat-card'));
  const checks = cards.map(c => c.querySelector('.cat-check'));
  const inputs = cards.map(c => c.querySelector('.hs-cat-input'));

  const totalEl   = document.getElementById('percentTotal');
  const submitBtn = document.getElementById('submitBtn');

  function toInt(v) {
    const n = Number(v);
    if (!Number.isFinite(n)) return 0;
    return Math.max(0, Math.min(100, Math.floor(n)));
  }

  function reset(i) {
    inputs[i].value = '';
    inputs[i].disabled = true;
    delete inputs[i].dataset.manual;
    cards[i].classList.remove('manual-set', 'is-active');
  }

  function markActive(i) {
    inputs[i].disabled = false;
    cards[i].classList.add('is-active');
  }

  function updateTotalAndSubmit() {
    let total = 0;
    let checkedCount = 0;

    checks.forEach((chk, i) => {
      if (!chk.checked) return;
      checkedCount++;
      total += toInt(inputs[i].value || 0);
    });

    totalEl.textContent = total + '%';
    submitBtn.disabled = !(checkedCount > 0 && total === 100);
  }

  function redistribute() {
    // 1) 先處理勾選/取消勾選的 UI 狀態
    checks.forEach((chk, i) => {
      if (!chk.checked) {
        reset(i);
      } else {
        markActive(i);
      }
    });

    // 2) 算 manualTotal / autoCandidates
    let manualTotal = 0;
    const autoIdx = [];

    checks.forEach((chk, i) => {
      if (!chk.checked) return;

      const val = toInt(inputs[i].value || 0);

      if (inputs[i].dataset.manual === 'true') {
        manualTotal += val;
      } else {
        autoIdx.push(i);
      }
    });

    // 3) 把剩餘分配給非 manual 的欄位
    let remaining = 100 - manualTotal;

    if (autoIdx.length > 0) {
      // remaining < 0 代表 manual 超過 100，auto 全部歸 0（但送出會被鎖）
      if (remaining < 0) {
        autoIdx.forEach(i => { inputs[i].value = 0; });
      } else {
        const base = Math.floor(remaining / autoIdx.length);
        let extra = remaining % autoIdx.length;

        autoIdx.forEach((i) => {
          const add = extra > 0 ? 1 : 0;
          if (extra > 0) extra--;
          inputs[i].value = base + add;
        });
      }
    }

    updateTotalAndSubmit();
  }

  // ✅ 整張卡可點（保留 label 原生 toggle checkbox）
  cards.forEach((card, i) => {
    card.addEventListener('click', (e) => {
      // 點到 input 不干擾（避免點數字時也切 checkbox）
      if (e.target.closest('.hs-cat-input')) return;

      setTimeout(() => {
        if (checks[i].checked) {
          inputs[i].focus();
        }
      }, 0);
    });
  });

  // checkbox 改變 → 自動重分配
  checks.forEach((chk) => {
    chk.addEventListener('change', redistribute);
  });

  // 手動輸入 → 鎖定 manual（之後不被自動改）
  inputs.forEach((inp, i) => {
    inp.addEventListener('input', () => {
      if (!checks[i].checked) return;

      inp.value = toInt(inp.value);
      inp.dataset.manual = 'true';
      cards[i].classList.add('manual-set');

      redistribute();
    });
  });

  // 初始化：如果後端有帶 percent()，你希望視為 manual 或 auto？
  // 這裡我採用：有值就先當 manual（避免一載入就被洗掉）
  inputs.forEach((inp, i) => {
    const v = String(inp.value || '').trim();
    if (v !== '' && toInt(v) > 0) {
      inp.dataset.manual = 'true';
      cards[i].classList.add('manual-set');
    }
  });

  redistribute();
}

document.addEventListener('DOMContentLoaded', initCategoryCard);
