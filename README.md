# Walk Safe AI

> “Can I walk from here to there right now – and how safe is it likely to be?”

Walk Safe AI is a learning/portfolio project exploring how to score walking AI on **safety**, not just distance. Given an origin and destination, the app aims to compare a shortest route against a safer alternative using open data (lighting, police facilities, incident hotspots) plus simple heuristics.

---

## What This Repo Contains
- Project vision, architecture notes, and implementation plan.
- No runnable backend/frontend code has been committed yet; this README is the source of truth for how the project should be assembled.

---

## Goals & Features (planned)
- Compare **Shortest** vs **Safer** routes with an overall safety score.
- Show factor breakdowns (lighting density, proximity to police/public spaces, incident density, crowd proxy).
- Mobile-friendly map UI to pick start/end points and view routes.
- Transparent heuristics: simple scoring you can read and tweak.
- Clearly credited open datasets; no claim of production-grade safety guarantees.

---

## Tech Stack (planned)

**Backend**
- Language: Java 21
- Framework: Spring Boot (targeting 3.x)
- Database: **MongoDB** with geospatial indexes (replaces earlier PostgreSQL/PostGIS plan).
- Build: Maven
- Responsibilities: REST APIs for route safety scoring, ingesting open geospatial datasets, running simple heuristics, and brokering calls to an external routing API (e.g., OpenRouteService/OSRM).

**Frontend**
- Framework: Next.js (React)
- Styling: Tailwind CSS
- Map UI: React Leaflet or MapLibre over OpenStreetMap
- Responsibilities: map-based origin/destination selection, rendering alternate routes, surfacing scores and factor breakdowns.

---

## High-Level Architecture
```text
[Browser: Next.js + Tailwind] -- HTTPS --> [Spring Boot API]
                                            |
                                            +--> Routing API (OpenRouteService / OSRM)
                                            |
                                            +--> MongoDB (geospatial collections)
                                                 - streetLights
                                                 - policeStations / publicSafetyFacilities
                                                 - incidents (sample/open data)
```

**Safety scoring heuristic (illustrative)**
- Lighting score: density and proximity of street lights along/near the path.
- Incident penalty: density of past incidents inside a buffer around the path.
- Police proximity: distance to nearby police/public safety facilities.
- Crowd proxy: favors main/commercial roads over alleys when possible.
- Time-of-day adjustment: penalizes poorly lit night routes.

---

## Data Model (MongoDB)
- `streetLights`: `{ location: { type: "Point", coordinates: [lng, lat] }, source, lastSeenAt }` with 2dsphere index.
- `policeStations`: `{ name, location: { type: "Point", coordinates }, source }` with 2dsphere index.
- `incidents`: `{ category, occurredAt, location: { type: "Point", coordinates }, severity?, source }` with 2dsphere index and optional time filters.
- `routes` (optional cache): `{ routeId, geometry, shortestScore, saferScore, factors }`.

> MongoDB geospatial note: use `2dsphere` indexes on all location fields; route geometry can be stored as `LineString` for spatial queries (`$near`, `$geoWithin`, `$geoIntersects`).

---

## Project Structure (planned)
```text
walk-safe-AI/
├─ backend/
│  ├─ src/main/java/com/hashlahiri/walksafeai/
│  │  ├─ controller/    # REST endpoints
│  │  ├─ service/       # safety scoring, orchestration
│  │  ├─ repository/    # Mongo repositories + geospatial queries
│  │  ├─ model/         # DTOs, documents
│  │  └─ config/        # Mongo + routing API config
│  └─ pom.xml
│
└─ frontend/
   ├─ app/ or src/      # Next.js routes/app router
   ├─ components/       # map view, route cards, score badges
   ├─ lib/              # API client/hooks
   ├─ styles/           # Tailwind setup
   ├─ package.json
   └─ tailwind.config.js
```

---

## Getting Started (when code is added)

### Prerequisites
- Java 21
- Node 20+
- MongoDB 7+ running locally (or connection string to a remote cluster)
- Routing API key (e.g., OpenRouteService) if you want live routing

### MongoDB setup
```bash
brew services start mongodb-community      # or use Docker: docker run -p 27017:27017 mongo:7
mongo <<'EOF'
use walk_safe_routes
db.streetLights.createIndex({ location: "2dsphere" })
db.policeStations.createIndex({ location: "2dsphere" })
db.incidents.createIndex({ location: "2dsphere" })
EOF
```

### Backend (planned)
```bash
cd backend
cp .env.example .env        # create and fill values when added
mvn spring-boot:run
```

Example backend env vars to plan for:
```
MONGODB_URI=mongodb://localhost:27017/walk_safe_ai
ROUTING_API_BASE=https://api.openrouteservice.org
ROUTING_API_KEY=your-key
```

### Frontend (planned)
```bash
cd frontend
npm install
cp .env.local.example .env.local   # create when added
npm run dev
```

Example frontend env vars to plan for:
```
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## Data Sources (planned)
- Base map/roads: OpenStreetMap
- Routing: OpenRouteService, OSRM, or similar open-source engine
- Lighting: City-level open data where available (or sample datasets)
- Police/public safety facilities: OpenStreetMap POIs or municipal open data
- Incidents/harassment hotspots: sample/synthetic datasets for demos; future: civic complaint portals or NGO datasets (if legally allowed)

All real datasets used will be credited here once integrated.

---

## Roadmap
- Bootstrap backend (Spring Boot + MongoDB + geospatial indexes)
- Add routing API integration and request/response DTOs
- Implement baseline safety scoring heuristic with factor breakdowns
- Seed Mongo with sample lighting/police/incident data for one city
- Build Next.js + Tailwind map UI with shortest vs safer routes
- Add user feedback pins (“I felt unsafe here”) and moderation flow
- Performance pass (caching routes/results) and accessibility polish

---

## Notes & Limitations
- Educational/demo intent only; no real-time guarantees about safety.
- Scoring is heuristic and depends on the completeness/quality of open data.
- Current repo is documentation-first; code will follow the structure above.
