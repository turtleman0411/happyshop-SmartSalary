document.addEventListener("DOMContentLoaded", () => {

  /* ======================================================
     1) Edit Modal：塞資料
  ====================================================== */
  document.querySelectorAll(".js-edit-open").forEach(btn => {
    btn.addEventListener("click", () => {
      const txId = btn.dataset.transactionId || "";
      const dateText = btn.dataset.date || "";
      const categoryDisplay = btn.dataset.categoryDisplay || "";
      const amount = btn.dataset.amount || "0";
      const note = btn.dataset.note || "";
      const imageUrl = btn.dataset.imageUrl || "";

      // hidden id
      const txIdEl = document.getElementById("editTransactionId");
      if (txIdEl) txIdEl.value = txId;

      // readonly display
      const dateEl = document.getElementById("editDateText");
      if (dateEl) dateEl.value = dateText;

      const catEl = document.getElementById("editCategoryDisplay");
      if (catEl) catEl.textContent = categoryDisplay;

      // editable
      const amountEl = document.getElementById("editAmount");
      if (amountEl) amountEl.value = amount;

      const noteEl = document.getElementById("editNote");
      if (noteEl) noteEl.value = note;

      // reset file input + new preview
      const fileEl = document.getElementById("editImage");
      if (fileEl) fileEl.value = "";

      const previewWrap = document.getElementById("editImagePreviewWrap");
      const previewImg = document.getElementById("editImagePreview");
      if (previewWrap && previewImg) {
        previewImg.removeAttribute("src");
        previewWrap.style.display = "none";
      }

      // current image
      const currentWrap = document.getElementById("editCurrentImageWrap");
      const currentImg = document.getElementById("editCurrentImage");
      if (currentWrap && currentImg) {
        if (imageUrl && imageUrl.trim() !== "") {
          currentImg.src = imageUrl;
          currentWrap.style.display = "";
        } else {
          currentImg.removeAttribute("src");
          currentWrap.style.display = "none";
        }
      }

      // optional: reset budget block (avoid stale UI)
      setText("editSpent", "0");
      setText("editBudget", "0");
      setText("editRemaining", "0");
      setText("editOver", "0");
      setText("editUsage", "0%");
      const overWrap = document.getElementById("editOverWrap");
      if (overWrap) overWrap.style.display = "none";
      const bar = document.getElementById("editBar");
      if (bar) bar.style.width = "0%";
    });
  });

  /* ======================================================
     2) Edit Modal：新圖片預覽
  ====================================================== */
  const editImageInput = document.getElementById("editImage");
  if (editImageInput) {
    editImageInput.addEventListener("change", () => {
      const file = editImageInput.files && editImageInput.files[0];
      const wrap = document.getElementById("editImagePreviewWrap");
      const img = document.getElementById("editImagePreview");
      if (!wrap || !img) return;

      if (!file) {
        img.removeAttribute("src");
        wrap.style.display = "none";
        return;
      }

      const url = URL.createObjectURL(file);
      img.src = url;
      wrap.style.display = "";
    });
  }

  /* ======================================================
     3) Image Modal：縮圖點開 → 放大圖
        （用 event delegation，桌機/手機都穩）
  ====================================================== */
  document.addEventListener("click", (e) => {
    const thumb = e.target.closest(".transaction-thumb");
    if (!thumb) return;

    const url = thumb.dataset.imageUrl;
    const modalImg = document.getElementById("modalImage");
    if (modalImg && url) modalImg.src = url;
  });

  /* ======================================================
     4) Image Modal：點圖片 → iOS 縮回 + 關閉
  ====================================================== */
  const modalImage = document.getElementById("modalImage");
  const imageModalEl = document.getElementById("imageModal");

  if (modalImage && imageModalEl) {
    modalImage.addEventListener("click", () => {
      modalImage.style.transform = "scale(0.96)";
      modalImage.style.transition = "transform .12s ease";

      setTimeout(() => {
        modalImage.style.transform = "";
        // ✅ getOrCreateInstance 比 getInstance 更穩
        bootstrap.Modal.getOrCreateInstance(imageModalEl).hide();
      }, 120);
    });
  }

  function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
  }
});
