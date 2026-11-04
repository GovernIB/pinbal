/**
 * Substitut de $(target).load(url[, data]) per a pàgines /modal/* que s'han d'inserir
 * dins un modal de Bootstrap de la pàgina actual (no dins un iframe).
 *
 * Les pàgines /modal/* es decoren amb senseCapNiPeus.jsp, que és un document HTML complet
 * amb els seus propis <script> de jQuery, bootstrap.js, font-awesome... Amb .load() aquests
 * scripts es tornen a executar dins la pàgina principal: es redefineix window.$ (es perden
 * els plugins ja carregats) i bootstrap torna a registrar els seus handlers sobre document,
 * de manera que els dropdowns s'obren i es tanquen amb el mateix clic i deixen de funcionar.
 *
 * Aquesta funció insereix el contingut però descarta els <script src> i <link href> que ja
 * estan carregats a la pàgina actual. Els scripts inline i les llibreries que encara no hi són
 * s'executen igual que amb .load(), en el mateix ordre.
 */
function webutilLoadModalContent(target, url, data, complete) {
	var $target = $(target);
	var ajaxOpts = {url: url, dataType: 'html'};
	if (data && typeof data === 'object') {
		// Mateix criteri que $.fn.load: si es passen dades, la petició és POST
		ajaxOpts.type = 'POST';
		ajaxOpts.data = data;
	}
	return $.ajax(ajaxOpts).done(function(html) {
		var carregats = {};
		$('script[src]').each(function() { carregats[this.src] = true; });
		$('link[href]').each(function() { carregats[this.href] = true; });
		// innerHTML no executa els scripts; elimina les etiquetes html/head/body i en manté el contingut
		var contenidor = document.createElement('div');
		contenidor.innerHTML = html;
		$(contenidor).find('title, meta').remove();
		$(contenidor).find('script[src]').filter(function() { return carregats[this.src]; }).remove();
		$(contenidor).find('link[href]').filter(function() { return carregats[this.href]; }).remove();
		// .append() de jQuery executa els scripts que queden, en ordre
		$target.empty().append($(contenidor).contents());
	}).always(function() {
		if (typeof complete === 'function') {
			complete.apply(this, arguments);
		}
	});
}
