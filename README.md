# 🍱 noms
[![Build](https://github.com/googleinterns/noms/workflows/Build/badge.svg)](https://github.com/googleinterns/noms/actions) [![Test](https://github.com/googleinterns/noms/workflows/Test/badge.svg)](https://github.com/googleinterns/noms/actions) [![Lint](https://github.com/googleinterns/noms/workflows/Lint/badge.svg)](https://github.com/googleinterns/noms/actions)


>A full-stack web application connecting students and college organizations to battle food insecurity and food waste.

## Overview
- [Description](#Description)
- [Motivation](#Motivation)
- [Technologies](#Technologies)
- [Demos](#Demos)
  - [Find your college](#find-your-college)
  - [Find food at your college](#find-food-at-your-college)
  - [Make a new post](#make-a-new-post)
  - [Sign up for email notifications](#sign-up-for-email-notifications)
  - [Receive email notifications](#receive-email-notifications)
- [Usage](#Usage)
- [Authors](#Authors)
- [Contributing](#Contributing)
- [License](#License)
- [Disclaimer](#Disclaimer)

## Description

**noms** is a web app that allows college organizations to post the locations of free food on campus and for students to view and receive notifications for these posts.

Primary features include the ability to:
- create posts about free food on a college campus
- view posts at a college campus in a feed and on an interactive map
- subscribe/unsubscribe to notifications informing them of free food near them

The intended target audiences are:

- *College students*, who are primarily motivated to use this app to find free food near them.

- *College organizations*, which are motivated to use this app in order to quickly distribute leftover food so that it doesn’t have to go to waste, which has both monetary ($ spent on food isn’t wasted), environmental (food doesn’t end up in landfill), humanitarian (disadvantaged college students get food), and publicity (organizations can get more students at their events) benefits.

## Motivation

The US is home to ~20 million students attending 4,000+ degree-granting institutions. Despite [around 80%](https://nces.ed.gov/fastfacts/display.asp?id=31) of students being awarded financial aid, researchers found that [around half](https://hope4college.com/wp-content/uploads/2018/10/StillHungryMA-4-1.html) of students were still ['basic-needs insecure'](http://ccsne.compact.org/wp-content/uploads/large/sites/50/2017/07/Basic-Needs-Insecurity-College-Students.pdf), which includes food insecurity, housing insecurity, and homelessness. Of this group, the most common challenge was food insecurity, with [around 40%](https://hope4college.com/wp-content/uploads/2018/10/StillHungryMA-4-1.html) of respondents identifying with statements such as "I worried whether my food would run out before I got money to buy more", and "The food that I bought just didn't last and I didn't have money to get more".

The day-to-day programming on campus belies the reality of many students; while they struggle to gain reliable access to food, on-campus organizations host hundreds of catered events a year, ranging from quick club meetings to multi-day academic conferences and hackathons. When these events end, the leftover food needs to be consumed quickly or risk being thrown away. This potential source of food could be routed to those who need it most, but college organizations may find they lack the infrastructure to quickly notify and distribute their leftover food, despite the fact that the students are otherwise ideally located to receive the food.

It is not just students that suffer from leftover food being thrown out; the Earth does, too. This kind of potential food waste is, environmentally speaking, [the worst kind there is](https://moveforhunger.org/the-environmental-impact-of-food-waste): food that has completed the entire cycle of growing, transportation, storage, and preparation—just to end up in the bin.

**noms** hopes to bridge this gap between college organizations and students, and aid the environment while we're at it.

## Technologies

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (Servlets)
- **APIs**:
  * [Google Cloud Datastore](https://cloud.google.com/datastore)
  * [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/overview)
  * [Gmail API](https://developers.google.com/gmail/api)
  * [Google Geocoding Service](https://developers.google.com/maps/documentation/javascript/geocoding)
  * [Google Secret Manager](https://cloud.google.com/secret-manager)
  * [Google Cloud Scheduler](https://cloud.google.com/scheduler/docs)
- **Testing**: JUnit/Mockito (Java), Mocha/Chai (JavaScript)
- **Assets**: Fontawesome, [Iconixar](https://www.flaticon.com/authors/iconixar) icons
- **Build Automation**: Maven
- **Deployment**: Google App Engine

## Demos

### Find your college
The landing page presents users the option to find their college's page. Upon typing, an autocomplete dropdown  suggests college names.

![](https://user-images.githubusercontent.com/35010111/91064467-24261080-e5e4-11ea-9954-b1712004ebb8.gif)


### Find food at your college
On a college's page, the map and feed allow users to browse food available at their college. The user is present on the map as a blue marker. Filters allow the user to narrow down the type of post.

![](https://user-images.githubusercontent.com/35010111/91066586-bd562680-e5e6-11ea-9248-8b3ef66ad6dc.gif)
![](https://user-images.githubusercontent.com/35010111/91067113-5e44e180-e5e7-11ea-9026-bf5ca84d6b9c.gif)


### Make a new post
The new post modal features validation for all of its fields and uses the Geocoding Service to automatically translate addresses entered by the user into map coordinates.

![](https://user-images.githubusercontent.com/35010111/91067907-5afe2580-e5e8-11ea-98c9-07d6fd0ae721.gif)

### Sign up for email notifications
Signing up for email notifications allows users to get emails for each new post at their college of interest. They're greeted with a welcome email upon signing up.

![](https://user-images.githubusercontent.com/35010111/91069224-fba11500-e5e9-11ea-8e01-6e17694f9a73.gif)
<img src="https://user-images.githubusercontent.com/35010111/91068374-09a26600-e5e9-11ea-982e-579fd0f8c943.png" width="800">

### Receive email notifications
Upon organizations making new posts at a user's college of interest, email notifications will give the user a brief overview of the event in question. The box's style is smaller than that of the welcome email to denote that a single post is a smaller event.

<img src="https://user-images.githubusercontent.com/35010111/91069756-a4e80b00-e5ea-11ea-9616-2a653af159c8.png" width="800">

## Usage

To run a local server, execute this command:

```bash
mvn package appengine:run
```

## Authors
Google STEP (Student Training in Engineering Program) Pod #186, Summer 2020

[@areetaw](https://github.com/areetaw):
- Email notifications
- Contact page

[@mirrorkeydev](https://github.com/mirrorkeydev):
- Embedded maps
- Landing + about page

[@ubahl](https://github.com/ubahl):
- Creating + storing posts
- Displaying posts


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
**noms** is licensed under the Apache 2.0 License.

## Disclaimer
This is not an officially supported Google product.
