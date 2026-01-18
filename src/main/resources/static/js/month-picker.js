/**
 * Month Picker — Bank Pill UI (NO arrows)
 * - Month mode: shows "YYYY" + 12 months
 * - Tap year => switch to year mode (range 9 years, centered around current year)
 * - Tap year => back to month mode
 * - Tap month => navigate baseUrl?month=YYYY-MM
 */

document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("monthPickerModal");
  const body = document.getElementById("monthPickerBody");
  if (!modal || !body) return;

  modal.addEventListener("show.bs.modal", () => {
    const card = document.querySelector(".hs-monthbar-card");
    if (!card) return;

    const baseUrl = card.dataset.baseUrl;
    const current = card.dataset.currentMonth; // YYYY-MM
    if (!baseUrl || !current) return;

    const [y, m] = current.split("-").map(Number);

    renderMonthMode(body, baseUrl, y, m);
  });
});

/* =========================
   Month Mode
========================= */
function renderMonthMode(container, baseUrl, year, currentMonth) {
  container.innerHTML = `
    <div class="mp" data-mode="month" data-year="${year}">
      <div class="mp-year">
        <button type="button" class="mp-year-btn" data-action="open-year">
          ${year} 年
        </button>
      </div>

      <div class="mp-month-grid">
        ${renderMonthButtons(year, currentMonth)}
      </div>

      <div class="mp-footer">
        <button type="button" class="mp-today" data-action="today">
          回到本月
        </button>
      </div>
    </div>
  `;

  // events
  container.querySelector('[data-action="open-year"]')
    ?.addEventListener("click", () => renderYearMode(container, baseUrl, year, currentMonth));

  container.querySelectorAll('[data-action="pick-month"]').forEach(btn => {
    btn.addEventListener("click", () => {
      const ym = btn.dataset.ym;
      window.location.href = `${baseUrl}?month=${encodeURIComponent(ym)}`;
    });
  });

  container.querySelector('[data-action="today"]')
    ?.addEventListener("click", () => {
      window.location.href = `${baseUrl}?month=${encodeURIComponent(currentYearMonth())}`;
    });
}

function renderMonthButtons(year, currentMonth) {
  return Array.from({ length: 12 }, (_, i) => {
    const m = i + 1;
    const ym = `${year}-${String(m).padStart(2, "0")}`;
    const active = (m === currentMonth) ? "is-active" : "";
    return `<button type="button" class="mp-pill ${active}" data-action="pick-month" data-ym="${ym}">${m}月</button>`;
  }).join("");
}

/* =========================
   Year Mode (9-grid)
   Rule: [newestYear, newestYear-1, ... newestYear-8]
========================= */
function renderYearMode(container, baseUrl, newestYear, currentMonth) {
  const start = newestYear;
  const end = newestYear - 8;

  container.innerHTML = `
    <div class="mp" data-mode="year" data-center="${newestYear}">
      <div class="mp-year">
        <button type="button" class="mp-year-btn" data-action="close-year">
          ${end} – ${start}
        </button>
      </div>

      <div class="mp-year-grid">
        ${renderYearButtons(newestYear)}
      </div>
    </div>
  `;

  // 點區間標題：回到月模式（留同一年）
  container.querySelector('[data-action="close-year"]')
    ?.addEventListener("click", () => renderMonthMode(container, baseUrl, newestYear, currentMonth));

  // 點年份：回到月模式（切換年份）
  container.querySelectorAll('[data-action="pick-year"]').forEach(btn => {
    btn.addEventListener("click", () => {
      const y = Number(btn.dataset.year);
      renderMonthMode(container, baseUrl, y, currentMonth);
    });
  });
}

function renderYearButtons(newestYear) {
  // 9 宮格：第一格最新，後面 8 格是過去
  const years = Array.from({ length: 9 }, (_, i) => newestYear - i);

  return years.map((y, idx) => {
    const active = (idx === 0) ? "is-active" : ""; // 最新那一年高亮
    return `<button type="button" class="mp-pill ${active}" data-action="pick-year" data-year="${y}">${y}</button>`;
  }).join("");
}

/* =========================
   Utils
========================= */
function currentYearMonth() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}
