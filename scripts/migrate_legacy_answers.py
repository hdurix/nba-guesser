#!/usr/bin/env python3
"""Migrate legacy answer items to add the 'timestamp' field.

Legacy items (poll_id starting with 'legacy-') have a 'date' field but no
'timestamp'. The Java code (DynamoDbQuizAnswers.fromItem) expects 'timestamp',
so these items are invisible to the stats command.

This script scans all legacy items and adds timestamp = date for each one.

Prerequisites:
  pip install boto3
  AWS credentials configured (profile "perso", region eu-west-3)

Usage:
  python3 scripts/migrate_legacy_answers.py              # dry-run (default)
  python3 scripts/migrate_legacy_answers.py --execute     # actually update DynamoDB
"""

import sys
import time

import boto3
from botocore.config import Config

ANSWERS_TABLE = "nba-guesser-answers"
REGION = "eu-west-3"
PROFILE = "perso"


def scan_legacy_items(client):
    """Scan all legacy items that are missing the timestamp field."""
    items = []
    params = {
        "TableName": ANSWERS_TABLE,
        "FilterExpression": "begins_with(poll_id, :prefix) AND attribute_not_exists(#ts)",
        "ExpressionAttributeValues": {":prefix": {"S": "legacy-"}},
        "ExpressionAttributeNames": {"#ts": "timestamp"},
    }

    while True:
        response = client.scan(**params)
        items.extend(response.get("Items", []))

        if "LastEvaluatedKey" not in response:
            break
        params["ExclusiveStartKey"] = response["LastEvaluatedKey"]

    return items


def update_item(client, item, dry_run):
    """Add timestamp field (= date value) to a single item."""
    date_value = item["date"]["S"]

    if dry_run:
        return True

    client.update_item(
        TableName=ANSWERS_TABLE,
        Key={
            "poll_id": item["poll_id"],
            "user_id": item["user_id"],
        },
        UpdateExpression="SET #ts = :ts",
        ExpressionAttributeNames={"#ts": "timestamp"},
        ExpressionAttributeValues={":ts": {"S": date_value}},
    )
    return True


def main():
    dry_run = "--execute" not in sys.argv

    if dry_run:
        print("DRY RUN — pass --execute to actually update DynamoDB\n")

    session = boto3.Session(profile_name=PROFILE, region_name=REGION)
    client = session.client("dynamodb", config=Config(retries={"max_attempts": 3, "mode": "adaptive"}))

    # Scan legacy items missing timestamp
    print("Scanning for legacy items missing 'timestamp'...")
    items = scan_legacy_items(client)
    print(f"  Found {len(items)} items to migrate\n")

    if not items:
        print("Nothing to migrate.")
        return

    # Show a sample
    sample = items[0]
    print(f"  Sample: poll_id={sample['poll_id']['S']}, user_id={sample['user_id']['N']}, date={sample['date']['S']}")
    print(f"  Will add: timestamp={sample['date']['S']}\n")

    # Update items
    updated = 0
    errors = 0
    for i, item in enumerate(items):
        try:
            update_item(client, item, dry_run)
            updated += 1
        except Exception as e:
            errors += 1
            print(f"  ERROR on poll_id={item['poll_id']['S']}, user_id={item['user_id']['N']}: {e}", file=sys.stderr)

        if (i + 1) % 500 == 0:
            print(f"  Progress: {i + 1}/{len(items)}")

    # Summary
    print(f"\n{'DRY RUN ' if dry_run else ''}Summary:")
    print(f"  Updated: {updated}")
    print(f"  Errors:  {errors}")

    if not dry_run:
        # Validate: count items still missing timestamp
        remaining = len(scan_legacy_items(client))
        print(f"\n  Remaining items without timestamp: {remaining}")
        if remaining == 0:
            print("  Validation passed!")
        else:
            print(f"  WARNING: {remaining} items still missing timestamp", file=sys.stderr)


if __name__ == "__main__":
    main()
