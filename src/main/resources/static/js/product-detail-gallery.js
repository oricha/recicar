/**
 * Gallery + lightbox: thumbs, swipe, keyboard (left/right/Escape).
 */
(function () {
    'use strict';

    function qs(root, sel) {
        return (root || document).querySelector(sel);
    }

    function initGallery(wrapper) {
        var mainImg = qs(wrapper, '#pd-main-img');
        var thumbs = wrapper.querySelectorAll('[data-pd-thumb-index]');
        var images = [];

        thumbs.forEach(function (btn) {
            var img = btn.querySelector('img');
            images.push(img ? img.getAttribute('src') : '');
            btn.addEventListener('click', function () {
                setActive(parseInt(btn.getAttribute('data-pd-thumb-index'), 10));
            });
        });

        if (images.length === 0 && mainImg && mainImg.getAttribute('src')) {
            images.push(mainImg.getAttribute('src'));
        }

        function setActive(idx) {
            if (Number.isNaN(idx) || idx < 0 || idx >= images.length) return;
            if (mainImg) {
                mainImg.setAttribute('src', images[idx]);
                mainImg.setAttribute('data-pd-current-index', String(idx));
            }
            thumbs.forEach(function (b) {
                b.classList.toggle('is-active', parseInt(b.getAttribute('data-pd-thumb-index'), 10) === idx);
            });
        }

        var box = qs(document, '#pd-lightbox');
        var boxImg = box ? qs(box, '#pd-lightbox-img') : null;

        function currentIndex() {
            if (!mainImg) return 0;
            return parseInt(mainImg.getAttribute('data-pd-current-index') || '0', 10);
        }

        function openLb(fromIdx) {
            if (!box || !boxImg || !images.length) return;
            var i = fromIdx;
            if (Number.isNaN(i) || i < 0 || i >= images.length) i = 0;
            boxImg.setAttribute('src', images[i]);
            boxImg.setAttribute('data-lb-index', String(i));
            box.classList.add('is-open');
            box.removeAttribute('hidden');
            box.setAttribute('aria-hidden', 'false');
            document.body.style.overflow = 'hidden';
            var closeBtn = qs(box, '.pd-lightbox-close');
            if (closeBtn) closeBtn.focus();
        }

        function closeLb() {
            if (!box || !boxImg) return;
            box.classList.remove('is-open');
            box.setAttribute('hidden', 'hidden');
            box.setAttribute('aria-hidden', 'true');
            document.body.style.overflow = '';
            boxImg.removeAttribute('src');
        }

        function stepLb(delta) {
            if (!images.length || !boxImg) return;
            var i = parseInt(boxImg.getAttribute('data-lb-index') || String(currentIndex()), 10);
            if (Number.isNaN(i)) i = 0;
            i = i + delta;
            if (i < 0) i = images.length - 1;
            if (i >= images.length) i = 0;
            boxImg.setAttribute('src', images[i]);
            boxImg.setAttribute('data-lb-index', String(i));
            setActive(i);
        }

        var openBtn = qs(wrapper, '[data-pd-open-lightbox]');
        if (openBtn) {
            openBtn.addEventListener('click', function () {
                openLb(currentIndex());
            });
        }

        if (box) {
            var closeEl = qs(box, '.pd-lightbox-close');
            if (closeEl) closeEl.addEventListener('click', closeLb);
            var prev = qs(box, '[data-pd-lb-prev]');
            if (prev) prev.addEventListener('click', function () {
                stepLb(-1);
            });
            var next = qs(box, '[data-pd-lb-next]');
            if (next) next.addEventListener('click', function () {
                stepLb(1);
            });
            box.addEventListener('click', function (e) {
                if (e.target === box) closeLb();
            });
        }

        document.addEventListener('keydown', function (e) {
            if (!box || !box.classList.contains('is-open')) return;
            if (e.key === 'Escape') closeLb();
            else if (e.key === 'ArrowLeft') stepLb(-1);
            else if (e.key === 'ArrowRight') stepLb(1);
        });

        var touchStartX = null;
        if (wrapper) {
            wrapper.addEventListener('touchstart', function (e) {
                touchStartX = e.changedTouches[0].screenX;
            }, { passive: true });
            wrapper.addEventListener('touchend', function (e) {
                if (touchStartX == null || !mainImg || images.length < 2) return;
                var dx = e.changedTouches[0].screenX - touchStartX;
                touchStartX = null;
                if (Math.abs(dx) < 40) return;
                var i = currentIndex();
                i = dx > 0 ? i - 1 : i + 1;
                if (i < 0) i = images.length - 1;
                if (i >= images.length) i = 0;
                setActive(i);
            }, { passive: true });
        }

        /** Sync qty mobile <-> desktop for add-to-cart */
        var qDesktop = qs(document, '#quantity-input-desktop');
        var qMobile = qs(document, '#quantity-input-mobile');
        if (qDesktop && qMobile) {
            qMobile.addEventListener('input', function () {
                qDesktop.value = qMobile.value;
            });
            qDesktop.addEventListener('input', function () {
                qMobile.value = qDesktop.value;
            });
        }

        /** Mobile toolbar padding */
        window.addEventListener('resize', function () {
            if (window.matchMedia('(max-width: 991px)').matches) {
                document.body.classList.add('pd-mobile-bar-active');
            } else {
                document.body.classList.remove('pd-mobile-bar-active');
            }
        });
        if (window.matchMedia('(max-width: 991px)').matches) {
            document.body.classList.add('pd-mobile-bar-active');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('[data-pd-gallery]').forEach(initGallery);

        var specBtn = qs(document, '#pd-spec-toggle');
        if (specBtn) {
            specBtn.addEventListener('click', function () {
                var rows = document.querySelectorAll('.pd-spec-extra-row');
                if (!rows.length) return;
                var hidden = rows[0].classList.contains('d-none');
                rows.forEach(function (r) {
                    r.classList.toggle('d-none', !hidden);
                });
                specBtn.textContent = hidden ? 'Ocultar' : 'Ver más especificaciones';
            });
        }

        var fitInput = qs(document, '#pd-vehicle-fit-filter');
        if (fitInput) {
            fitInput.addEventListener('input', function () {
                var q = fitInput.value.toLowerCase().trim();
                document.querySelectorAll('[data-fit-row][data-fit-text]').forEach(function (row) {
                    var t = row.getAttribute('data-fit-text').toLowerCase();
                    row.classList.toggle('d-none', q.length > 0 && !t.includes(q));
                });
            });
        }

        var donorBtn = qs(document, '#pd-donor-toggle');
        var donorPanel = qs(document, '#pd-donor-panel');
        if (donorBtn && donorPanel) {
            donorBtn.addEventListener('click', function () {
                var expanded = donorBtn.getAttribute('aria-expanded') === 'true';
                donorBtn.setAttribute('aria-expanded', String(!expanded));
                donorPanel.hidden = expanded;
            });
        }

        var mobileAdd = qs(document, '#pd-mobile-add-submit');
        var form = qs(document, '#add-to-cart-form');
        if (mobileAdd && form) {
            mobileAdd.addEventListener('click', function () {
                form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
            });
        }
    });
}());
