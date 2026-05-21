// ============================================================
//  LughatPro Service Worker
//  Strategy:
//    - App shell (HTML/CSS/JS) → Cache First
//    - API calls                → Network First (fresh data)
//    - Offline fallback page    → shown when network fails
// ============================================================

const CACHE_NAME     = 'lughatpro-v1';
const API_CACHE_NAME = 'lughatpro-api-v1';

// Files to cache immediately on install (app shell)
const STATIC_ASSETS = [
  '/index.html',
  '/admin.html',
  '/member.html',
  '/manifest.json',
  '/offline.html',
  '/icons/icon-192.png',
  '/icons/icon-512.png'
];

// ── INSTALL: cache app shell ──────────────────────────────────
self.addEventListener('install', event => {
  console.log('[SW] Installing LughatPro service worker...');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('[SW] Caching app shell');
        // Use addAll but don't fail if one asset is missing
        return Promise.allSettled(
          STATIC_ASSETS.map(url => cache.add(url).catch(e => console.warn('[SW] Could not cache:', url, e)))
        );
      })
      .then(() => self.skipWaiting())
  );
});

// ── ACTIVATE: clean old caches ────────────────────────────────
self.addEventListener('activate', event => {
  console.log('[SW] Activating...');
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(
        keys
          .filter(k => k !== CACHE_NAME && k !== API_CACHE_NAME)
          .map(k => { console.log('[SW] Deleting old cache:', k); return caches.delete(k); })
      )
    ).then(() => self.clients.claim())
  );
});

// ── FETCH: routing strategy ───────────────────────────────────
self.addEventListener('fetch', event => {
  const url = new URL(event.request.url);

  // Skip non-GET requests
  if (event.request.method !== 'GET') return;

  // Skip chrome-extension, browser internals
  if (!url.protocol.startsWith('http')) return;

  // ── API calls → Network First, fall back to cache ──────────
  if (url.href.includes('/api/')) {
    event.respondWith(networkFirstWithCache(event.request));
    return;
  }

  // ── CDN resources (Tailwind, Google Fonts) → Cache First ───
  if (url.hostname.includes('cdn.tailwindcss.com') ||
      url.hostname.includes('fonts.googleapis.com') ||
      url.hostname.includes('fonts.gstatic.com')) {
    event.respondWith(cacheFirstWithNetwork(event.request));
    return;
  }

  // ── App shell (HTML pages, icons, manifest) → Cache First ──
  if (url.origin === self.location.origin) {
    event.respondWith(cacheFirstWithNetwork(event.request));
    return;
  }
});

// ── Network First (for API) ───────────────────────────────────
async function networkFirstWithCache(request) {
  try {
    const response = await fetch(request);
    // Cache successful GET API responses
    if (response.ok) {
      const cache = await caches.open(API_CACHE_NAME);
      cache.put(request, response.clone());
    }
    return response;
  } catch (err) {
    // Offline — try cache
    const cached = await caches.match(request);
    if (cached) {
      console.log('[SW] Serving API from cache (offline):', request.url);
      return cached;
    }
    // Return empty JSON array so the app doesn't crash
    return new Response(JSON.stringify({ content: [], totalItems: 0, totalPages: 0, message: 'You are offline' }),
      { headers: { 'Content-Type': 'application/json' } });
  }
}

// ── Cache First (for static assets) ──────────────────────────
async function cacheFirstWithNetwork(request) {
  const cached = await caches.match(request);
  if (cached) return cached;
  try {
    const response = await fetch(request);
    if (response.ok) {
      const cache = await caches.open(CACHE_NAME);
      cache.put(request, response.clone());
    }
    return response;
  } catch (err) {
    // If HTML page requested while offline → show offline page
    if (request.headers.get('accept')?.includes('text/html')) {
      const offline = await caches.match('/offline.html');
      if (offline) return offline;
    }
    return new Response('Offline', { status: 503 });
  }
}

// ── Background sync for offline actions ──────────────────────
self.addEventListener('message', event => {
  if (event.data === 'SKIP_WAITING') self.skipWaiting();
});