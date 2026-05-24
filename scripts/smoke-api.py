#!/usr/bin/env python3
"""Post-deploy smoke checks for the public portfolio API."""

from __future__ import annotations

import json
import sys
import time
import urllib.error
import urllib.request

BASE_URL = "https://api.caseycapozzi.com"
MAX_ATTEMPTS = 12
RETRY_SECONDS = 15
FISHING_TIMEOUT_SECONDS = 45


def fetch_json(path: str, timeout: int = 20) -> tuple[int, dict | list | None]:
    url = f"{BASE_URL}{path}"
    request = urllib.request.Request(url, headers={"Accept": "application/json"})
    with urllib.request.urlopen(request, timeout=timeout) as response:
        body = response.read().decode("utf-8")
        return response.status, json.loads(body) if body else None


def check_health() -> None:
    status, body = fetch_json("/health")
    if status != 200 or not isinstance(body, dict) or body.get("status") != "UP":
        raise AssertionError(f"/health unexpected response: status={status} body={body}")


def check_status() -> None:
    status, body = fetch_json("/api/v1/status")
    if status != 200 or not isinstance(body, dict) or "status" not in body:
        raise AssertionError(f"/api/v1/status unexpected response: status={status} body={body}")


def check_fishing(path: str) -> None:
    status, body = fetch_json(path, timeout=FISHING_TIMEOUT_SECONDS)
    if status != 200 or not isinstance(body, dict):
        raise AssertionError(f"{path} unexpected response: status={status} body={body}")
    score = body.get("biteScore")
    if not isinstance(score, int) or score < 0 or score > 100:
        raise AssertionError(f"{path} invalid biteScore: {score!r}")


def run_checks() -> None:
    check_health()
    check_status()
    check_fishing("/api/fishing/brule/status")
    check_fishing("/api/fishing/colfax/status")


def main() -> int:
    print(f"Smoke testing {BASE_URL} ...")
    last_error: Exception | None = None

    for attempt in range(1, MAX_ATTEMPTS + 1):
        try:
            run_checks()
            print("All API smoke checks passed.")
            return 0
        except (urllib.error.URLError, urllib.error.HTTPError, AssertionError, json.JSONDecodeError) as exc:
            last_error = exc
            print(f"Attempt {attempt}/{MAX_ATTEMPTS} failed: {exc}")
            if attempt < MAX_ATTEMPTS:
                time.sleep(RETRY_SECONDS)

    print(f"ERROR: API smoke checks failed after {MAX_ATTEMPTS} attempts.")
    if last_error:
        print(last_error)
    return 1


if __name__ == "__main__":
    sys.exit(main())
