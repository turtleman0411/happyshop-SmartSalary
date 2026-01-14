// src/main/resources/static/js/category-card.js
// 覆蓋版：保留全部既有邏輯 + 修正 disabled input 無法送出問題

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

      inputs.forEach(inp => {
        inp.value = '';
        inp.disabled = true;
      });
      return;
    }

    const base = Math.floor(100 / idxs.length);
    let remain = 100 - base * idxs.length;

    inputs.forEach(inp => {
      inp.value = '';
      inp.disabled = true;
    });

    idxs.forEach(i => {
      inputs[i].value = base;
      inputs[i].disabled = false;
    });

    if (remain > 0) {
      inputs[idxs[0]].value = base + remain;
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

    if (total === 100) {
      submitBtn.disabled = false;
      totalEl.classList.remove('text-danger');
      totalEl.classList.add('text-success');
    } else {
      submitBtn.disabled = true;
      totalEl.classList.remove('text-success');
      totalEl.classList.add('text-danger');
    }
  }

  checks.forEach(chk => {
    chk.addEventListener('change', redistribute);
  });

  inputs.forEach(inp => {
    inp.addEventListener('input', recalc);
  });

  /**
   * ⭐⭐ 關鍵修正 ⭐⭐
   * HTML form submit 時，disabled input 不會被送出
   * 在 submit 前，強制把「有值的 input」解除 disabled
   */
  if (form) {
    form.addEventListener('submit', function () {
      inputs.forEach(inp => {
        if (inp.value !== '') {
          inp.disabled = false;
        }
      });
    });
  }

  recalc();
}

// ⭐ 掛到全域（給 Thymeleaf inline script 呼叫）
window.initCategoryCard = initCategoryCard;
