#!/usr/bin/env node

/**
 * Fetches current NBA players from stats.nba.com and saves the response
 * as nba-players.json, matching the format expected by the app.
 *
 * Usage: node fetch-nba-players.js [season]
 * Example: node fetch-nba-players.js 2025-26
 *
 * Then update the GitHub gist with the generated nba-players.json:
 * https://gist.github.com/hdurix/c83df92b0385c7d3d4c033bfd34e4909
 */

const https = require('https');
const fs = require('fs');
const path = require('path');

function getCurrentSeason() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;

  // NBA season runs Oct-Jun: Oct-Dec = year/(year+1), Jan-Sep = (year-1)/year
  if (month >= 10) {
    return `${year}-${String(year + 1).slice(2)}`;
  }
  return `${year - 1}-${String(year).slice(2)}`;
}

const season = process.argv[2] || getCurrentSeason();

const requestHeaders = {
  Accept: '*/*',
  'Accept-Language': 'fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7',
  Connection: 'keep-alive',
  Origin: 'https://www.nba.com',
  Referer: 'https://www.nba.com/',
  'Sec-Fetch-Dest': 'empty',
  'Sec-Fetch-Mode': 'cors',
  'Sec-Fetch-Site': 'same-site',
  'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
  'sec-ch-ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"',
  'sec-ch-ua-mobile': '?0',
  'sec-ch-ua-platform': '"Linux"',
};

const queryParams = {
  College: '',
  Country: '',
  DraftPick: '',
  DraftRound: '',
  DraftYear: '',
  Height: '',
  Historical: '1',
  LeagueID: '00',
  Season: season,
  SeasonType: 'Regular Season',
  TeamID: '0',
  Weight: '',
};

const queryString = Object.entries(queryParams)
  .map(([key, val]) => `${key}=${encodeURIComponent(val)}`)
  .join('&');

const options = {
  hostname: 'stats.nba.com',
  path: `/stats/playerindex?${queryString}`,
  method: 'GET',
  headers: requestHeaders,
};

console.log(`Fetching NBA players for season ${season}...`);

const req = https.request(options, res => {
  let rawData = '';

  res.on('data', chunk => (rawData += chunk));
  res.on('end', () => {
    if (res.statusCode !== 200) {
      console.error(`API returned status ${res.statusCode}`);
      process.exit(1);
    }

    try {
      const data = JSON.parse(rawData);
      const rowSet = data.resultSets[0].rowSet;
      const activeCount = rowSet.filter(row => row[19] === 1.0).length;

      const outputPath = path.join(__dirname, 'nba-players.json');
      fs.writeFileSync(outputPath, JSON.stringify(data, null, 2));

      console.log(`Total players: ${rowSet.length}`);
      console.log(`Active players (roster status 1.0): ${activeCount}`);
      console.log(`Saved to ${outputPath}`);
      console.log(`\nUpdate the gist: https://gist.github.com/hdurix/c83df92b0385c7d3d4c033bfd34e4909`);
    } catch (err) {
      console.error('Failed to parse API response:', err.message);
      process.exit(1);
    }
  });
});

req.on('error', err => {
  console.error('Request failed:', err.message);
  process.exit(1);
});

req.end();
