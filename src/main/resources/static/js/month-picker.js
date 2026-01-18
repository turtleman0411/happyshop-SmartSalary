/**
 * Month Picker
 * - 只負責 UI 互動
 * - 不呼叫後端
 * - 不碰商業邏輯
 *
 * 依賴：
 * - hs-monthbar-card 上的 data-base-url
 * - hs-mb-month-text 上的 data-current-month (YYYY-MM)
 */

document.addEventListener("DOMContentLoaded", () => {

  const modal = document.getElementById("monthPickerModal");
  const body = document.getElementById("monthPickerBody");

  if (!modal || !body) return;

  modal.addEventListener("show.bs.modal", () => {
    const card = document.querySelector(".hs-monthbar-card");
    if (!card) return;

    const baseUrl = card.dataset.baseUrl;
    const current = card.dataset.currentMonth;

    if (!current) return;

    const [year, month] = current.split("-").map(Number);

    renderMonthPicker(body, baseUrl, year, month);
  });

});

/* =========================
   Render
========================= */

function renderMonthPicker(container, baseUrl, year, currentMonth) {
  container.innerHTML = `
    <div class="month-picker">

      <div class="month-picker-header">
        <button class="nav-btn" data-year="${year - 1}">‹</button>
        <div class="year-label">${year} 年</div>
        <button class="nav-btn" data-year="${year + 1}">›</button>
      </div>

      <div class="month-grid">
        ${renderMonths(baseUrl, year, currentMonth)}
      </div>

      <div class="month-picker-footer">
        <a class="today-btn"
           href="${baseUrl}?month=${currentYearMonth()}">
          回到本月
        </a>
      </div>

    </div>
  `;

  bindYearSwitch(container, baseUrl, currentMonth);
}

/* =========================
   Months
========================= */

function renderMonths(baseUrl, year, currentMonth) {
  return Array.from({ length: 12 }, (_, i) => {
    const m = i + 1;
    const ym = `${year}-${String(m).padStart(2, "0")}`;
    const active = m === currentMonth ? "active" : "";

    return `
      <a class="month-btn ${active}"
         href="${baseUrl}?month=${ym}">
        ${m} 月
      </a>
    `;
  }).join("");
}

/* =========================
   Year switch
========================= */

function bindYearSwitch(container, baseUrl, currentMonth) {
  container.querySelectorAll(".nav-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const year = Number(btn.dataset.year);
      container.querySelector(".year-label").textContent = `${year} 年`;
      container.querySelector(".month-grid").innerHTML =
        renderMonths(baseUrl, year, currentMonth);
    });
  });
}

/* =========================
   Utils
========================= */

function currentYearMonth() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}
