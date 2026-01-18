/**
 * Month Picker — iOS Style (Month / Year Mode)
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
    if (!current) return;

    const [year, month] = current.split("-").map(Number);

    renderMonthMode(body, baseUrl, year, month);
  });

});

/* ======================================================
   MODE: MONTH
====================================================== */

function renderMonthMode(container, baseUrl, year, currentMonth) {
  container.dataset.mode = "month";

  container.innerHTML = `
    <div class="month-picker">

      <div class="month-picker-header">
        <button class="nav-btn" data-year="${year - 1}">‹</button>

        <div class="year-label js-year-toggle">${year} 年</div>

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

  bindMonthEvents(container, baseUrl, year, currentMonth);
}

/* ======================================================
   MODE: YEAR
====================================================== */

function renderYearMode(container, baseUrl, centerYear, currentMonth) {
  container.dataset.mode = "year";

  const start = centerYear - 4;

  container.innerHTML = `
    <div class="month-picker">

      <div class="month-picker-header">
        <button class="nav-btn" data-center="${centerYear - 9}">‹</button>

        <div class="year-label">${centerYear - 4} – ${centerYear + 4}</div>

        <button class="nav-btn" data-center="${centerYear + 9}">›</button>
      </div>

      <div class="year-grid">
        ${Array.from({ length: 9 }, (_, i) => {
          const y = start + i;
          const active = y === centerYear ? "active" : "";
          return `<button class="year-btn ${active}" data-year="${y}">${y}</button>`;
        }).join("")}
      </div>

    </div>
  `;

  bindYearEvents(container, baseUrl, currentMonth);
}

/* ======================================================
   Render Helpers
====================================================== */

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

/* ======================================================
   Bind Events
====================================================== */

function bindMonthEvents(container, baseUrl, year, currentMonth) {

  // 年份點擊 → 年份模式
  container.querySelector(".js-year-toggle")
    ?.addEventListener("click", () => {
      renderYearMode(container, baseUrl, year, currentMonth);
    });

  // 上下年
  container.querySelectorAll(".nav-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const y = Number(btn.dataset.year);
      renderMonthMode(container, baseUrl, y, currentMonth);
    });
  });
}

function bindYearEvents(container, baseUrl, currentMonth) {

  // 點某一年 → 回到月份模式
  container.querySelectorAll(".year-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const y = Number(btn.dataset.year);
      renderMonthMode(container, baseUrl, y, currentMonth);
    });
  });

  // 年份區間切換
  container.querySelectorAll(".nav-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const center = Number(btn.dataset.center);
      renderYearMode(container, baseUrl, center, currentMonth);
    });
  });
}

/* ======================================================
   Utils
====================================================== */

function currentYearMonth() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}
