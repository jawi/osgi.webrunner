function r(id) {
  $('a[rel=ajax][href!=\\'#\\']').each(function() {
    var h=this.href; 
    this.href='#';
    this.onclick=function() { 
      $.ajax({ 
        url:h, 
        success:function(data) {
          $('#'+id).replaceWith(data);
          r(id);
        }
      });
    }
  });
}
// for each element with class 'iframe', replace its contents with the url denoted in the 'src' attribute...
$(document).ready(function() { 
  $('.iframe').each(function() { 
    $(this).load( 
      $(this).attr('src'), 
      function() {
        // obtain the id of the first loaded child, and replace all URIs inside that element...
        var id=$(this).children(':first-child').attr('id');
        if (id) {
          r(id)
        }
      }
    )
  });
});
