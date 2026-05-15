(function () {
  'use strict';

  var REGION_STORAGE = 'mkt_region';
  var VAT_STORAGE = 'mkt_vat';

  function postJson(url, data) {
    return fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
  }

  function mirrorPreferencesToLocalStorage(regionCode, includeVat) {
    try {
      if (regionCode) {
        localStorage.setItem(REGION_STORAGE, regionCode);
      }
      localStorage.setItem(VAT_STORAGE, includeVat ? '1' : '0');
    } catch (e) {
      /* private mode or quota */
    }
  }

  function applyMarketUxFromDom() {
    var regionEl = document.getElementById('mkt_region_select');
    var vatEl = document.getElementById('mkt_vat_toggle');
    if (!regionEl) return;
    var payload = {
      region: regionEl.value,
      includeVat: vatEl ? vatEl.checked : true
    };
    postJson('/api/v1/client-preferences', payload).then(function (r) {
      if (r.ok) {
        mirrorPreferencesToLocalStorage(regionEl.value, payload.includeVat);
        window.location.reload();
      }
    }).catch(function () { /* no-op */ });
  }

  function bindMarketUx() {
    var regionEl = document.getElementById('mkt_region_select');
    var vatEl = document.getElementById('mkt_vat_toggle');
    if (regionEl) {
      regionEl.addEventListener('change', applyMarketUxFromDom);
    }
    if (vatEl) {
      vatEl.addEventListener('change', applyMarketUxFromDom);
    }
  }

  function bindHamburger() {
    var btn = document.getElementById('hamburgerBtn');
    var root = document.getElementById('mainNavRoot');
    if (!btn || !root) return;
    var list = root.querySelector('ul');
    if (!list) return;
    btn.addEventListener('click', function () {
      var open = list.classList.toggle('mnav-open');
      btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    });
  }

  function debounce(fn, ms) {
    var t;
    return function () {
      var args = arguments;
      clearTimeout(t);
      t = setTimeout(function () {
        fn.apply(null, args);
      }, ms);
    };
  }

  function bindSearchSuggestions() {
    var input = document.getElementById('header-search-input');
    var panel = document.getElementById('header-search-suggestions');
    if (!input || !panel) return;

    input.setAttribute('role', 'combobox');
    input.setAttribute('aria-autocomplete', 'list');
    input.setAttribute('aria-controls', 'header-search-suggestions');
    input.setAttribute('aria-expanded', 'false');
    panel.setAttribute('role', 'listbox');
    panel.setAttribute('aria-label', 'Sugerencias de búsqueda');

    function closePanel() {
      panel.innerHTML = '';
      panel.hidden = true;
      input.setAttribute('aria-expanded', 'false');
    }

    function openWithButtons(buttons) {
      panel.innerHTML = '';
      buttons.forEach(function (b) {
        panel.appendChild(b);
      });
      panel.hidden = !buttons.length;
      input.setAttribute('aria-expanded', buttons.length ? 'true' : 'false');
    }

    var run = debounce(function () {
      var q = (input.value || '').trim();
      if (q.length < 2) {
        closePanel();
        return;
      }
      fetch('/api/v1/search/suggestions?q=' + encodeURIComponent(q))
        .then(function (r) { return r.json(); })
        .then(function (arr) {
          if (!Array.isArray(arr) || !arr.length) {
            closePanel();
            return;
          }
          var buttons = [];
          arr.forEach(function (s) {
            var b = document.createElement('button');
            b.type = 'button';
            b.setAttribute('role', 'option');
            b.textContent = s;
            b.addEventListener('click', function () {
              input.value = s;
              closePanel();
              input.form && input.form.submit();
            });
            buttons.push(b);
          });
          openWithButtons(buttons);
        })
        .catch(function () {
          closePanel();
        });
    }, 200);

    input.addEventListener('input', run);
    input.addEventListener('focus', run);
    input.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') {
        closePanel();
      }
    });
    document.addEventListener('click', function (e) {
      if (!panel.contains(e.target) && e.target !== input) {
        closePanel();
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    bindHamburger();
    bindMarketUx();
    bindSearchSuggestions();
  });
})();
