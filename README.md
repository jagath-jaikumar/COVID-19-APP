
[![ForTheBadge built-with-science](http://ForTheBadge.com/images/badges/built-with-science.svg)](https://GitHub.com/Naereen/) [![ForTheBadge built-with-swag](http://ForTheBadge.com/images/badges/built-with-swag.svg)](https://GitHub.com/Naereen/)[! [forthebadge made-with-python](http://ForTheBadge.com/images/badges/made-with-python.svg)](https://www.python.org/)


HotSpot
=====================================================

The HotSpot platform consists of an Android app, backend server, and web app for the purposes of informing people of areas where there is a high potential for the spread of germs.

## Overview

**Idea**: 

The idea for this app is to inform people of potential "HotSpots": these are places where there has been a significant human presence and therefore a high chance of there being Coronavirus germs. I have found myself unsure of what department stores and grocery stores are more unsafe than others, so this app will help people like me quickly find out the safest places to be. 



One main consideration I had while making this app is that I wanted people to feel comfortable providing their location data, so I have anonymized all device information. I do not store any personal information on any device, and there is no login required to use my application.



**UI/UX Design**:

I tried to keep my design as uncluttered as possible. My application is a single page app, with a map taking up most of the screen, and two buttons at the bottom of the screen for scores. Users will be able to see the heatmap over the base map to determine hotspots around them.



For the Public Health App (PHA), I went for a siilar minimalist approach. The PHA is just a heatmap that shows the popular places people have been visiting in the past day.



**Feature Engineering**:

I did not use any external sensors for my app outside of GPS data. I had to do some processing on this data to extract the relevant Latitude and Longitude data, but other than that I did not have to worry too much about feature engineering or machine learning. 



My main concerns were in transfering data back and forth from the server to the android device. I used Android Volley and Python Flask to set up a client server model and transfer informtation via JSON objects and Strings.



**Evaluation**:

In evaluating my app, I used my own personal GPS data. I consulted my sister, who is a resident doctor at UMass Worcester, and she told me that informing the public of potentially dangerous places is extremely important during a crisis like this. I asked her to take a look at HotSpot PHA, and she appreciated the easy to use design and powerful HotSpot information.



I tested my app in both simulation and on a real android device. For the purpose of my video demo, I decided to use a simulated device because it is easier to record.



**Differentiating my Contribution**

Although I used the starter code, I added several features.

App Features:

- Score buttons for last day and all time
- Tile HeatMap of HotSpots
- Background Location Service saving location data
- Android Volley Rest Calls to POST and GET data



Server Features:

- Database for storing location data
- Functions for handling location POST requests
- Functions for handling score GET requests
- Scoring function for travel and interaction scores
- Function for returning popular places



PHA Features:

- Bootstrapped Plotly Dash Web App
- Heatmap showing popular places in the past day



Prerequisites
--------------

- Android SDK v24
- Latest Android Build Tools
- Android Support Repository
- Google Map API
- Ubuntu 18.04 Server with python3/pip


Author
-------

Jagath Jai Kumar

590U Final Project

License
-------

MIT License

Copyright (c) 2020 Jagath Jai Kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
