"use strict";
$("#modeSwitcher").on("click", function (e) {
  e.preventDefault(), modeSwitch(), location.reload()
}), $(".collapseSidebar").on("click", function (e) {
  $(".vertical").hasClass("narrow") ? $(".vertical").toggleClass("open") : ($(".vertical").toggleClass("collapsed"), $(".vertical").hasClass("hover") && $(".vertical").removeClass("hover")), e.preventDefault()
}), $(".sidebar-left").hover(function () {
  $(".vertical").hasClass("collapsed") && $(".vertical").addClass("hover"), $(".narrow").hasClass("open") || $(".vertical").addClass("hover")
}, function () {
  $(".vertical").hasClass("collapsed") && $(".vertical").removeClass("hover"), $(".narrow").hasClass("open") || $(".vertical").removeClass("hover")
}), $(".toggle-sidebar").on("click", function () {
  $(".navbar-slide").toggleClass("show")
}), function (a) {
  a(".dropdown-menu a.dropdown-toggle").on("click", function (e) {
    return a(this).next().hasClass("show") || a(this).parents(".dropdown-menu").first().find(".show").removeClass("show"), a(this).next(".dropdown-menu").toggleClass("show"), a(this).parents("li.nav-item.dropdown.show").on("hidden.bs.dropdown", function (e) {
      a(".dropdown-submenu .show").removeClass("show")
    }), !1
  })
}(jQuery), $(".navbar .dropdown").on("hidden.bs.dropdown", function () {
  $(this).find("li.dropdown").removeClass("show open"), $(this).find("ul.dropdown-menu").removeClass("show open")
}), $(".file-panel .card").on("click", function () {
  $(this).hasClass("selected") ? ($(this).removeClass("selected"), $(this).find("bg-light").removeClass("shadow-lg"), $(".file-container").removeClass("collapsed")) : ($(this).addClass("selected"), $(this).addClass("shadow-lg"), $(".file-panel .card").not(this).removeClass("selected"), $(".file-container").addClass("collapsed"))
}), $(".close-info").on("click", function () {
  $(".file-container").hasClass("collapsed") && ($(".file-container").removeClass("collapsed"), $(".file-panel").find(".selected").removeClass("selected"))
}), $(function () {
  $(".info-content").stickOnScroll({topOffset: 0, setWidthOnStick: !0})
});
var basic_wizard = $("#example-basic");
basic_wizard.length && basic_wizard.steps({
  headerTag: "h3",
  bodyTag: "section",
  transitionEffect: "slideLeft",
  autoFocus: !0
});
var vertical_wizard = $("#example-vertical");
vertical_wizard.length && vertical_wizard.steps({
  headerTag: "h3",
  bodyTag: "section",
  transitionEffect: "slideLeft",
  stepsOrientation: "vertical"
});
var form = $("#example-form");
form.length && (form.validate({
  errorPlacement: function (e, a) {
    a.before(e)
  }, rules: {confirm: {equalTo: "#password"}}
}), form.children("div").steps({
  headerTag: "h3",
  bodyTag: "section",
  transitionEffect: "slideLeft",
  onStepChanging: function (e, a, o) {
    return form.validate().settings.ignore = ":disabled,:hidden", form.valid()
  },
  onFinishing: function (e, a) {
    return form.validate().settings.ignore = ":disabled", form.valid()
  },
  onFinished: function (e, a) {
    alert("Submitted!")
  }
}));
var ChartOptions = {
  maintainAspectRatio: !1,
  responsive: !0,
  legend: {display: !1},
  scales: {
    xAxes: [{gridLines: {display: !1}}],
    yAxes: [{gridLines: {display: !1, color: colors.borderColor, zeroLineColor: colors.borderColor}}]
  }
}, ChartData = {
  labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep"],
  datasets: [{
    label: "Visitors",
    barThickness: 10,
    backgroundColor: base.primaryColor,
    borderColor: base.primaryColor,
    pointRadius: !1,
    pointColor: "#3b8bba",
    pointStrokeColor: "rgba(60,141,188,1)",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(60,141,188,1)",
    data: [28, 48, 40, 19, 64, 27, 90, 85, 92],
    fill: "",
    lineTension: .1
  }, {
    label: "Orders",
    barThickness: 10,
    backgroundColor: "rgba(210, 214, 222, 1)",
    borderColor: "rgba(210, 214, 222, 1)",
    pointRadius: !1,
    pointColor: "rgba(210, 214, 222, 1)",
    pointStrokeColor: "#c1c7d1",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(220,220,220,1)",
    data: [65, 59, 80, 42, 43, 55, 40, 36, 68],
    fill: "",
    borderWidth: 2,
    lineTension: .1
  }]
}, lineChartData = {
  labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep"],
  datasets: [{
    label: "Visitors",
    barThickness: 10,
    borderColor: base.primaryColor,
    pointRadius: !1,
    pointColor: "#3b8bba",
    pointStrokeColor: "rgba(60,141,188,1)",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(60,141,188,1)",
    data: [28, 48, 30, 19, 64, 27, 90, 85, 92],
    fill: "",
    lineTension: .2
  }, {
    label: "Sales",
    barThickness: 10,
    borderColor: "rgba(40, 167, 69, 0.8)",
    pointRadius: !1,
    pointColor: "#3b8bba",
    pointStrokeColor: "rgba(60,141,188,1)",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(60,141,188,1)",
    data: [8, 18, 20, 29, 26, 7, 30, 25, 48],
    fill: "",
    borderWidth: 2,
    lineTension: .2
  }, {
    label: "Orders",
    backgroundColor: "rgba(210, 214, 222, 1)",
    borderColor: "rgba(210, 214, 222, 1)",
    pointRadius: !1,
    pointColor: "rgba(210, 214, 222, 1)",
    pointStrokeColor: "#c1c7d1",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(220,220,220,1)",
    data: [65, 59, 80, 42, 43, 55, 40, 36, 68],
    fill: "",
    borderWidth: 2,
    lineTension: .2
  }]
}, pieChartData = {
  labels: ["Clothing", "Shoes", "Electronics", "Books", "Cosmetics"],
  datasets: [{data: [18, 30, 42, 12, 7], backgroundColor: chartColors, borderColor: colors.borderColor}]
}, areaChartData = {
  labels: ["Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
  datasets: [{
    label: "Visitors",
    barThickness: 10,
    backgroundColor: base.primaryColor,
    borderColor: base.primaryColor,
    pointRadius: !1,
    pointColor: "#3b8bba",
    pointStrokeColor: "rgba(60,141,188,1)",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(60,141,188,1)",
    data: [19, 64, 37, 76, 68, 88, 54, 46, 58],
    lineTension: .1
  }, {
    label: "Orders",
    barThickness: 10,
    backgroundColor: "rgba(210, 214, 222, 1)",
    borderColor: "rgba(255, 255, 255, 1)",
    pointRadius: !1,
    pointColor: "rgba(210, 214, 222, 1)",
    pointStrokeColor: "#c1c7d1",
    pointHighlightFill: "#fff",
    pointHighlightStroke: "rgba(220,220,220,1)",
    data: [42, 43, 55, 40, 36, 68, 22, 66, 49],
    lineTension: .1
  }]
}, barChartjs = document.getElementById("barChartjs");
barChartjs && new Chart(barChartjs, {type: "bar", data: ChartData, options: ChartOptions});
var lineChartjs = document.getElementById("lineChartjs");
lineChartjs && new Chart(lineChartjs, {type: "line", data: lineChartData, options: ChartOptions});
var pieChartjs = document.getElementById("pieChartjs");
pieChartjs && new Chart(pieChartjs, {
  type: "pie",
  data: pieChartData,
  options: {maintainAspectRatio: !1, responsive: !0}
});
var areaChartjs = document.getElementById("areaChartjs");
areaChartjs && new Chart(areaChartjs, {
  type: "line",
  data: areaChartData,
  options: ChartOptions
}), $(".sparkline").length && ($(".inlinebar").sparkline([3, 2, 7, 5, 4, 6, 8], {
  type: "bar",
  width: "100%",
  height: "32",
  barColor: base.primaryColor,
  barWidth: 4,
  barSpacing: 2
}), $(".inlineline").sparkline([2, 0, 5, 7, 4, 6, 8], {
  type: "line",
  width: "100%",
  height: "32",
  defaultPixelsPerValue: 5,
  lineColor: base.primaryColor,
  fillColor: "transparent",
  minSpotColor: !1,
  spotColor: !1,
  highlightSpotColor: "",
  maxSpotColor: !1,
  lineWidth: 2
}), $(".inlinepie").sparkline([5, 7, 4, 6, 8], {type: "pie", height: "32", width: "32", sliceColors: chartColors}));
var gauge1, svgg1 = document.getElementById("gauge1");
svgg1 && (gauge1 = Gauge(svgg1, {
  max: 100,
  dialStartAngle: -90,
  dialEndAngle: -90.001,
  value: 100,
  showValue: !1,
  label: function (e) {
    return Math.round(100 * e) / 100
  },
  color: function (e) {
    return e < 20 ? base.primaryColor : e < 40 ? base.successColor : e < 60 ? base.warningColor : base.dangerColor
  }
}), function e() {
  gauge1.setValue(90), gauge1.setValueAnimated(30, 1), window.setTimeout(e, 6e3)
}());
var gauge2, svgg2 = document.getElementById("gauge2");
svgg2 && (gauge2 = Gauge(svgg2, {max: 100, value: 46, dialStartAngle: -0, dialEndAngle: -90.001}), function e() {
  gauge2.setValue(40), gauge2.setValueAnimated(30, 1), window.setTimeout(e, 6e3)
}());
var gauge3, svgg3 = document.getElementById("gauge3");
svgg3 && (gauge3 = Gauge(svgg3, {
  max: 100,
  dialStartAngle: -90,
  dialEndAngle: -90.001,
  value: 80,
  showValue: !1,
  label: function (e) {
    return Math.round(100 * e) / 100
  }
}));
var gauge4, svgg4 = document.getElementById("gauge4");
svgg4 && (gauge4 = Gauge(document.getElementById("gauge4"), {
  max: 500,
  dialStartAngle: 90,
  dialEndAngle: 0,
  value: 50
}));