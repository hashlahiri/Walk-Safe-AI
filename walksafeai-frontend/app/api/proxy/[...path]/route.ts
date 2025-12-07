import type { NextRequest } from "next/server";

const API_ORIGIN = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:9000";

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  const { path } = await params;
  const targetPath = path.join("/");
  const targetUrl = `${API_ORIGIN}/${targetPath}${req.nextUrl.search}`;

  const upstream = await fetch(targetUrl, {
    headers: {
      // Forward only basic headers to avoid CORS/hop-by-hop issues
      "content-type": req.headers.get("content-type") ?? undefined,
      accept: req.headers.get("accept") ?? undefined,
    },
    cache: "no-store",
  });

  const body = await upstream.text();

  // Copy safe headers from upstream
  const headers = new Headers();
  const contentType = upstream.headers.get("content-type");
  if (contentType) headers.set("content-type", contentType);

  return new Response(body, {
    status: upstream.status,
    statusText: upstream.statusText,
    headers,
  });
}
