// Basat en http://stefangabos.ro/jquery/jquery-plugin-boilerplate-revisited/
(function($) {
	$.extend( true, $.fn.dataTable.defaults, {
		"dom": "<'row'<'col-md-6'i><'col-md-6'>><'row'<'col-md-12'rt>><'row'<'col-md-6'l><'col-md-6'p>>",
	    "preDrawCallback": function(settings_) {
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
	                    botons.append('<button value="' + $(this).val() + '" class="btn btn-default' + ((active) ? ' active': '') + '">' + $(this).val() + '</button>')
	                });
					$(botons.find('button')[0]).addClass('active');
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
	    }
	} );
}(jQuery));