$(document).ready(function() {
	$(window).on("resize", function () {
		var ampleLogos = $(this).width() - $("#main-menu").width() - 70;
		if (ampleLogos > 300) $("#govern-logo").show();
		
		if (ampleLogos < 530 && ampleLogos >= 360) {
			var ampleGov = Math.min(Math.round(ampleLogos * 0.325), 159);
			var ampleApp = Math.min(Math.round(ampleLogos * 0.445), 217);
			var ampleMar = Math.min(Math.round(ampleLogos * 0.08), 40);
			var ampleRes =  Math.min(ampleLogos - ampleGov - ampleApp - (2 * ampleMar), 33);
			
			$("#govern-logo").css('padding-right', ampleRes + 'px');
			$("#govern-img").width(ampleGov);
			$("#app-logo").css('padding-left', ampleMar + 'px');
			$("#app-logo").css('padding-right', ampleMar + 'px');
			$("#app-img").width(ampleApp);
		} else if (ampleLogos < 360) {
			$("#govern-logo").hide();
			var ampleApp = Math.min(Math.round(ampleLogos * 0.85), 217);
			
			$("#app-logo").css('padding-left', '40px');
			$("#app-logo").css('padding-right', (ampleLogos - ampleApp) + 'px');
			$("#app-img").width(ampleApp);
		} else {
			$("#govern-logo").css('padding-right', '33px');
			$("#govern-img").width(159);
			$("#app-logo").css('padding-left', '40px');
			$("#app-logo").css('padding-right', '40px');
			$("#app-img").width(217);
		}
	}).resize();
});