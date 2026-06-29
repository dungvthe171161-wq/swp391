document.addEventListener("DOMContentLoaded", () => {
  initializeDashboard()
})

const chartPalette = {
  primary: "#00482f",
  brand: "#006241",
  accent: "#00754a",
  mint: "#87d7ad",
  sage: "#afcdc3",
  gold: "#cba258",
  ceramic: "#eae8e3",
  red: "#c82014",
  text: "#1b1c19",
  muted: "rgba(0, 0, 0, 0.58)",
  grid: "rgba(0, 0, 0, 0.06)"
}

const centerTextPlugin = {
  id: "betterhrCenterText",
  afterDraw(chart, args, options) {
    if (!options || !options.display) return

    const { ctx, chartArea } = chart
    if (!chartArea) return

    const total = options.total ?? 0
    const centerX = (chartArea.left + chartArea.right) / 2
    const centerY = (chartArea.top + chartArea.bottom) / 2

    ctx.save()
    ctx.textAlign = "center"
    ctx.textBaseline = "middle"
    ctx.fillStyle = chartPalette.text
    ctx.font = '800 30px "Hanken Grotesk", "Segoe UI", Arial, sans-serif'
    ctx.fillText(String(total), centerX, centerY - 7)
    ctx.fillStyle = chartPalette.muted
    ctx.font = '600 12px "Hanken Grotesk", "Segoe UI", Arial, sans-serif'
    ctx.fillText("Tổng số", centerX, centerY + 20)
    ctx.restore()
  }
}

if (typeof Chart !== "undefined") {
  try {
    Chart.register(centerTextPlugin)
  } catch (error) {
    // Chart.js skips duplicate plugin ids in most versions; this guard keeps reloads quiet.
  }
}

function initializeDashboard() {
  initializeEmployeeChart()
  initializeStatusChart()
  initializeActivityChart()
  setupEventListeners()
}

function translateStatusLabel(label) {
  const normalized = String(label || "").trim()
  const labels = {
    Active: "Chính thức",
    Probation: "Thử việc",
    Inactive: "Không hoạt động",
    Intern: "Thực tập",
    Official: "Chính thức",
    "Chính thức": "Chính thức",
    "Thử việc": "Thử việc"
  }

  return labels[normalized] || normalized
}

function formatShortDate(dateString) {
  if (!dateString) return "-"

  const dateParts = dateString.split("-")
  if (dateParts.length !== 3) return dateString

  const year = parseInt(dateParts[0], 10)
  const month = parseInt(dateParts[1], 10) - 1
  const day = parseInt(dateParts[2], 10)
  const date = new Date(year, month, day)
  const monthNames = ["Thg 1", "Thg 2", "Thg 3", "Thg 4", "Thg 5", "Thg 6", "Thg 7", "Thg 8", "Thg 9", "Thg 10", "Thg 11", "Thg 12"]

  return `${monthNames[date.getMonth()]} ${date.getDate()}`
}

function initializeEmployeeChart() {
  const ctx = document.getElementById("employeeChart")
  if (!ctx || typeof Chart === "undefined") return

  const data = window.dashboardData.employeeDistribution || {}
  const labels = Object.keys(data)
  const values = Object.values(data)
  const maxValue = values.length ? Math.max(...values) : 0
  const barColors = [chartPalette.accent, chartPalette.gold, chartPalette.mint, chartPalette.sage, chartPalette.ceramic, chartPalette.brand]

  new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Số nhân viên",
          data: values,
          backgroundColor: labels.map((label, index) => barColors[index % barColors.length]),
          borderColor: "transparent",
          borderWidth: 0,
          borderRadius: 6,
          borderSkipped: false,
          barPercentage: 0.56,
          categoryPercentage: 0.72
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: "nearest",
        intersect: false
      },
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          enabled: false
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          suggestedMax: Math.max(4, Math.ceil(maxValue + 1)),
          border: {
            display: false
          },
          grid: {
            color: chartPalette.grid,
            drawBorder: false
          },
          ticks: {
            color: chartPalette.muted,
            precision: 0,
            stepSize: 1,
            font: {
              size: 12,
              weight: "600"
            }
          }
        },
        x: {
          border: {
            display: false
          },
          grid: {
            display: false
          },
          ticks: {
            color: chartPalette.muted,
            maxRotation: 0,
            minRotation: 0,
            autoSkip: false,
            font: {
              size: 11,
              weight: "600"
            }
          }
        }
      }
    }
  })
}

