/**
 * Transaction Page JS — Stable v3 (Mobile-safe) + Edit Modal
 */
(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {
    initCategoryDropdownFilter();
    initRecentClickExact();
    initReceiptPreview();
    initDeleteButtons();
    initEditTxModal(); // ✅ NEW

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
    document.querySelectorAll('.recent-item').forEach(item => {
      item.addEventListener('pointerdown', e => {
        e.preventDefault();

        const txId = item.dataset.txId || '';
        const category = item.dataset.category || '';
        const displayName =
          item.querySelector('.recent-title')?.textContent?.trim() || '此分類';

        clearPicked('.recent-item');
        item.classList.add('is-picked');

        const select = document.getElementById('categoryFilter');
        if (select) {
          select.value = category;
          filterByCategory(category);
        } else {
          filterByCategory(category);
        }

        showNote(`已切換到「${displayName}」`);

        clearPicked('.transaction-item');

        const target = txId
          ? document.querySelector(`.transaction-item[data-tx-id="${cssEscape(txId)}"]`)
          : null;

        if (target && target.style.display !== 'none') {
          target.classList.add('is-picked');
          target.scrollIntoView({ behavior: 'smooth', block: 'center' });
          return;
        }

        const fallback = Array.from(document.querySelectorAll('.transaction-item'))
          .find(it => it.style.display !== 'none');

        if (fallback) {
          fallback.classList.add('is-picked');
          fallback.scrollIntoView({ behavior: 'smooth', block: 'center' });
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
      img.addEventListener('pointerdown', e => {
        e.preventDefault();
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
     Edit modal (fill form)
  ========================= */
  function initEditTxModal() {
    const modalEl = document.getElementById('editTxModal');
    if (!modalEl) return;

    const txIdEl = document.getElementById('editTxId');
    const monthEl = document.getElementById('editMonth');
    const amountEl = document.getElementById('editAmount');
    const noteEl = document.getElementById('editNote');
    const imgEl = document.getElementById('editImage');

    // 沒有 modal 表單欄位就直接略過
    if (!txIdEl || !monthEl || !amountEl || !noteEl) return;

    // month：優先用 body data-month（你有在 <body data-month=...>）
    const fallbackMonth =
      document.body.dataset.month ||
      new URLSearchParams(location.search).get('month') ||
      '';

    document.querySelectorAll('.js-edit-tx').forEach(btn => {
      btn.addEventListener('click', () => {
        // 你的 HTML 是 data-tx-id / data-amount / data-note / data-month
        txIdEl.value = btn.dataset.txId || '';
        monthEl.value = btn.dataset.month || fallbackMonth || '';
        amountEl.value = btn.dataset.amount || '';
        noteEl.value = btn.dataset.note || '';

        // 清空檔案欄位避免殘留
        if (imgEl) imgEl.value = '';
      });
    });
  }

  /* =========================
     Delete (form submit)
  ========================= */
  function initDeleteButtons() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('pointerdown', e => {
        e.preventDefault();
        handleDelete(btn.dataset.id);
      });
    });
  }

  function handleDelete(transactionId) {
    if (!confirm('確定要刪除這筆交易嗎？')) return;

    const month =
      document.body.dataset.month ||
      new URLSearchParams(location.search).get('month') ||
      '';

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
    if (note) note.classList.add('hidden');
  }

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
