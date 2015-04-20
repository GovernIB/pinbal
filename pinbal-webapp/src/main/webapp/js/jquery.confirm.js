(function($){
	
	$.confirm = function(params){
		
		if($('#confirmOverlay').length){
			// A confirm is already shown on the page:
			return false;
		}
		
		var buttonHTML = '';
		$.each(params.buttons,function(name,obj){
			
			// Generating the markup for the buttons:
			
			buttonHTML += '<div class="button '+obj['class']+'">'+name+'</div>';
			
			if(!obj.action){
				obj.action = function(){};
			}
		});
		var markup = [
			'<div id="confirmOverlay">',
			'<div id="confirmBox">',
			'<div id="confirmText">',params.message,'</div>',
			'<div id="confirmButtons">',
			buttonHTML,
			'<br class="clear"></div></div></div>'
		].join('');
		
		$(markup).hide().appendTo('body').fadeIn();
		
		var buttons = $('#confirmBox .button'),
			i = 0;

		$.each(params.buttons,function(name,obj){
			buttons.eq(i++).click(function(){
				
				// Calling the action attribute when a
				// click occurs, and hiding the confirm.
				
				obj.action();
				$.confirm.hide();
				return false;
			});
		});
	}

	$.confirm.hide = function(){
		$('#confirmOverlay').fadeOut(function(){
			$(this).remove();
		});
	}
	
})(jQuery);