function initializeStatusChart() {
  const ctx = document.getElementById("statusChart")
  if (!ctx || typeof Chart === "undefined") return

  const data = window.dashboardData.employeeStatus || {}
  const labels = Object.keys(data)
  const values = Object.values(data)
  const total = values.reduce((sum, value) => sum + Number(value || 0), 0)

  new Chart(ctx, {
    type: "doughnut",
    data: {
      labels,
      datasets: [
        {
          data: values,
          backgroundColor: [chartPalette.accent, chartPalette.gold, chartPalette.mint, chartPalette.red],
          borderColor: "#ffffff",
          borderWidth: 3,
          hoverOffset: 4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: "68%",
      plugins: {
        betterhrCenterText: {
          display: true,
          total
        },
        tooltip: {
          backgroundColor: "rgba(27, 28, 25, 0.9)",
          titleColor: "#ffffff",
          bodyColor: "#ffffff",
          padding: 10,
          cornerRadius: 8,
          callbacks: {
            title(items) {
              const item = items && items[0]
              return item ? translateStatusLabel(item.label) : ""
            },
            label(context) {
              const value = Number(context.parsed || 0)
              const percent = total ? Math.round((value / total) * 100) : 0
              return `${value} nhân viên (${percent}%)`
            }
          }
        },
        legend: {
          position: "bottom",
          align: "center",
          labels: {
            boxWidth: 8,
            boxHeight: 8,
            usePointStyle: true,
            pointStyle: "circle",
            padding: 14,
            color: chartPalette.text,
            font: {
              size: 12,
              weight: "700"
            },
            generateLabels(chart) {
              const dataset = chart.data.datasets[0] || {}
              return chart.data.labels.map((label, index) => {
                const value = Number(dataset.data[index] || 0)
                const percent = total ? Math.round((value / total) * 100) : 0
                const color = Array.isArray(dataset.backgroundColor)
                  ? dataset.backgroundColor[index % dataset.backgroundColor.length]
                  : dataset.backgroundColor

                return {
                  text: `${translateStatusLabel(label)} ${value} (${percent}%)`,
                  fillStyle: color,
                  strokeStyle: color,
                  lineWidth: 0,
                  hidden: false,
                  index
                }
              })
            }
          }
        }
      }
    }
  })
}

let activityChartInstance = null

function initializeActivityChart() {
  const ctx = document.getElementById("activityChart")
  if (!ctx || typeof Chart === "undefined") return

  const data = window.dashboardData.activityData || []
  updateActivityChart(data)
  calculateActivityStatistics(data)
}

function updateActivityChart(data) {
  const ctx = document.getElementById("activityChart")
  if (!ctx || typeof Chart === "undefined") return

  const labels = data.map(item => formatShortDate(item.date))
  const values = data.map(item => item.count)

  if (activityChartInstance) {
    activityChartInstance.destroy()
  }

  activityChartInstance = new Chart(ctx, {
    type: "line",
    data: {
      labels,
      datasets: [
        {
          label: "Hoạt động hệ thống",
          data: values,
          borderColor: chartPalette.accent,
          backgroundColor: "rgba(0, 117, 74, 0.10)",
          borderWidth: 2,
          fill: true,
          tension: 0.35,
          pointRadius: 3,
          pointHoverRadius: 5,
          pointBackgroundColor: chartPalette.accent,
          pointBorderColor: "#ffffff",
          pointBorderWidth: 2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: "rgba(27, 28, 25, 0.9)",
          titleColor: "#ffffff",
          bodyColor: "#ffffff",
          padding: 10,
          cornerRadius: 8,
          callbacks: {
            label(context) {
              return `Hoạt động: ${context.parsed.y}`
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          min: 0,
          border: {
            display: false
          },
          grid: {
            color: chartPalette.grid
          },
          ticks: {
            color: chartPalette.muted,
            stepSize: 1,
            precision: 0,
            font: {
              size: 12,
              weight: "600"
            }
          }
        },
        x: {
          border: {
            display: false
          },
          grid: {
            display: false
          },
          ticks: {
            color: chartPalette.muted,
            font: {
              size: 12,
              weight: "600"
            }
          }
        }
      }
    }
  })
}

function calculateActivityStatistics(data) {
  const totalActivitiesElement = document.getElementById("totalActivities")
  const avgActivitiesElement = document.getElementById("avgActivities")
  const peakDayElement = document.getElementById("peakDay")
  if (!totalActivitiesElement || !avgActivitiesElement || !peakDayElement) return

  if (!data || data.length === 0) {
    totalActivitiesElement.textContent = "0"
    avgActivitiesElement.textContent = "0"
    peakDayElement.textContent = "-"
    return
  }

  const totalActivities = data.reduce((sum, item) => sum + item.count, 0)
  const avgActivities = data.length > 0 ? (totalActivities / data.length).toFixed(1) : 0
  let peakDay = null
  let peakCount = 0

  data.forEach(item => {
    if (item.count > peakCount) {
      peakCount = item.count
      peakDay = item.date
    }
  })

  totalActivitiesElement.textContent = totalActivities.toString()
  avgActivitiesElement.textContent = avgActivities.toString()
  peakDayElement.textContent = peakDay ? formatShortDate(peakDay) : "-"
}

function loadActivityData(days) {
  const url = window.adminBaseUrl
    ? `${window.adminBaseUrl}?action=activity-data&days=${days}`
    : `${window.contextPath || ""}/admin?action=activity-data&days=${days}`

  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      return response.json()
    })
    .then(data => {
      if (data.error) {
        alert(`Không thể tải dữ liệu hoạt động: ${data.error}`)
        return
      }

      if (data.activityData && Array.isArray(data.activityData)) {
        updateActivityChart(data.activityData)
        calculateActivityStatistics(data.activityData)

        const activityChartCard = document.querySelector("#activityChart").closest(".chart-card")
        if (activityChartCard) {
          const chartInfo = activityChartCard.querySelector(".chart-info")
          if (chartInfo) {
            chartInfo.textContent = `${days} ngày qua`
          }
        }
      } else {
        updateActivityChart([])
        calculateActivityStatistics([])
      }
    })
    .catch(() => {
      alert("Không thể tải dữ liệu hoạt động. Vui lòng thử lại.")
    })
}

function setupEventListeners() {
  const activityChartRange = document.getElementById("activityChartRange")
  if (activityChartRange) {
    activityChartRange.addEventListener("change", (event) => {
      const days = parseInt(event.target.value, 10)
      const url = new URL(window.location)
      url.searchParams.set("days", days.toString())
      window.history.pushState({}, "", url)
      loadActivityData(days)
    })
  }

}
