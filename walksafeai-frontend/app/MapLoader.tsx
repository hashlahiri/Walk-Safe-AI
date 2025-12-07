"use client";
import dynamic from "next/dynamic";

const MapScreen = dynamic(() => import("./MapScreen"), {
  ssr: false,
  loading: () => (
    <div className="flex h-screen w-full items-center justify-center text-sm text-gray-600">
      Loading nearby map…
    </div>
  ),
});

export default function MapLoader() {
  return <MapScreen />;
}
