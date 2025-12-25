# Carpooling Android Application
## Tech Stack 
- Java
- SQLite database.
- Google Maps for locations
- Places API for searching locations
  • RecyclerView to view offered vehicles/drivers
  • Navigation between activities with intents
  • Fragments for portrait/landscape different view of
  a certain schedule

## Roles
- Passenger
- Driver
  
##Functionalities
- You can register as driver or as passenger.
- The driver is able to add his details such as the price per km, vehicle model(he can choose one from the spinner or add a new one manually), his start and end time of availability.
- If the logged driver has inserted his data previously, when he logins again he can edit the data.
- The driver is able to see list of his rides and rate them(rate the passenger).
- The logged driver can see a map where he can choose his destination using the search bar and when he selects a destination a polyline is added from the origin to the destination. With click on the markers you can see the address of the origin and destination. Once the destination is chosen, the passenger is available to proceed and to choose a driver.
- The passenger is able to search the drivers by their name, surname or vehicle model. Clicking on element of the driver list opens an activity where the passenger can see the drivers details(rating, price perkm,availability etc.). In landscape the driver list and the clicked driver details are shown in the same time using fragments. The passenger is able to select a driver with clicking on the button select driver. 
- The next activity after selecting a driver is rating of the driver. Also the total price and the distance of the trip are shown here. This informations and the ratings of the drivers are also shown in the My Rides activity.

Video of the mobile app:

https://github.com/user-attachments/assets/78f6d46a-e94c-4b8a-9e88-34444d6fd93c


![image](https://github.com/user-attachments/assets/5aefe636-7c7c-4c79-b139-98fb835033a9)
![image](https://github.com/user-attachments/assets/bf955ecc-4eca-4fd1-939b-6017b71c0945)






