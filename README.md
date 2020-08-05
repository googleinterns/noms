# noms

A full-stack web application that connects students and college organizations by posting about free food events on campus.

## Context

In the US, there are 4,360 degree granting institutions in the United States; including 2-year and 4-year colleges. Through all of these colleges, there are 19.9 million students who attend and specifically at 4-year colleges, 85% are awarded financial aid. Averaging the amount of college students and institutions, there are around 4500 students per campus; college campuses host a large number of organizations, which frequently have catered events, ranging from quick club meetings to multi-day academic conferences and hackathons. It is difficult for these organizations to accurately predict how many attendees their events will have, and they may err on the side of ordering too much food. When these events end, the leftover food needs to be consumed quickly or risk being thrown away. This kind of potential food waste is, environmentally speaking, the worst kind there is: food that has completed the entire cycle of growing, transportation, storage, and preparation—just to end up in the bin. 

Combine this issue with the reality that at some colleges, up to half of all students regularly experience food insecurity, and getting leftover food into the right hands (and stomachs) becomes not just an environmental necessity, but a humanitarian one as well. However, in attempting to address these issues, college organizations may find they lack the infrastructure to quickly notify and distribute their leftover food to the students who need it most, despite the fact that the students are otherwise ideally located to receive the food.

## Functional Requirements

To battle food insecurity and decrease food waste, we will build an app that effectively connects students and college organizations with the following features:
1. All users can post where free food is available
2. All users can view a map of nearest locations of where free food is available
3. All users can view a centralized feed where they can view and sort through most recent events 
4. All users can subscribe/unsubscribe to receive notifications informing them of free food near them
5. Website will be responsive and inclusive to various device types, ranging from mobile (with possibly outdated hardware) to desktop
6. Website will follow best practices for accessibility as described here, where possible

Primary users:

**College students**, who are primarily motivated to use this app to find free food near them. They may also occasionally post free food events if they see that an organization hasn’t done so. 

**College organizations**, which are motivated to use this app in order to quickly distribute leftover food so that it doesn’t have to go to waste, which has both monetary ($ spent on food isn’t wasted), environmental (food doesn’t end up in landfill), humanitarian (disadvantaged college students get food), and a publicity (organizations can get more students at their events) benefits.

## Installation

APIs Needed:
* Google Datastore
* Google Maps API
* Gmail API
* Geocoding Service API
* Google Secret Manager

## Usage

To run a local server, execute this command:

```bash
mvn package appengine:run
```

## Contributors

[@areetaw](https://github.com/areetaw): Email notifications

[@mirrorkeydev](https://github.com/mirrorkeydev): Embedded map

[@ubahl](https://github.com/ubahl): Add and view postings


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
