# WebWifiApp
http://wigle-wifi-app.herokuapp.com/ 
<br>
login into username auto for automatic updating feature.

# This is the diagram of our program
# ![diagram](https://github.com/Ariel-OOP/WifiApp/blob/master/Diagram_of_classes_after_teacher.png?raw=true "Title")

# Our division into packages is:
Our project is divided into packages when each package contains several departments that have a common camp.
### Packages are:
**Algorithms** - Contains the classes responsible for performing algorithm 1, 2 in the assignment.

**Attributes** - Contains the classes that represent the data structures and objects that we have in the project.

**CSV_IO** - Contains the classes that are responsible for reading the CSV files to their destination and writing the CSV files to Combo CSV and KML.

**Console_App** - Contains the classes that are responsible for the Console interface (if something wants to run and not through the GUI Web

**DataBase** - Contains the class responsible for reading from a database.

**Filters** - Contains the classes responsible for filtering data.

# Instructions:

1- Double click on App.bat to launch or go to https://wigle-wifi-app.herokuapp.com/  to play around
2- Use the name auto to receive updates to files automatically

# Almost done explainig,

Login in to your user account to keep changes.<br>
Those changes and uploads are stored in the UserFiles folder.<br>
In the UserFiles folder there are multiple folders.<br><br>
	 1- upload - the upload folder contains what a folder that has been uploaded by the user.<br>
	 2 - comboFolder - the upload folder contains what has been uploaded by the user.<br>
	 3 - output - is the csv that is created without filters<br>
	 4 - filteredOutput - is the csv that is filtered <br><br>
This project currently uses <strong>Maven</strong> to download jars and to automatically deploy to a heroku server.
<br>Also, the project heavily depends on the <strong>Spark framework</strong> for routing.

# The processes in our system run in parallel.
### There are two main processes,
1) a process that is responsible for receiving information from the user that includes Wigle, Combo CSV files and table information for reading, as well as responsible for receiving the filters.
2) Listens to changes made to files in a folder and can update the table accordingly.

# Other inclusions

Uml diagrams in uml and jpeg formats.
