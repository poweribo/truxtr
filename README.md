Truxtr
======

Demo web application that tells the user what types of food trucks might be found near a specific location on a map.
It uses sfgov.org's data for [mobile food](https://data.sfgov.org/Permitting/Mobile-Food-Facility-Permit/rqzj-sfat) facility permit applications.

See the demo hosted at OpenShift : [Truxtr](http://truxtr-ibo.rhcloud.com/assets/index.html) (Defunct)

This is a full-stack application for practice/demo purposes. not a reference for best practices.

Backend: Dropwizard, Guice, Hibernate, Redis, Redis and H2, Maven
Frontend: Backbone.JS + JQuery, Bootstrap CSS and Google Maps 
Others: SODA API, Configured app to be deployed on Redhat's OpenShift Cloud Hosting


Notes/Possible enhancements
----------------------------------

* The use of H2 is only a quick and dirty way of caching items instead of querying the SODA Api directly. Wanted the results
  to be displayed quickly. May not be wise to continue using it if the dataset grows a lot bigger. It also uses the default
  column sizes of varchar(255). Also unit test for persistence wont be useful until H2 is switched from "mem" to regular FS db.

* The polling service that populates H2/Redis does a delete all and bulk insert (no updates). Could result into blank results if user does
  a search while bulk inserts are happening in the background. Could have used a better strategy.

* use Vue or React to replace backbone/jquery

* add schedules as part of criteria when displaying Search results.

* Support other data sources for food truck locations in other cities (ie NYC)

* Enhancements for front-end: auto-zoom to markers, bigger map layout, better UX


Requirements to build and run
-----------------------------

This project includes configuration needed to deploy on Openshift. Pls see .openshift folder for action hooks.

To compile and run locally, clone this repo and do the ff steps :

1. install redis w/ default settings
2. make sure you have Java 7 JDK and Maven
3. mvn package
4. java -jar target/truckotracker-1.0-SNAPSHOT.jar server truckotracker.yml
5. enter http://localhost:8080/assets/index.html on your browser

Created: Feb 2014