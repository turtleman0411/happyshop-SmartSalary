/**
 * Transaction Page JS — Stable v3
 * - 下拉：前端即時過濾
 * - 最近3筆：精準定位到同一筆 tx（靠 data-tx-id UUID）
 * - Select-like 特效：綠框 + 左綠條 + 勾勾 + 注記
 * - 收據：點圖放大 / 關閉清空
 * - 刪除：維持 form submit
 */
(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {
    initCategoryDropdownFilter();
    initRecentClickExact();
    initReceiptPreview();
    initDeleteButtons();

    // 首次載入：若後端已預選分類，套用 filter
    const select = document.getElementById('categoryFilter');
    if (select && select.value) filterByCategory(select.value);
  });

  /* =========================
     Filter
  ========================= */
  function initCategoryDropdownFilter() {
    const select = document.getElementById('categoryFilter');
    if (!select) return;

    select.addEventListener('change', () => {
      clearPicked('.recent-item');
      clearPicked('.transaction-item');
      hideNote();
      filterByCategory(select.value);
    });
  }

  function filterByCategory(category) {
    const items  = document.querySelectorAll('.transaction-item');
    const groups = document.querySelectorAll('.transaction-group');

    items.forEach(item => {
      const cat = item.dataset.category || '';
      const show = (!category || cat === category);
      item.style.display = show ? '' : 'none';
    });

    groups.forEach(group => {
      const groupItems = group.querySelectorAll('.transaction-item');
      const hasVisible = Array.from(groupItems).some(it => it.style.display !== 'none');
      group.style.display = hasVisible ? '' : 'none';
    });
  }

  /* =========================
     Recent -> Exact highlight
  ========================= */
  function initRecentClickExact() {
    const recents = document.querySelectorAll('.recent-item');
    if (recents.length === 0) return;

    recents.forEach(item => {
      item.addEventListener('click', () => {
        const txId = item.dataset.txId || '';
        const category = item.dataset.category || '';

        // recent 顯示名
        const displayName =
          item.querySelector('.recent-title')?.textContent?.trim() || '此分類';

        // 1) recent 編框
        clearPicked('.recent-item');
        item.classList.add('is-picked');

        // 2) 下拉切到該分類 + 過濾
        const select = document.getElementById('categoryFilter');
        if (select) {
          select.value = category;
          filterByCategory(category);
        } else {
          // 沒下拉也照樣過濾（保險）
          filterByCategory(category);
        }

        // 3) 注記條
        showNote(`已切換到「${displayName}」`);

        // 4) 主清單：精準找到同一筆交易（靠 data-tx-id）
        clearPicked('.transaction-item');

        const target = txId
          ? document.querySelector(`.transaction-item[data-tx-id="${cssEscape(txId)}"]`)
          : null;

        if (target && target.style.display !== 'none') {
          target.classList.add('is-picked');
          target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          return;
        }

        // fallback：找第一筆可見交易（避免完全沒反應）
        const fallback = Array.from(document.querySelectorAll('.transaction-item'))
          .find(it => it.style.display !== 'none');

        if (fallback) {
          fallback.classList.add('is-picked');
          fallback.scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else {
          const g = Array.from(document.querySelectorAll('.transaction-group'))
            .find(x => x.style.display !== 'none');
          if (g) g.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    });
  }

  /* =========================
     Receipt preview modal
  ========================= */
  function initReceiptPreview() {
    const modalEl = document.getElementById('receiptModal');
    const imgEl = document.getElementById('receiptModalImg');
    if (!modalEl || !imgEl || !window.bootstrap) return;

    const modal = new bootstrap.Modal(modalEl);

    document.querySelectorAll('.receipt-click').forEach(img => {
      img.addEventListener('click', () => {
        const url = img.dataset.imageUrl || img.getAttribute('src');
        if (!url) return;
        imgEl.src = url;
        modal.show();
      });
    });

    modalEl.addEventListener('hidden.bs.modal', () => {
      imgEl.src = '';
    });
  }

  /* =========================
     Delete (form submit)
  ========================= */
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

    const month = document.body.dataset.month || new URLSearchParams(location.search).get('month') || '';

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/happyshop/transaction/delete';
    form.style.display = 'none';
    form.innerHTML = `
      <input type="hidden" name="transactionId" value="${escapeHtml(transactionId)}">
      <input type="hidden" name="month" value="${escapeHtml(month)}">
      <input type="hidden" name="_method" value="DELETE">
    `;
    document.body.appendChild(form);
    form.submit();
  }

  /* =========================
     UI helpers
  ========================= */
  function clearPicked(selector) {
    document.querySelectorAll(selector).forEach(el => el.classList.remove('is-picked'));
  }

  function showNote(text) {
    const note = document.getElementById('txNote');
    const t = document.getElementById('txNoteText');
    if (!note || !t) return;
    t.textContent = text;
    note.classList.remove('hidden');
  }

  function hideNote() {
    const note = document.getElementById('txNote');
    if (!note) return;
    note.classList.add('hidden');
  }

  // dataset 用 selector 安全處理（避免引號爆掉）
  function cssEscape(s) {
    return String(s).replace(/"/g, '\\"');
  }

  function escapeHtml(s) {
    return String(s)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }
})();
