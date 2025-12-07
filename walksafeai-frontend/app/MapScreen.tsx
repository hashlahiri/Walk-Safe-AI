"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import maplibregl from "maplibre-gl";
import { MapPin, ShieldCheck } from "lucide-react";

const API = "/api/proxy";

// --- Types
type LatLngTuple = [number, number]; // [lat, lng]

type GeoPoint = {
  coordinates: [number, number]; // [lng, lat]
};

type Police = { id?: string; name: string; location: GeoPoint };
type Facility = { id?: string; name: string; type: string; location: GeoPoint };

const FALLBACK_CENTER: LatLngTuple = [18.5204, 73.8567]; // Pune

// OpenFreeMap MapLibre style (no API key)
const STYLE_URL = "https://tiles.openfreemap.org/styles/liberty";

export default function MapScreen() {
  const [pos, setPos] = useState<LatLngTuple | null>(null);
  const [police, setPolice] = useState<Police[]>([]);
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [radius, setRadius] = useState(1200);

  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<maplibregl.Map | null>(null);
  const markerRefs = useRef<maplibregl.Marker[]>([]);

  // Get location
  useEffect(() => {
    if (!navigator.geolocation) {
      setPos(FALLBACK_CENTER);
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (p) => setPos([p.coords.latitude, p.coords.longitude]),
      () => setPos(FALLBACK_CENTER),
      { enableHighAccuracy: true, timeout: 8000 }
    );
  }, []);

  // Fetch nearby data
  useEffect(() => {
    if (!pos) return;
    const [lat, lng] = pos;

    (async () => {
      try {
        const [policeRes, facRes] = await Promise.all([
          fetch(`${API}/nearby/police?lat=${lat}&lng=${lng}&radiusMeters=${radius}`),
          fetch(`${API}/nearby/facilities?lat=${lat}&lng=${lng}&radiusMeters=${radius}`),
        ]);

        setPolice(policeRes.ok ? await policeRes.json() : []);
        setFacilities(facRes.ok ? await facRes.json() : []);
      } catch {
        setPolice([]);
        setFacilities([]);
      }
    })();
  }, [pos, radius]);

  const center = useMemo<LatLngTuple>(() => pos ?? FALLBACK_CENTER, [pos]);

  // Init map once
  useEffect(() => {
    if (mapRef.current) return;
    if (!mapContainerRef.current) return;

    const [lat, lng] = center;

    const map = new maplibregl.Map({
      container: mapContainerRef.current,
      style: STYLE_URL,
      center: [lng, lat],
      zoom: 14,
    });

    map.addControl(new maplibregl.NavigationControl(), "bottom-right");

    mapRef.current = map;

    return () => {
      map.remove();
      mapRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Fly to user location when center changes
  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    const [lat, lng] = center;
    map.flyTo({ center: [lng, lat], zoom: 14, essential: true });
  }, [center]);

  // Update markers
  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    // clear previous
    markerRefs.current.forEach((m) => m.remove());
    markerRefs.current = [];

    // 1) My location marker
    if (pos) {
      const [lat, lng] = pos;
      const myMarker = new maplibregl.Marker({ color: "#2563eb" })
        .setLngLat([lng, lat])
        .setPopup(
          new maplibregl.Popup({ offset: 16 }).setHTML(
            `<div style="font-weight:600">You are here</div>`
          )
        )
        .addTo(map);

      markerRefs.current.push(myMarker);
    }

    // 2) Police markers
    police.forEach((p) => {
      const [lng, lat] = p.location.coordinates;
      const marker = new maplibregl.Marker({ color: "#111827" })
        .setLngLat([lng, lat])
        .setPopup(
          new maplibregl.Popup({ offset: 16 }).setHTML(
            `<div><b>Police</b><div>${escapeHtml(p.name)}</div></div>`
          )
        )
        .addTo(map);

      markerRefs.current.push(marker);
    });

    // 3) Safety facility markers
    facilities.forEach((f) => {
      const [lng, lat] = f.location.coordinates;
      const marker = new maplibregl.Marker({ color: "#16a34a" })
        .setLngLat([lng, lat])
        .setPopup(
          new maplibregl.Popup({ offset: 16 }).setHTML(
            `<div><b>Safety Facility</b><div>${escapeHtml(f.name)}</div><div style="font-size:11px;opacity:.7">${escapeHtml(
              f.type
            )}</div></div>`
          )
        )
        .addTo(map);

      markerRefs.current.push(marker);
    });
  }, [pos, police, facilities]);

  return (
    <div className="relative h-screen w-full">
      {/* Control panel */}
      <div className="absolute z-[10] p-3">
        <div className="max-w-xs space-y-2 rounded-lg bg-white p-3 shadow">
          <div className="flex items-center gap-2 text-base font-semibold">
            <ShieldCheck className="h-4 w-4 text-blue-600" />
            Walk Safe AI (Demo)
          </div>
          <div className="text-xs text-gray-600">
            Educational prototype. Scores depend on data completeness.
          </div>

          <label className="block text-xs font-medium text-gray-700">
            Nearby radius (meters)
            <input
              type="number"
              value={radius}
              min={200}
              max={5000}
              onChange={(e) => setRadius(Number(e.target.value))}
              className="mt-1 w-full rounded border border-gray-300 px-2 py-1 text-sm shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </label>

          <div className="text-xs text-gray-800">
            Found: {police.length} police, {facilities.length} safety facilities
          </div>

          <div className="pt-1 text-[10px] text-gray-500">
            Map data from OpenStreetMap via OpenFreeMap.
          </div>
        </div>
      </div>

      {/* Map */}
      <div ref={mapContainerRef} className="h-full w-full" />

      {/* Tiny legend (optional) */}
      <div className="absolute bottom-3 left-3 z-[10] rounded bg-white/90 px-2 py-1 text-[10px] text-gray-700 shadow">
        <span className="inline-flex items-center gap-1">
          <MapPin className="h-3 w-3 text-blue-600" />
          You
        </span>
        <span className="mx-2">•</span>
        <span>Police</span>
        <span className="mx-2">•</span>
        <span>Safety Facility</span>
      </div>
    </div>
  );
}

// Simple HTML escape for popup safety
function escapeHtml(str: string) {
  return str.replace(/[&<>"']/g, (m) => {
    switch (m) {
      case "&":
        return "&amp;";
      case "<":
        return "&lt;";
      case ">":
        return "&gt;";
      case '"':
        return "&quot;";
      case "'":
        return "&#039;";
      default:
        return m;
    }
  });
}
