(function ($) {
    "use strict";

    new WOW().init();  

    /*---background image---*/
	function dataBackgroundImage() {
		$('[data-bgimg]').each(function () {
			var bgImgUrl = $(this).data('bgimg');
			$(this).css({
				'background-image': 'url(' + bgImgUrl + ')', // + meaning concat
			});
		});
    }
    
    $(window).on('load', function () {
        dataBackgroundImage();
    });
    
    /*---stickey menu---*/
    $(window).on('scroll',function() {    
           var scroll = $(window).scrollTop();
           if (scroll < 100) {
            $(".sticky-header").removeClass("sticky");
           }else{
            $(".sticky-header").addClass("sticky");
           }
    });
    

    /*---slider activation---*/
    $('.slider_area').owlCarousel({
        animateOut: 'fadeOut',
        autoplay: true,
		loop: true,
        nav: false,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 1,
        dots:true,
    });
    
    /*---product column5 activation---*/
       $('.product_column5').on('changed.owl.carousel initialized.owl.carousel', function (event) {
        $(event.target).find('.owl-item').removeClass('last').eq(event.item.index + event.page.size - 1).addClass('last')}).owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 5,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            576:{
				items:2,
			},
            768:{
				items:3,
			},
            992:{
				items:4,
			},
            1200:{
				items:5,
			},


		  }
    });
    
    /*---product column4 activation---*/
       $('.product_column4').on('changed.owl.carousel initialized.owl.carousel', function (event) {
        $(event.target).find('.owl-item').removeClass('last').eq(event.item.index + event.page.size - 1).addClass('last')}).owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 4,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            576:{
				items:2,
			},
            768:{
				items:3,
			},
            992:{
				items:4,
			},


		  }
    });
    
    
     /*---product column4 activation---*/
       $('.product_sidebar_column4').on('changed.owl.carousel initialized.owl.carousel', function (event) {
        $(event.target).find('.owl-item').removeClass('last').eq(event.item.index + event.page.size - 1).addClass('last')}).owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 4,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            576:{
				items:2,
			},
            768:{
				items:3,
			},
                
            1200:{
				items:4,
			},
		  }
    });
    
    /*---featured column3 activation---*/
    $('.featured_column3').on('changed.owl.carousel initialized.owl.carousel', function (event) {
        $(event.target).find('.owl-item').removeClass('last').eq(event.item.index + event.page.size - 1).addClass('last')}).owlCarousel({
        autoplay: true,
		loop: true,
        nav: false,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 3,
        dots:false,
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            768:{
				items:2,
			},
            992:{
				items:3,
			},

		  }
    });
    

    /*---product column3 activation---*/
    $('.product_column3').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 3,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            768:{
				items:2,
			},
            992:{
				items:3,
			},

		  }
    });
    
    /*---product column2 activation---*/
    $('.product_column2').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 2,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            768:{
				items:1,
                margin:0,
			},
             992:{
				items:2,
			},   
		  }
    });
    
    $('#nav-tab a,#nav-tab2 a').on('click', function (e) {
        e.preventDefault()
        $(this).tab('show')
    })
    
    /*---product column4 activation---*/
    $('.blog_column4').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 4,
        margin:20,
        dots:false,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            768:{
				items:3,
			},
             992:{
				items:3,
			}, 
            1200:{
				items:4,
			}, 
		  }
    });
    
    /*---blog thumb activation---*/
    $('.blog_thumb_active').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 1,
        margin:20,
        navText: ['<i class="ion-ios-arrow-thin-left"></i>','<i class="ion-ios-arrow-thin-right"></i>'],
    });
    
    /*---brand container activation---*/
     $('.brand_container').on('changed.owl.carousel initialized.owl.carousel', function (event) {
        $(event.target).find('.owl-item').removeClass('last').eq(event.item.index + event.page.size - 1).addClass('last')}).owlCarousel({
        autoplay: true,
		loop: true,
        nav: false,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 5,
        dots:false,
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            480:{
				items:2,
			},
            768:{
				items:3,
			},
            992:{
				items:4,
			},
            1200:{
				items:5,
			},

		  }
    });
    
    /*---small product activation---*/
    $('.small_product_active').slick({
        centerMode: true,
        centerPadding: '0',
        slidesToShow: 1,
        arrows:false,
        rows: 3,
        responsive:[
            {
              breakpoint: 480,
              settings: {
                slidesToShow: 1,
                  slidesToScroll: 1,
              }
            },
            {
              breakpoint: 768,
              settings: {
                slidesToShow: 1,
                  slidesToScroll: 1,
              }
            },
            {
              breakpoint: 991,
              settings: {
                slidesToShow: 2,
                  slidesToScroll: 2,
              }
            },
        ]
    });

    /*---single product activation---*/
    $('.single-product-active').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 4,
        margin:15,
        dots:false,
        navText: ['<i class="fa fa-angle-left"></i>','<i class="fa fa-angle-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            320:{
				items:2,
			},
            992:{
				items:3,
			},
            1200:{
				items:4,
			},


		  }
    });
 
    /*---testimonial active activation---*/
    $('.testimonial_active').owlCarousel({
        autoplay: true,
		loop: true,
        nav: false,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 1,
        dots:true,
    })
 
    /*---product navactive activation---*/
    $('.product_navactive').owlCarousel({
        autoplay: true,
		loop: true,
        nav: true,
        autoplay: false,
        autoplayTimeout: 8000,
        items: 4,
        dots:false,
        navText: ['<i class="fa fa-angle-left"></i>','<i class="fa fa-angle-right"></i>'],
        responsiveClass:true,
		responsive:{
				0:{
				items:1,
			},
            250:{
				items:2,
			},
            480:{
				items:3,
			},
            768:{
				items:4,
			},
		  
        }
    });

    $('.modal').on('shown.bs.modal', function (e) {
        $('.product_navactive').resize();
    })

    $('.product_navactive a').on('click',function(e){
      e.preventDefault();

      var $href = $(this).attr('href');

      $('.product_navactive a').removeClass('active');
      $(this).addClass('active');

      $('.product-details-large .tab-pane').removeClass('active show');
      $('.product-details-large '+ $href ).addClass('active show');

    })
       
    /*--- Magnific Popup Video---*/
    $('.video_popup').magnificPopup({
        type: 'iframe',
        removalDelay: 300,
        mainClass: 'mfp-fade'
    });
    
    /*--- new vidio play Video---*/
    $('.new_vidio_play').magnificPopup({
        type: 'iframe',
        removalDelay: 300,
        mainClass: 'mfp-fade'
    });
    
    /*--- Magnific Popup Video---*/
    $('.port_popup').magnificPopup({
        type: 'image',
        gallery: {
            enabled: true
        }
    });
 
    /*--- niceSelect---*/
     $('.select_option').niceSelect();
    
    /*---  Accordion---*/
    $(".faequently-accordion").collapse({
        accordion:true,
        open: function() {
        this.slideDown(300);
      },
      close: function() {
        this.slideUp(300);
      }		
    });	  

   

    /*---  ScrollUp Active ---*/
    $.scrollUp({
        scrollText: '<i class="fa fa-angle-double-up"></i>',
        easingType: 'linear',
        scrollSpeed: 900,
        animation: 'fade'
    });   
    
    /*---countdown activation---*/
		
	 $('[data-countdown]').each(function() {
		var $this = $(this), finalDate = $(this).data('countdown');
		$this.countdown(finalDate, function(event) {
		$this.html(event.strftime('<div class="countdown_area"><div class="single_countdown"><div class="countdown_number">%D</div><div class="countdown_title">Days</div></div><div class="single_countdown"><div class="countdown_number">%H</div><div class="countdown_title">Hours</div></div><div class="single_countdown"><div class="countdown_number">%M</div><div class="countdown_title">Mins</div></div><div class="single_countdown"><div class="countdown_number">%S</div><div class="countdown_title">Secs</div></div></div>'));     
               
       });
	});	
    
    /*---slider-range here---*/
    $( "#slider-range" ).slider({
        range: true,
        min: 0,
        max: 500,
        values: [ 0, 500 ],
        slide: function( event, ui ) {
        $( "#amount" ).val( "€" + ui.values[ 0 ] + " - €" + ui.values[ 1 ] );
       }
    });
    $( "#amount" ).val( "€" + $( "#slider-range" ).slider( "values", 0 ) +
       " - €" + $( "#slider-range" ).slider( "values", 1 ) );
    
    /*---niceSelect---*/
     $('.niceselect_option').niceSelect();
    
    /*---elevateZoom---*/
    $("#zoom1").elevateZoom({
        gallery:'gallery_01', 
        responsive : true,
        cursor: 'crosshair',
        zoomType : 'inner'
    
    });  
    
    /*---portfolio Isotope activation---*/
      $('.portfolio_gallery').imagesLoaded( function() {

        var $grid = $('.portfolio_gallery').isotope({
           itemSelector: '.gird_item',
            percentPosition: true,
            masonry: {
                columnWidth: '.gird_item'
            }
        });

          /*---ilter items on button click---*/
        $('.portfolio_button').on( 'click', 'button', function() {
           var filterValue = $(this).attr('data-filter');
           $grid.isotope({ filter: filterValue });
            
           $(this).siblings('.active').removeClass('active');
           $(this).addClass('active');
        });
       
    });
    
    
    
     /*---mini cart activation---*/
    $('.mini_cart_wrapper > a').on('click', function(){
        $('.mini_cart,.off_canvars_overlay').addClass('active')
    });
    
    $('.mini_cart_close,.off_canvars_overlay').on('click', function(){
        $('.mini_cart,.off_canvars_overlay').removeClass('active')
    });
    
    
    /*categories slideToggle*/
    $(".categories_title").on("click", function() {
        $(this).toggleClass('active');
        $('.categories_menu_toggle').slideToggle('medium');
    }); 

    
   /*----------  Category more toggle  ----------*/
	$(".categories_menu_toggle li.hidden").hide();
	   $("#more-btn").on('click', function (e) {

		e.preventDefault();
		$(".categories_menu_toggle li.hidden").toggle(500);
		var htmlAfter = '<i class="fa fa-minus" aria-hidden="true"></i> Less Categories';
		var htmlBefore = '<i class="fa fa-plus" aria-hidden="true"></i> More Categories';


		if ($(this).html() == htmlBefore) {
			$(this).html(htmlAfter);
		} else {
			$(this).html(htmlBefore);
		}
	});
    
    
    /*---MailChimp---*/
    $('#mc-form').ajaxChimp({
        language: 'en',
        callback: mailChimpResponse,
        // ADD YOUR MAILCHIMP URL BELOW HERE!
        url: 'http://devitems.us11.list-manage.com/subscribe/post?u=6bbb9b6f5827bd842d9640c82&amp;id=05d85f18ef'

    });
    function mailChimpResponse(resp) {

        if (resp.result === 'success') {
            $('.mailchimp-success').addClass('active')
            $('.mailchimp-success').html('' + resp.msg).fadeIn(900);
            $('.mailchimp-error').fadeOut(400);

        } else if(resp.result === 'error') {
            $('.mailchimp-error').html('' + resp.msg).fadeIn(900);
        }  
    }
    
    
    /* ---------------------
	 Category menu
	--------------------- */
    
   
    function categorySubMenuToggle(){
        $('.categories_menu_toggle li.menu_item_children > a').on('click', function(){
        if($(window).width() < 991){
            $(this).removeAttr('href');
            var element = $(this).parent('li');
            if (element.hasClass('open')) {
                element.removeClass('open');
                element.find('li').removeClass('open');
                element.find('ul').slideUp();
            }
            else {
                element.addClass('open');
                element.children('ul').slideDown();
                element.siblings('li').children('ul').slideUp();
                element.siblings('li').removeClass('open');
                element.siblings('li').find('li').removeClass('open');
                element.siblings('li').find('ul').slideUp();
            }
        }
        });
        $('.categories_menu_toggle li.menu_item_children > a').append('<span class="expand"></span>');
    }
    categorySubMenuToggle();


    /*---shop grid activation---*/
    $('.shop_toolbar_btn > button').on('click', function (e) {
        
		e.preventDefault();
        
        $('.shop_toolbar_btn > button').removeClass('active');
		$(this).addClass('active');
        
		var parentsDiv = $('.shop_wrapper');
		var viewMode = $(this).data('role');
        
        
		parentsDiv.removeClass('grid_3 grid_4 grid_5 grid_list').addClass(viewMode);

		if(viewMode == 'grid_3'){
			parentsDiv.children().addClass('col-lg-4 col-md-4 col-sm-6').removeClass('col-lg-3 col-cust-5 col-12');
            
		}

		if(viewMode == 'grid_4'){
			parentsDiv.children().addClass('col-lg-3 col-md-4 col-sm-6').removeClass('col-lg-4 col-cust-5 col-12');
		}
        
        if(viewMode == 'grid_list'){
			parentsDiv.children().addClass('col-12').removeClass('col-lg-3 col-lg-4 col-md-4 col-sm-6 col-cust-5');
		}
            
	});
  
    
    
   /*---Newsletter Popup activation---*/
   
       setTimeout(function() {
            if($.cookie('shownewsletter')==1) $('.newletter-popup').hide();
            $('#subscribe_pemail').keypress(function(e) {
                if(e.which == 13) {
                    e.preventDefault();
                    email_subscribepopup();
                }
                var name= $(this).val();
                  $('#subscribe_pname').val(name);
            });
            $('#subscribe_pemail').change(function() {
             var name= $(this).val();
                      $('#subscribe_pname').val(name);
            });
            //transition effect
            if($.cookie("shownewsletter") != 1){
                $('.newletter-popup').bPopup();
            }
            $('#newsletter_popup_dont_show_again').on('change', function(){
                if($.cookie("shownewsletter") != 1){   
                    $.cookie("shownewsletter",'1')
                }else{
                    $.cookie("shownewsletter",'0')
                }
            }); 
        }, 2500);
    
    /*---canvas menu activation---*/
    
    /*---canvas menu activation---*/
    $('.canvas_open').on('click', function(){
        $('.Offcanvas_menu_wrapper,.off_canvars_overlay').addClass('active')
    });
    
    $('.canvas_close,.off_canvars_overlay').on('click', function(){
        $('.Offcanvas_menu_wrapper,.off_canvars_overlay').removeClass('active')
    });
    
    
    
    /*---Off Canvas Menu---*/
    var $offcanvasNav = $('.offcanvas_main_menu'),
        $offcanvasNavSubMenu = $offcanvasNav.find('.sub-menu');
    $offcanvasNavSubMenu.parent().prepend('<span class="menu-expand"><i class="fa fa-angle-down"></i></span>');
    
    $offcanvasNavSubMenu.slideUp();
    
    $offcanvasNav.on('click', 'li a, li .menu-expand', function(e) {
        var $this = $(this);
        if ( ($this.parent().attr('class').match(/\b(menu-item-has-children|has-children|has-sub-menu)\b/)) && ($this.attr('href') === '#' || $this.hasClass('menu-expand')) ) {
            e.preventDefault();
            if ($this.siblings('ul:visible').length){
                $this.siblings('ul').slideUp('slow');
            }
            else {
                $this.addClass('open');
                $this.children('ul').slideDown('slow');
                $this.siblings('li').children('ul').slideUp('slow');
                $this.siblings('li').removeClass('open');
                $this.siblings('li').find('li').removeClass('open');
                $this.siblings('li').find('ul').slideUp('slow');
            }
        }
        if( $this.is('a') || $this.is('span') || $this.attr('clas').match(/\b(menu-expand)\b/) ){
        	$this.parent().toggleClass('menu-open');
        }else if( $this.is('li') && $this.attr('class').match(/\b('menu-item-has-children')\b/) ){
        	$this.toggleClass('menu-open');
        }
    });
    
    
    /*js ripples activation*/
    $('.js-ripples').ripples({
		resolution: 512,
		dropRadius: 20,
		perturbance: 0.04
	});
    
    
    
    /*---small product activation---*/
    jQuery(window).on('load', function () {
        $('.instagram-wrap').slick({
            centerMode: true,
            centerPadding: '0',
            slidesToShow: 4,
            arrows:true,
            prevArrow:'<button class="prev_arrow"><i class="ion-ios-arrow-thin-left"></i></button>',
            nextArrow:'<button class="next_arrow"><i class="ion-ios-arrow-thin-right"></i></button>',
            responsive:[
                {
                  breakpoint: 300,
                  settings: {
                    slidesToShow: 1,
                  }
                },
                {
                  breakpoint: 576,
                  settings: {
                    slidesToShow: 2,
                  }
                },
                {
                  breakpoint: 768,
                  settings: {
                    slidesToShow: 3,
                  }
                },
                {
                  breakpoint: 991,
                  settings: {
                    slidesToShow: 3,
                  }
                },
            ]
        });
    });
    
    // instafeed active
    var activeId = $("#instafeed"),
        myTemplate = '<div class="col"><div class="instagram-item"><a href="{{link}}" target="_blank" id="{{id}}"><img src="{{image}}" /></a><div class="instagram-hvr-content"><span class="totalcomments"><i class="fa fa-comment"></i>{{comments}}</span><span class="tottallikes"><i class="fa fa-heart"></i>{{likes}}</span></div></div></div>';

    if (activeId.length) {
        var userID = activeId.attr('data-userid'),
            accessTokenID = activeId.attr('data-accesstoken'),

            userFeed = new Instafeed({
                get: 'user',
                userId: userID,
                accessToken: accessTokenID,
                resolution: 'standard_resolution',
                template: myTemplate,
                sortBy: 'least-recent',
                limit: 10,
                links: false
            });
        userFeed.run();
    }
    
    
})(jQuery);

    // Cart functionality
    $(document).ready(function() {
        // Initialize cart count on page load
        updateCartCount();

        // Handle Add to Cart button clicks
        $(document).on('click', '.js-add-to-cart', function(e) {
            e.preventDefault();

            const button = $(this);
            const productId = button.data('product-id');
            const quantity = 1; // Default quantity, could be enhanced to get from input field

            if (!productId) {
                console.error('Product ID not found');
                return;
            }

            // Disable button and show loading state
            const originalHtml = button.html();
            button.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i>');

            // Add item to cart
            $.ajax({
                url: '/api/cart/items',
                method: 'POST',
                data: {
                    productId: productId,
                    quantity: quantity
                },
                success: function(cartData) {
                    // Update cart count after successful addition
                    updateCartCount();

                    // Show success feedback
                    button.html('<i class="fas fa-check"></i> Added!');
                    setTimeout(function() {
                        button.prop('disabled', false).html(originalHtml);
                    }, 2000);
                },
                error: function(xhr, status, error) {
                    console.error('Error adding item to cart:', error);

                    // Handle different error types
                    if (xhr.status === 400) {
                        // Bad request - possibly out of stock or invalid product
                        alert('Unable to add item to cart. Please try again.');
                    } else {
                        // Generic error
                        alert('An error occurred while adding the item to your cart.');
                    }

                    // Reset button
                    button.prop('disabled', false).html(originalHtml);
                }
            });
        });
    });

    // Function to update cart count in header
    function updateCartCount() {
        $.ajax({
            url: '/api/cart/count',
            method: 'GET',
            success: function(data) {
                const count = data.count || 0;
                // Update both counter elements
                $('#cart-count').text(count);
                $('.cart_quantity').text(count);
            },
            error: function(xhr, status, error) {
                console.error('Error updating cart count:', error);
                // Don't show error to user for count updates, just log it
            }
        });
    }

    function updateWishlistCount() {
        $.ajax({
            url: '/api/wishlist/count',
            method: 'GET',
            success: function(data) {
                const count = (data && typeof data.count === 'number') ? data.count : 0;
                // Update both header indicators
                $('#wishlist-count').text(count);
                $('.wishlist_quantity').text(count);
            },
            error: function() {
                // Silent failure for count polling
            }
        });
    }

    // Car Part Search Logic
    $(document).ready(function() {
        // Initialize counts on page load
        updateCartCount();
        updateWishlistCount();

        // Hydrate initial wishlist state for toggles
        $.ajax({
            url: '/api/wishlist/items',
            method: 'GET',
            success: function(data) {
                var ids = (data && data.items) ? new Set(data.items) : new Set();
                $(document).find('.js-wishlist-toggle').each(function(){
                    var $btn = $(this);
                    var id = $btn.data('product-id');
                    if (!id) return;
                    if (ids.has(id)) {
                        $btn.addClass('is-wishlisted').attr('data-wishlisted', 'true');
                        // If button has text content, change it; if icon-only, just title
                        if ($btn.text().trim().length > 0) {
                            $btn.text('Quitar de la lista de deseos');
                        }
                        $btn.attr('title', 'Remove from wishlist');
                    } else {
                        $btn.removeClass('is-wishlisted').attr('data-wishlisted', 'false');
                        if ($btn.text().trim().length > 0) {
                            $btn.text('+ Añadir a la lista de deseos');
                        }
                        $btn.attr('title', 'Add to wishlist');
                    }
                });
            }
        });

        // Unified wishlist toggle (add/remove)
        $(document).on('click', '.js-wishlist-toggle', function(e) {
            e.preventDefault();
            const $btn = $(this);
            const productId = $btn.data('product-id');
            if (!productId) return;
            const isWishlisted = ($btn.attr('data-wishlisted') === 'true') || $btn.hasClass('is-wishlisted');
            if (!isWishlisted) {
                $.ajax({
                    url: '/api/wishlist/items',
                    method: 'POST',
                    data: { productId: productId },
                    success: function(res) {
                        const count = (res && typeof res.count === 'number') ? res.count : undefined;
                        if (typeof count === 'number') {
                            $('#wishlist-count').text(count);
                            $('.wishlist_quantity').text(count);
                        } else {
                            updateWishlistCount();
                        }
                        $btn.addClass('is-wishlisted').attr('data-wishlisted','true');
                        if ($btn.text().trim().length > 0) {
                            $btn.text('Quitar de la lista de deseos');
                        }
                        $btn.attr('title', 'Remove from wishlist');
                    },
                    error: function(err) {
                        console.error('Failed to add to wishlist', err);
                    }
                });
            } else {
                $.ajax({
                    url: `/api/wishlist/items/${productId}`,
                    method: 'DELETE',
                    success: function(res) {
                        const count = (res && typeof res.count === 'number') ? res.count : undefined;
                        if (typeof count === 'number') {
                            $('#wishlist-count').text(count);
                            $('.wishlist_quantity').text(count);
                        } else {
                            updateWishlistCount();
                        }
                        $btn.removeClass('is-wishlisted').attr('data-wishlisted','false');
                        if ($btn.text().trim().length > 0) {
                            $btn.text('+ Añadir a la lista de deseos');
                        }
                        $btn.attr('title', 'Add to wishlist');
                    },
                    error: function(err) {
                        console.error('Failed to remove from wishlist', err);
                    }
                });
            }
        });

        // Remove-from-wishlist handler (wishlist page)
        $(document).on('click', '.js-remove-from-wishlist', function(e) {
            e.preventDefault();
            const $btn = $(this);
            const productId = $btn.data('product-id');
            if (!productId) return;
            $.ajax({
                url: `/api/wishlist/items/${productId}`,
                method: 'DELETE',
                success: function(res) {
                    const count = (res && typeof res.count === 'number') ? res.count : undefined;
                    if (typeof count === 'number') {
                        $('#wishlist-count').text(count);
                        $('.wishlist_quantity').text(count);
                    } else {
                        updateWishlistCount();
                    }
                    // Remove the row from the table
                    const $row = $btn.closest('tr');
                    $row.fadeOut(200, function(){ $(this).remove(); });
                },
                error: function(err) {
                    console.error('Failed to remove from wishlist', err);
                }
            });
        });
        const carMakeSelect = $('#car-make');
        const carModelSelect = $('#car-model');
        const carEngineSelect = $('#car-engine');
        const carYearSelect = $('#car-year');
        const carSearchForm = $('#car-search-form');

        // Function to populate dropdowns
        function populateDropdown(selectElement, data, defaultOptionText) {
            selectElement.empty();
            selectElement.append(`<option value="">${defaultOptionText}</option>`);
            data.forEach(item => {
                selectElement.append(`<option value="${item}">${item}</option>`);
            });
            selectElement.prop('disabled', false);
        }

        // Fetch Makes on page load
        $.ajax({
            url: '/api/search/vehicles/makes',
            method: 'GET',
            success: function(data) {
                populateDropdown(carMakeSelect, data, 'Elija una marca');
            },
            error: function(error) {
                console.error('Error fetching car makes:', error);
                carMakeSelect.prop('disabled', true);
            }
        });

        // Fetch Models when Make changes
        carMakeSelect.on('change', function() {
            const selectedMake = $(this).val();
            carModelSelect.prop('disabled', true).empty().append('<option value="">Elija un modelo</option>');
            carEngineSelect.prop('disabled', true).empty().append('<option value="">Elija un tipo de motor</option>');
            carYearSelect.prop('disabled', true).empty().append('<option value="">Elija un año</option>');

            if (selectedMake) {
                $.ajax({
                    url: `/api/search/vehicles/models?make=${selectedMake}`,
                    method: 'GET',
                    success: function(data) {
                        populateDropdown(carModelSelect, data, 'Elija un modelo');
                    },
                    error: function(error) {
                        console.error('Error fetching car models:', error);
                    }
                });
            }
        });

        // Fetch Engines when Model changes
        carModelSelect.on('change', function() {
            const selectedMake = carMakeSelect.val();
            const selectedModel = $(this).val();
            carEngineSelect.prop('disabled', true).empty().append('<option value="">Elija un tipo de motor</option>');
            carYearSelect.prop('disabled', true).empty().append('<option value="">Elija un año</option>');

            if (selectedMake && selectedModel) {
                $.ajax({
                    url: `/api/search/vehicles/engines?make=${selectedMake}&model=${selectedModel}`,
                    method: 'GET',
                    success: function(data) {
                        populateDropdown(carEngineSelect, data, 'Elija un tipo de motor');
                    },
                    error: function(error) {
                        console.error('Error fetching car engines:', error);
                    }
                });
            }
        });

        // Fetch Years when Engine changes
        carEngineSelect.on('change', function() {
            const selectedMake = carMakeSelect.val();
            const selectedModel = carModelSelect.val();
            const selectedEngine = $(this).val();
            carYearSelect.prop('disabled', true).empty().append('<option value="">Elija un año</option>');

            if (selectedMake && selectedModel && selectedEngine) {
                $.ajax({
                    url: `/api/search/vehicles/years?make=${selectedMake}&model=${selectedModel}&engine=${selectedEngine}`,
                    method: 'GET',
                    success: function(data) {
                        populateDropdown(carYearSelect, data, 'Elija un año');
                    },
                    error: function(error) {
                        console.error('Error fetching car years:', error);
                    }
                });
            }
        });

        // Handle form submission
        carSearchForm.on('submit', function(event) {
            event.preventDefault();

            const make = carMakeSelect.val();
            const model = carModelSelect.val();
            const engine = carEngineSelect.val();
            const year = carYearSelect.val();

            if (make && model && engine && year) {
                // Redirect to shop-right-sidebar-list.html with query parameters
                window.location.href = `/shop-right-sidebar-list.html?make=${make}&model=${model}&engine=${engine}&year=${year}`;
            } else {
                alert('Por favor, seleccione Marca, Modelo, Motor y Año para buscar.');
            }
        });
    });
