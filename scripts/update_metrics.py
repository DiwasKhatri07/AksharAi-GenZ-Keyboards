#!/usr/bin/env python3
"""Refresh aggregate public repository/release metrics; never collect user identities."""
from __future__ import annotations

import json
import os
import sys
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path

OWNER = "DiwasKhatri07"
REPO = "AksharAi-GenZ-Keyboards"
API = f"https://api.github.com/repos/{OWNER}/{REPO}"
OUT = Path(__file__).resolve().parents[1] / "metrics" / "latest.json"

def get_json(url: str, token: str | None = None):
    headers = {
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
        "User-Agent": "akshar-ai-repository-metrics",
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(url, headers=headers)
    with urllib.request.urlopen(request, timeout=20) as response:
        return json.load(response)

def main() -> int:
    try:
        repo = get_json(API)
        releases = get_json(API + "/releases?per_page=1")
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as exc:
        print(f"Could not fetch public repository metrics: {exc}", file=sys.stderr)
        return 1

    release = releases[0] if releases else None
    asset_downloads = sum(a.get("download_count", 0) for a in release.get("assets", [])) if release else 0
    new = {
        "schema_version": 1,
        "repository": {
            "name": repo.get("full_name"),
            "url": repo.get("html_url"),
            "description": repo.get("description"),
            "default_branch": repo.get("default_branch"),
            "stars": repo.get("stargazers_count", 0),
            "forks": repo.get("forks_count", 0),
            "open_items_including_pull_requests": repo.get("open_issues_count", 0),
            "watchers": repo.get("subscribers_count", 0),
            "topics": repo.get("topics", []),
        },
        "latest_release": {
            "tag": release.get("tag_name") if release else None,
            "published_at": release.get("published_at") if release else None,
            "asset_downloads": asset_downloads,
            "assets": [
                {"name": a.get("name"), "downloads": a.get("download_count", 0)}
                for a in (release.get("assets", []) if release else [])
            ],
        },
        "traffic_14_days": None,
        "notes": [
            "Aggregate repository/release counts only; no visitor identities or GitHub search queries are available here.",
            "GitHub traffic is optional and requires a TRAFFIC_API_TOKEN secret with Administration: read permission.",
            "GitHub API traffic graphs cover a rolling 14-day window and may be delayed.",
        ],
    }

    token = os.environ.get("TRAFFIC_API_TOKEN", "").strip()
    if token:
        try:
            views = get_json(API + "/traffic/views", token)
            clones = get_json(API + "/traffic/clones", token)
            referrers = get_json(API + "/traffic/popular/referrers", token)
            new["traffic_14_days"] = {
                "views": views.get("count", 0),
                "unique_views": views.get("uniques", 0),
                "clones": clones.get("count", 0),
                "unique_clones": clones.get("uniques", 0),
                "top_referrers": [
                    {"referrer": item.get("referrer"), "views": item.get("count", 0)}
                    for item in referrers
                ],
            }
        except (urllib.error.HTTPError, urllib.error.URLError, TimeoutError, json.JSONDecodeError) as exc:
            # Do not fail public metrics if optional traffic permission is unavailable.
            print(f"Optional traffic metrics unavailable; skipping this sample: {exc}")

    previous = {}
    try:
        previous = json.loads(OUT.read_text(encoding="utf-8"))
    except (FileNotFoundError, json.JSONDecodeError):
        pass

    old_values = {k: v for k, v in previous.items() if k != "last_updated_utc"}
    if old_values == new:
        print("Metrics unchanged; no file update needed.")
        return 0

    new["last_updated_utc"] = datetime.now(timezone.utc).replace(microsecond=0).isoformat()
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(new, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Updated {OUT.relative_to(Path(__file__).resolve().parents[1])}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
