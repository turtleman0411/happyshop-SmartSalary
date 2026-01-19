/**
 * HappyShop – Transaction Page JS（FIXED）
 * 對齊目前 HTML 結構（dropdown + recent）
 */

(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {
    initCategoryDropdown();
    initRecentClick();
    initDeleteButtons();
  });

  /* ======================================================
     1️⃣ 下拉分類篩選（修正版）
  ====================================================== */
  function initCategoryDropdown() {
    const select = document.getElementById('categoryFilter');
    if (!select) return;

    select.addEventListener('change', () => {
      filterByCategory(select.value);
    });
  }

  function filterByCategory(category) {
    const items = document.querySelectorAll('.transaction-item');
    const groups = document.querySelectorAll('.transaction-group');

    items.forEach(item => {
      const cat = item.dataset.category || '';
      item.style.display =
        !category || cat === category ? '' : 'none';
    });

    // 正確判斷群組是否還有可見交易
    groups.forEach(group => {
      const hasVisible = Array.from(
        group.querySelectorAll('.transaction-item')
      ).some(item => item.offsetParent !== null);

      group.style.display = hasVisible ? '' : 'none';
    });
  }

  /* ======================================================
     2️⃣ 最近消費 → 切分類 + 捲動
  ====================================================== */
  function initRecentClick() {
    const recentItems = document.querySelectorAll('.recent-item');
    if (recentItems.length === 0) return;

    recentItems.forEach(item => {
      item.addEventListener('click', () => {
        const category =
          item.querySelector('.chip-icon')?.textContent || '';

        // 從 recent item 直接讀分類（靠 data-category 最穩）
        const txCategory = item.getAttribute('data-category');

        const select = document.getElementById('categoryFilter');
        if (select && txCategory) {
          select.value = txCategory;
          filterByCategory(txCategory);
        }

        // 捲動到第一個可見群組
        const targetGroup = document.querySelector(
          '.transaction-group:not([style*="display: none"])'
        );

        if (targetGroup) {
          targetGroup.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
          });
        }
      });
    });
  }

  /* ======================================================
     3️⃣ 刪除
  ====================================================== */
  function initDeleteButtons() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', e => {
        e.preventDefault();
        handleDelete(btn.dataset.id);
      });
    });
  }

  function handleDelete(transactionId) {
    if (!confirm('確定要刪除這筆交易嗎？')) return;

    const urlParams = new URLSearchParams(window.location.search);
    const month = urlParams.get('month') || '2026-01';

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/happyshop/transaction/delete';
    form.style.display = 'none';
    form.innerHTML = `
      <input type="hidden" name="transactionId" value="${transactionId}">
      <input type="hidden" name="month" value="${month}">
      <input type="hidden" name="_method" value="DELETE">
    `;

    document.body.appendChild(form);
    form.submit();
  }

})();
