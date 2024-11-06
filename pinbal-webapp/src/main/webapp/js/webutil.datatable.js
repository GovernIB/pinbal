// Basat en http://stefangabos.ro/jquery/jquery-plugin-boilerplate-revisited/
(function($) {
	let length = 10;
	let path = typeof ctxPath !== 'undefined' ? ctxPath : '/pinbal'
	$.ajax({
		url: path + "/usuari/num/elements/pagina/defecte",
		async: false,
		success: elements => {
			length = elements;
		}
	});
	debugger;
	$.extend(true, $.fn.dataTable.defaults, {
		dom: "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
		pageLength: length,
		aLengthMenu: [ 10, 20, 50, 100, 250 ],
		preDrawCallback: function(settings_) {
			if (settings_.botonsTemplate && settings_.botonsTemplate.length > 0) {
				$.templates("templateNew", $(settings_.botonsTemplate).html());
				var targetBotons = $('.botons', this.parent());
				if (!targetBotons.data('botons-creats')) {
					targetBotons.html($.render.templateNew());
					targetBotons.data('botons-creats', 'true');
				}
			}
			$('div.dataTables_length', $(this).closest('.dataTables_wrapper')).each(function() {
				if (!$('label', $(this)).data('processed')) {
					var label = $('label', $(this));
					var botons = $('<div class="btn-group"></div>');
					$('option', label).each(function() {
						var active = ($(this).val() == settings_.pageLength);
						debugger;
						botons.append('<button value="' + $(this).val() + '" class="btn btn-default' + ((active) ? ' active' : '') + '">' + $(this).val() + '</button>')
					});
					debugger;
					var botoActiu = $('button.active', botons);
					if (botoActiu.length == 0) {
						botons.find('button[value="' + length + '"]').addClass('active');
					}
					label.css('display', 'none');
					label.data('processed', 'true');
					$(this).prepend(botons);
					var select = $('select', $(this));
					$('button', $(this)).on('click', function() {
						$('button', $(this).parent()).removeClass('active');
						$(this).addClass('active');
						select.val($(this).val()).trigger('change');
					});
				}
			});
			if (settings_.filtre) {
				$(settings_.filtre).webutilNetejarErrorsCamps();
			}
		},
		drawCallback: function() {
			//console.log('drawCallback');
			if ($.fn.webutilModalEval) {
				//console.log('webutilModalEval');
				$(this).closest('.dataTables_wrapper').webutilModalEval();
			}
		}
	});
}(jQuery));