Raphael.fn.drawGrid = function (x, y, w, h, wv, hv, color) {
	color = color || "#000";
	var path = ["M", Math.round(x) + .5, Math.round(y) + .5, "L", Math.round(x + w) + .5, Math.round(y) + .5, Math.round(x + w) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y) + .5],
	rowHeight = h / hv,
	columnWidth = w / wv;
	for (var i = 1; i < hv; i++) {
		path = path.concat(["M", Math.round(x) + .5, Math.round(y + i * rowHeight) + .5, "H", Math.round(x + w) + .5]);
	}
	for (i = 1; i < wv; i++) {
		path = path.concat(["M", Math.round(x + i * columnWidth) + .5, Math.round(y) + .5, "V", Math.round(y + h) + .5]);
	}
	return this.path(path.join(",")).attr({stroke: color});
};

$(function () {
	$("#data").css({
		position: "absolute",
		left: "-9999em",
		top: "-9999em"
	});
});


//CARREGAR PARÀMETRES
window.onload = function () {
	function getAnchors(p1x, p1y, p2x, p2y, p3x, p3y) {
		var l1 = (p2x - p1x) / 2,
		l2 = (p3x - p2x) / 2,
		a = Math.atan((p2x - p1x) / Math.abs(p2y - p1y)),
		b = Math.atan((p3x - p2x) / Math.abs(p2y - p3y));
		a = p1y < p2y ? Math.PI - a : a;
		b = p3y < p2y ? Math.PI - b : b;
		var alpha = Math.PI / 2 - ((a + b) % (Math.PI * 2)) / 2,
		dx1 = l1 * Math.sin(alpha + a),
		dy1 = l1 * Math.cos(alpha + a),
		dx2 = l2 * Math.sin(alpha + b),
		dy2 = l2 * Math.cos(alpha + b);
		return {
			x1: p2x - dx1,
			y1: p2y + dy1,
			x2: p2x + dx2,
			y2: p2y + dy2
		};
	}

	// Grab the data
	var labels = [],
	data = [];
	$("#data tfoot th").each(function () {
		labels.push($(this).html());
	});
	$("#data tbody td").each(function () {
		data.push($(this).html());
	});

	// Draw
	var width = 275,
	height = 280,
	leftgutter = 10,
	bottomgutter = 40,
	topgutter = 20,
	colorhue = .6 || Math.random(),
	color = "hsl(" + [colorhue, .5, .5] + ")",
	r = Raphael("holder2", width, height),
	txt = {font: '10px Helvetica, Arial', fill: "#000"},
	txt1 = {font: '11px Helvetica, Arial', fill: "#fff","text-anchor":"start"},
	txt2 = {font: '10px Helvetica, Arial', fill: "#fff","text-anchor":"start"},
	X = (width - leftgutter) / labels.length,
	max = Math.max.apply(Math, data),
	Y = (height - bottomgutter - topgutter) / max;
	r.drawGrid(leftgutter + X * .5 + .5, topgutter + .5, width - leftgutter - X, height - topgutter - bottomgutter, 1, 1, "#000");
	var path = r.path().attr({stroke: color, "stroke-width": 4, "stroke-linejoin": "round"}),
	bgp = r.path().attr({stroke: "none", opacity: .3, fill: color}),
	clr = [],

	months = ["receivePackets", "transmitPackets", "receiveBytes", "transmitBytes", "receiveDropped", "transmitDropped", "receiveErrors", "transmitErrors", "receiveFrameErrors", "receiveFrameErrors", "receiveFrameErrors", "collisions"],
	values = [],
	now = 0,
	month = r.text(140, 7, months[now]).attr({fill: "#000", stroke: "none", "font": '100 18px "Helvetica Neue", Helvetica, "Arial Unicode MS", Arial, sans-serif'}),
	rightc = r.circle(234, 7, 10).attr({fill: "#fff", stroke: "none"}),
	right = r.path("M230,2l10,5 -10,5z").attr({fill: "#000"}),
	leftc = r.circle(46, 7, 10).attr({fill: "#fff", stroke: "none"}),
	left = r.path("M50,2l-10,5 10,5z").attr({fill: "#000"}),
	c = r.path("M0,0").attr({fill: "none", "stroke-width": 4, "stroke-linecap": "round"}),
	bg = r.path("M0,0").attr({stroke: "none", opacity: .3}),

	label = r.set(),
	lx = 0, ly = 0,
	is_label_visible = false,
	leave_timer,
	blanket = r.set();
	label.push(r.text(60, 12, "                      bytes").attr(txt1));
	label.push(r.text(60, 27, "                      ").attr(txt2).attr({fill: color}));
	label.hide();
	var frame = r.popup(300, 100, label, "right").attr({fill: "#000", stroke: "#666", "stroke-width": 2, "fill-opacity": .7}).hide();

	var p, bgpp;
	for (var i = 0, ii = labels.length; i < ii; i++) {
		var y = Math.round(height - bottomgutter - Y * data[i]),
		x = Math.round(leftgutter + X * (i + .5)),
		t = r.text(x, height - 4, labels[i]).attr(txt).attr({transform: "r270"}).toBack();
		if (!i) {
			p = ["M", x, y, "C", x, y];
			bgpp = ["M", leftgutter + X * .5, height - bottomgutter, "L", x, y, "C", x, y];
		}
		if (i && i < ii - 1) {
			var Y0 = Math.round(height - bottomgutter - Y * data[i - 1]),
			X0 = Math.round(leftgutter + X * (i - .5)),
			Y2 = Math.round(height - bottomgutter - Y * data[i + 1]),
			X2 = Math.round(leftgutter + X * (i + 1.5));
			var a = getAnchors(X0, Y0, x, y, X2, Y2);
			p = p.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
			bgpp = bgpp.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
		}
		var dot = r.circle(x, y, 4).attr({fill: "#333", stroke: color, "stroke-width": 2});
		blanket.push(r.rect(leftgutter + X * i, 0, X, height - bottomgutter).attr({stroke: "none", fill: "#fff", opacity: 0}));
		var rect = blanket[blanket.length - 1];
		(function (x, y, data, lbl, dot) {
			var timer, i = 0;
			rect.hover(function () {
				clearTimeout(leave_timer);
				var side = "right";
				if (x + frame.getBBox().width > width) {
					side = "left";
				}
				var ppp = r.popup(x, y, label, side, 1),
				anim = Raphael.animation({
					path: ppp.path,
					transform: ["t", ppp.dx, ppp.dy]
				}, 200 * is_label_visible);
				lx = label[0].transform()[0][1] + ppp.dx;
				ly = label[0].transform()[0][2] + ppp.dy;
				frame.show().stop().animate(anim);
				label[0].attr({text: data + "                      bytes" + (data == 1 ? " " : " ")}).show().stop().animateWith(frame, anim, {transform: ["t", lx, ly]}, 200 * is_label_visible);
				label[1].attr({text: lbl + "                      "}).show().stop().animateWith(frame, anim, {transform: ["t", lx, ly]}, 200 * is_label_visible);
				dot.attr("r", 6);
				is_label_visible = true;
			}, function () {
				dot.attr("r", 4);
				leave_timer = setTimeout(function () {
					frame.hide();
					label[0].hide();
					label[1].hide();
					is_label_visible = false;
				}, 1);
			});
		})(x, y, data[i], labels[i], dot);
	}
	p = p.concat([x, y, x, y]);
	bgpp = bgpp.concat([x, y, x, y, "L", x, height - bottomgutter, "z"]);
	path.attr({path: p});
	bgp.attr({path: bgpp});
	frame.toFront();
	label[0].toFront();
	label[1].toFront();
	blanket.toFront();


	//
	c.attr({path: values[0], stroke: clr[0]});
	bg.attr({path: values[0] + "L590,250 10,250z", fill: clr[0]});
	var animation = function () {
		var time = 500;
		if (now == 12) {
			now = 0;
		}
		if (now == -1) {
			now = 11;
		}
		var anim = Raphael.animation({path: values[now], stroke: clr[now]}, time, "<>");
		c.animate(anim);
		bg.animateWith(c, anim, {path: values[now] + "L590,250 10,250z", fill: clr[now]}, time, "<>");
		month.attr({text: months[now]});
	};
	var next = function () {
		now++;
		animation();
	},
	prev = function () {
		now--;
		animation();
	};
	rightc.click(next);
	right.click(next);
	leftc.click(prev);
	left.click(prev);

};