import 'htmx.org';
import 'css/app.css';


/**
 * Register an event at the document for the specified selector,
 * so events are still catched after DOM changes.
 */
function handleEvent(eventType, selector, handler) {
  document.addEventListener(eventType, function(event) {
    if (event.target.matches(selector + ', ' + selector + ' *')) {
      handler.apply(event.target.closest(selector), arguments);
    }
  });
}

handleEvent('click', 'body', function(event) {
  // close any open dropdown
  const $clickedDropdown = event.target.closest('.js-dropdown');
  const $dropdowns = document.querySelectorAll('.js-dropdown');
  $dropdowns.forEach(($dropdown) => {
    if ($clickedDropdown !== $dropdown && $dropdown.getAttribute('data-dropdown-keepopen') !== 'true') {
      $dropdown.ariaExpanded = 'false';
      $dropdown.nextElementSibling.classList.add('hidden');
    }
  });
  // toggle selected if applicable
  if ($clickedDropdown) {
    $clickedDropdown.ariaExpanded = '' + ($clickedDropdown.ariaExpanded !== 'true');
    $clickedDropdown.nextElementSibling.classList.toggle('hidden');
    event.preventDefault();
  }
});
