# StopWatch Software Requirements

## User Stories
### MVP
1. As a user, I want to be able to select a location on a map and set it as my destination endpoint so that I can be notified when I am close to my destination.
2. As a user, I want to be notified when it is almost time to get off the bus so that I can focus my attention on my book/music/game/conversation.
3. As a user, I want to be able to save destinations that I use frequently so that I can access them without setting a new pin on the map.
4. As a user, I want to be able to see my location on the map so that I can track my progress toward reaching my destination.
5. As a user, I want to be able to see the notification radius around my destination on the map.

### Stretch Goals
6. As a user, I want to be able to use my location to show nearby bus stops so that I can select a starting point and bus route for my trip.
7. As a user, I want to be able to select a bus stop destination so that I can set a destination endpoint that is within-route for my bus.
8. As a user, I want to be able to save bus stops and routes that I use frequently so that I can access them quickly and easily.
9. As a user, I want to be able to adjust when I am notified that the bus is approaching my destination stop so that I can choose between X minutes away, X stops away, or X blocks away.

## Feature Tasks
1. 
   - Show map
   - Show user location
   - Get latitude and longitude for user-selected map location
   - Show map marker on map at user-selected map location
2. 
   - Set destination radius
   - Monitor changing user location
   - Check to see if user location is within radius
   - Send (notification / SMS) or make the phone vibrate when user location is within radius
   - Ensure that a notification or SMS is received when user location falls within radius.
3. 
   - Show “Save this destination” checkbox
   - Save destination in Shared Preferences
   - Top dropdown menu includes link to see “Saved Destinations”
4. 
   - Show map
   - Show user location
   - Show destination location
5. 
   - Show location radius around destination location

## Acceptance Tests
1. 
   - Ensure that user marker on map matches latitude and longitude data within reasonable error margin.
   - Ensure that tapped map location matches latitude and longitude data within reasonable error margin.
2.
   - Ensure that the within radius determination is true for user locations that are less than the defined distance away from the destination.
   - Ensure that the within radius determination is false for user locations that are more than the defined distance away from the destination.
3.
   - Ensure that dropdown menu includes “Saved Destinations”
   - Ensure that Shared Preferences includes destinations that are saved by the user.
4.
   - Ensure that map view orients around user location.
   - Ensure that user location appears on map.
5.  
   - Ensure that the location radius displays on map around destination.
   


## Wireframes

### Select Route
![](./assets/wireframes/SelectRouteWireFrame.png)

### Select Destination Stop
![](./assets/wireframes/SelectDestinationStopWireFrame.png)

### Set Approaching Destination Alert
![](./assets/wireframes/SetAlertWireFrame.png)