(function () {
  'use strict';

  function postJson(url, data) {
    return fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
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
    var run = debounce(function () {
      var q = (input.value || '').trim();
      if (q.length < 2) {
        panel.innerHTML = '';
        panel.hidden = true;
        return;
      }
      fetch('/api/v1/search/suggestions?q=' + encodeURIComponent(q))
        .then(function (r) { return r.json(); })
        .then(function (arr) {
          if (!Array.isArray(arr) || !arr.length) {
            panel.innerHTML = '';
            panel.hidden = true;
            return;
          }
          panel.innerHTML = '';
          arr.forEach(function (s) {
            var b = document.createElement('button');
            b.type = 'button';
            b.textContent = s;
            b.addEventListener('click', function () {
              input.value = s;
              panel.innerHTML = '';
              panel.hidden = true;
              input.form && input.form.submit();
            });
            panel.appendChild(b);
          });
          panel.hidden = false;
        })
        .catch(function () {
          panel.innerHTML = '';
          panel.hidden = true;
        });
    }, 200);
    input.addEventListener('input', run);
    input.addEventListener('focus', run);
    document.addEventListener('click', function (e) {
      if (!panel.contains(e.target) && e.target !== input) {
        panel.hidden = true;
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    bindHamburger();
    bindMarketUx();
    bindSearchSuggestions();
  });
})();
