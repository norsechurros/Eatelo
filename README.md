# Eatelo

Eatelo is a restaurant ranking application that uses an Elo-based rating system to rank restaurants based on user preferences.

Instead of traditional star ratings, Eatelo improves rankings dynamically by asking users to compare restaurants head-to-head. Each choice updates the rankings using the Elo algorithm, resulting in a more accurate and continuously evolving list of top restaurants.

## Overview

Eatelo applies the Elo rating system, commonly used in competitive games, to food discovery. Users are shown pairs of restaurants and select the one they prefer. Based on these selections, each restaurant gains or loses rating points.

Over time, this produces a ranking that reflects collective user preferences rather than static reviews.

## Key Idea

* Restaurants are assigned Elo ratings
* Users compare two restaurants at a time
* Rankings update after every user decision
* The system converges toward a reliable ordering of restaurants

This approach reduces bias found in traditional rating systems and makes rankings more interactive and data-driven.

## Features

* Pairwise comparison of restaurants
* Dynamic ranking using Elo algorithm
* Continuously improving leaderboard
* Simple and interactive user experience

## How It Works

1. The app presents two restaurants to the user
2. The user selects the one they prefer
3. The Elo rating formula updates both restaurants' scores
4. Higher-rated restaurants are more likely to appear as top recommendations

The system becomes more accurate as more comparisons are made.

## Why Elo?

Traditional rating systems (like 1–5 stars) suffer from inflation and inconsistency. The Elo system solves this by:

* Making every vote relative
* Rewarding upsets (choosing a lower-ranked restaurant)
* Penalizing expected outcomes less
* Continuously refining rankings

## Future Improvements

* Location-based restaurant filtering
* Personalised rankings per user
* Social features and shared rankings
* Integration with maps and reviews
