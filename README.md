Carpooling Android Application: Java, SQLite for the database. You can register as driver or as passenger. The driver is able to add his details such as the price per km, vehicle model(he can choose one from 
the spinner or add a new one manually), his start and end time of availability. If the logged driver has inserted his data previously, when he logins again he can edit the data. The driver is able to see 
list of his rides and rate them(rate the passenger). The logged driver can see a map where he can choose his destination using the search bar and when he selects a destination a polyline is added 
from the origin to the destination. With click on the markers you can see the address of the origin and destination. Once the destination is chosen, the passenger is available to proceed and to choose a driver.
The passenger is able to search the drivers by their name, surname or vehicle model. Clicking on element of the driver list opens an activity where the passenger can see the drivers details(rating, price per
km,availability etc.). In landscape the driver list and the clicked driver details are shown in the same time using fragments. The passenger is able to select a driver with clicking on the button select driver. 
The next activity after selecting a driver is rating of the driver. Also the total price and the distance of the trip are shown here. This informations and the ratings of the drivers are also shown in the
My Rides activity.
