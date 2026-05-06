/**
 * Advanced search: live API preview (before first submit), brand→models cascade,
 * suggestions on #adv-q, save search for authenticated users.
 */
(function () {
  'use strict';

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

  function buildApiParams() {
    var f = document.getElementById('adv-search-form');
    if (!f) {
      return '';
    }
    var fd = new FormData(f);
    fd.delete('submitted');
    var u = new URLSearchParams();
    fd.forEach(function (v, k) {
      if (v !== '' && v != null) {
        u.append(k, v);
      }
    });
    return u.toString();
  }

  function escapeHtml(s) {
    if (!s) {
      return '';
    }
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/"/g, '&quot;');
  }

  function renderPreview(json) {
    var el = document.getElementById('adv-live-results');
    if (!el || !json) {
      return;
    }
    if (!json.content || !json.content.length) {
      el.innerHTML = '<p class="text-muted small mb-0">Sin resultados en vista previa. Ajusta filtros o pulsa Buscar.</p>';
      return;
    }
    var html = '<div class="row">';
    json.content.forEach(function (it) {
      html += '<div class="col-md-4 mb-2">';
      html += '<div class="border rounded p-2 small h-100 bg-white">';
      html += '<strong>' + escapeHtml(it.name) + '</strong><br/>';
      html += '<span class="text-muted">' + escapeHtml(it.partNumber || '') + '</span><br/>';
      html += '<span class="text-primary">€' + (it.price != null ? it.price : '—') + '</span>';
      html += '</div></div>';
    });
    html += '</div>';
    el.innerHTML = html;
  }

  var liveFetch = debounce(function () {
    var el = document.getElementById('adv-live-results');
    if (!el) {
      return;
    }
    var qs = buildApiParams();
    if (!qs) {
      el.innerHTML = '';
      return;
    }
    fetch('/api/v1/search/advanced?' + qs + '&page=0&size=12')
        .then(function (r) {
          return r.json();
        })
        .then(renderPreview)
        .catch(function () {
          /* no-op */
        });
  }, 450);

  function bindLive() {
    var f = document.getElementById('adv-search-form');
    if (!f) {
      return;
    }
    f.addEventListener(
        'input',
        function (e) {
          if (e.target.type === 'submit') {
            return;
          }
          liveFetch();
        },
        true
    );
    f.addEventListener(
        'change',
        function (e) {
          if (e.target.type === 'submit') {
            return;
          }
          liveFetch();
        },
        true
    );
  }

  function bindModelsFromBrand() {
    var sel = document.getElementById('adv-brand');
    var mdl = document.getElementById('adv-vehicle-model');
    if (!sel || !mdl) {
      return;
    }
    function load() {
      var opt = sel.options[sel.selectedIndex];
      var slug = opt && opt.getAttribute('data-slug');
      var current = (mdl.getAttribute('data-initial') || '').trim();
      mdl.removeAttribute('data-initial');
      mdl.innerHTML = '<option value="">—</option>';
      if (!slug) {
        return;
      }
      fetch('/api/v1/brands/' + encodeURIComponent(slug) + '/models')
          .then(function (r) {
            return r.json();
          })
          .then(function (arr) {
            if (!Array.isArray(arr)) {
              return;
            }
            arr.forEach(function (x) {
              var name = x.modelName || '';
              var o = document.createElement('option');
              o.value = name;
              o.textContent = name;
              if (current && current === name) {
                o.selected = true;
              }
              mdl.appendChild(o);
            });
          })
          .catch(function () {
            /* no-op */
          });
    }
    sel.addEventListener('change', load);
    if (sel.selectedIndex > 0) {
      mdl.setAttribute('data-initial', mdl.value || '');
      load();
    }
  }

  function bindSuggestions() {
    var input = document.getElementById('adv-q');
    var panel = document.getElementById('adv-q-suggestions');
    if (!input || !panel) {
      return;
    }
    var run = debounce(function () {
      var q = (input.value || '').trim();
      if (q.length < 2) {
        panel.innerHTML = '';
        panel.hidden = true;
        return;
      }
      fetch('/api/v1/search/suggestions?q=' + encodeURIComponent(q))
          .then(function (r) {
            return r.json();
          })
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
                panel.hidden = true;
                liveFetch();
              });
              panel.appendChild(b);
            });
            panel.hidden = false;
          })
          .catch(function () {
            panel.hidden = true;
          });
    }, 200);
    input.addEventListener('input', run);
    document.addEventListener('click', function (e) {
      if (panel.contains(e.target) || e.target === input) {
        return;
      }
      panel.hidden = true;
    });
  }

  function bindSave() {
    var btn = document.getElementById('btn-save-search');
    if (!btn) {
      return;
    }
    btn.addEventListener('click', function () {
      var f = document.getElementById('adv-search-form');
      if (!f) {
        return;
      }
      var fd = new FormData(f);
      var filters = {};
      fd.forEach(function (v, k) {
        if (k !== 'submitted') {
          filters[k] = v;
        }
      });
      var qEl = document.getElementById('adv-q');
      var body = {
        query: qEl ? qEl.value || '' : '',
        filtersJson: JSON.stringify(filters)
      };
      fetch('/api/v1/user/saved-searches', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        credentials: 'same-origin',
        body: JSON.stringify(body)
      }).then(function (r) {
        if (r.status === 401) {
          window.alert('Inicia sesión para guardar búsquedas.');
          return;
        }
        if (r.ok) {
          window.alert('Búsqueda guardada.');
        } else {
          window.alert('No se pudo guardar.');
        }
      });
    });
  }

  function hasFilterParams() {
    var f = document.getElementById('adv-search-form');
    if (!f) {
      return false;
    }
    var fd = new FormData(f);
    var n = 0;
    fd.forEach(function (v, k) {
      if (k === 'submitted') {
        return;
      }
      if (v !== '' && v != null) {
        n++;
      }
    });
    return n > 0;
  }

  document.addEventListener('DOMContentLoaded', function () {
    bindLive();
    bindModelsFromBrand();
    bindSuggestions();
    bindSave();
    if (document.getElementById('adv-live-results') && hasFilterParams()) {
      liveFetch();
    }
  });
})();
