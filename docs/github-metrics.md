# Repository metrics automation

`.github/workflows/repository-metrics.yml` runs on a ten-minute cron and can also be started manually. The schedule is best-effort: GitHub may delay or drop scheduled workflow starts during high load, and scheduled workflows run from the default branch. The workflow requests public repository metadata and latest-release download totals, then updates `metrics/latest.json` only when values change so it does not create a commit every ten minutes.

## Optional traffic graph data

GitHub's views/clones/referrer endpoints are aggregate and cover a rolling 14-day window. They require repository Administration read access. GitHub's built-in `GITHUB_TOKEN` does not include that permission. If you want these aggregate fields, create a narrowly scoped fine-grained personal access token with **Administration: read** for this repository, then add it as a repository Actions secret named `TRAFFIC_API_TOKEN`. Never put a token in source, a workflow file, logs, or an issue. If the secret is absent or cannot read traffic, the workflow still updates public counters and skips traffic data.

This automation cannot identify individual repository visitors or reveal the exact words someone typed into GitHub Search. It does not add visitor tracking. Traffic fields are intentionally optional.

## Files

- `scripts/update_metrics.py` fetches public repo/release metadata and optional traffic aggregates.
- `.github/workflows/repository-metrics.yml` schedules the job and commits changed metrics using the short-lived workflow token with `contents: write`.
- `metrics/latest.json` is a machine-readable snapshot. It contains aggregate counters only.

## Schedule adjustment

Edit the workflow's `cron` expression if you want a different interval. GitHub documents a minimum scheduled interval of five minutes; this repository uses ten minutes to reduce unnecessary runs. Scheduled jobs use UTC by default and are not guaranteed to start at the exact minute.
